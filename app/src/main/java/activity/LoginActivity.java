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
import com.tri.myfirstapp.R;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import util.SmartUtil;
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

                //创建观察者对象，并且声明观察者的业务逻辑，这里的String泛型表示的OnSubscribe类的call方法传递给subscriber的onNext方法的对象类型，在下面会详细提到
                mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<String>() {

                    @Override
                    public void call(final Subscriber<? super String> subscriber)
                    {
                        try
                        {
                            //这里是与后台服务交互的开始，参数subscriber是用来接收交互结果的订阅者形参，与交互本身其实并没有关系
                            //按照结构构造，生成TOKEN
                            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            String pwdmd5 = StringUtils.upperCase(DigestUtils.md5Hex(pwd));
                            String token = new StringBuilder(account).append("|").append(pwdmd5).append("|").append(tm.getLine1Number()).toString();
                            token = new String(Base64.encodeBase64(token.getBytes()));
                            //获取sysLogin名称配置的url连接，url配置文件在res/xml/url.xml中配置
                            Map<String, String> urlData = UrlConfigManager.findURL(LoginActivity.this, "sysLogin");
                            //构造请求参数
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("authToken", token);
                            //调用封装好的okHttp的get方法，返回Call对象
                            final Call call = SmartUtil.okHttpGet(urlData.get("url"), params);
                            //这里是将call的引用注册到订阅者，使订阅者在取消订阅的时候，可以触发call的cancel方法，关闭未结束的连接，防止占用线程。
                            //(订阅者取消订阅会在activity的ondestroy时被管理订阅者关系的管理器调用取消订阅方法，从而触发关闭okHttp连接)
                            //每个call都要单独注册
                            subscriber.add(new SmartUtil.Subscription(call));
                            //调用call异步发送请求，并定义回调方法内容
                            call.enqueue(new SmartUtil.Callback(subscriber){
                                @Override
                                public void onResponse(Response response) throws IOException
                                {
                                    //这里的body就是后台服务返回的json字符串了，或者如果是下载文件的话，可以对response有不同的处理，这里是登录功能，所以返回json字符串。
                                    //这调用订阅者的onNext方法，表示对后台的请求正常返回，并进入下一步进行处理。
                                    // 这里onNext方法的参数类型，就是开始创建new Observable.OnSubscribe<String>()时的泛型，也就是说这个泛型代表第一步后台通讯后的返回结果类型
                                    subscriber.onNext(response.body().string());
                                }
                            });
                        } catch (IOException e)
                        {
                            //如果期间出现异常，那么可以调用订阅者的onError方法，参数是Exception，那么可以在后面定义订阅者的时候统一对异常进行处理，当然如果这里没有catch到异常
                            //直接出现了runTime异常，抛出到方法外的，那么没关系，框架本身也会自动拦截，并且在下面的订阅者中统一回调，让你处理，具体位置下面再说
                            //（我这里写catch是因为，上面有一个地方显示抛出异常，并且这个重载的call方法没有定义抛出异常的声明，所以只能显示的try catch了）
                            subscriber.onError(new Exception("网络无法连接"));
                        }
                    }
                })
                        //这里是配置观察者要在io线程中执行，也就是上面的OnSubscribe类的代码，以及下面的map中的回调Func1中的代码都是与ui无关的代码，都在io线程的范畴内
                        .subscribeOn(Schedulers.io())
                        //这里是配置订阅者要在ui线程中执行，因为涉及页面跳转等ui操作。
                        .observeOn(AndroidSchedulers.mainThread())
                        //这一步的map可有可无，主要就是演示一下map方法的用法，是用来处理加工返回值的。以便ui线程用起来方便
                        //Func1是框架提供的表示一个参数，有返回值的回调类
                        .map(new Func1<String, Map>() {
                            @Override
                            public Map call(String s)
                            {
                                //参数是String类型，这里要和上边观察者onNext时的参数类型相同，因为那个的输入，就是这个参数，这里面做了json反序列化，输出map
                                return JSON.parseObject(s, Map.class);
                            }
                        })
                        //这是定义订阅者的内容，泛型是上边最后一个类似map方法的操作符类型方法的输出一致。上边的输出是这里的输入
                        //这里订阅者是在ui线程中执行，其中包括3个重写的方法，其中onError是上边一直提到的异常处理，包括开发人员显示调用的subscriber.onError,和没有捕获抛出的异常
                        //onNext承接上边的输出map数据
                        .subscribe(new Subscriber<Map>() {
                            @Override
                            public void onCompleted()
                            {
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(Map reObj)
                            {
                                //对返回值进行处理
                                String rel = (String) reObj.get("ret");
                                if (StringUtils.equalsIgnoreCase(rel, "faild"))
                                {
                                    //登陆失败
                                    Toast.makeText(LoginActivity.this, (String) reObj.get("errorMsg"), Toast.LENGTH_SHORT).show();
                                    return;
                                } else
                                {
                                    //存储到全局变量中
                                    SharedPreferences.Editor spEditor = getSharedPreferences("global", Activity.MODE_PRIVATE).edit();
                                    spEditor.putString("token", rel);
                                    spEditor.putString("roleIds", (String) reObj.get("roleIds"));
                                    spEditor.putString("realName", (String) reObj.get("username"));
                                    spEditor.commit();

                                    //登陆成功跳转到下一个页面
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //结束本activity
                                    finish();
                                }
                            }
                        }));
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
