package com.tri.mobile.baselib.RxOkHttp;

import java.io.IOException;

/**
 * Created by aaa on 2016/9/5.
 */
public interface RxFunc1<T, R> {
    R call(T t) throws IOException;
}
