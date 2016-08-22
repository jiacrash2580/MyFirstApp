package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.foxit.ui.FoxitViewActivity;
import com.tri.myfirstapp.R;

import java.io.File;

import util.ResourceDownloader;

/**
 * Created by selim_tekinarslan on 10.10.2014.
 */
public class PdfViewActivity extends AppCompatActivity {
    private static final String TAG = "PdfViewActivity";
    private static final String SAMPLE_FILE = "foxit.pdf";
    private static final String FILE_PATH = "filepath";
    private static final String SEARCH_TEXT = "text";

    private Context context;
    private File pdf;
    private int PROGRESS_BAR_COUNT = 0x001;
    private ProgressBar pbar = null;
    private int pjj = 0;

    Handler barUpate = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == PROGRESS_BAR_COUNT){
                Log.d("receive msg","");
                pbar.setProgress(pjj);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pbar = (ProgressBar)findViewById(R.id.progressBar);
        context = PdfViewActivity.this;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        downloadPDF(url);
    }

    private void downloadPDF(String url)
    {
        new ResourceDownloader(context, new ResourceDownloader.DownloadCallback() {
            @Override
            public void onProgressUpdated(long curr, long total)
            {
                pjj = (int)Math.round(curr*100.0/total);
                Log.d("send msg","");
                barUpate.sendEmptyMessage(PROGRESS_BAR_COUNT);
            }

            @Override
            public void onCompleted(boolean success, File file)
            {
                if (success)
                {
                    pdf = file;
                    openPdfWithFragment(pdf);
                }
            }
        }).start(url);
    }

    public void openPdfWithFragment(File file)
    {
        Intent intent = new Intent();
        intent.putExtra("fileDir", file.getPath());
        intent.setClass(PdfViewActivity.this, FoxitViewActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pdf != null)
        {
            pdf.delete();
        }
    }
}
