package com.foxit.view;

import com.foxit.bean.PDFContext;
import com.foxit.task.TaskManager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class PDFViewPager extends ViewPager implements OnScaleGestureListener
{
	public static final String TAG = "PDFViewPager";
	private static final float MAX_SCALE = 2.0f;
	private static final float MIN_SCALE = 1.0f;
	
	private TaskManager taskManager = null;
	/**
	 * scale gesture detector
	 */
	private ScaleGestureDetector scaleGestureDetector = null;
	
	public PDFViewPager(Context context)
	{
		super(context);
	}

	public PDFViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PDFViewPager(TaskManager taskManager)
	{
		super(taskManager.getPDFContext().getContext());
		this.taskManager = taskManager;
		this.scaleGestureDetector = new ScaleGestureDetector(taskManager.getPDFContext().getContext(), this);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		PDFContext pdfContext = null;
		if (taskManager == null) return;
		pdfContext = taskManager.getPDFContext();
//		float scale1 = 0f;
//		float scale2 = ((float)pdfContext.getCurrentState().screenWidth / (float)pdfContext.getCurrentState().screenHeight);
		if (pdfContext.getCurrentState().screenWidth > pdfContext.getCurrentState().screenHeight)
		{			
			pdfContext.getCurrentState().bVertical = false;
//			scale1 = (pdfContext.getCurrentState().pageSize.getHeight() / pdfContext.getCurrentState().pageSize.getWidth());
		}
		else {
			pdfContext.getCurrentState().bVertical = true;
//			scale1 = (pdfContext.getCurrentState().pageSize.getWidth() / pdfContext.getCurrentState().pageSize.getHeight());
		}
//		if(scale1 > scale2)
//		{
//			pdfContext.getCurrentState().scale = w / (float)pdfContext.getCurrentState().screenWidth;
//		}
//		else
//		{
//			pdfContext.getCurrentState().scale = w / ((pdfContext.getCurrentState().screenHeight / pdfContext.getCurrentState().pageSize.getHeight()) * pdfContext.getCurrentState().pageSize.getWidth());
//		}
//		pdfContext.getCurrentState().isScaling = true;
	}
	
	//Disable touch sliding
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		scaleGestureDetector.onTouchEvent(ev);
		return false;
	}
	
	/**
	 * Scale Gesture
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{	
		// When doing PSI, the scale feature will be disabled.
		if (taskManager.getPDFContext().isSupportPSI() == true) return false;
		float scaleFactor = detector.getScaleFactor();
		float scale = 0;
		if (taskManager == null)
			return false;
		
		scale = taskManager.getPDFContext().getCurrentState().scale;

		float curScale = scale * scaleFactor;
		if (curScale > MAX_SCALE)
		{
			taskManager.getPDFContext().getCurrentState().scale = MAX_SCALE;
		}
		else if (curScale < MIN_SCALE)
		{			
			taskManager.getPDFContext().getCurrentState().scale = MIN_SCALE;
		}
		else
		{
			taskManager.getPDFContext().getCurrentState().scale = curScale;
		}
		//need optimization by according the layout mode?

		//invalidate PDFView, re-draw bitmap
		PDFPagerAdapter pagerAdapter = (PDFPagerAdapter) this.getAdapter();
		Handler handler = new Handler();
		handler.post(pagerAdapter.getViewMap().get(getCurrentItem()));

		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector)
	{
		if (taskManager == null)
			return false;
		
		taskManager.getPDFContext().getCurrentState().isScaling = true;
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector)
	{
		if (taskManager == null)
			return;
		
		taskManager.getPDFContext().getCurrentState().isScaling = false;
	}	
}
