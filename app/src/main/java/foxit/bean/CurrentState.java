package foxit.bean;

import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.utils.SizeF;
import com.foxit.view.IPageLayoutMode;

public class CurrentState
{
	public float scale = 1.0f;
	
	public boolean isScaling = false;
	public int	screenWidth = 0;  // the screen width
	public int  screenHeight = 0;   // the screen height
	public float movesizeX = 0;
	public float movesizeY = 0;
	public float downX = 0;
	public float downY = 0;
	
	public int  curPageIndex = 0;
	public SizeF pageSize = null;
	public int display_width = 0;
	public int display_height = 0;
	public int  layoutMode = IPageLayoutMode.SINGLEPAGE;
	public boolean bDrawable = false;
	public boolean bChangeSize = false;
	public boolean bVertical = false;//is vertical or horizontal
	public int rotation = PDFPage.ROTATION_0;
	public boolean bRenderAnnot = false;// only render annotation.
	public boolean bAnnotLicense = false;
}
