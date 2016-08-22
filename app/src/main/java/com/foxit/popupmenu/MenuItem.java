package com.foxit.popupmenu;

import android.content.Context;

public class MenuItem
{
	private CharSequence item;
	
	public MenuItem(CharSequence item)
	{
		this.item = item;
	}
	
	public MenuItem(Context context, int itemID)
	{
		this.item = context.getResources().getText(itemID);
	}
	
	public void setItem(CharSequence item)
	{
		this.item = item;
	}
	public CharSequence getItem()
	{
		return item;
	}
}
