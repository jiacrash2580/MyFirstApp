package com.foxit.popupmenu;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tri.myfirstapp.R;

public class PopupMenu extends PopupWindow
{
	public static final String TAG = "PopupMenu";
	
	private Context context = null;
	private int nScreenWidth = 0;
	private int nScreenHeight = 0;
	private ListView menuListView;
	private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	private OnItemOnClickListener itemOnClickListener;
	private int LEFT_BOTTOM = 1;
	private int RIGHT_BOTTOM = 2;
	
	public static interface OnItemOnClickListener
	{
		public void onItemClick(MenuItem item , int position);
	}
	
	public PopupMenu(Context context)
	{
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public PopupMenu(Context context, int width, int height)
	{
		this.context = context;
		
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		
		WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		nScreenWidth = display.getWidth();
		nScreenHeight = display.getHeight();
		
		setWidth(width);
		setHeight(height);
		
		setContentView(LayoutInflater.from(context).inflate(R.layout.popup_menu, null));
		initialize();
	}
	
	
	private void initialize()
	{
		menuListView = (ListView) getContentView().findViewById(R.id.popupmenu_list);
		menuListView.setOnItemClickListener(new  OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				dismiss();
				
				if (itemOnClickListener != null)
				{
					itemOnClickListener.onItemClick(menuItems.get(position), position);
				}
			}
		});
	}
	
	public void addMenuItem(MenuItem menuItem)
	{
		if (menuItem != null)
		{
			menuItems.add(menuItem);
		}
	}
	
	public MenuItem getMenuItem(int position)
	{
		if (position < 0 || position > menuItems.size())
			return null;
		return menuItems.get(position);
	}
	
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.itemOnClickListener = onItemOnClickListener;
	}
	
	public void inflateMenuItems()
	{
		menuListView.setAdapter(new BaseAdapter()
		{
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				TextView textView = null;
				if (convertView == null)
				{
					textView = new TextView(context);
					textView.setSingleLine(true);
					textView.setTextColor(context.getResources().getColor(android.R.color.white));
					textView.setTextSize(15);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(0, 10, 0, 10);
				}
				else {
					textView = (TextView) convertView;
				}
				
				MenuItem menuItem = menuItems.get(position);
				
				textView.setText(menuItem.getItem());
				
				return textView;
			}
			
			@Override
			public long getItemId(int position)
			{
				return position;
			}
			
			@Override
			public Object getItem(int position)
			{
				return menuItems.get(position);
			}
			
			@Override
			public int getCount()
			{
				// TODO Auto-generated method stub
				return menuItems.size();
			}
		});
	}
	
	public void show(View parent, int position)
	{
		Rect rect = new Rect();
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		rect.set(location[0], location[1], location[0] + parent.getWidth(), location[1] + parent.getHeight());
		
		if (!this.isShowing())
		{
			if (position == RIGHT_BOTTOM)
			{
				showAtLocation(parent, Gravity.NO_GRAVITY, nScreenWidth - (getWidth()/2), rect.bottom + 6);
			}
			else if(position == LEFT_BOTTOM){
				showAtLocation(parent, Gravity.NO_GRAVITY, 0, rect.bottom + 6);
			}
			else {
				showAtLocation(parent, Gravity.NO_GRAVITY, getWidth()*3, rect.bottom + 6);
			}
		}
		else {
			dismiss();
		}
	}
	
	public void show(View parent, int x, int y)
	{
		if (!this.isShowing())
		{
			showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
		}
		else {
			dismiss();
		}
	}
}
