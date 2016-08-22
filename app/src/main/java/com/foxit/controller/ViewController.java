package com.foxit.controller;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.foxit.bean.AnnotInfo;
import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.image.Image;
import com.foxit.gsdk.utils.FileHandler;
import com.foxit.task.SearchTask;
import com.foxit.task.TaskManager;
import com.foxit.view.IPageLayoutMode;
import com.foxit.view.PDFPagerAdapter;
import com.foxit.view.PDFViewPager;
import com.foxit.view.PageChangeListener;

public class ViewController implements IViewController
{
	public static final String TAG = "ViewController";
	
	//rotation direction
	public static final int ROTATIONDIRECTION_LEFT  = 0;
	public static final int ROTATIONDIRECTION_RIGHT = 1;	
	
	private PDFContext pdfContext = null;
	private TaskManager taskManager = null;
	private PDFViewPager viewPager = null;
	private PDFPagerAdapter pagerAdapter = null;
	private PageChangeListener pageChangeListener = null;
	private Context context = null;
	public boolean bInitialized = false;//initial or not
	private int layoutMode = IPageLayoutMode.SINGLEPAGE;//page view mode, default SINGLEPAGE.
	
	private ViewController(Context context)
	{
		this.context = context;	
		taskManager = new TaskManager(context);
		//viewPager = new PDFViewPager(context);
		viewPager = new PDFViewPager(taskManager);
		pdfContext = taskManager.getPDFContext();
	}
	
	public static  ViewController create(Context context)
	{
		// TODO Auto-generated method stub
		return new ViewController(context);
	}

	public ViewPager getViewPager()
	{
		return viewPager;
	}
	
	public TaskManager getTaskManager()
	{
		return taskManager;
	}
	
	@Override
	public boolean initialize()
	{
		if (context == null) return false;
		
		//get display size
		DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
		pdfContext.getCurrentState().screenWidth = dm.widthPixels;
		pdfContext.getCurrentState().screenHeight = dm.heightPixels;
		
		// set adapter for viewpager
		pagerAdapter = new PDFPagerAdapter(taskManager);
		viewPager.setAdapter(pagerAdapter);
		
		//set listener for viewpager
		//pageChangeListener = new PageChangeListener();
		//viewPager.setOnPageChangeListener(pageChangeListener);
		
		bInitialized = true;
		return true;
	}
	
	@Override
	public void openDocument(String path, byte[] password) throws PDFException
	{
		if (taskManager != null)
			taskManager.openDocument(path, password);
	}

	public void closeDocument()
	{
		if (taskManager != null)
			taskManager.closeDocument();
	}
	
	public void closePage()
	{
		if (taskManager != null)
			taskManager.closePage();
	}
	
	public void turnpage(boolean bNext) 
	{
		if (taskManager != null)
		{
			if (bNext)
			{
				pdfContext.pageIndex = pdfContext.pageIndex + 1;
				if(this.pdfContext.pageIndex > this.pdfContext.pageCount - 1)
					this.pdfContext.pageIndex = this.pdfContext.pageCount - 1;
			}
			else 
			{
				pdfContext.pageIndex = pdfContext.pageIndex -1;
				if(this.pdfContext.pageIndex < 0)
					this.pdfContext.pageIndex = 0;
			}
			addNewItem(pdfContext.pageIndex);
		}
	}
	
	public void zoom(boolean bZoomIn)
	{
		if (taskManager != null)
		{
			taskManager.zoom(pagerAdapter, bZoomIn);
			addNewItem(pdfContext.pageIndex);
			
			pagerAdapter.changeSelectState(viewPager.getCurrentItem());
		}
	}
	
	@Override
	public void setPageLayoutMode(int layoutMode)
	{
		// TODO Auto-generated method stub
		if (!bInitialized)
		{
			Log.i(TAG, "Please initlize this object first!");
			return;
		}
		
		if (layoutMode == this.layoutMode) return;
		switch (layoutMode)
		{
		case IPageLayoutMode.SINGLEPAGE:
			
			break;

		default:
			break;
		}
		this.layoutMode = layoutMode;
	}
	
