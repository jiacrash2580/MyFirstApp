package foxit.task;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;

public class ClosePageTask extends AbstractTask
{
	public static final String TAG = "ClosePageTask";
	
	private PDFContext pdfContext = null;
	public ClosePageTask(PDFContext pdfContext)
	{
		this.taskType = TYPE_CLOSEPAGE;
		this.pdfContext = pdfContext;
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext != null && pdfContext.document != null && pdfContext.page != null)
		{
			try
			{
				if(pdfContext.getCurrentState().bAnnotLicense)
					pdfContext.page.unloadAnnots();
				pdfContext.document.closePage(pdfContext.page);
			}
			catch (PDFException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			pdfContext.page = null;
			return true;
		}
		return false;
	}

}
