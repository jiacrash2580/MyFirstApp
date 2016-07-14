package com.tri.myfirstapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.tri.myfirstapp.util.SmartUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DisplayMessageActivity extends AppCompatActivity {

    private TextView textView = null;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        msg = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);
        textView = new TextView(this);
        textView.setTextSize(40);
        setContentView(textView);
        new loginAsyncTask().execute(msg);
    }

    class loginAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg)
        {
            try
            {
                return SmartUtil.httpGet(arg[0]);
            } catch (Exception e)
            {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            textView.setText(result);
        }
    }
}
