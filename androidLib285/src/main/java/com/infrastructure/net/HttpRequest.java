package com.infrastructure.net;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.infrastructure.utils.BaseUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest implements Runnable {
    private HttpUriRequest request = null;
    private URLData urlData = null;
    private RequestCallback requestCallback = null;
    private Map<String, String> parameter = null;
    private String url = null;
    private HttpResponse response = null;
    private DefaultHttpClient httpClient;
    private boolean showDlgFlag;

    protected Handler handler;

    public HttpRequest(final URLData data, final Map<String, String> params, final RequestCallback callBack, final boolean showDlgFlag)
    {
        urlData = data;
        url = urlData.getUrl();
        this.parameter = params;
        requestCallback = callBack;
        this.showDlgFlag = showDlgFlag;

        if (httpClient == null)
        {
            httpClient = new DefaultHttpClient();
        }

        handler = new Handler();
    }

    /**
     * 获取HttpUriRequest请求
     *
     * @return
     */
    public HttpUriRequest getRequest()
    {
        return request;
    }

    @Override
    public void run()
    {
        try
        {
            if (urlData.getNetType().equals("get"))
            {
                if ((showDlgFlag && requestCallback != null))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            requestCallback.showDlg();
                        }
                    });
                }
                // 添加参数
                final StringBuffer paramBuffer = new StringBuffer();
                if ((parameter != null) && (parameter.size() > 0))
                {
                    for (final String pName : parameter.keySet())
                    {
                        if (paramBuffer.length() == 0)
                        {
                            paramBuffer.append(pName + "=" + BaseUtils.UrlEncodeUnicode(parameter.get(pName)));
                        } else
                        {
                            paramBuffer.append("&" + pName + "=" + BaseUtils.UrlEncodeUnicode(parameter.get(pName)));
                        }
                    }
                    String newUrl = url + "?" + paramBuffer.toString();
                    request = new HttpGet(newUrl);
                } else
                {
                    request = new HttpGet(url);
                }
            } else if (urlData.getNetType().equals("post"))
            {
                if ((showDlgFlag && requestCallback != null))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            requestCallback.showDlg();
                        }
                    });
                }
                request = new HttpPost(url);
                // 添加参数
                if ((parameter != null) && (parameter.size() > 0))
                {
                    final List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                    for (final String pName : parameter.keySet())
                    {
                        list.add(new BasicNameValuePair(pName, parameter.get(pName)));
                    }
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
                }
            } else
            {
                return;
            }

            request.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
            request.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

            // 发送请求
            response = httpClient.execute(request);

            // 设置回调函数, requestCallback可能为空，说明不需要知道返回结果，也就是不需要回调
            if ((requestCallback != null))
            {
                // 获取状态
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK)
                {
                    final ByteArrayOutputStream content = new ByteArrayOutputStream();
                    response.getEntity().writeTo(content);
                    final String strResponse = new String(content.toByteArray()).trim();
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            requestCallback.onResult(strResponse);
                        }
                    });
                } else
                {
                    handleNetworkError("网络异常");
                }
            } else
            {
                handleNetworkError("网络异常");
            }
        } catch (final java.lang.IllegalArgumentException e)
        {
            handleNetworkError("网络异常");
        } catch (final UnsupportedEncodingException e)
        {
            handleNetworkError("网络异常");
        } catch (final IOException e)
        {
            handleNetworkError("网络异常");
        }
    }

    public void handleNetworkError(final String errorMsg)
    {
        if ((requestCallback != null))
        {
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    HttpRequest.this.requestCallback.onFail(errorMsg);
                }
            });
        }
    }

    public static String inputStreamToString(final InputStream is)
            throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1)
        {
            baos.write(i);
        }
        return baos.toString();
    }
}