package com.foxit.bean;

import com.foxit.gsdk.IInvalidate;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.Renderer;
import com.foxit.gsdk.psi.PSI;
import com.foxit.view.PDFView;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;


public class PSIOperation{
	
    private PSI mPsi = null;
    
    public PSI getPsi(){
    	return mPsi;
    }
  
    public int init(float width, float height) {
    	try {
    		mPsi = PSI.create(true);
    		mPsi.initCanvas(width, height);//initialize canvas for the PSI
    		mPsi.setInkColor(0x00ff0000);//customer can use this interface to set the color of PSI
			float opacity = 1.0f;
			mPsi.setOpacity(opacity);//set ink opacity of the PSI object
			int diameter = 20;
			mPsi.setInkDiameter(diameter);//set ink diameter of the PSI object
		} catch (PDFException e) {
			mPsi = null;
			e.printStackTrace();
			return e.getLastError();
		}
		return PDFException.ERRCODE_SUCCESS;
	}
    
    public int addPointFToPsi(float fx, float fy, int flag){
    	if (mPsi == null) {
 			return PDFException.ERRCODE_ERROR;
 		}
    	// In this demo, the pressure is set to 1.0f by default.
    	int result = addPointFToPsi(fx, fy, 1.0f, flag); 
    	if (result != 0) {
			System.out.println("PSI ERROR!");
			return PDFException.ERRCODE_ERROR;
		}
    	return PDFException.ERRCODE_SUCCESS;
    }
    
    //add a point to PSI object with pressure.
    public int addPointFToPsi(float x, float y, float pressure, int flag) {
 		if (mPsi == null) {
 			return PDFException.ERRCODE_ERROR;
 		}
 		PointF pf = new PointF();
 		pf.set(x, y);
 		try {
 			mPsi.addPoint(pf, pressure, flag);
 		} catch (PDFException e) {
 			e.printStackTrace();
 			return e.getLastError();
 		}
 		return PDFException.ERRCODE_SUCCESS;
 	}
    
    class MyIInvalidate extends IInvalidate {
		@SuppressWarnings("unused")
		private Bitmap mBmp;

		public MyIInvalidate(PDFView view, Bitmap bmp) {
			clientData = view;
			mBmp = bmp;
		}

		@Override
		public void invalidateRect(Object clientData, int objectType,
				RectF rectF) {
			int err = renderPsi(mBmp, new Rect((int) rectF.left,
					(int) rectF.top, (int) rectF.right, (int) rectF.bottom));
			if (err != PDFException.ERRCODE_SUCCESS) {
				return;
			}
			PDFView pdfView = (PDFView) clientData;
			pdfView.setImageBitmap(mBmp);
			pdfView.invalidate(new Rect((int) rectF.left, (int) rectF.top,
					(int) rectF.right, (int) rectF.bottom));
			
			Handler handler = new Handler();
			handler.post(pdfView);
		}

	}

	public int invalidateRect(PDFView view, Bitmap bmp) {
		if (mPsi == null) {
			return PDFException.ERRCODE_ERROR;
		}
		IInvalidate iv = new MyIInvalidate(view, bmp);
		try {
			mPsi.invalidateRect(iv);
			return PDFException.ERRCODE_SUCCESS;
		} catch (PDFException e) {
			e.printStackTrace();
			return e.getLastError();
		}
	}
	
    //render the PSI object
    public int renderPsi(Bitmap bmp, Rect rect) {
		if (mPsi == null) {
			return PDFException.ERRCODE_ERROR;
		}
		try {
			Renderer render = Renderer.create(bmp);
			mPsi.render(render, new Point(rect.left, rect.top), rect.width(),
					rect.height(), new PointF(rect.left, rect.top));
			render.release();
			return PDFException.ERRCODE_SUCCESS;
		} catch (PDFException e) {
			e.printStackTrace();
			return e.getLastError();
		}
	}
   
    //get contents rectangle of the PSI object.
    public RectF getRect() {
    	RectF rect = null;
    	if (mPsi != null)
    	{
			try {
				rect = mPsi.getContentsRect();
			} catch (PDFException e) {
				e.printStackTrace();
			}
    	}
    	return rect;
    }
    
    //convert the PSI object to a PDF annotation.
    public int convert(RectF psiRect, PDFPage page, RectF annotRect) {
    	if (mPsi == null)
    		return PDFException.ERRCODE_ERROR;
	   	try {
			mPsi.convertToPDFAnnot(psiRect, page, annotRect);
		} catch (PDFException e) {
			e.printStackTrace();
		}
    	
    	return PDFException.ERRCODE_SUCCESS;
    }
    
    //destroy the PSI object.
    public void release() {
    	if (mPsi != null)
    	{
			try {
				mPsi.release();
				mPsi = null;
			} catch (PDFException e) {
				e.printStackTrace();
			}
    	}
    }
}
