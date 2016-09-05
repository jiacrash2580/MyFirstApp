package com.tri.mobile.baselib.context;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by aaa on 2016/9/5.
 */
public interface RxContext {
    public CompositeSubscription getCompositeSubscription();
}
