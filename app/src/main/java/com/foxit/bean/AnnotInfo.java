package com.foxit.bean;

import android.graphics.Color;
import android.graphics.RectF;

import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.DefaultAppearance;
import com.foxit.gsdk.pdf.FontManager;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.action.PDFAction;
import com.foxit.gsdk.pdf.action.PDFURIAction;
import com.foxit.gsdk.pdf.annots.Annot;
import com.foxit.gsdk.pdf.annots.Annot.Border;
import com.foxit.gsdk.pdf.annots.Annot.QuadpointsF;
import com.foxit.gsdk.pdf.annots.FreeText;
import com.foxit.gsdk.pdf.annots.Highlight;
import com.foxit.gsdk.pdf.annots.Link;
import com.foxit.gsdk.pdf.annots.Text;
import com.foxit.gsdk.pdf.annots.UnderLine;

public class AnnotInfo
{
	//operation type
	public static final int TYPE_UNSUPPOTED = -1;
	public static final int TYPE_ADD 		= 0;
	public static final int TYPE_EDIT 		= 1;
	public static final int TYPE_DELETE 	= 2;
	
	private int operationType = -1;
	private Annot annot = null;
	private String annotType = null;
	private QuadpointsF[] quadpointsFs = null;
	
	public AnnotInfo(Annot annot, int operationType)
	{
		this.annot = annot;
		this.operationType = operationType;
	}
	
	public AnnotInfo(String annotType, int operationType)
	{
		this.annotType = annotType;
		this.operationType = operationType;
	}
	
	public Annot getAnnot()
	{
		return annot;
	}
	
	public void setOperationType(int operationType)
	{
		this.operationType = operationType;
	}
	
	public int getOperationType()
	{
		return operationType;
	}
	
	public String getAnnotType()
	{
		if (annot != null)
		{
			try
			{
				annotType = annot.getType();
			}
			catch (PDFException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return annotType;
	}
	
	public void setAnnotProperty(Annot annot, String annotType)
	{
		if (annot == null || annotType == null || annotType.length() < 1)
			return;
		setCommonProperty(annot, annotType);
		if (annotType.contentEquals(Annot.TYPE_HIGHLIGHT))
		{
			setHighlightProperty((Highlight)annot);
		}
		else if (annotType.contentEquals(Annot.TYPE_UNDERLINE))
		{
			setUnderlineProperty((UnderLine)annot);
		}
		else if (annotType.contentEquals(Annot.TYPE_LINK))
		{
			setLinkProperty((Link)annot);
		}
		else if (annotType.contentEquals(Annot.TYPE_FREETEXT))
		{
			setFreeTextProperty((FreeText)annot);
		}
		else if (annotType.contentEquals(Annot.TYPE_TEXT))
		{
			setNoteProperty((Text)annot);
		}
	}
	
	private void setCommonProperty(Annot annot, String annotType)
	{
		try
		{
			annot.setBorderColor(Color.YELLOW);
			/*Border border = new Border();
			border.setCloudIntensity(-1);
			border.setWidth((float) 2.0);
			float[] dashPttern = new float[]{(float) 2.0, (float) 1.0};
			border.setDashPattern(dashPttern);
			border.setDashPhase(5);
			annot.setBorder(border);*/
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//add Highlight & underline annotation need set QuadPoints first.
	public void setQuadPoints(QuadpointsF[] quadPoints)
	{
		quadpointsFs = quadPoints;
	}
	
	private void setHighlightProperty(Highlight highlight)
	{
		try
		{
			if (quadpointsFs == null) return;
			highlight.setQuadPoints(quadpointsFs);
			highlight.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setUnderlineProperty(UnderLine underLine)
	{
		try
		{
			if (quadpointsFs == null) return;
			underLine.setQuadPoints(quadpointsFs);
			underLine.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAction(Link link, String url)
	{
		PDFURIAction action;
		try
		{
			action = (PDFURIAction) PDFAction.createURIAction(url, false);
			link.insertAction(Link.TRIGGER_ANNOT_MU, 0, action);
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void setLinkProperty(Link link)
	{
		try
		{
			Border border = new Border();
			border.setWidth(2);
			link.setBorder(border);
			link.setBorderColor(Color.RED);
			link.setHighLightingMode(Link.HIGHLIGHTINGMODE_OUTLINE);
			link.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setFreeTextProperty(FreeText freeText)
	{
		try
		{
			freeText.setAlignment(0);
			freeText.setIntent(FreeText.INTENTNAME_FREETEXT_TYPEWRITER);
			freeText.setOpacity((float) 1.0);
			DefaultAppearance defaultAppearance = new DefaultAppearance();
			defaultAppearance.textColor = 0x000000FF;
			defaultAppearance.flags = DefaultAppearance.DA_TEXTCOLOR | DefaultAppearance.DA_FONT;
			defaultAppearance.font = FontManager.createStandard(FontManager.STDFONT_COURIER);
			defaultAppearance.fontSize = 15;
			defaultAppearance.textMatrix = null;
			//defaultAppearance.
			freeText.setDefaultAppearance(defaultAppearance);
			freeText.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setNoteProperty(Text text)//text
	{
		try
		{
			text.setIconName(Text.ICONNAME_COMMENT);
			text.setOpacity((float) 1.0);
			text.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteAnnot(PDFPage page, Annot annot)
	{
		if (annot == null)
			return;
		
		try
		{
			page.removeAnnot(annot);
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getAnnotType(Annot annot)
	{
		if (annot == null)
		{
			return null;
		}
		
		String type = null;
		try
		{
			type = annot.getType();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return type;
	}
	
	public void editAnnot(Annot annot, String contents)
	{
		if (annot == null)
			return;
		String type = getAnnotType(annot);
		
		if (type.contentEquals(Annot.TYPE_FREETEXT))
		{
			editFreeText((FreeText)annot, contents);
		}
		else if (type.contentEquals(Annot.TYPE_TEXT)) 
		{
			editNote((Text)annot, contents);
		}
	}
	
	private void editFreeText(FreeText freeText, String contents)
	{
		try
		{
			freeText.setContents(contents);
			freeText.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void editNote(Text text, String contents)//text
	{
		try
		{
			text.setContents(contents);
			text.resetAppearance();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//only support FreeText and Text annotations now.
	public String getAnnotContents(Annot annot)
	{
		if (annot == null)
			return null;
		
		String type = getAnnotType(annot);
		String contents = null;
		
		if (type.contentEquals(Annot.TYPE_FREETEXT))
		{
			contents = getFreeTextContents((FreeText)annot);
		}
		else if (type.contentEquals(Annot.TYPE_TEXT)) 
		{
			contents = getNoteContents((Text)annot);
		}
		
		return contents;
	}
	
	private String getFreeTextContents(FreeText freeText)
	{
		String contents = null; 
		try
		{
			contents = freeText.getContents();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}
	
	private String getNoteContents(Text text)//text
	{
		String contents = null;
		try
		{
			contents = text.getContents();
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}
	
	public Annot addAnnot(PDFPage page, RectF rectf, String annotType, String filter, int index)
	{
		Annot annot = null;
		try
		{
			annot = page.addAnnot(rectf, annotType, filter, index);
		}
		catch (PDFException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return annot;
	}
}