	private void addNewItem(int item)
	{
		pagerAdapter.addNewItem(viewPager, item);
		viewPager.setCurrentItem(item, false);
	}
	
	@Override
	public void startSearch(Activity parentActivity, final String target)
	{
		if(taskManager != null)
			taskManager.search(parentActivity, pagerAdapter, target, SearchTask.FLAG_START);
	}
	
	@Override
	public void searchPrev()
	{
		if(taskManager != null)
			taskManager.search(pagerAdapter, SearchTask.FLAG_PREVIOUS);
	}
	
	@Override
	public void searchNext()
	{
		if(taskManager != null)
			taskManager.search(pagerAdapter, SearchTask.FLAG_NEXT);
	}
	
	public void setParent(Activity activity)
	{
		if (pagerAdapter != null)
		{
			pagerAdapter.setParent(activity);
		}
	}
	
	public void resetConfiguration()
	{
		DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
		pdfContext.getCurrentState().screenWidth = dm.widthPixels;
		pdfContext.getCurrentState().screenHeight = dm.heightPixels;
	}
	
	public void destroy()
	{
		try
		{
			if (pdfContext.textSearch != null)
			{
				pdfContext.textSearch.release();
				pdfContext.textSearch = null;
			}
			if (pdfContext.textSelection != null)
			{
				pdfContext.textSelection.release();
				pdfContext.textSelection = null;
			}
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void setAnnotInfo(AnnotInfo annotInfo)
	{
		if (pagerAdapter == null) return;
		pagerAdapter.setAnnotInfo(annotInfo, viewPager.getCurrentItem());
	}

	public int initPsi(){
		if (pagerAdapter == null) return 0;
		return pagerAdapter.initPsi(viewPager.getCurrentItem());
	}
	
	public void releasePsi(){
		if (pagerAdapter == null) return;
		pagerAdapter.releasePsi(viewPager.getCurrentItem());
	}
	
	public void saveDocument(String fileName, int saveFlag)
	{
		if (taskManager != null)
			taskManager.saveDocument(fileName, saveFlag);
	}
	
	public void importAnnotsFromFDF(String fileName)
	{
		try
		{
			FileHandler fdfHandler = FileHandler.create(fileName, FileHandler.FILEMODE_READONLY);
			pdfContext.document.importAnnotsFromFDF(fdfHandler);
			//update
			Handler handler = new Handler();
			handler.post(pagerAdapter.getViewMap().get(viewPager.getCurrentItem()));
			fdfHandler.release();
		}
		catch (PDFException e)
		{
			e.printStackTrace();
		}
	}
	
	public void exportAnnotsToFDF(String pdfPath, String fdfName)
	{
		try
		{
			FileHandler fdfHandler = FileHandler.create(fdfName, FileHandler.FILEMODE_TRUNCATE);
			pdfContext.document.exportAnnotsToFDF(pdfPath, fdfHandler);
			fdfHandler.release();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertImageToPage(String imageName)
	{
		try {
			FileHandler imageHandler = FileHandler.create(imageName, FileHandler.FILEMODE_READONLY);
			pdfContext.image = Image.load(imageHandler);
			pdfContext.image.loadFrame(0);
		} catch (PDFException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//rotate the page
	public void onRotation(int direction)
	{

		int rotation = pdfContext.getCurrentState().rotation;
		if (direction == ROTATIONDIRECTION_LEFT)
		{
			rotation = (rotation + 3) % 4; //counterclockwise rotation
		}
		else if (direction == ROTATIONDIRECTION_RIGHT)
		{
			rotation = (rotation + 1) % 4; //clockwise rotation
		}
		pdfContext.getCurrentState().rotation = rotation;

		//update
		Handler handler = new Handler();
		handler.post(pagerAdapter.getViewMap().get(viewPager.getCurrentItem()));
	}
	
	public void cancelPsi(){
		if (pagerAdapter == null) return;
		pagerAdapter.cancelPsi(viewPager.getCurrentItem());
	}
	
	public void confirmPsi(){
		if (pagerAdapter == null) return;
		pagerAdapter.confirmPsi(viewPager.getCurrentItem());
	}
}
