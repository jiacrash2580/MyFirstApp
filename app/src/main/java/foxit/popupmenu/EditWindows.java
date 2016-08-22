package foxit.popupmenu;

import com.foxit.view.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class EditWindows extends PopupWindow
{
	public static final String TAG = "EditWindows";
	
	public static final int EDITSTATE_READY = 0;
	public static final int EDITSTATE_EDIT = 1;
	public static final int EDITSTATE_CANCEL = 2;
	
	private Context context = null;
	private int nScreenWidth = 0;
	private int nScreenHeight = 0;
	private String content = null;
	private Handler handler = null;
	
	private int bEditState = EDITSTATE_READY;
	
	public EditWindows(Context context)
	{
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public EditWindows(Context context, int width, int height)
	{
		this.context = context;
		
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(false);
		
		update();
		
		WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		nScreenWidth = display.getWidth();
		nScreenHeight = display.getHeight();
		
		setWidth(width);
		setHeight(height);
		View view = LayoutInflater.from(context).inflate(R.layout.edit_window, null);
		setContentView(view);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(keyListener);
		initialize();
	}
	
	private void initialize()
	{	
		Button button_ok = (Button) getContentView().findViewById(R.id.ok);
		button_ok.setOnClickListener(clickListener);
		
		Button button_cancel = (Button) getContentView().findViewById(R.id.cancel);
		button_cancel.setOnClickListener(clickListener);
	}
	
	public void setContents(String contents)
	{
		EditText editText = (EditText) getContentView().findViewById(R.id.edit_text);
		editText.setText(contents);
	}
	
	public void setHint(String contents)
	{
		EditText editText = (EditText) getContentView().findViewById(R.id.edit_text);
		editText.setHint(contents);
	}
	
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	
	public String getContents()
	{
		return content;
	}
	
	private String getEditText()
	{
		EditText editText = (EditText) getContentView().findViewById(R.id.edit_text);
		String text = editText.getText().toString();
		
		return text;
	}
	
	OnClickListener clickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.ok:
				
				content = getEditText();
				bEditState = EDITSTATE_EDIT;
				Message message = new Message();
				message.what = bEditState;
				
				Bundle data = new Bundle();
				data.putString("content", content);
				message.setData(data);
				handler.sendMessage(message);
				break;
			case R.id.cancel:
				bEditState = EDITSTATE_CANCEL;
				message = new Message();
				message.what = bEditState;
				handler.sendMessage(message);
				break;
			default:
				break;
			}
			dismiss();
		}
	};
	
	public int getEditState()
	{
		return bEditState;
	}
	
	public void show(View parent, int x, int y)
	{
		showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
	}
	
	OnKeyListener keyListener = new OnKeyListener()
	{
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			if ((keyCode == KeyEvent.KEYCODE_BACK))
			{
				dismiss();
				return true;
			}
			return false;
		}
	};
}
