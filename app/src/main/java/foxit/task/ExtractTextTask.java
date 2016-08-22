package foxit.task;

import java.util.Vector;

import android.app.Activity;
import android.graphics.RectF;
import android.widget.Toast;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFTextPage;
import com.foxit.gsdk.pdf.PDFTextSelection;

public class ExtractTextTask extends AbstractTask
{
	private static final String TAG = "ExtractTextTask";
	private PDFContext pdfContext = null;
	private Activity activity = null;//use to show the message dialog
	private RectF rect = null;//the rectangle used to extract text.
	private Vector<RectF> rectFs = null;
	private int piecesRotation[] = null;

	public ExtractTextTask(Activity activity, PDFContext pdfContext, RectF rect)
	{
		this.taskType = TYPE_EXTRACTTEXT;
		this.activity = activity;
		this.pdfContext = pdfContext;
		this.rect = rect;
		rectFs = new Vector<RectF>();
	}
	
	@Override
	public boolean execute()
	{
		if (pdfContext.page == null || pdfContext.page.getHandle() == 0)
			return false;
		PDFTextPage textPage = null;
		try
		{
			textPage = PDFTextPage.create(pdfContext.page);
			if (rect == null) return false;

			PDFTextSelection textSelection = textPage.selectByRectangle(rect);
			final String text = textSelection.getChars();
			if (text == null) 
			{
				bFinished = true;
				textSelection.release();
				textPage.release();
				return true;
			}
			
			int count = textSelection.countPieces();
			rectFs.clear();
			piecesRotation = new int[count];
			for(int i = 0; i < count; i++)
			{
				RectF pieceRect = textSelection.getPieceRect(i);		
				try {
					piecesRotation[i] = textSelection.getPieceRotation(i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					piecesRotation[i] = piecesRotation[0];
				}

				rectFs.add(pieceRect);
			}
			
			bFinished = true;
			//show the text in the main activity
			if (activity != null) 
			{
				activity.runOnUiThread(new Runnable()
				{
				
					@Override
					public void run()
					{
						//Toast or dialog
						Toast.makeText(activity.getBaseContext(), text, Toast.LENGTH_SHORT).show();
					}
				});
			}
			textSelection.release();
			textPage.release();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public Vector<RectF> getRectFs()
	{
		return rectFs;
	}
	
	public int[] getAllPiecesRotation()
	{
		return piecesRotation;
	}
}
