package com.foxit.ui;

import java.io.File;

import com.foxit.bean.AnnotInfo;
import com.foxit.bean.PDFContext;
import com.foxit.controller.ViewController;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.PDFLibrary;
import com.foxit.gsdk.pdf.PDFDocument;
import com.foxit.gsdk.pdf.annots.Annot;
import com.foxit.popupmenu.MenuItem;
import com.foxit.popupmenu.PopupMenu;
import com.foxit.popupmenu.PopupMenu.OnItemOnClickListener;
import com.foxit.view.PDFPagerAdapter;
import com.foxit.view.PDFView;
import com.foxit.view.PDFViewPager;
import com.tri.myfirstapp.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FoxitViewActivity extends Activity
{
	private static final String TAG = "FoxitViewActivity";
	
	private PDFLibrary pdfLibrary = null;
	private ViewController viewController = null;
	
	private static String license_id = "TZYqmDh9ls+NXj7G8mzitUTKehitnZX8/Qjo3iixZkXlRsAMO60cKA==";
	private static String unlockCode = "8f3o18ONtRkJBDdKtFJS8bag2ZnOJ5lPFhWFgykpJ4nNY9XwbHM54RKWFiPt7rG3mAVdC5Bm7v60wWH1Seq06vA1Ue4oB8K/e/cWPbMwLg16tEp+F3Qc9qr21NTNiQRqyeO+A6rqC4Bpw4txYVy+Y/xHKQY0C3kbZCjzRFdNItM+Yd6n9HSxQnBSIWXdH1cKuFN9QlgYe1ua68+LxsmvvXZYCfLYTOvcnHOVnAjw0OmmxBU9XmVar+7srzmom4E3A4XLagbwAdsPwlmMkU65g5DN99pDt3hkuQAxTIO3YMJYZrlpmT/srmcnn4PbySIOsM8p7rf4X/HZjMuyrI89AriVD+E2JrCyhdcs4tljNoVKZmxCUer9Uas1mqEn9ccS8+sqfTqbxM5tHnYmXp9tiFQuaFWZkKNvCcjZCE08pUGijwbBPHb8embB6eYo32kA5CxzSI0XIL5VhhqHL0ThOFvOnvWYRj7q3ZQHFdM7jEUcvn41lp/O/CZFoxsXXGTa3O568oNm8CV8pS41zyAKwa4uGMCmtdxvdRtXAY9F3q2Q/0WXQGLEZPd06YvTe9lQFInsVGFgsduca3NHXMZcpwHTPjNBZzgnNUjEiSAvnVs6FXQ5BhVcO4zB6N1I6gMCAZ9YfAr3GyFnJDBg+yz4aIfkVX3PQJNPc7KRE2qEpbvcwh/ci5P9thxa6W9+zqJvCgn9S8JspeEvG58C1deXWU0+t9WV61Du2r4zIJHt9YXF58ll6esgWx7AJ5F5kjwd8mLsvQLknElpJzwfDQNuMTtuywVO7XuEaYYVmtzCd+KJh/1veFzE9x+ZeUgYp3Na20IjVkK1/XZMHLtiSF8prve51RdK1FqWdgIuSeLAmtU7pzHVsoN8r0iddllxFDtuaQ/X0Ot2faUArNTzN+Pl0rzbdTZv+820t5nyKRoNgu/47ZygdQCs2XVwhFeAUIf3nJkMvcmWstTAUfzZgvvCqiEUwos/Uo3KD278odii39/bpj4yfjTHe2ZpkCT4K7n9UjYj67sD0DPfknKbz8NtnXwF11dDnmWcT3kt0QAI3e3F2/U45tDFq8tQfuhC1QPKCqNMszXmxnH7NS7+kN8jTIDhh81uU8QirW6a9gwypnvfk7vaL5OnqoepYuywQTrO3CYpUlDZf15EemcgE5FabHKxYW9JE75KL+4R/PodIk3HZuAIZAy0xfBeR+vjuUIMenpMEvb0jx8nr8GA0Lr4AeBk4lONFFYkkgiLTOEJle6WDrlrio8GEVJQgOeeP8P+5ho3pPFi/Omq/t8vQsht3D5/iIgFRX+0PDuhnkCJ5pPTROEMfBDv0NbGfHEbZFWgnhgP73WhHNMOAIBh8VV1haeHnOiTFm3xkRTXp/qegZtUTiTa0/8Qh9WIYPkmH8TY6MAMYrPs3yZPLnt94+pdcmx8XJYjag5nQIDOPa4EX3FgSsHIrB8QDMMWKvgxnEOUQseiFExli5WAAVlPTIf55KM1xD6yOSACDssqVKBO5Bw65oVEOppoV6hletn2TLZ0jE8AiStFFwRMrkjwRluC+HRMIalhNSWlfq03uuJTBm0J3/7fdUXNU20MbiLTajA6n6DkuKWZoOUzKLl+H9L99buuHv3KV5eodLgpE+36uI3oxan0GVx5nsZt5T/YX8P0ZhGRn9MQWijgIN95xMfbV+L/64cfJexrPnEQJxW1IBrXeOk3slhYN3C/iWwp6+tBcN13wy4eCfr1Hcyo7/N+gWX8dbnzAT9zSvikrqXMBlY=";
	private int memorySize = 12 * 1024 * 1024;
	private boolean scaleable = true;
	//variables for layout
	private RelativeLayout layout = null;
	private View topBarView = null;
	private View bottomBarView = null;
	private View search_topBarView = null;
	private View search_bottomBarView = null;
	private EditText searchEditText = null;
	private String searchText = null;
	private static boolean isSeacrhing = false;
	private static String lastFileName = null;  //whether is the same file
	private boolean bOpenSuccess = true;
	private TextView pageindexView = null;
	private PDFContext pdfContext = null;
	
	private static final int MODEL_IMPORT = 1;
	private static final int MODEL_EXPORT = 2;
	private static final int MODEL_INSERT = 3;
	private int operationMode = 0;//import or export
	public static String checkLicense = "Invalid license!!! Please check whether the license has related module!!!";
	private boolean bPressSave = false;
	
	//load foxit pdf library
	static{
		System.loadLibrary("fsdk_android");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//initialize PDF library for using Foixt APIs
		pdfLibrary = PDFLibrary.getInstance();
		try
		{
			pdfLibrary.initialize(memorySize, scaleable);
			pdfLibrary.unlock(license_id, unlockCode);
			int type = pdfLibrary.getLicenseType();
	    	if(type == PDFLibrary.LICENSETYPE_EXPIRED || type == PDFLibrary.LICENSETYPE_INVALID)
	    	{
	    		Log.e(TAG, "onCreate: License is invalid or expired!!!");
	    		Toast.makeText(this, "License is invalid or expired!!!", Toast.LENGTH_LONG).show();
				this.finish();
				return;
	    	}
		}
		catch (PDFException e)
		{
			e.printStackTrace();
			Toast.makeText(FoxitViewActivity.this, generateMsg(e.getLastError(), "Fail to unlock PDF library!!!", true), Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		
		viewController = ViewController.create(this);
		
		String filePath = getFilePath();
		if (lastFileName == null) 
		{
			lastFileName = filePath;
		}
		else
		{
			if (!lastFileName.equals(filePath)) 
			{	
				isSeacrhing = false;
				lastFileName = filePath;
			}
		}
		try
		{
			viewController.openDocument(filePath, null);
		}
		catch (PDFException e)
		{
			if (e.getLastError() == PDFException.ERRCODE_PASSWORD)
			{
				PasswordDialog passwordDialog = new PasswordDialog(this, viewController, filePath, R.style.PasswordDialog);
				passwordDialog.setCanceledOnTouchOutside(false);
				passwordDialog.showDialog();
			}
			else {
				Toast.makeText(FoxitViewActivity.this, generateMsg(e.getLastError(), "Fail to open document!!!", true), Toast.LENGTH_LONG).show();
				bOpenSuccess = false;
				this.finish();			
				return;
			}
			
		}
		if(bOpenSuccess)
		{
			viewController.initialize();
			viewController.setParent(this);
			pdfContext = viewController.getTaskManager().getPDFContext();

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			//show view
			layout = new RelativeLayout(this);	
			if (!isSeacrhing)
			{
				setLayout(layout);
			}
			else {
				resetLayoutOnSearch(layout);
			}
		}
	}

	public static String generateMsg(int err, String msg, boolean printErr) { 
		if (err == PDFException.ERRCODE_INVALIDLICENSE)
			return checkLicense;
		else if (printErr)
			return msg + " Error code:" + err;
		else
			return msg;
	} 
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		if(pdfContext.isSupportPSI() == true)
			getMenuInflater().inflate(R.menu.psi_menu, menu);
		else {
			getMenuInflater().inflate(R.menu.main, menu);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item)
	{
		if (item.getItemId() == R.id.save)
		{
			//save the current document
			if (viewController != null && lastFileName != null)
			{
				String fileName = lastFileName + ".tmp";
				viewController.saveDocument(fileName, PDFDocument.SAVEFLAG_OBJECTSTREAM);
				bPressSave = true;
			}
		}
		//cancel PSI operation
		if(item.getItemId() == R.id.cancel){
			pdfContext.setPSIFlag(false);
			viewController.cancelPsi();
			resetLayoutAfterPsi(layout);
		}
		//comfirm PSI operation
		if(item.getItemId() == R.id.confirm){
			pdfContext.setPSIFlag(false);
			viewController.confirmPsi();
			resetLayoutAfterPsi(layout);
		}
		
		return true;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if(viewController!=null)
		{
			viewController.destroy();
			viewController.closePage();			
			viewController.closeDocument();
		}
		viewController = null;
		if(pdfLibrary!=null)
			pdfLibrary.destroy();
		pdfLibrary = null;
		viewController = null;
	}
	
	private String getFilePath() 
	{
		Intent intent = getIntent();
		String filePath = intent.getStringExtra("fileDir");
		
		if(filePath == null || filePath.length() == 0)
		{
			Uri uri = intent.getData();
			filePath = uri.getPath();
		}
		
		return filePath;
	}
	
	//set and show view
	private void setLayout(RelativeLayout layout)
	{
		
		layout.addView(viewController.getViewPager());//viewpager
		
		//top bar
		topBarView = View.inflate(this, R.layout.top_bar, null);
		
		layout.addView(topBarView);
		
		//bottom bar
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,    
                LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		bottomBarView = View.inflate(this, R.layout.bottom_bar, null);
		layout.addView(bottomBarView, layoutParams);	
		
		layout.addView(View.inflate(this, R.layout.pageindex, null));
		
		setContentView(layout);

		//show pageindex
		pageindexView = (TextView) findViewById(R.id.textView1);
		pageindexView.setText((pdfContext.pageIndex + 1) + "/" + pdfContext.pageCount);
		//monitor Bar
		monitorBar(toolbarListener);
	}
	
	private void monitorBar(OnCheckedChangeListener listener)
	{
		//Monitor top bar
		RadioGroup topBarGroup = (RadioGroup) findViewById(R.id.toolbar_top);
		topBarGroup.setOnCheckedChangeListener(listener);
		
		//Monitor bottom bar
		RadioGroup bottomBarGroup = (RadioGroup) findViewById(R.id.toolbar_bottom);
		bottomBarGroup.getBackground().setAlpha(128);
		bottomBarGroup.setOnCheckedChangeListener(listener);
	}
	
	//reset layout
	private void resetLayoutOnSearch(RelativeLayout layout)
	{
		if (isSeacrhing)
		{
			layout.addView(viewController.getViewPager());
		}
		else {//remove top&bottom bar first
			layout.removeViewInLayout(topBarView);
			layout.removeViewInLayout(bottomBarView);
		}
	
		//add search_top&bottom bar to the layout
		search_topBarView = View.inflate(FoxitViewActivity.this, R.layout.search_top_bar, null);
		layout.addView(search_topBarView);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,    
                LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		search_bottomBarView = View.inflate(FoxitViewActivity.this, R.layout.search_bottom_bar, null);
		layout.addView(search_bottomBarView, layoutParams);
		
		if (isSeacrhing)
		{	
			layout.addView(View.inflate(this, R.layout.pageindex, null));
			setContentView(layout);
			//show pageindex
			pageindexView = (TextView) findViewById(R.id.textView1);
			pageindexView.setText((pdfContext.pageIndex + 1) + "/" + pdfContext.pageCount);
		}
		
		//monitor search bar
		monitorSearchBar(toolbarListener);
		
		searchEditText = (EditText) findViewById(R.id.searchText);
		searchEditText.setOnKeyListener(keyListener);
		
		isSeacrhing = true;
	}
	
	private void resetLayoutAfterSearch(RelativeLayout layout)
	{
		//remove search top&bottom bar first
		layout.removeViewInLayout(search_topBarView);
		layout.removeViewInLayout(search_bottomBarView);
		
		//add top&bottom bar to the layout
		//top bar
		topBarView = View.inflate(this, R.layout.top_bar, null);
		layout.addView(topBarView);
		
		//bottom bar
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,    
                LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		bottomBarView = View.inflate(this, R.layout.bottom_bar, null);
		layout.addView(bottomBarView, layoutParams);
		
		//monitor Bar
		monitorBar(toolbarListener);
		
		isSeacrhing = false;
	}
	
	private void resetLayoutOnPsi(RelativeLayout layout){
		
		layout.removeViewInLayout(topBarView);
		layout.removeViewInLayout(bottomBarView);
		setContentView(layout);
	
	}
	
	private void resetLayoutAfterPsi(RelativeLayout layout){
		//top bar
		topBarView = View.inflate(this, R.layout.top_bar, null);
		
		layout.addView(topBarView);
		
		//bottom bar
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,    
                LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		bottomBarView = View.inflate(this, R.layout.bottom_bar, null);
		layout.addView(bottomBarView, layoutParams);	
		
		//monitor Bar
		monitorBar(toolbarListener);
	}
	private void monitorSearchBar(OnCheckedChangeListener listener)
	{
		RadioGroup bottomBarGroup = (RadioGroup) findViewById(R.id.search_toolbar_bottom);
		if (bottomBarGroup == null) return;
		bottomBarGroup.getBackground().setAlpha(128);
		bottomBarGroup.setOnCheckedChangeListener(listener);
	}
	
	OnCheckedChangeListener toolbarListener = new OnCheckedChangeListener()	
	{
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			if(viewController == null)
				return;
			RadioButton radioButton = (RadioButton) FoxitViewActivity.this.findViewById(checkedId);
			switch (checkedId)
			{
			case R.id.radio_prevPage:
				viewController.turnpage(false);
				break;
			case R.id.radio_search:
				resetLayoutOnSearch(layout);
				break;
			case R.id.radio_nextPage:
				viewController.turnpage(true);
				break;
			case R.id.radio_zoomIn:
				viewController.zoom(true);
				break;
			case R.id.radio_zoomOut:
				viewController.zoom(false);
				break;
			case R.id.radio_searchPrev:
				viewController.searchPrev();
				break;
			case R.id.radio_searchNext:
				viewController.searchNext();
				break;
			case R.id.radio_annot:
			{
				WindowManager wm = (WindowManager)FoxitViewActivity.this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				PopupMenu menu = new PopupMenu(FoxitViewActivity.this, display.getWidth() / 5, LayoutParams.WRAP_CONTENT);
				menu.addMenuItem(new MenuItem("Highlight"));
				menu.addMenuItem(new MenuItem("Underline"));
				menu.addMenuItem(new MenuItem("Link"));
				menu.addMenuItem(new MenuItem("FreeText"));
				menu.addMenuItem(new MenuItem("Note"));
				menu.addMenuItem(new MenuItem("PSI"));
				menu.addMenuItem(new MenuItem("Import"));
				menu.addMenuItem(new MenuItem("Export"));
				menu.inflateMenuItems();
				menu.show(radioButton, 3);				
				menu.setItemOnClickListener(annotItemOnClickListener);
			}
				break;
			case R.id.radio_rotate:
			{
				WindowManager wm = (WindowManager)FoxitViewActivity.this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				PopupMenu menu = new PopupMenu(FoxitViewActivity.this, display.getWidth() / 5, LayoutParams.WRAP_CONTENT);
				menu.addMenuItem(new MenuItem("Rotate Left"));
				menu.addMenuItem(new MenuItem("Rotate Right"));
				menu.inflateMenuItems();
				menu.show(radioButton, 1);				
				menu.setItemOnClickListener(rotationItemOnClickListener);
			}
				break;
			default:
				break;
			}
			radioButton.setChecked(false);
			//update pageindex
			if(pageindexView != null)
			    pageindexView.setText((pdfContext.pageIndex + 1) + "/" + pdfContext.pageCount);
		}
	};

	public void onBack(View view)
	{
		resetLayoutAfterSearch(layout);
		//reset searchText
		searchText = null;
		clearHighlightText();
	}
	
	OnKeyListener keyListener = new OnKeyListener()
	{
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			if (KeyEvent.KEYCODE_ENTER == keyCode
			        && event.getAction() == KeyEvent.ACTION_DOWN)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				
				clearHighlightText();
				searchText = searchEditText.getText().toString();
				if(!searchText.equals(""))
					viewController.startSearch(FoxitViewActivity.this, searchText);
				else 
					Toast.makeText(FoxitViewActivity.this, "Please input search text!", Toast.LENGTH_SHORT).show();
				return true;
			}
			return false;
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if( viewController== null)
			return;
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){ 
		    viewController.resetConfiguration();
		} 
		else if (this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) { 
			viewController.resetConfiguration(); 
		}
	}
	
	private void clearHighlightText()
	{
		if(viewController == null)
			return;
		PDFViewPager pdfViewPager = (PDFViewPager)viewController.getViewPager();
		PDFPagerAdapter pdfPagerAdapter = (PDFPagerAdapter)pdfViewPager.getAdapter();
		int index = viewController.getTaskManager().getPDFContext().pageIndex;
		PDFView pdfView = pdfPagerAdapter.getViewMap().get(index);
		if (pdfView == null)
			return;
		else
		{
			pdfView.clearHighlight();
			pdfView.postInvalidate();
		}	
		viewController.destroy();
	}
	
	OnItemOnClickListener annotItemOnClickListener = new OnItemOnClickListener()
	{
		
		@Override
		public void onItemClick(MenuItem item, int position)
		{
			String type = null;
			if(pdfContext.getCurrentState().bAnnotLicense == false)
			{
				//Toast or dialog
				Toast.makeText(FoxitViewActivity.this, "no annot license!!!", Toast.LENGTH_LONG).show();
				return;
			}
			switch (position)
			{
			case 0:
				type = Annot.TYPE_HIGHLIGHT;
				break;
			case 1:
				type = Annot.TYPE_UNDERLINE;
				break;
			case 2:
				type = Annot.TYPE_LINK;
				break;
			case 3:
				type = Annot.TYPE_FREETEXT;				
				break;
			case 4:
				type = Annot.TYPE_TEXT;
				break;
			case 5:
				{	//click to start PSI operation
					if(pdfContext.getCurrentState().rotation == 0){
						if (viewController != null) {
							resetLayoutOnPsi(layout);
							int ret = viewController.initPsi();
							if(ret == PDFException.ERRCODE_INVALIDLICENSE)
							{
								Toast.makeText(FoxitViewActivity.this, "no psi license!!!", Toast.LENGTH_LONG).show();
								return;
							}
							pdfContext.setPSIFlag(true);
						}
					}
				}
				break;
			case 6:
				{
					Intent intent = new Intent();
					intent.setClass(FoxitViewActivity.this, FileSelectorActivity.class);
					startActivityForResult(intent, 1);//requestCode = 1
					operationMode = MODEL_IMPORT;
				}
				break;
			case 7:
				{
					Intent intent = new Intent();
					intent.setClass(FoxitViewActivity.this, FileSelectorActivity.class);
					startActivityForResult(intent, 1);//requestCode = 1
					operationMode = MODEL_EXPORT;
				}
				break;
			
			default:
				break;
			}
			
			if (type != null)
			{
				AnnotInfo annotInfo = new AnnotInfo(type, AnnotInfo.TYPE_ADD);
				//use in ViewContoller
				if(viewController != null)
					viewController.setAnnotInfo(annotInfo);
			}
		}
	};

	OnItemOnClickListener rotationItemOnClickListener = new OnItemOnClickListener()
	{
		
		@Override
		public void onItemClick(MenuItem item, int position)
		{
			int direction = -1;
			switch (position)
			{
			case 0:
				direction = ViewController.ROTATIONDIRECTION_LEFT;
				break;
			case 1:
				direction = ViewController.ROTATIONDIRECTION_RIGHT;
				break;
			default:
				break;
			}			
			if(viewController != null)
				viewController.onRotation(direction);
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		if(viewController == null)
			return;
		if (requestCode == 1 && resultCode == 2)
		{
			Bundle bundle = data.getBundleExtra("Result");
			String filePath = bundle.getString("file");

			if (operationMode == MODEL_IMPORT)
			{					
				//import annot from fdf
				viewController.importAnnotsFromFDF(filePath);
			}
			else if (operationMode == MODEL_EXPORT)
			{
				//export annot to fdf
				viewController.exportAnnotsToFDF(lastFileName, filePath);
			}
			else if (operationMode == MODEL_INSERT) {
				viewController.insertImageToPage(filePath);
			}
			operationMode = 0;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK && viewController != null))
		{
			viewController.destroy();
			viewController.closePage();			
			viewController.closeDocument();
			//release psi
			if (pdfContext.isSupportPSI() == true)
			{
				viewController.releasePsi();
				pdfContext.setPSIFlag(false);
			}
			
			if (bPressSave)
			{
				File file = new File(lastFileName);
				if (file != null)
					file.delete();
				File newfile = new File(lastFileName + ".tmp");
				if (newfile != null)
					newfile.renameTo(file);
				
				bPressSave = false;
			}
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
