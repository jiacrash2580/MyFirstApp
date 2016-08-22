package com.foxit.view;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


import com.foxit.bean.AnnotInfo;
import com.foxit.bean.CurrentState;
import com.foxit.bean.PDFContext;
import com.foxit.bean.PSIOperation;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.action.PDFAction;
import com.foxit.gsdk.pdf.action.PDFURIAction;
import com.foxit.gsdk.pdf.annots.Annot;
import com.foxit.gsdk.pdf.annots.Link;
import com.foxit.gsdk.pdf.annots.Annot.QuadpointsF;
import com.foxit.gsdk.psi.PSI;
import com.foxit.gsdk.utils.SizeF;
import com.foxit.popupmenu.EditWindows;
import com.foxit.popupmenu.MenuItem;
import com.foxit.popupmenu.PopupMenu;
import com.foxit.popupmenu.PopupMenu.OnItemOnClickListener;
import com.foxit.task.TaskManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;

import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

//change page to bitmap and insert to page adapter.
public class PDFView extends View implements Runnable
{
	public static final String TAG = "PDFView";
	
	private PDFContext pdfContext = null;
//	private int pageIndex = 0;
	private Bitmap bitmap = null;
	private ArrayList<Bitmap> highlightBitmap = null;
	private ArrayList<Integer> highlightX = null;
	private ArrayList<Integer> highlightY = null;
	private CurrentState currentState = null;
	private TaskManager taskManager = null;
	private Matrix selectMatrix = null;
	private RectF selectRect = null;
	private Activity parent = null;
	private boolean bUp = false;
	private boolean bLongPressed = false;
	private LongPress lastPress = null;
	private boolean bDrawSelectRect = false;
	
	//use for Annot
	private GestureDetector gestureDetector;
	private Annot annot = null;
	private boolean bFound = false;
	private boolean bSupported = false;
	private AnnotInfo annotInfo;
	private boolean bAddAnnot = false;
	private Vector<RectF> annotRectFs = null;
	private RectF annotDeviceRectF = null;
	
	private PSIOperation psiOperation = null;
	
	static class LongPress
	{
		float 	x;
		float   y;
		long	time;
	}
	
	public PDFView(TaskManager taskManager, int pageIndex)
	{
		super(taskManager.getPDFContext().getContext());
		this.taskManager = taskManager;
//		this.pageIndex = pageIndex;
		this.pdfContext = taskManager.getPDFContext();
		this.currentState = pdfContext.getCurrentState();
		selectRect = new RectF();
		lastPress = new LongPress();
		gestureDetector = new GestureDetector(pdfContext.getContext(), new GestureListener());
		highlightBitmap = new ArrayList<Bitmap>();
		highlightX = new ArrayList<Integer>();
		highlightY = new ArrayList<Integer>();
	}
	
