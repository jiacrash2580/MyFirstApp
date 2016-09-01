package activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tri.myfirstapp.R;

import fragment.DetailMessage;
import fragment.ListMessage;

public class DisplayMessageActivity extends AppCompatActivity implements ListMessage.ItemClickCallBack {

    public SharedPreferences sp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("global", LoginActivity.MODE_PRIVATE);
        setContentView(R.layout.display_message);
        initCustomActionBar();
        addListFragment();
    }

    private void initCustomActionBar()
    {
        View view = getLayoutInflater().inflate(R.layout.custom_action_bar, null);
        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        final TextView backTitle = (TextView) view.findViewById(R.id.back_title);
        backTitle.setText(R.string.shouye);
        backTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(R.string.wenjian_sudi);

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ActionBar intentBar = getSupportActionBar();
        intentBar.setCustomView(view, layout);
        intentBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    private void addListFragment()
    {
        Intent intent = getIntent();
        String pageNum = intent.getStringExtra("pageNum");
        String pageSize = intent.getStringExtra("pageSize");
        String status = intent.getStringExtra("status");
        Bundle args = new Bundle();
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
