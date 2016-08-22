package foxit.task;

import com.foxit.bean.CurrentState;
import com.foxit.bean.PDFContext;
import com.foxit.view.PDFPagerAdapter;

public class ZoomTask extends AbstractTask
{
	private static final String TAG = "ZoomTask";
	
	private PDFContext pdfContext = null;
	private boolean bZoomIn = false;// zoom flag, true --> zoom in, false --> zoom out
	private PDFPagerAdapter adapter = null;
	
	public ZoomTask(PDFPagerAdapter adapter, PDFContext pdfContext, boolean bZoomIn)
	{
		this.taskType = TYPE_ZOOM;
		this.adapter = adapter;
		this.pdfContext = pdfContext;
		this.bZoomIn = bZoomIn;
	}
	
	@Override
	public boolean execute()
	{
		boolean bSuccess = false;
		if (bZoomIn)
		{
			bSuccess = zoomIn();
		}
		else {
			bSuccess = zoomOut();
		}
		return bSuccess;
	}

	private boolean zoomIn()
	{
		CurrentState currentState = pdfContext.getCurrentState();
		//currentState.isScaling = true;
		currentState.scale = currentState.scale + 0.2f;
		if(currentState.scale > 2.0f)
			currentState.scale = 2.0f;
		return true;
	}
	
	private boolean zoomOut()
	{
		CurrentState currentState = pdfContext.getCurrentState();
		//currentState.isScaling = true;
		currentState.scale = currentState.scale - 0.2f;
		if(currentState.scale < 1.0f)
			currentState.scale = 1.0f;
		return true;
	}
}
