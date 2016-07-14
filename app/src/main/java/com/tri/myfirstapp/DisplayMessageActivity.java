package com.tri.myfirstapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.util.SmartUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMessageActivity extends AppCompatActivity {

    private ListView list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_test);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String token = intent.getStringExtra("token");
        String pageNum = intent.getStringExtra("pageNum");
        String pageSize = intent.getStringExtra("pageSize");
        String status = intent.getStringExtra("status");
        list = (ListView)findViewById(R.id.list1);

        new loginAsyncTask().execute(url, token, pageNum, pageSize, status);
    }

    class loginAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg)
        {
            try
            {
                Map params = new HashMap();
                params.put("token", arg[1]);
                params.put("pageNum", arg[2]);
                params.put("pageSize", arg[3]);
                params.put("status", arg[4]);
                return SmartUtil.httpGet(arg[0], params);
            } catch (Exception e)
            {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Map reObj = JSON.parseObject(result, Map.class);
            List<Map<String, String>> infoList = (List<Map<String, String>>)reObj.get("fileExpList");
            SimpleAdapter sa = new SimpleAdapter(DisplayMessageActivity.this,infoList, R.layout.listview_item, new String[]{"TITLE", "LEADERID", "SENDTIME"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
            list.setAdapter(sa);
        }
    }
}
