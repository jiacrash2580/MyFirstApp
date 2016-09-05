package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.mobile.baselib.RxOkHttp.RxFunc1;
import com.tri.mobile.baselib.RxOkHttp.RxOkHttpUtil;
import com.tri.mobile.baselib.RxOkHttp.RxSubscriber;
import com.tri.myfirstapp.R;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import rx.subscriptions.CompositeSubscription;
import util.UrlConfigManager;

public class LoginActivity extends AppCompatActivity {
    private EditText txtAccount = null;
    private EditText txtPassword = null;
    private Button btnLogin = null;
    //这个是用来管理监听者和观察者关系的管理对象，用来在activity的onDestroy时，取消订阅者关系，释放资源，从而触发取消未结束的okhttp连接。
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //获取组件引用
        findViews();
        //主要看这个方法，添加登录按钮点击事件
        addClickListner();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //结束所有添加到其中的订阅者的观察关系
        mCompositeSubscription.unsubscribe();
    }

    /**
     * 添加监听登录按钮点击的方法
     */
    private void addClickListner()
    {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String account = StringUtils.trim(txtAccount.getText().toString());
                final String pwd = StringUtils.trim(txtPassword.getText().toString());
                //准备输入的账号密码
                if (StringUtils.isBlank(account) || StringUtils.isBlank(pwd))
                {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT);
                    return;
                }

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String pwdmd5 = StringUtils.upperCase(DigestUtils.md5Hex(pwd));
                String token = new StringBuilder(account).append("|").append(pwdmd5).append("|").append(tm.getLine1Number()).toString();
                token = new String(Base64.encodeBase64(token.getBytes()));
                //获取sysLogin名称配置的url连接，url配置文件在res/xml/url.xml中配置
                Map<String, String> urlData = UrlConfigManager.findURL(LoginActivity.this, "sysLogin");
                //构造请求参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("authToken", token);
                RxOkHttpUtil.okHttpGet(mCompositeSubscription, urlData.get("url"), params, new RxFunc1<Response, Map>() {
                    @Override
                    public Map call(Response response) throws IOException
                    {
                        return JSON.parseObject(response.body().string(), Map.class);
                    }
                }, new RxSubscriber<Map>() {
                    @Override
                    public void onError(Throwable e)
                    {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Map map)
                    {
                        //对返回值进行处理
                        String rel = (String) map.get("ret");
                        if (StringUtils.equalsIgnoreCase(rel, "faild"))
                        {
                            //登陆失败
                            Toast.makeText(LoginActivity.this, (String) map.get("errorMsg"), Toast.LENGTH_SHORT).show();
                            return;
                        } else
                        {
                            //存储到全局变量中
                            SharedPreferences.Editor spEditor = getSharedPreferences("global", Activity.MODE_PRIVATE).edit();
                            spEditor.putString("token", rel);
                            spEditor.putString("roleIds", (String) map.get("roleIds"));
                            spEditor.putString("realName", (String) map.get("username"));
                            spEditor.commit();

                            //登陆成功跳转到下一个页面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            //结束本activity
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void findViews()
    {
        txtAccount = (EditText) findViewById(R.id.txtAccount);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

}
