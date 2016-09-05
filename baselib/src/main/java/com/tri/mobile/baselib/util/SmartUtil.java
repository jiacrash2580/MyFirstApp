package com.tri.mobile.baselib.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by aaa on 2016/7/12.
 */
public class SmartUtil {

    /**
     * 使用okHttp发送http的post请求
     *
     * @param url    请求地址，http协议的地址
     * @param params 请求表单参数
     * @return Call
     */
    public static Call okHttpPost(String url, Map<String, String> params)
    {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                bodyBuilder.add(key, params.get(key));
            }
        }
        OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Call call = httpClient.newCall(new Request.Builder().url(url).post(bodyBuilder.build()).build());
        return call;
    }

    /**
     * 使用okHttp发送http的get请求
     *
     * @param url    请求地址，http协议的地址
     * @param params 请求表单参数
     * @return Call
     */
    public static Call okHttpGet(String url, Map<String, String> params)
    {
        String urlStr = url;
        if (params != null && !params.isEmpty())
        {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (String key : params.keySet())
            {
                urlBuilder.addQueryParameter(key, params.get(key));
            }
            urlStr = urlBuilder.build().toString();
        }
        OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Call call = httpClient.newCall(new Request.Builder().url(urlStr).get().build());
        return call;
    }

    /**
     * 使用okHttp发送https的post请求
     *
     * @param url    请求地址，https协议的地址
     * @param params 请求表单参数
     * @param cert   SSL证书文件流
     * @return Call
     * @throws Exception
     */
    public static Call okHttpsPost(String url, Map<String, String> params, InputStream cert) throws Exception
    {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                bodyBuilder.add(key, params.get(key));
            }
        }
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS);
        setCertificates(okClientBuilder, cert);
        OkHttpClient httpClient = okClientBuilder.build();
        Call call = httpClient.newCall(new Request.Builder().url(url).post(bodyBuilder.build()).build());
        return call;
    }

    /**
     * 使用okHttp发送https的get请求
     *
     * @param url    请求地址，https协议的地址
     * @param params 请求表单参数
     * @param cert   SSL证书文件流
     * @return Call
     * @throws Exception
     */
    public static Call okHttpsGet(String url, Map<String, String> params, InputStream cert) throws Exception
    {
        String urlStr = url;
        if (params != null && !params.isEmpty())
        {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (String key : params.keySet())
            {
                urlBuilder.addQueryParameter(key, params.get(key));
            }
            urlStr = urlBuilder.build().toString();
        }
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS);
        setCertificates(okClientBuilder, cert);
        OkHttpClient httpClient = okClientBuilder.build();
        Call call = httpClient.newCall(new Request.Builder().url(urlStr).get().build());
        return call;
    }

    /**
     * 为okHttpClient添加SSL证书
     *
     * @param okClientBuilder client构造器
     * @param cert            证书文件流
     * @throws Exception
     */
    public static void setCertificates(OkHttpClient.Builder okClientBuilder, InputStream cert) throws Exception
    {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(cert));
        if (cert != null)
        {
            try
            {
                cert.close();
            } catch (IOException e)
            {
            }
        }
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        okClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
    }

    /**
     * dip转换成像素
     *
     * @param context 上下文
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 像素转换成dip
     *
     * @param context 上下文
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static abstract class Callback<T> implements okhttp3.Callback {
        private Subscriber subscriber = null;

        public Callback(Subscriber<? super T> subscriber)
        {
            this.subscriber = subscriber;
        }

        /**
         * 连接失败，回调订阅者onError方法
         * @param call
         * @param e
         */
        @Override
        public void onFailure(Call call, IOException e)
        {
            subscriber.onError(new Exception("网络无法连接"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException
        {
            if (response.code() == 200)
            {
                //请求成功，回调抽象处理方法onResponse
                onResponse(response);
            } else
            {
                //请求失败，回调订阅者onError方法
                subscriber.onError(new Exception("网络请求失败"));
            }
        }

        public abstract void onResponse(Response response) throws IOException;
    }

    public static class Subscription implements rx.Subscription {
        private Call call = null;

        public Subscription(Call call)
        {
            this.call = call;
        }

        @Override
        public boolean isUnsubscribed()
        {
            return false;
        }

        /**
         * 在订阅者取消订阅时会被调用，用来关闭未结束的okHttp连接
         */
        @Override
        public void unsubscribe()
        {
            this.call.cancel();
        }
    }
}
