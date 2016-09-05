package com.tri.mobile.baselib.RxOkHttp;

import rx.Subscriber;

/**
 * Created by aaa on 2016/9/5.
 */
public abstract class RxSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted()
    {
    }
}
