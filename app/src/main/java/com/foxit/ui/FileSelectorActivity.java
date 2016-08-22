package com.foxit.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tri.myfirstapp.R;

public class FileSelectorActivity extends ListActivity
{
	public static final String TAG = "FileSelectorActivity";
	
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private String curPath = "/";
	private TextView selector_path = null;
	private EditText selector_fileName = null;
	private String fileName = null;	
	private String folderName = null;
	private String filePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.file_selector);
		
		rootPath = Environment.getExternalStorageDirectory().getPath();
		curPath = rootPath;
		
		selector_path = (TextView) findViewById(R.id.selector_path);
		selector_fileName = (EditText) findViewById(R.id.selector_fileName);
		Button btn_confirm = (Button) findViewById(R.id.selector_confirm);
		btn_confirm.setOnClickListener(clickListener);
		Button btn_cancel = (Button) findViewById(R.id.selector_cancel);
		btn_cancel.setOnClickListener(clickListener);
		
		getFileDirectory(rootPath);
	}
	
	OnClickListener clickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.selector_confirm:
				if(fileName == null)
				{
					fileName = selector_fileName.getText().toString();
					filePath = folderName + "/" + fileName;
				}
				Intent data = new Intent(FileSelectorActivity.this, FoxitViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("file", filePath);
				data.putExtra("Result", bundle);
				setResult(2, data);// resultCode = 2
				finish();
				break;
			case R.id.selector_cancel:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	private void getFileDirectory(String filePath)
	{
		selector_path.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		folderName = filePath;
		if (!filePath.equals(rootPath)) {
			items.add("backHome");
			paths.add(rootPath);
			items.add("backSuper");
			paths.add(file.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			items.add(f.getName());
			paths.add(f.getPath());
		}

		setListAdapter(new FileSelectorAdapter(this, items, paths));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		File file = new File(paths.get(position));
		if (file.isDirectory()) {
			curPath = paths.get(position);
			getFileDirectory(paths.get(position));
		} else {
			filePath = paths.get(position);
			fileName = file.getName();
			selector_fileName.setText(fileName);
		}
	}
}
