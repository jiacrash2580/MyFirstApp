package com.foxit.view;

import java.util.HashMap;

import com.foxit.bean.AnnotInfo;
import com.foxit.bean.PSIOperation;
import com.foxit.task.TaskManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class PDFPagerAdapter extends PagerAdapter
{
	 private static final String TAG = "PDFPagerAdapter";
	 
	 private static final int MAXCACHE_COUNT = 4;//
	 private TaskManager taskManager = null;
	 private HashMap<Integer, PDFView> viewMap = null;
	 private Activity parent = null;
	 
	 @SuppressLint("UseSparseArrays")
	public PDFPagerAdapter(TaskManager taskManager)
	{
		this.taskManager = taskManager;
		viewMap = new HashMap<Integer, PDFView>(MAXCACHE_COUNT);
	}
	 
	 public void setParent(Activity activity)
	{
		this.parent = activity;
	}
		 
	@Override
	public Object instantiateItem(View container, int position)
	{
		PDFView pdfView = viewMap.get(position);
		if (pdfView == null)
		{
			pdfView = new PDFView(taskManager, position);
			pdfView.setParent(parent);
			taskManager.loadPage(position);
			viewMap.put(position, pdfView);
		}
		else
		{
			((PDFViewPager)container).removeViewAt(position);
			viewMap.remove(position);
			
			taskManager.loadPage(position);
			viewMap.put(position, pdfView);
		}
		((PDFViewPager)container).addView(pdfView, position);
		Handler handler = new Handler();
		handler.post(pdfView);
		return pdfView;
	}

	@Override
	public int getCount()
	{
		while (taskManager.getPDFContext().pageCount <= 0)
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
		return taskManager.getPDFContext().pageCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		// TODO Auto-generated method stub
		return view == object;
	}

	@Override
	public void destroyItem(View container, int position, Object object)
	{
		if (viewMap.containsKey(container))
		{
			PDFView pdfView = viewMap.get(position);
			
			taskManager.closePage();
			((PDFViewPager)container).removeView(pdfView);
			
			viewMap.remove(position);
		}
	}
	
	public Object addNewItem(View container, int position)
	{
		return instantiateItem(container, position);
	}

	public HashMap<Integer, PDFView> getViewMap() 
	{
		return viewMap;
	}
	
	public void changeSelectState(int item)
	{
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return;
		
		pdfView.changeSelectState();
	}
	
	public void setAnnotInfo(AnnotInfo annotInfo, int item)
	{
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return;
		
		pdfView.setAnnotInfo(annotInfo);
	}
	
	public int initPsi(int item)
	{
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return 0;
		return pdfView.initPsi();
	}
	
	public void releasePsi(int item)
	{
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return;
		pdfView.releasePsi();
	}
	
	public void cancelPsi(int item){
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return;
		pdfView.cancelPsi();
	}
	
	public void confirmPsi(int item){
		PDFView pdfView = viewMap.get(item);
		if (pdfView == null) return;
		pdfView.confirmPsi();
	}

}
