package com.tri.myfirstapp.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by aaa on 2016/7/12.
 */
public class SmartUtil {

    public static String httpGet(String urlPath) throws Exception
    {
        String result = "";
        BufferedReader in = null;
        try
        {
            URL realUrl = new URL(urlPath);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 建立实际的连接
            conn.connect();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        } catch (Exception e)
        {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ex)
            {
            }
        }
        return result;
    }

    public static String httpGet(String urlPath, Map<String, String> params) throws Exception
    {
        StringBuilder urlBuilder = new StringBuilder(urlPath);
        if (!params.isEmpty())
        {
            if (StringUtils.contains(urlPath, "?"))
            {
                if (!StringUtils.endsWith(urlPath, "?"))
                {
                    urlBuilder.append("&");
                }
            } else
            {
                urlBuilder.append("?");
            }
            for (String name : params.keySet())
            {
                urlBuilder.append(name).append("=").append(params.get(name)).append("&");
            }
            urlBuilder = new StringBuilder(urlBuilder.substring(0, urlBuilder.length() - 1));
        }
        return httpGet(urlBuilder.toString());
    }

    public static String httpPost(String urlPath, String params) throws Exception
    {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL(urlPath);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(params);  // ②
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        } catch (Exception e)
        {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ex)
            {
            }
        }
        return result;
    }

    public static String httpPost(String urlPath, Map<String, String> params) throws Exception
    {
        StringBuilder urlBuilder = new StringBuilder();
        if (!params.isEmpty())
        {
            for (String name : params.keySet())
            {
                urlBuilder.append(name).append("=").append(params.get(name)).append("&");
            }
            urlBuilder = new StringBuilder(urlBuilder.substring(0, urlBuilder.length() - 1));
        }
        return httpPost(urlPath, urlBuilder.toString());
    }
}