	public void setParent(Activity activity)
	{
		this.parent = activity;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		synchronized (this)
		{
			bitmap = PDFContext.pageBitmap;
			
			Rect viewRect = new Rect();
			getLocalVisibleRect(viewRect);
			
			Paint paint = new Paint();								
			canvas.drawColor(Color.BLACK);
			paint.setColor(Color.WHITE);
			
		
			if (bitmap != null)
			{
				if(currentState.display_width == this.getWidth())
				{
					float scale = this.getWidth() * 1.0f / currentState.display_width;

					if (currentState.movesizeX < 0 - (currentState.screenWidth * (currentState.scale - 1)))
						currentState.movesizeX = 0 - (currentState.screenWidth * (currentState.scale - 1));
					if (currentState.movesizeX > 0)
						currentState.movesizeX = 0;
					if(currentState.display_height * scale * currentState.scale > currentState.screenHeight)
					{
						if (currentState.movesizeY < currentState.screenHeight - currentState.display_height * scale * currentState.scale)
							currentState.movesizeY = currentState.screenHeight - currentState.display_height * scale * currentState.scale;
						if (currentState.movesizeY > 0)
							currentState.movesizeY = 0;
					}
					else
						currentState.movesizeY = (currentState.screenHeight - currentState.display_height * scale * currentState.scale) / 2;

				}
				else if(currentState.display_height == this.getHeight())
				{
					float scale = this.getHeight() * 1.0f / currentState.display_height;
		
					if (currentState.movesizeY < 0 - (currentState.screenHeight * (currentState.scale - 1)))
						currentState.movesizeY = 0 - (currentState.screenHeight * (currentState.scale - 1));
					if (currentState.movesizeY > 0)
						currentState.movesizeY = 0;				
					if(currentState.display_width * scale * currentState.scale > currentState.screenWidth)
					{
						if (currentState.movesizeX < currentState.screenWidth - currentState.display_width * scale * currentState.scale)
							currentState.movesizeX = currentState.screenWidth - currentState.display_width * scale * currentState.scale;
						if (currentState.movesizeX > 0)
							currentState.movesizeX = 0;
					}
					else
						currentState.movesizeX = (currentState.screenWidth - currentState.display_width * scale * currentState.scale) / 2;
				}
				
				Matrix matrix = new Matrix();
				if (currentState.bDrawable)
				{
					matrix.postTranslate(currentState.movesizeX, currentState.movesizeY);
					canvas.clipRect(currentState.movesizeX, currentState.movesizeY, currentState.display_width * currentState.scale + currentState.movesizeX, currentState.display_height * currentState.scale + currentState.movesizeY);
					canvas.drawBitmap(bitmap, matrix, paint);
					currentState.bDrawable = false;
				}
				else 
				{
					matrix.postTranslate(currentState.movesizeX, currentState.movesizeY);
					canvas.clipRect(currentState.movesizeX, currentState.movesizeY, currentState.display_width * currentState.scale + currentState.movesizeX, currentState.display_height * currentState.scale + currentState.movesizeY);
					canvas.drawBitmap(bitmap, matrix, paint);
				}
			}
			
			for(int i=0; i<highlightBitmap.size(); i++)
			{
				Bitmap oldbm = highlightBitmap.get(i);
				if(oldbm != null)
				{
					Bitmap newBitmap = Bitmap.createScaledBitmap(oldbm, (int)(oldbm.getWidth()*currentState.scale), (int)(oldbm.getHeight()*currentState.scale), false);
					canvas.drawBitmap(newBitmap, highlightX.get(i)*currentState.scale + currentState.movesizeX, highlightY.get(i)*currentState.scale + currentState.movesizeY, null);				
				}
			}
			
			if (selectRect.isEmpty() == false && bDrawSelectRect)// not empty
			{	
				Paint paint2 = new Paint();
				paint2.setColor(0x88ff0000);
				if (!bUp)
				{
					paint2.setStyle(Style.STROKE);
					paint2.setStrokeWidth(DRAWING_CACHE_QUALITY_AUTO);
					canvas.drawRect(selectRect, paint2);
				}
				else {
					Vector<RectF> rectFs = taskManager.getSelectVector();
					if (rectFs == null || rectFs.isEmpty()) return;
					for (int i = 0; i < rectFs.size(); i++)
					{
						RectF rect = rectFs.elementAt(i);
						RectF newRect = new RectF();
						selectMatrix.mapRect(newRect, rect);
						canvas.drawRect(newRect, paint2);						
					}
				}

			}
			
			if (annotDeviceRectF != null && !annotDeviceRectF.isEmpty())
			{
				Paint paint2 = new Paint();
				paint2.setColor(0x88ff0000);

				paint2.setStyle(Style.STROKE);
				paint2.setStrokeWidth(DRAWING_CACHE_QUALITY_AUTO);
				canvas.drawRect(annotDeviceRectF, paint2);
			}
		}
	}

