package com.foxit.task;

import android.util.Log;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.ui.FoxitViewActivity;

public class CloseDocumentTask extends AbstractTask
{
	public static final String TAG = "CloseDocumentTask";

	private PDFContext pdfContext = null;
	
	public CloseDocumentTask(PDFContext pdfContext)
	{
		this.pdfContext = pdfContext;
		this.taskType = TYPE_CLOSEDOCUMENT;
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext != null && pdfContext.document != null)
		{
			try
			{
				pdfContext.document.close();
			}
			catch (PDFException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, FoxitViewActivity.generateMsg(e.getLastError(), "Failed to close document!!!", true));
				return false;
			}
			pdfContext.document = null;
			return true;
		}
		return false;
	}

}
