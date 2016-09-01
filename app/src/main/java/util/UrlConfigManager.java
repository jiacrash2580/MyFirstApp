package util;

import android.app.Activity;
import android.content.res.XmlResourceParser;

import com.tri.myfirstapp.R;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UrlConfigManager {
    private static ArrayList<Map<String, String>> urlList;

    private static void fetchUrlDataFromXml(final Activity activity)
    {
        urlList = new ArrayList<Map<String, String>>();

        final XmlResourceParser xmlParser = activity.getApplication().getResources().getXml(R.xml.url);

        int eventCode;
        try
        {
            eventCode = xmlParser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT)
            {
                switch (eventCode)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (StringUtils.equalsIgnoreCase("node", xmlParser.getName()))
                        {
                            final Map<String, String> urlData = new HashMap<String, String>();
                            int len = xmlParser.getAttributeCount();
                            for(int i = 0; i < len; i++){
                                urlData.put(xmlParser.getAttributeName(i), xmlParser.getAttributeValue(i));
                            }
                            urlList.add(urlData);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventCode = xmlParser.next();
            }
        } catch (final XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (final IOException e)
        {
            e.printStackTrace();
        } finally
        {
            xmlParser.close();
        }
    }

    public static Map<String, String> findURL(final Activity activity, final String findKey)
    {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (urlList == null || urlList.isEmpty())
        {
            fetchUrlDataFromXml(activity);
        }
        for (Map<String, String> data : urlList)
        {
            if (StringUtils.equals(findKey, data.get("id")))
            {
                return data;
            }
        }
        return null;
    }
}