	@Override
	public void run()
	{
		synchronized (this)
		{
			currentState.bDrawable = false;
			taskManager.drawSinglePageBitmap();
			while (currentState.bDrawable == false)
			{		
				try
				{
					Thread.sleep(30);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			postInvalidate();
		}	
	}
	
	public void addHighlightBitmap(Bitmap bitmap, int x, int y)
	{
		highlightBitmap.add(bitmap);
		highlightX.add(x);
		highlightY.add(y);
	}
	
	public void clearHighlight() 
	{
		highlightBitmap.clear();
		highlightX.clear();
		highlightY.clear();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		/**
		 * do scaling
		 */
		if (taskManager.getPDFContext().getCurrentState().isScaling)
			return true;
		
		bDrawSelectRect = false;
		//clear highlight bitmap
//		if(pdfContext.textSearch == null)
//		{
//			this.clearHighlight();
//			this.invalidate();
//		}

		/**
		 * do some about annotation
		 */
		
		if(pdfContext.isSupportPSI() == true){	
    		return onPsiTouch(this,event); 
		}
		if(pdfContext.isInsertImg() == true)
		{
			return StartInsertImage(event);
		}
		else if (hasAnnotAtPosition((int)event.getX(), (int)event.getY()) || 
				(annotInfo != null && annotInfo.getOperationType() == AnnotInfo.TYPE_ADD))
		{
			//invalidate
	    	postInvalidate();
	    	if (hasAnnotAtPosition((int)event.getX(), (int)event.getY()))
	    	{
	    		bAddAnnot = false;
	    	}
	    	else if (annotInfo != null && annotInfo.getOperationType() == AnnotInfo.TYPE_ADD)
			{
				//add annot
				bAddAnnot = true;
	    		
				if ((annotInfo.getAnnotType().contentEquals(Annot.TYPE_HIGHLIGHT)
								|| annotInfo.getAnnotType().contentEquals(Annot.TYPE_UNDERLINE)))
				{
					if (annotDeviceRectF == null)
						annotDeviceRectF = new RectF();
					return onHighlightAndUnderlineTouchEvent(event, annotDeviceRectF);
				}	
			}
	    	
	    	else if (!bSupported)//edit & delete
	    	{
	    		//unSupported annot type
	    		return false;
	    	}
	    	
	    	
	    	if (annotInfo == null)
	    		annotInfo = new AnnotInfo(annot, AnnotInfo.TYPE_EDIT);
			return gestureDetector.onTouchEvent(event);
		}
		
		/**
		 * move, select text
		 */
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			lastPress.x = event.getX(); 
			lastPress.y = event.getY();
			Date date = Calendar.getInstance().getTime();
			//extract text
			lastPress.time = date.getTime();
			selectRect.left = (int)event.getX();  
			selectRect.top = (int)event.getY(); 
			//move page
			currentState.downX = event.getX();
			currentState.downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!bLongPressed)
			{
				Date date2 = Calendar.getInstance().getTime();
				long time = date2.getTime();
				bLongPressed = isLongPress(lastPress.x, lastPress.y, 
						event.getX(), event.getY(), lastPress.time, time);
			}
			if (bLongPressed)
			{
				selectRect.right = (int)event.getX();  
				selectRect.bottom = (int)event.getY();
				bUp = false;
				bDrawSelectRect = true;
				postInvalidate();
			}
			else {
				if(currentState.scale > 1.0f)
				{
					currentState.movesizeX += (event.getX() - currentState.downX)/10;
					currentState.movesizeY += (event.getY() - currentState.downY)/10;
				}
			}			
			break;
		case MotionEvent.ACTION_UP:
			if (bLongPressed)
			{				
				selectRect.right = (int)event.getX();  
				selectRect.bottom = (int)event.getY();
				RectF newRectF = ConvertDeviceRect(selectRect, pdfContext.page);
				taskManager.extractText(parent, newRectF);

				bUp = true;
				bDrawSelectRect = true;
				bLongPressed = false;
			}
			break;
		default:
			break;
		}
		postInvalidate();
		return true;
	}
	
