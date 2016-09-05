package com.tri.mobile.baselib.context;

import android.app.Fragment;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by aaa on 2016/9/5.
 */
public class RxFragment extends Fragment implements RxContext {

    //这个是用来管理监听者和观察者关系的管理对象，用来在activity的onDestroy时，取消订阅者关系，释放资源，从而触发取消未结束的okhttp连接。
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    /**
     * 务必调用super.onDestroy()
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public CompositeSubscription getCompositeSubscription()
    {
        return mCompositeSubscription;
    }
}
