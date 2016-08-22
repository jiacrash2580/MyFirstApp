package com.infrastructure.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.example.androidlib.R;
import com.infrastructure.net.RequestCallback;

public abstract class AppBaseActivity extends BaseActivity {

    protected ProgressDialog dlg;

    public abstract class AbstractRequestCallback
            implements RequestCallback {

        public abstract void onSuccess(String content);

        public void showDlg()
        {
            if (dlg == null)
            {
                dlg = new ProgressDialog(AppBaseActivity.this);
                dlg.setMessage(AppBaseActivity.this.getString(R.string.str_loading));
                dlg.setCanceledOnTouchOutside(false);
            }
            dlg.show();
        }

        public void onResult(String content)
        {
            if (dlg != null)
            {
                dlg.dismiss();
            }
            onSuccess(content);
        }

        public void onFail(String errorMessage)
        {
            if (dlg != null)
            {
                dlg.dismiss();
            }
            new AlertDialog.Builder(AppBaseActivity.this).setTitle("提示").setMessage(errorMessage).setPositiveButton("确定", null).show();
        }
    }
}