	public boolean StartInsertImage(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			lastPress.x = event.getX(); 
			lastPress.y = event.getY();
			Date date = Calendar.getInstance().getTime();
			//extract text
			lastPress.time = date.getTime();
			selectRect.left = (int)event.getX();  
			selectRect.top = (int)event.getY(); 
			//move page
			currentState.downX = event.getX();
			currentState.downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!bLongPressed)
			{
				Date date2 = Calendar.getInstance().getTime();
				long time = date2.getTime();
				bLongPressed = isLongPress(lastPress.x, lastPress.y, 
						event.getX(), event.getY(), lastPress.time, time);
			}
			if (bLongPressed)
			{
				selectRect.right = (int)event.getX();  
				selectRect.bottom = (int)event.getY();
				bUp = false;
				bDrawSelectRect = true;
				postInvalidate();
			}
			else {
				if(currentState.scale > 1.0f)
				{
					currentState.movesizeX += (event.getX() - currentState.downX)/10;
					currentState.movesizeY += (event.getY() - currentState.downY)/10;
				}
			}			
			break;
		case MotionEvent.ACTION_UP:
			if (bLongPressed)
			{				
				selectRect.right = (int)event.getX();  
				selectRect.bottom = (int)event.getY();
				RectF newRectF = ConvertDeviceRect(selectRect, pdfContext.page);
				taskManager.InsertImgTask(parent, newRectF);

				bUp = true;
				bDrawSelectRect = true;
				bLongPressed = false;
				pdfContext.setInsertImgFlag(false);
				this.run();
			}
			break;
		default:
			break;
		}
		
		postInvalidate();
		return true;
	}
	
	public RectF ConvertDeviceRect(RectF srcRect, PDFPage page)
	{		
		RectF dstRect = new RectF();
		SizeF pagesize;
		try 
		{
			if(page == null)
				return dstRect;
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
				int vwidth = this.getWidth();
				s = (vwidth*1.0f)/(width*vscale);
			}
			else if(currentState.display_height == currentState.screenHeight)
			{
				int vheight = this.getHeight();
				s = (vheight*1.0f)/(height*vscale);
			}
	    	width *= s;
	    	height *= s;
			Matrix matrix = page.getDisplayMatrix(0, 0, width, height, currentState.rotation);
			matrix.postScale(currentState.scale, currentState.scale);
			matrix.postTranslate(currentState.movesizeX, currentState.movesizeY);
			selectMatrix = matrix;
			Matrix inverse = new Matrix();
			matrix.invert(inverse);
			inverse.mapRect(dstRect, srcRect);
		} 
		catch (PDFException e)
		{
			e.printStackTrace();
		}
		return dstRect;
	}

	private boolean isLongPress(float lastX, float lastY, float thisX, float thisY,
			long lastTime, long thisTime)
	{
		float offsetX = Math.abs(thisX - lastX);
		float offsetY = Math.abs(thisY - lastY);
		long interval = Math.abs(thisTime - lastTime);
		
		if (offsetX <= 5 && offsetY <= 5 && interval >= 200)
			return true;
		else 
			return false;
	}
	
	public void changeSelectState()
	{
		bDrawSelectRect = false;
	}
	
	private Matrix getMatrix(PDFPage page)
	{
		SizeF pagesize;
		Matrix matrix = null;
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
				int vwidth = this.getWidth();
				s = (vwidth*1.0f)/(width*vscale);
			}
			else if(currentState.display_height == currentState.screenHeight)
			{
				int vheight = this.getHeight();
				s = (vheight*1.0f)/(height*vscale);
			}
	    	width *= s;
	    	height *= s;
			matrix = page.getDisplayMatrix(0, 0, width, height, currentState.rotation);			
			matrix.postScale(currentState.scale, currentState.scale);
			matrix.postTranslate(currentState.movesizeX, currentState.movesizeY);
		}
		catch (PDFException e)
		{
			e.printStackTrace();
		}
		
		return matrix;
	}
	
	private boolean hasAnnotAtPosition(int x, int y)
	{
		// initialize
		bFound = false;
		bSupported = false;
		Point point = new Point(x, y);
		try
		{
			if (pdfContext.page == null)
				return false;
			Matrix matrix = getMatrix(pdfContext.page);
			annot = pdfContext.page.getAnnotAtDevicePos(null, matrix, point, 1.0f);
			if (annot != null) 
			{
				bFound = true;
				String type = annot.getType();
				if (type.contentEquals(Annot.TYPE_HIGHLIGHT)
						|| type.contentEquals(Annot.TYPE_UNDERLINE)
						|| type.contentEquals(Annot.TYPE_LINK)
						|| type.contentEquals(Annot.TYPE_FREETEXT)
						|| type.contentEquals(Annot.TYPE_TEXT))
				{
					bSupported = true;//support
				}				
			}
		}
		catch (PDFException e)
		{
			e.printStackTrace();
		}
		
		return bFound;
	}
	
	private void addAnnot(PointF pointF)
	{
		Matrix matrix = getMatrix(pdfContext.page);
		Matrix inverse = new Matrix();
		matrix.invert(inverse);
		float[] srcPoint = new float[]{pointF.x, pointF.y};
		float[] pagePoint = new float[2];
		inverse.mapPoints(pagePoint, srcPoint);
		//rectf in PDF page coordinate system
		RectF rectf = new RectF();
		if (annotInfo.getAnnotType().contentEquals(Annot.TYPE_LINK))
		{
			rectf.left = pagePoint[0] - 30;
			rectf.right = pagePoint[0] + 30;
			rectf.top = pagePoint[1] + 15;
			rectf.bottom = pagePoint[1] - 15;
		}
		else {
			rectf.left = pagePoint[0] - 15;
			rectf.right = pagePoint[0] + 15;
			rectf.top = pagePoint[1] + 15;
			rectf.bottom = pagePoint[1] - 15;
		}

		Annot annot = annotInfo.addAnnot(pdfContext.page, rectf, annotInfo.getAnnotType(), null, -1);
		annotInfo.setAnnotProperty(annot, annotInfo.getAnnotType());
		
		if (annot == null) return;
		editAnnot(annot);
	}
	
	private void editAnnot(Annot annot)
	{	
		final EditWindows editWindows = new EditWindows(pdfContext.getContext(), pdfContext.getCurrentState().screenWidth / 2, LayoutParams.WRAP_CONTENT);
		String annotType = "";
		try
		{
			annotType = annot.getType();
		}
		catch (PDFException e)
		{
			e.printStackTrace();
		}
		
		if (annotType.contentEquals(Annot.TYPE_LINK))
		{
			editWindows.setHint("Please enter the web address!");
		}
		else 
		{
			String contents = annotInfo.getAnnotContents(annot);
			editWindows.setContents(contents);
		}
		
		editWindows.show(PDFView.this, pdfContext.getCurrentState().screenWidth / 4, 100);
		
		//update the annot content after edit annot
		final EditAnnotHandler handler = new EditAnnotHandler(annotInfo, annot, pdfContext.page, PDFView.this);
		editWindows.setHandler((EditAnnotHandler)handler);

	}
	
	private String getAnnotType()
	{
		String type = null;
		
		try
		{
			type = annot.getType();
		}
		catch (PDFException e)
		{
			e.printStackTrace();
		}
		
		return type;
	}
	
	class GestureListener extends SimpleOnGestureListener
	{
		@Override
		public boolean onDown(MotionEvent e)
		{
			if (bAddAnnot && annotInfo != null && annotInfo.getOperationType() == AnnotInfo.TYPE_ADD)
			{
				//add annotation except highlight & underline
				if (annotInfo.getAnnotType().contentEquals(Annot.TYPE_HIGHLIGHT)
						|| annotInfo.getAnnotType().contentEquals(Annot.TYPE_UNDERLINE))
				{
					return true;
				}
				
				//add the other annotations
				addAnnot(new PointF(e.getX(), e.getY()));
			}
			
			return true;
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			if (bSupported && annotInfo.getOperationType() != AnnotInfo.TYPE_ADD)//edit or delete
			{
				PopupMenu menu = new PopupMenu(pdfContext.getContext(), 150, LayoutParams.WRAP_CONTENT);
				if (getAnnotType().contentEquals(Annot.TYPE_HIGHLIGHT)
						|| getAnnotType().contentEquals(Annot.TYPE_UNDERLINE))
				{
					menu.addMenuItem(new MenuItem("Delete"));
				} 
				else if (getAnnotType().contentEquals(Annot.TYPE_FREETEXT)
						|| getAnnotType().contentEquals(Annot.TYPE_TEXT)) 
				{
					menu.addMenuItem(new MenuItem("Edit"));
					menu.addMenuItem(new MenuItem("Delete"));
				}
				else if (getAnnotType().contentEquals(Annot.TYPE_LINK)) 
				{
					//go action
					Link link = (Link) annot;
					try
					{
						PDFAction action = link.getAction(Link.TRIGGER_ANNOT_MU, 0);
						if (action != null && action.getType() == PDFAction.TYPE_URI)
						{
							PDFURIAction uriAction = (PDFURIAction)action;
							final String uri = uriAction.getURL();
							parent.runOnUiThread(new Runnable()
							{
								
								@Override
								public void run()
								{
									Toast.makeText(parent.getBaseContext(), "It could connect to " + uri + ". This is only for demo.", 
											Toast.LENGTH_LONG).show();
								}
							});
						}
					}
					catch (PDFException e1)
					{
						e1.printStackTrace();
					}
				}
				
				if (!getAnnotType().contentEquals(Annot.TYPE_LINK))
				{
					menu.inflateMenuItems();
					menu.show(PDFView.this, (int)e.getX(), (int)e.getY());
					menu.setItemOnClickListener(manageAnnotListener);
				}
			}			

			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e)
		{
			if (bSupported && getAnnotType().contentEquals(Annot.TYPE_LINK))
			{
				PopupMenu menu = new PopupMenu(pdfContext.getContext(), 150, LayoutParams.WRAP_CONTENT);
				menu.addMenuItem(new MenuItem("Delete"));
				menu.inflateMenuItems();
				menu.show(PDFView.this, (int)e.getX(), (int)e.getY());
				menu.setItemOnClickListener(manageAnnotListener);
			} 
		}
	}
	
	OnItemOnClickListener manageAnnotListener = new OnItemOnClickListener()
	{
		
		@Override
		public void onItemClick(MenuItem item, int position)
		{
			if (item.getItem().equals("Edit"))
			{
				annotInfo = new AnnotInfo(annot, AnnotInfo.TYPE_EDIT);
				editAnnot(annot);
			}
			else if (item.getItem().equals("Delete")) 
			{
				annotInfo = new AnnotInfo(annot, AnnotInfo.TYPE_DELETE);
				annotInfo.deleteAnnot(pdfContext.page, annot);
				Handler handler = new Handler();
				handler.post(PDFView.this);
			}
		}
	};
	
	public void setAnnotInfo(AnnotInfo annotInfo)
	{
		this.annotInfo = annotInfo;
	}
	
	public void setImageBitmap(Bitmap b) {
		bitmap = b;
	}
	//init some properties of PSI
	public int initPsi()
	{
		if(this.psiOperation == null)
			this.psiOperation = new PSIOperation();
		return this.psiOperation.init(currentState.display_width * currentState.scale , currentState.display_height * currentState.scale);
		
	}
	public void releasePsi()
	{
		psiOperation.release();
	}
	//cancel the PSI operation
	public void cancelPsi(){
		psiOperation.release();
		Handler handler = new Handler();
		handler.post(this);
	}
	//confirm to convert the pressure sensitive ink object to a PDF annotation
	public void confirmPsi(){
		RectF devRect = psiOperation.getRect();
		RectF annotRect = new RectF();
		Matrix matrix = null;
		try {
			matrix = pdfContext.page.getDisplayMatrix(0, 0, currentState.display_width, currentState.display_height, currentState.rotation);
		} catch (PDFException e) {
			e.printStackTrace();
		}
		matrix.postScale(currentState.scale, currentState.scale);
		Matrix inverse = new Matrix();
		matrix.invert(inverse);
		inverse.mapRect(annotRect, devRect);
		float temp = annotRect.bottom;
		annotRect.bottom = annotRect.top;
		annotRect.top = temp;
		RectF psiRect = new RectF(0, 0, currentState.display_width * currentState.scale, currentState.display_height * currentState.scale);
		psiOperation.convert(psiRect, pdfContext.page, annotRect);
		psiOperation.release();
	}
	
	//add Highlight or underline annotation	
	private boolean onHighlightAndUnderlineTouchEvent(MotionEvent event, RectF rectF)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			rectF.left = event.getX();  
			rectF.top = event.getY(); 
			break;
		case MotionEvent.ACTION_MOVE:
			rectF.right = event.getX();  
			rectF.bottom = event.getY();
			postInvalidate();
			break;
		case MotionEvent.ACTION_UP:			
			rectF.right = event.getX();  
			rectF.bottom = event.getY();
			RectF newRectF = ConvertDeviceRect(rectF, pdfContext.page);
			if (annotRectFs != null)
				annotRectFs.clear();
			int[] piecesRotation = taskManager.getPaintRects(newRectF);
			annotRectFs = taskManager.getSelectVector();

			//clear annot device rect
			annotDeviceRectF.setEmpty();
			if (annotRectFs == null || annotRectFs.isEmpty()) 
			{
				annotInfo.setOperationType(AnnotInfo.TYPE_UNSUPPOTED);
				postInvalidate();
				return true;
			}

			QuadpointsF[] quadpointsF = new QuadpointsF[annotRectFs.size()];
			for (int i = 0; i < annotRectFs.size(); i++)
			{
				RectF rect = annotRectFs.elementAt(i);
				//pdf coordinates 
				quadpointsF[i] = new QuadpointsF();
				quadpointsF[i].x1 = rect.left;
				quadpointsF[i].y1 = rect.top;
				quadpointsF[i].x2 = rect.right;
				quadpointsF[i].y2 = rect.top;
				quadpointsF[i].x3 = rect.left;
				quadpointsF[i].y3 = rect.bottom;
				quadpointsF[i].x4 = rect.right;
				quadpointsF[i].y4 = rect.bottom;
				
				if (annotInfo.getAnnotType() == Annot.TYPE_UNDERLINE)
				{
					if (piecesRotation[i] <= 100 && piecesRotation[i] >= 80)
					{
						quadpointsF[i].x1 = rect.left;
						quadpointsF[i].y1 = rect.bottom;
						quadpointsF[i].x2 = rect.left;
						quadpointsF[i].y2 = rect.top;
						quadpointsF[i].x3 = rect.right;
						quadpointsF[i].y3 = rect.bottom;
						quadpointsF[i].x4 = rect.right;
						quadpointsF[i].y4 = rect.top;
					}
					else if (piecesRotation[i] <= 280 && piecesRotation[i] >= 260) 
					{
						quadpointsF[i].x1 = rect.right;
						quadpointsF[i].y1 = rect.bottom;
						quadpointsF[i].x2 = rect.right;
						quadpointsF[i].y2 = rect.top;
						quadpointsF[i].x3 = rect.left;
						quadpointsF[i].y3 = rect.bottom;
						quadpointsF[i].x4 = rect.left;
						quadpointsF[i].y4 = rect.top;
					}
				}
		
			}
			annotRectFs.clear();
			Annot annot = annotInfo.addAnnot(pdfContext.page, new RectF(0, 0, 0, 0), annotInfo.getAnnotType(), null, -1);
			annotInfo.setQuadPoints(quadpointsF);
			annotInfo.setAnnotProperty(annot, annotInfo.getAnnotType());
			//only add once
			annotInfo.setOperationType(AnnotInfo.TYPE_UNSUPPOTED);
			pdfContext.getCurrentState().bRenderAnnot = true;
			Handler handler = new Handler();
			handler.post(this);
			break;
		default:
			break;
		}
		
		postInvalidate();
		return true;
	}
	//draw PSI
	public boolean onPsiTouch(View v, MotionEvent event) {
		int actionType = event.getAction();
		
		float fx = (float) (event.getX() - currentState.movesizeX);
		float fy = (float) (event.getY() - currentState.movesizeY);

		switch (actionType){
			case MotionEvent.ACTION_DOWN:	
				psiOperation.addPointFToPsi(fx, fy, PSI.PT_MOVETO);
				break;
			case MotionEvent.ACTION_UP:
				psiOperation.addPointFToPsi(fx, fy, PSI.PT_ENDPATH);	
				break;
			case MotionEvent.ACTION_MOVE:
				psiOperation.addPointFToPsi(fx, fy, PSI.PT_LINETO);
				break;
			default:
				break;		
		}
		psiOperation.renderPsi(bitmap, new Rect(0, 0, (int)(currentState.display_width * currentState.scale), (int)(currentState.display_height * currentState.scale)));
		
		invalidate();
		return true;
	}
}


