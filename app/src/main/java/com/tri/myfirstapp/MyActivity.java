package com.tri.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.tri.myfirstapp.extra_msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    public void btnClick(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.text_id);
        String msg = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, "http://210.74.194.118:8082/gw-web-manager/gws/sysLogin?authToken=eXVud2VpfDc2RDgwMjI0NjExRkM5MTlBNUQ1NEYwRkY5RkJBNDQ2");
        startActivity(intent);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
