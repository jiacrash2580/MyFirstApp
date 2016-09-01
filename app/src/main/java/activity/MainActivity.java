package activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.tri.myfirstapp.R;
import util.SmartUtil;

/**
 * Created by aaa on 2016/8/9.
 */
public class MainActivity extends AppCompatActivity {
    private ImageButton btn_main1 = null;
    private ImageButton btn_main2 = null;
    private ImageButton btn_main3 = null;
    private ImageButton btn_main4 = null;
    private ImageButton btn_main5 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initAnimator();
        initBtnClickListener();
    }

    private void initBtnClickListener()
    {
        btn_main4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                intent.putExtra("pageNum", "1");
                intent.putExtra("pageSize", "-1");
                intent.putExtra("status", "1");
                startActivity(intent);
            }
        });
    }

    private void findViews()
    {
        btn_main1 = (ImageButton) findViewById(R.id.btn_main1);
        btn_main2 = (ImageButton) findViewById(R.id.btn_main2);
        btn_main3 = (ImageButton) findViewById(R.id.btn_main3);
        btn_main4 = (ImageButton) findViewById(R.id.btn_main4);
        btn_main5 = (ImageButton) findViewById(R.id.btn_main5);
    }

    private void initAnimator()
    {
        runAnimatorToView(btn_main1, R.animator.btn_main1, R.id.txt_main1);
        runAnimatorToView(btn_main2, R.animator.btn_main2, R.id.txt_main2);
        runAnimatorToView(btn_main3, R.animator.btn_main3, R.id.txt_main3);
        runAnimatorToView(btn_main4, R.animator.btn_main4, R.id.txt_main4);
        runAnimatorToView(btn_main5, R.animator.btn_main5, R.id.txt_main5);
    }

    private void runAnimatorToView(final View view, int animatorId, final int viewTextId)
    {
        ValueAnimator ani = (ValueAnimator) AnimatorInflater.loadAnimator(MainActivity.this, animatorId);
        ani.addUpdateListener(new btnAnimatorUpdater(view));
        ani.start();
        ani.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                View viewText = findViewById(viewTextId);
                viewText.setVisibility(View.VISIBLE);
                viewText.setX(view.getX() + view.getWidth() / 2 - viewText.getWidth() / 2);
                viewText.setY(view.getY() - viewText.getHeight());
            }
        });
    }

    class btnAnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
        View view = null;
        final float centerX = SmartUtil.dip2px(MainActivity.this, 446);
        final float centerY = SmartUtil.dip2px(MainActivity.this, 540);
        final float rLength = SmartUtil.dip2px(MainActivity.this, 395);

        public btnAnimatorUpdater(View view)
        {
            this.view = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation)
        {
            float value = (Integer) animation.getAnimatedValue();
            float x = (float) Math.cos((value / 180f) * Math.PI) * rLength + centerX;
            float y = (float) Math.sin((value / 180f) * Math.PI) * rLength + centerY;
            view.setX(x);
            view.setY(y);
        }
    }
}
