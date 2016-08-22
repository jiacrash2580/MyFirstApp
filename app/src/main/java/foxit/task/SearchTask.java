package foxit.task;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.widget.Toast;

import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFTextSearch;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.PDFTextPage;
import com.foxit.gsdk.pdf.PDFTextSelection;
import com.foxit.ui.MainActivity;
import com.foxit.view.PDFView;
import com.foxit.view.PDFPagerAdapter;

public class SearchTask extends AbstractTask
{
	private static final String TAG = "SearchTask";
	private PDFContext pdfContext = null;
	
	public static final int FLAG_START 		= 0;
	public static final int FLAG_PREVIOUS 	= 1;
	public static final int FLAG_NEXT 		= 2;
	
	private int searchFlag = FLAG_START;
	private String target = null; //The target string will be search.
	private PDFPagerAdapter adapter = null; 
	private PDFView mView = null;
	
	public SearchTask(PDFPagerAdapter adapter, PDFContext pdfContext, int searchFlag)
	{
		this.taskType = TYPE_SEARCH;
		this.adapter = adapter;
		this.pdfContext = pdfContext;
		this.searchFlag = searchFlag;
	}
	
	public SearchTask(PDFPagerAdapter adapter, PDFContext pdfContext, String target, int searchFlag)
	{
		this.taskType = TYPE_SEARCH;
		this.adapter = adapter;
		this.pdfContext = pdfContext;
		this.target = target;
		this.searchFlag = searchFlag;
	}
	
	@Override
	public boolean execute()
	{
		boolean bSuccess = false;
		
		switch (searchFlag)
		{
		case FLAG_START:
			bSuccess = startSearch();
			break;
		case FLAG_PREVIOUS:
			bSuccess = searchPrev();
			break;
		case FLAG_NEXT:
			bSuccess = searchNext();
			break;
		default:
			break;
		}
		if (bSuccess) 
		{
			mView.postInvalidate();
		}
		return bSuccess;
	}

	private boolean startSearch()
	{
		boolean isFindFlag = false;
		mView = getCurrentView(adapter, pdfContext.pageIndex);
		if(mView == null)
			return isFindFlag;
		try 
		{
			PDFPage page = pdfContext.page;
			if (page == null)
				return isFindFlag;
				
			PDFTextPage textPage = PDFTextPage.create(page);
			if (textPage == null)
			{
				return isFindFlag;
			}
			
			pdfContext.textSearch = new PDFTextSearch(textPage);
			if (pdfContext.textSearch == null)
			{
				textPage.release();
				return isFindFlag;				
			}
				
			if(pdfContext.textSearch.startSearch(target, 0, 0))
			{
				if(pdfContext.textSearch.findNext())
				{
					PDFTextSelection selection = pdfContext.textSearch.getSelection();
					highlightSearchText(selection);
					isFindFlag = true;
				}
				else 
				{
					showToast("Text Not Found");
					pdfContext.textSearch.release();
					textPage.release();
					return isFindFlag;	
				}
			}
		}
		catch (PDFException e)
		{
			// TODO: handle exception
			if(e.getLastError() == PDFException.ERRCODE_INVALIDLICENSE)
				showToast(MainActivity.checkLicense);
			else
				showToast("Text Not Found!!!");
		}			
		return isFindFlag;
}
	
	private boolean searchPrev()
	{
		boolean isFindFlag = false;
		if (pdfContext.textSearch != null)
		{
			mView = getCurrentView(adapter, pdfContext.pageIndex);
			if(mView == null)
				return isFindFlag;
			try
			{
				if (pdfContext.textSearch.findPrev())
				{
					PDFTextSelection selection = pdfContext.textSearch.getSelection();
					mView.clearHighlight();
					highlightSearchText(selection);
					isFindFlag = true;
				}
				else 
				{
					showToast("Already Reach the First");
				}
			}
			catch (PDFException e)
			{
				// TODO: handle exception
				if(e.getLastError() == PDFException.ERRCODE_INVALIDLICENSE)
					showToast(MainActivity.checkLicense);
				else
					showToast("Text Not Found!!!");
			}

		}
		return isFindFlag;
	}
	
	private boolean searchNext()
	{
		boolean isFindFlag = false;
		if (pdfContext.textSearch != null)
		{
			mView = getCurrentView(adapter, pdfContext.pageIndex);
			if(mView == null)
				return isFindFlag;
			try
			{
				if (pdfContext.textSearch.findNext())
				{
					PDFTextSelection selection = pdfContext.textSearch.getSelection();
					mView.clearHighlight();
					highlightSearchText(selection);
					isFindFlag = true;
				}
				else
				{
					showToast("Already Reach the Last");
				}
			}
			catch (PDFException e)
			{
				// TODO: handle exception
				if(e.getLastError() == PDFException.ERRCODE_INVALIDLICENSE)
					showToast(MainActivity.checkLicense);
				else
					showToast("Text Not Found!!!");
			}

		}
		return isFindFlag;
	}
	
	private void highlightSearchText(PDFTextSelection selection)
	{
		try 
		{
			if(selection != null)
			{
				int count = selection.countPieces();
				for(int j = 0; j < count; j++)
				{
					RectF rect;
					rect = selection.getPieceRect(j);
					//highlight
					RectF dstRect = ConvertDeviceRect(rect, pdfContext.page, mView, pdfContext.getCurrentState());
		    		int width = (int) (dstRect.right+1 - dstRect.left);
		    		int height = (int) (dstRect.bottom+2 - dstRect.top);
		    			
		    		//Highlight the matching text
					Bitmap pageBitmap = GetHighlightMarkedRectBitmap(width,height);			
					mView.addHighlightBitmap(pageBitmap,(int)dstRect.left,(int)dstRect.top);
				}
			}
		}
		catch (PDFException e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private Bitmap GetHighlightMarkedRectBitmap(int width,int height)
	{
		//init a blue color bitmap   
		int[] colors=new int[width*height]; 
		for(int i = 0;i< width * height;i++)
			colors[i] = 0;
		int r = 255;  
		int g = 255;  
		int b = 0;  
		int a = 150;  
		int color_blue = Color.argb(a, r, g, b);
		for(int j =0;j< width * height;j++)
			colors[j] = color_blue;

		Bitmap map = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
		return map;
	}
	
	private void showToast(final String text)
	{
		if(parentActivity != null)
		{
			parentActivity.runOnUiThread(new Runnable()
			{
			    @Override
			    public void run()
			    {
			     //Toast or dialog
			        Toast.makeText(parentActivity.getBaseContext(), text, Toast.LENGTH_SHORT).show();
			    }
			});				
		}
	}
}