//Receive the message to update the edited annotation.
class EditAnnotHandler extends Handler
{
	private AnnotInfo annotInfo = null;
	private Runnable runnable = null;
	private Annot annot = null;
	private PDFPage page = null;
	
	public EditAnnotHandler(AnnotInfo annotInfo, Annot annot, PDFPage page, Runnable runnable)
	{
		this.annotInfo = annotInfo;
		this.annot = annot;
		this.runnable = runnable;
		this.page = page;
	}
	
	@Override
	public void handleMessage(Message msg)
	{
		super.handleMessage(msg);
		if (msg.what == EditWindows.EDITSTATE_EDIT)
		{
			Bundle data = msg.getData();			
			annotInfo.editAnnot(annot, data.getString("content"));
			if (annotInfo.getOperationType() == AnnotInfo.TYPE_ADD)
			{
				try
				{
					if ((annot.getType().contentEquals(Annot.TYPE_FREETEXT)
							|| annot.getType().contentEquals(Annot.TYPE_LINK))
							&& data.getString("content") == null)
					{
						annotInfo.deleteAnnot(page, annot);
					}
					//add annot link && url is valid.
					if (annot.getType().contentEquals(Annot.TYPE_LINK)
							&& data.getString("content") != null)
					{
						annotInfo.setAction((Link)annot, data.getString("content"));
					}
				}
				catch (PDFException e)
				{
					e.printStackTrace();
				}
				//only add once
				annotInfo.setOperationType(AnnotInfo.TYPE_UNSUPPOTED);
			}
			Handler handler = new Handler();
			handler.post(runnable);
		}
		else if (annotInfo.getOperationType() == AnnotInfo.TYPE_ADD)
		{
			//only add once
			annotInfo.setOperationType(AnnotInfo.TYPE_UNSUPPOTED);
			try
			{
				if (annot.getType().contentEquals(Annot.TYPE_FREETEXT)
						|| annot.getType().contentEquals(Annot.TYPE_LINK))
				{
					annotInfo.deleteAnnot(page, annot);
				}
			}
			catch (PDFException e)
			{
				e.printStackTrace();
			}
			Handler handler = new Handler();
			handler.post(runnable);
		}
	}
}