package activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.tri.myfirstapp.R;
import com.tri.myfirstapp.fragment.PdfFragment;
import com.tri.myfirstapp.util.ResourceDownloader;

import java.io.File;

/**
 * Created by selim_tekinarslan on 10.10.2014.
 */
public class PdfViewActivity extends Activity {
    private static final String TAG = "PdfViewActivity";
    private static final String SAMPLE_FILE = "foxit.pdf";
    private static final String FILE_PATH = "filepath";
    private static final String SEARCH_TEXT = "text";
    private PdfFragment fragment;
    private static Context context;
    private File pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        context = PdfViewActivity.this;
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        downloadPDF(url);
    }

    private void downloadPDF(String url) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_custom_progress);
        dialog.show();


        new ResourceDownloader(context, new ResourceDownloader.DownloadCallback() {
            @Override
            public void onProgressUpdated(long curr, long total) {
            }

            @Override
            public void onCompleted(boolean success, File file) {
                if(success){
                    dialog.dismiss();
                    pdf = file;
                    openPdfWithFragment(file);

                }
            }
        }).start(url);
    }

    public void openPdfWithFragment(File file) {
        fragment = new PdfFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH, file.getAbsolutePath() );
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdf != null) {
            pdf.delete();
        }
    }
}
