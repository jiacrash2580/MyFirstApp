package com.foxit.ui;

import com.foxit.controller.ViewController;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.PDFLibrary;
import com.tri.myfirstapp.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordDialog extends Dialog
{
	public static final int OK = 0;
	public static final int CANCEL = -1;
	public static final int PASSWORDERROR = -2;
	
	private ViewController viewController = null;
	private int theme = 0;
	private Context context = null;
	private Dialog dialog;
	private Button btn_ok = null;
	private Button btn_cancel = null;
	private EditText passwordText = null;
	private String path = null;

	
	private Handler handler = null;
	private int result = 0;
	public PasswordDialog(Context context, ViewController viewController, String filePath, int theme)
	{
		super(context);
		this.viewController = viewController;
		this.theme = theme;
		this.context = context;
		this.path = filePath;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_dialog);
		
		btn_cancel = (Button) findViewById(R.id.dialog_button_cancel);
		btn_ok = (Button) findViewById(R.id.dialog_button_ok);
		passwordText = (EditText) findViewById(R.id.dialog_password_content);
		dialog = this;

		btn_ok.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				String password = passwordText.getText().toString();
				try
				{
					viewController.openDocument(path, password.getBytes());
					result = OK;
				}
				catch (PDFException e)
				{
					//bSuccessed = false;
					int err = e.getLastError();
					if (err == PDFException.ERRCODE_PASSWORD)
					{
						result = PASSWORDERROR;
						passwordText.setText("Please input password.");
						Toast.makeText(context, "The password is wrong.", Toast.LENGTH_SHORT).show();
					}
				}
				finally
				{
					if (result != PASSWORDERROR)
					{
						endDialog(result);
					}
					
				}
				
			}
		});
		
		
		btn_cancel.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				endDialog(CANCEL);

				showFileBrowserActivity();
			}
		});
		
		passwordText.setOnKeyListener(new View.OnKeyListener()
		{
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (KeyEvent.KEYCODE_ENTER == keyCode
				        && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
									
					return true;
				}
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					endDialog(CANCEL);
					showFileBrowserActivity();
				}
				return false;
			}
		});
		
	}
	
	public int showDialog()
	{
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				throw new RuntimeException(); 
			}
		};
		
		super.show();

		try
		{
			Looper.getMainLooper();
			Looper.loop();
		}
		catch (RuntimeException  e)
		{
			// TODO: handle exception
		}
		return result;
	}
	
	public void endDialog(int result)
	{
		dismiss();
		this.result = result;
		if(result == OK)
		{
			Message message = handler.obtainMessage();
			handler.sendMessage(message);
		}		
	}
	
	private void showFileBrowserActivity()
	{		
		((FoxitViewActivity) context).finish();
		viewController.destroy();	
		viewController.closePage();
		viewController.closeDocument();
		viewController = null;
		PDFLibrary pdfLibrary = PDFLibrary.getInstance();
		pdfLibrary.destroy();
		pdfLibrary = null;
	}
}
