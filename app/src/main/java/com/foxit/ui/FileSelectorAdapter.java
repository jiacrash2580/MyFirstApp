package com.foxit.ui;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tri.myfirstapp.R;

public class FileSelectorAdapter extends BaseAdapter
{
	public static final String TAG = "FileSelectorAdapter";
	private LayoutInflater mInflater;
	private Bitmap homeIcon = null;
	private Bitmap superFloderIcon = null;
	private Bitmap floderIcon = null;
	private Bitmap fileIcon = null;
	private Bitmap othersIcon = null;
	private List<String> items;
	private List<String> paths;
	private class ViewHolder
	{
		TextView text;
		ImageView icon;
	}
	
	public FileSelectorAdapter(Context context, List<String> items, List<String> paths)
	{
		mInflater = LayoutInflater.from(context);
		this.items = items;
		this.paths = paths;
		
	    homeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.rv_homeback);
	    superFloderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fm_superfolder);
	    floderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fm_folder_icon);
	    fileIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fm_default_pdf_icon);
	    othersIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fm_file_others);
	}
	
	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.file_list, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.text = (TextView) convertView.findViewById(R.id.fileName);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		File file = new File(paths.get(position).toString());
		if (items.get(position).toString().equals("backHome"))
		{
		    holder.text.setText("Back Home..");//返回根目录
		    holder.icon.setImageBitmap(homeIcon);
		}
		else if (items.get(position).toString().equals("backSuper"))
		{
		    holder.text.setText("Back Super..");//返回上一级目录
		    holder.icon.setImageBitmap(superFloderIcon);
		}
		else 
		{
			
			if(file.isDirectory() && !file.getName().startsWith("."))
			{
				holder.text.setText(file.getName());
				holder.icon.setImageBitmap(floderIcon);
			}
			else {
				if (file.getName().endsWith(".fdf") || file.getName().endsWith(".pdf") || file.getName().endsWith(".bmp") ||
						file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jp2")
						|| file.getName().endsWith(".tiff") || file.getName().endsWith(".jb2") || file.getName().endsWith(".tif") 
						|| file.getName().endsWith(".jpx") || file.getName().endsWith(".gif") || file.getName().endsWith(".jpeg") 
					)
				{
					holder.text.setText(file.getName());
					holder.icon.setImageBitmap(fileIcon);
				}
				/*else {
					holder.icon.setImageBitmap(othersIcon);
				}*/
			}
		}
		return convertView;
	}

}
