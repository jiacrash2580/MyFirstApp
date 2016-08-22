package com.foxit.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;


import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFDocument;
import com.foxit.gsdk.utils.FileHandler;
import com.foxit.view.PDFPagerAdapter;

//Handle all the Task in an ordered synchronized manner
public class TaskManager
{
	public static final String TAG = "TaskManager";
	
	//tasks pool, manage the task thread.
	private ArrayList<AbstractTask> tasks = null;
	private Thread taskThread = null;
	
	private PDFContext pdfContext = null;
	private FileHandler fileHandler = null;//need release
	private Vector<RectF> selectVector = null;
	
	public TaskManager(Context context)
	{
		tasks = new ArrayList<AbstractTask>();
		taskThread = new Thread(new TaskThread(this));
		pdfContext = new PDFContext(context);
	}
	
	public PDFContext getPDFContext()
	{
		return pdfContext;
	}
	
	public Collection<AbstractTask> getCollection()
	{
		return Collections.unmodifiableList(tasks);
	}

	private void start()
	{
		if (taskThread.isAlive() == false)
			taskThread.start();
	}
	
	private void end()
	{
		removeAllTasks();
		while (taskThread.isAlive())
		{
			taskThread.interrupt();
		}
	}
	
	public AbstractTask getCurrenTask()
	{
		synchronized (tasks)
		{
			if (tasks.size() > 0)
				return tasks.get(0);
			else
				return null;
		}
	}
	
	public void addTask(AbstractTask task)
	{
		synchronized (tasks)
		{
			//the  current position
			int position = tasks.size();
			for (int i = tasks.size() - 1; i >= 0; i--)
			{
				AbstractTask tempTask = tasks.get(i);
				if (task.isEqual(tempTask)) return;
			}
			
			tasks.add(position, task);
		}
	}
	
	public void removeTask(AbstractTask task)
	{
		synchronized (tasks)
		{
			if (tasks.size() > 0)
				tasks.remove(task);
		}
	}
	
	public void removeAllTasks()
	{
		synchronized (tasks)
		{
			for (int i = tasks.size() - 1; i >= 0; i--)
			{
				AbstractTask tempTask = tasks.get(i);
				tasks.remove(tempTask);
			}
		}
	}
		
	public void openDocument(String path, byte[] password) throws PDFException
	{
		fileHandler = FileHandler.create(path, FileHandler.FILEMODE_READONLY);
		if (password == null)
		{
			pdfContext.document = PDFDocument.open(fileHandler, null);
		}
		else {
			pdfContext.document = PDFDocument.open(fileHandler, password);
		}
		
		//start task thread
		start();
		//start load document
		AbstractTask loadDocTask = new LoadDocumentTask(pdfContext);
		addTask(loadDocTask);
	}
	
	public void closeDocument()
	{
		// release file handler
		try
		{
			if(fileHandler != null)
			{
				fileHandler.release();
			}
		}
		catch (PDFException e)
		{
		}
		AbstractTask closeDocTask = new CloseDocumentTask(pdfContext);
		removeAllTasks();
		addTask(closeDocTask);
		//end task thread
		end();
	}
	
	public void loadPage(int pageIndex)
	{
		AbstractTask loadPageTask = new LoadPageTask(pdfContext, pageIndex);
		addTask(loadPageTask);
	}
	
	public void closePage()
	{
		AbstractTask closepageTask = new ClosePageTask(pdfContext);
		addTask(closepageTask);
	}
	
	public void drawSinglePageBitmap()
	{
		AbstractTask drawBitmapTask = new DrawSinglePageBitmapTask(pdfContext);
		addTask(drawBitmapTask);
	}
	
	public void search(Activity activity, PDFPagerAdapter adapter, final String target, int searchFlag)
	{
		AbstractTask searchTask = new SearchTask(adapter, pdfContext, target, searchFlag);
		searchTask.setParentActivity(activity);
		addTask(searchTask);
	}
	
	public void search(PDFPagerAdapter adapter, int searchFlag)
	{
		AbstractTask searchTask = new SearchTask(adapter, pdfContext, searchFlag);
		addTask(searchTask);
	}
	
	public void zoom(PDFPagerAdapter adapter, boolean bZoomIn)
	{
		AbstractTask zoomTask = new ZoomTask(adapter, pdfContext, bZoomIn);
		addTask(zoomTask);
	}
	
	//activity:used to show the text dialog, rect:the text rectangle
	public void extractText(Activity activity, RectF rect)
	{
		AbstractTask extractTask = new ExtractTextTask(activity, pdfContext, rect);
		
		addTask(extractTask);
		while(!extractTask.bFinished)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		extractTask.bFinished = false;
		selectVector = ((ExtractTextTask) extractTask).getRectFs();
	}
	
	public Vector<RectF> getSelectVector()
	{
		return selectVector;
	}
	
	public void saveDocument(String fileName, int saveFlag)
	{
		AbstractTask saveTask = new SaveDocumentTask(pdfContext, fileName, saveFlag);
		addTask(saveTask);
	}
	
	//add highlight/underline annotation.
	public int[] getPaintRects(RectF rect)
	{
		AbstractTask extractTask = new ExtractTextTask(null, pdfContext, rect);
		
		addTask(extractTask);
		while(!extractTask.bFinished)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		extractTask.bFinished = false;
		selectVector = ((ExtractTextTask) extractTask).getRectFs();
		return ((ExtractTextTask) extractTask).getAllPiecesRotation();
	}
	
	public void InsertImgTask(Activity activity, RectF rect)
	{
		if (selectVector != null) {
			selectVector.clear();
		}
		AbstractTask insertImgTask = new InsertImgTask(activity, pdfContext, rect);
		
		addTask(insertImgTask);
		while(!insertImgTask.bFinished)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		insertImgTask.bFinished = false;
	}

}
//task thread
class TaskThread implements Runnable
{
	public static final String TAG = "TaskThread";
	
	private TaskManager controller = null;
	
	public TaskThread(TaskManager controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void run()
	{
		Thread curThread = Thread.currentThread();
		while (!curThread.isInterrupted())
		{
			if (controller == null) return;
			AbstractTask tempTask = controller.getCurrenTask();
			if (tempTask !=null)
			{
				boolean ret = tempTask.execute();
				controller.removeTask(tempTask);
				if(ret == false && tempTask.taskType == AbstractTask.TYPE_LOADDOCUMENT)
				{
					Log.e(TAG, "TaskThread run failed!!!");
					System.exit(-1);
					return;
				}
			
			}			
		}
	}
	
}


