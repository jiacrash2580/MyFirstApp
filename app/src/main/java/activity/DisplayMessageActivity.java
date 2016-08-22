package activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.R;
import com.tri.myfirstapp.fragment.DetailMessage;
import com.tri.myfirstapp.fragment.ListMessage;
import com.tri.myfirstapp.util.SmartUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMessageActivity extends Activity implements ListMessage.ItemClickCallBack{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_message);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String token = intent.getStringExtra("token");
        String pageNum = intent.getStringExtra("pageNum");
        String pageSize = intent.getStringExtra("pageSize");
        String status = intent.getStringExtra("status");
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("token", token);
        args.putString("pageNum", pageNum);
        args.putString("pageSize", pageSize);
        args.putString("status", status);

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        ListMessage lm = new ListMessage();
        lm.setArguments(args);
        trans.add(R.id.frameLayout, lm);
        trans.commit();
    }

    @Override
    public void onItemClick(String id)
    {
        Bundle args = new Bundle();
        args.putString("id", id);
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        DetailMessage dm = new DetailMessage();
        dm.setArguments(args);
        trans.replace(R.id.frameLayout, dm);
        trans.addToBackStack(null);
        trans.commit();
    }
}
