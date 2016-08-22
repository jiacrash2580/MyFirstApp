package com.infrastructure.net;

import com.infrastructure.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Map;

public class RequestManager {
    ArrayList<HttpRequest> requestList = null;

    public RequestManager(final BaseActivity activity)
    {
        // 异步请求列表
        requestList = new ArrayList<HttpRequest>();
    }

    /**
     * 添加Request到列表
     */
    public void addRequest(final HttpRequest request)
    {
        requestList.add(request);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest()
    {
        if ((requestList != null) && (requestList.size() > 0))
        {
            for (final HttpRequest request : requestList)
            {
                if (request.getRequest() != null)
                {
                    try
                    {
                        request.getRequest().abort();
                        requestList.remove(request.getRequest());
                    } catch (final UnsupportedOperationException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 无参数调用
     */
    public HttpRequest createRequest(final URLData urlData,
                                     final RequestCallback requestCallback,
                                     final boolean showDlgFlag)
    {
        return createRequest(urlData, null, requestCallback, showDlgFlag);
    }

    /**
     * 有参数调用
     */
    public HttpRequest createRequest(final URLData urlData,
                                     final Map<String, String> params,
                                     final RequestCallback requestCallback,
                                     final boolean showDlgFlag)
    {
        final HttpRequest request = new HttpRequest(urlData, params, requestCallback, showDlgFlag);

        addRequest(request);
        return request;
    }
}
