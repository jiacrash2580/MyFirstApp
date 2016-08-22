package com.foxit.task;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.foxit.bean.CurrentState;
import com.foxit.bean.PDFContext;
import com.foxit.gsdk.PDFException;
import com.foxit.gsdk.pdf.PDFPage;
import com.foxit.gsdk.pdf.Progress;
import com.foxit.gsdk.pdf.RenderContext;
import com.foxit.gsdk.pdf.Renderer;
import com.foxit.gsdk.pdf.annots.Annot;
import com.foxit.ui.FoxitViewActivity;

public class DrawSinglePageBitmapTask extends AbstractTask {
    public static final String TAG = "DrawSinglePageBitmapTask";

    private PDFContext pdfContext = null;
    private Bitmap bitmap = null;

    public DrawSinglePageBitmapTask(PDFContext pdfContext)
    {
        this.pdfContext = pdfContext;
        this.taskType = TYPE_DRAWSINGLEBITMAPPAGE;
    }

    @Override
    public boolean execute()
    {
        if (pdfContext != null || pdfContext.page != null)
        {
            CurrentState currentState = pdfContext.getCurrentState();

            calculateRenderSize(currentState.rotation);
            try
            {
                Matrix matrix = pdfContext.page.getDisplayMatrix(0, 0, currentState.display_width, currentState.display_height, currentState.rotation);
                matrix.postScale(currentState.scale, currentState.scale);
                if (pdfContext.pageBitmap != null)
                {
                    bitmap = pdfContext.pageBitmap;
                } else
                    bitmap = Bitmap.createBitmap((int) (currentState.screenWidth * 2), (int) (currentState.screenHeight * 2), Config.ARGB_8888);
                if (!currentState.bRenderAnnot)
                    bitmap.eraseColor(Color.WHITE);

                Renderer renderer = Renderer.create(bitmap);
                RenderContext renderContext = RenderContext.create();
                renderContext.setFlags(RenderContext.FLAG_ANNOT);
                renderContext.setMatrix(matrix);
                Progress progress = null;
                if (currentState.bRenderAnnot)
                {
                    int count = pdfContext.page.countAnnots(null);
                    Annot[] annots = new Annot[1];
                    annots[0] = pdfContext.page.getAnnot(null, count - 1);
                    progress = pdfContext.page.startRenderAnnots(renderContext, renderer, annots);
                    pdfContext.getCurrentState().bRenderAnnot = false;
                } else
                {
                    progress = pdfContext.page.startRender(renderContext, renderer, PDFPage.RENDERFLAG_NORMAL);
                }

                if (progress != null)
                {
                    progress.continueProgress(0);
                    progress.release();
                }

                renderContext.release();
                renderer.release();

                pdfContext.pageBitmap = bitmap;
                pdfContext.getCurrentState().bDrawable = true;

            } catch (PDFException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, FoxitViewActivity.generateMsg(e.getLastError(), "Failed to draw single page!!!", true));
                return false;
            }
        }
        return true;
    }

    private void calculateRenderSize(int rotation)
    {
        CurrentState currentState = pdfContext.getCurrentState();
        float scale1 = 0;
        float scale2 = ((float) currentState.screenWidth / (float) currentState.screenHeight);
        if (rotation == PDFPage.ROTATION_90 || rotation == PDFPage.ROTATION_270)
        {
            scale1 = (currentState.pageSize.getHeight() / currentState.pageSize.getWidth());
            if (scale1 > scale2)
            {
                currentState.display_width = currentState.screenWidth;
                currentState.display_height = (int) ((currentState.screenWidth / currentState.pageSize.getHeight()) * currentState.pageSize.getWidth());
            } else
            {
                currentState.display_height = currentState.screenHeight;
                currentState.display_width = (int) ((currentState.screenHeight / currentState.pageSize.getWidth()) * currentState.pageSize.getHeight());
            }
        } else
        {
            scale1 = (currentState.pageSize.getWidth() / currentState.pageSize.getHeight());
            if (scale1 > scale2)
            {
                currentState.display_width = currentState.screenWidth;
                currentState.display_height = (int) ((currentState.screenWidth / currentState.pageSize.getWidth()) * currentState.pageSize.getHeight());
            } else
            {
                currentState.display_height = currentState.screenHeight;
                currentState.display_width = (int) ((currentState.screenHeight / currentState.pageSize.getHeight()) * currentState.pageSize.getWidth());
            }
        }
        int j=0;
        j++;
    }

}
