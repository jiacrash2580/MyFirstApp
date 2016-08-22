package foxit.task;

import android.util.Log;

import com.foxit.bean.CurrentState;
import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.ui.MainActivity;

public class LoadPageTask extends AbstractTask
{
	public static final String TAG = "LoadPageTask";
	private PDFContext pdfContext = null;
	private int pageIndex = 0;
	
	public LoadPageTask(PDFContext pdfContext, int pageIndex)
	{
		this.taskType = TYPE_LOADPAGE;
		this.pdfContext = pdfContext;
		this.pageIndex = pageIndex;
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext != null && pdfContext.document != null)
		{
			//pdfContext.pageIndex = pageIndex;
			pdfContext.getCurrentState().curPageIndex = pdfContext.pageIndex;
			
			try
			{
				pdfContext.page = pdfContext.document.getPage(pdfContext.pageIndex);
				if (pdfContext.page == null) return false;
				CurrentState currentState = pdfContext.getCurrentState();
				try {
					currentState.bAnnotLicense = true;
					pdfContext.page.loadAnnots();
				} catch (PDFException e) {
					// TODO: handle exception
					if(PDFException.ERRCODE_INVALIDLICENSE == e.getLastError())
					{
						currentState.bAnnotLicense = false;
					}
				}
				//get page size
				pdfContext.getCurrentState().pageSize = pdfContext.page.getSize();
				
				if (pdfContext.page.isParsed()) return true;
				
				Progress progress = null;
				progress = pdfContext.page.startParse(PDFPage.PARSEFLAG_NORMAL);
				if (progress != null)
					progress.continueProgress(0);
				
				progress.release();			
				//keep page in hashmap?
				
				return true;
			}
			catch (PDFException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, MainActivity.generateMsg(e.getLastError(), "Load page task failed!!!", true));
				return false;
			}
		}
		return false;
	}

}
