package com.foxit.task;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.Toast;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.gsdk.pdf.pageobjects.ImageObject;
import com.foxit.gsdk.pdf.pageobjects.PageObject;
import com.foxit.gsdk.pdf.pageobjects.PageObjects;

public class InsertImgTask extends AbstractTask
{
	private static final String TAG = "InsertImgTask";
	private PDFContext pdfContext = null;
	private Activity activity = null;//use to show the message dialog
	private RectF rect = null;//the rectangle used to insert image.
	public InsertImgTask(Activity activity, PDFContext pdfContext, RectF rect)
	{
		this.pdfContext = pdfContext;
		this.rect = rect;
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext.page == null || pdfContext.page.getHandle() == 0)
			return false;
		try
		{
			ImageObject imageObject = ImageObject.create(pdfContext.page);
			int width = (int) Math.abs(rect.right - rect.left);
			int height = (int)Math.abs(rect.top - rect.bottom);
			Matrix matrix =new Matrix();
			matrix.setScale(width, height);
			matrix.postTranslate(rect.left, rect.top);
			imageObject.setImage(pdfContext.image);
			imageObject.setMatrix(matrix);
			PageObjects pageobjs =  pdfContext.page.getPageObjects();
			pageobjs.insertObject(PageObject.TYPE_IMAGE, 0, imageObject);
			pageobjs.generateContents();

			bFinished = true;
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
