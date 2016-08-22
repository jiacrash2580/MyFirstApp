package foxit.bean;

import com.foxit.gsdk.image.Image;
import com.foxit.gsdk.pdf.PDFDocument;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.PDFTextSearch;
import com.foxit.gsdk.pdf.PDFTextSelection;
import android.content.Context;
import android.graphics.Bitmap;

//Keep the PDF document information.
public class PDFContext
{
	public static final String TAG = "PDFContext";
	
	private Context context = null;
	public PDFDocument document = null;
	public PDFPage page = null;
	public Image image = null;
	public int pageIndex = 0;
	public int pageCount = 0;
	public static Bitmap pageBitmap = null;
	private CurrentState currentState = null;
	public PDFTextSearch textSearch = null;
	public PDFTextSelection textSelection = null;
	private boolean PSI_FLAG = false;
	private boolean INSERTIMG_FLAG = false;
	
	public PDFContext(Context context)
	{
		this.context = context;
		currentState = new CurrentState();
	}
	
	public Context getContext()
	{
		return context;
	}
	
	public PDFDocument getDocument()
	{
		return document;
	}
	
	public CurrentState getCurrentState()
	{
		return currentState;
	}
	
	public void setPSIFlag(boolean flag)
	{
		this.PSI_FLAG = flag;
	}
	
	public boolean isSupportPSI(){
		return this.PSI_FLAG;
	}
	
	public void setInsertImgFlag(boolean flag)
	{
		this.INSERTIMG_FLAG = flag;
	}
	
	public boolean isInsertImg(){
		return this.INSERTIMG_FLAG;
	}
}
