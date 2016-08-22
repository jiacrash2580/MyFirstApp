package foxit.task;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.gsdk.utils.FileHandler;

public class SaveDocumentTask extends AbstractTask
{
	public static final String TAG = "SaveDocumentTask";
	
	private PDFContext pdfContext = null;
	private String fileName = null;
	private int saveFlag = 0;
	public SaveDocumentTask(PDFContext pdfContext, String fileName, int saveFlag)
	{
		this.pdfContext = pdfContext;
		this.taskType = TYPE_SAVEDOCUMENT;
		this.fileName = fileName;
		this.saveFlag = saveFlag;
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext != null && pdfContext.document != null)
		{
			FileHandler file;
			try
			{
				file = FileHandler.create(fileName, FileHandler.FILEMODE_TRUNCATE);
				Progress progress = pdfContext.document.startSaveToFile(file, saveFlag);
				if (progress != null)
				{
					int ret = Progress.TOBECONTINUED;
					while (ret == Progress.TOBECONTINUED)
					{
						ret = progress.continueProgress(0);
					}
				}
				progress.release();
				file.release();
			}
			catch (PDFException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bFinished = true;
			return true;
		}
		return false;
	}

}
