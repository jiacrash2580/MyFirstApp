package com.foxit.task;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.foxit.bean.CurrentState;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.utils.SizeF;
import com.foxit.view.PDFPagerAdapter;
import com.foxit.view.PDFView;

public abstract class AbstractTask
{
	private static final String TAG = "AbstractTask";
	
	public static final int TYPE_LOADDOCUMENT 				= 1;
	public static final int TYPE_CLOSEDOCUMENT 				= 2;
	public static final int TYPE_LOADPAGE 					= 3;
	public static final int TYPE_CLOSEPAGE 					= 4;
	public static final int TYPE_DRAWSINGLEBITMAPPAGE		= 5;
	public static final int TYPE_SEARCH						= 6;
	public static final int TYPE_ZOOM						= 7;
	public static final int TYPE_EXTRACTTEXT				= 8;
	public static final int TYPE_SAVEDOCUMENT				= 9;
	
	protected int taskType = 0;
	
	protected static Activity parentActivity = null;
	protected boolean bFinished = false;
	//
	public abstract boolean execute();
	
	//true : execute finish
	public boolean getExecuteState()
	{
		return bFinished;
	}
	
	public boolean isEqual(AbstractTask task)
	{		
		if (this.taskType == task.getType())
		{
			return true;
		}
		
		return false;
	}
	
	public int getType() {return taskType;}//task type
	
	public RectF ConvertDeviceRect(RectF srcRect, PDFPage page, PDFView view, CurrentState currentState)
	{		
		RectF dstRect = new RectF();
		SizeF pagesize;
		try 
		{
			pagesize = page.getSize();
			int width = (int)pagesize.getWidth();
			int height = (int) pagesize.getHeight();
			if (currentState.rotation == PDFPage.ROTATION_90 || currentState.rotation == PDFPage.ROTATION_270)
			{
				width = (int)pagesize.getHeight();
				height = (int) pagesize.getWidth();
			}
			
			float vscale = 1.0f;
			float s = 0f;
			if(currentState.display_width == currentState.screenWidth)
			{
				int vwidth = view.getWidth();
				s = (vwidth*1.0f)/(width*vscale);
			}
			else if(currentState.display_height == currentState.screenHeight)
			{
				int vheight = view.getHeight();
				s = (vheight*1.0f)/(height*vscale);
			}
			
	    	width *= s;
	    	height *= s;
			Matrix matrix = page.getDisplayMatrix(0, 0, width, height, currentState.rotation);		
			matrix.mapRect(dstRect, srcRect);
		} 
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dstRect;
	}
	
	public PDFView getCurrentView(PDFPagerAdapter adapter, int iPageIndex)
	{
		PDFView pdfView = null;
		for(int i = 0; i < adapter.getViewMap().size(); i++)
		{
			pdfView = adapter.getViewMap().get(i);
			if (pdfView == null)
				continue;
			else if(i != iPageIndex)
			{
				pdfView.clearHighlight();
				continue;
			}
			else
			{
				return pdfView;
			}
		}
		return null;
	}
	
	public void setParentActivity(Activity parentActivity)
	{
		AbstractTask.parentActivity = parentActivity;
	}
}
