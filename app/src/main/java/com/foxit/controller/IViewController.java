package com.foxit.controller;

import android.app.Activity;

import com.foxit.gsdk.PDFException;

public interface IViewController
{
	public static final String TAG = "IViewController";
	
	public abstract boolean initialize();
	public abstract void openDocument(String path, byte[] password) throws PDFException;
	public abstract void setPageLayoutMode(int layoutMode);
	
	//Searching text on text pdf document
	public abstract void startSearch(Activity parentActivity, final String target);
	public abstract void searchPrev();
	public abstract void searchNext();
}
