package com.tri.mobile.baselib.RxOkHttp;

import com.tri.mobile.baselib.context.RxContext;
import com.tri.mobile.baselib.util.SmartUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by aaa on 2016/9/5.
 */
public class RxOkHttpUtil {

    /**
     * @param rxContext    该参数是调用方法所在的activity或者fragment所在的Rx封装类，包括RxAppCompatActivity和RxFragment
     * @param url          请求地址url
     * @param params       请求参数
     * @param rxFunc1      okHttp请求的直接返回结果Response的处理回调类，可以获取成String也可以是inputStream分别应对返回的json或文件流
     * @param rxSubscriber 结果回调类，用户对于请求的error和成功结果进行处理
     * @param <T>          泛型，表示rxFunc1过程的返回结果和rxSubscriber过程得到的数据
     */
    public static <T> void okHttpPost(RxContext rxContext, final String url, final Map<String, String> params, final RxFunc1<Response, T> rxFunc1, RxSubscriber<T> rxSubscriber)
    {
        rxContext.getCompositeSubscription().add(Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber)
            {
                try
                {
                    final Call call = SmartUtil.okHttpPost(url, params);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber) {
                        @Override
                        public void onResponse(Response response) throws IOException
                        {
                            subscriber.onNext(rxFunc1.call(response));
                        }
                    });
                } catch (Exception e)
                {
                    subscriber.onError(new Exception("网络无法连接"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxSubscriber));
    }

    /**
     * @param rxContext    该参数是调用方法所在的activity或者fragment所在的Rx封装类，包括RxAppCompatActivity和RxFragment
     * @param url          请求地址url
     * @param params       请求参数
     * @param rxFunc1      okHttp请求的直接返回结果Response的处理回调类，可以获取成String也可以是inputStream分别应对返回的json或文件流
     * @param rxSubscriber 结果回调类，用户对于请求的error和成功结果进行处理
     * @param <T>          泛型，表示rxFunc1过程的返回结果和rxSubscriber过程得到的数据
     */
    public static <T> void okHttpGet(RxContext rxContext, final String url, final Map<String, String> params, final RxFunc1<Response, T> rxFunc1, RxSubscriber<T> rxSubscriber)
    {
        rxContext.getCompositeSubscription().add(Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber)
            {
                try
                {
                    final Call call = SmartUtil.okHttpGet(url, params);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber) {
                        @Override
                        public void onResponse(Response response) throws IOException
                        {
                            subscriber.onNext(rxFunc1.call(response));
                        }
                    });
                } catch (Exception e)
                {
                    subscriber.onError(new Exception("网络无法连接"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxSubscriber));
    }

    /**
     * @param rxContext    该参数是调用方法所在的activity或者fragment所在的Rx封装类，包括RxAppCompatActivity和RxFragment
     * @param url          请求地址url
     * @param params       请求参数
     * @param cert         https中自签名的ssl证书文件流
     * @param rxFunc1      okHttp请求的直接返回结果Response的处理回调类，可以获取成String也可以是inputStream分别应对返回的json或文件流
     * @param rxSubscriber 结果回调类，用户对于请求的error和成功结果进行处理
     * @param <T>          泛型，表示rxFunc1过程的返回结果和rxSubscriber过程得到的数据
     */
    public static <T> void okHttpsGet(RxContext rxContext, final String url, final Map<String, String> params, final InputStream cert, final RxFunc1<Response, T> rxFunc1, RxSubscriber<T> rxSubscriber)
    {
        rxContext.getCompositeSubscription().add(Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber)
            {
                try
                {
                    final Call call = SmartUtil.okHttpsGet(url, params, cert);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber) {
                        @Override
                        public void onResponse(Response response) throws IOException
                        {
                            subscriber.onNext(rxFunc1.call(response));
                        }
                    });
                } catch (Exception e)
                {
                    subscriber.onError(new Exception("网络无法连接"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxSubscriber));
    }

    /**
     * @param rxContext    该参数是调用方法所在的activity或者fragment所在的Rx封装类，包括RxAppCompatActivity和RxFragment
     * @param url          请求地址url
     * @param params       请求参数
     * @param cert         https中自签名的ssl证书文件流
     * @param rxFunc1      okHttp请求的直接返回结果Response的处理回调类，可以获取成String也可以是inputStream分别应对返回的json或文件流
     * @param rxSubscriber 结果回调类，用户对于请求的error和成功结果进行处理
     * @param <T>          泛型，表示rxFunc1过程的返回结果和rxSubscriber过程得到的数据
     */
    public static <T> void okHttpsPost(RxContext rxContext, final String url, final Map<String, String> params, final InputStream cert, final RxFunc1<Response, T> rxFunc1, RxSubscriber<T> rxSubscriber)
    {
        rxContext.getCompositeSubscription().add(Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber)
            {
                try
                {
                    final Call call = SmartUtil.okHttpsPost(url, params, cert);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber) {
                        @Override
                        public void onResponse(Response response) throws IOException
                        {
                            subscriber.onNext(rxFunc1.call(response));
                        }
                    });
                } catch (Exception e)
                {
                    subscriber.onError(new Exception("网络无法连接"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rxSubscriber));
    }
}
