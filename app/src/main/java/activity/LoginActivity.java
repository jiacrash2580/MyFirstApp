package activity;

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
import com.infrastructure.activity.AppBaseActivity;
import com.infrastructure.net.DefaultThreadPool;
import com.infrastructure.net.HttpRequest;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.net.URLData;
import com.infrastructure.net.UrlConfigManager;
import com.tri.myfirstapp.R;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppBaseActivity {
    private EditText txtAccount = null;
    private EditText txtPassword = null;
    private Button btnLogin = null;

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_my);
        findViews();
        addClickListner();
    }

    @Override
    protected void loadData()
    {
    }

    private void addClickListner()
    {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String account = txtAccount.getText().toString();
                String pwd = txtPassword.getText().toString();
                if (StringUtils.isBlank(account) || StringUtils.isBlank(pwd))
                {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                //生成TOKEN
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                pwd = StringUtils.upperCase(DigestUtils.md5Hex(pwd));
                String token = new StringBuilder(account).append("|").append(pwd).append("|").append(tm.getLine1Number()).toString();
                token = new String(Base64.encodeBase64(token.getBytes()));

                //发请求，接收结果并处理返回
                Map<String, String> params = new HashMap<String, String>();
                params.put("authToken", token);
                final URLData urlData = UrlConfigManager.findURL(LoginActivity.this, "sysLogin");
                HttpRequest request = LoginActivity.this.getRequestManager().createRequest(urlData, params, new AbstractRequestCallback() {
                    @Override
                    public void onSuccess(String content)
                    {
                        Map reObj = JSON.parseObject(content, Map.class);
                        String rel = (String) reObj.get("ret");
                        if (StringUtils.equalsIgnoreCase(rel, "faild"))
                        {
                            //登陆失败
                            Toast.makeText(LoginActivity.this, (String) reObj.get("errorMsg"), Toast.LENGTH_SHORT).show();
                            return;
                        } else
                        {
                            SharedPreferences sp = getSharedPreferences("global", LoginActivity.MODE_PRIVATE);
                            sp.edit().putString("token", rel);
                            sp.edit().putString("roleIds", (String) reObj.get("roleIds"));
                            sp.edit().putString("realName", (String) reObj.get("username"));
                            //登陆成功
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, true);
                DefaultThreadPool.getInstance().execute(request);
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
