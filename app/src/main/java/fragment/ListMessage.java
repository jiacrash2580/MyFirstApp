package fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.mobile.baselib.RxOkHttp.RxFunc1;
import com.tri.mobile.baselib.RxOkHttp.RxOkHttpUtil;
import com.tri.mobile.baselib.RxOkHttp.RxSubscriber;
import com.tri.mobile.baselib.context.RxFragment;
import com.tri.myfirstapp.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.DisplayMessageActivity;
import okhttp3.Response;
import util.UrlConfigManager;

/**
 * Created by aaa on 2016/7/28.
 */
public class ListMessage extends RxFragment {
    private ListView list = null;
    private List<Map<String, String>> infoList = null;
    private String token = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.listview_test, container, false);
        list = (ListView) view.findViewById(R.id.list1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Map<String, String> item = infoList.get(position);
                ((ItemClickCallBack) getActivity()).onItemClick(item.get("ID"));
            }
        });
        token = ((DisplayMessageActivity) getActivity()).sp.getString("token", "");
        loadDataList();
        return view;
    }

    private void loadDataList()
    {
        final Activity parentActivity = getActivity();
        Bundle args = getArguments();
        Map<String, String> urlData = UrlConfigManager.findURL(parentActivity, "fileExpressList");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("pageNum", args.getString("pageNum"));
        params.put("pageSize", args.getString("pageSize"));
        params.put("status", args.getString("status"));

        RxOkHttpUtil.okHttpPost(this, urlData.get("url"), params, new RxFunc1<Response, Map>() {
            @Override
            public Map call(Response response) throws IOException
            {
                return JSON.parseObject(response.body().string(), Map.class);
            }
        }, new RxSubscriber<Map>() {
            @Override
            public void onError(Throwable e)
            {
                Toast.makeText(parentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Map map)
            {
                if (map.containsKey("errorMsg"))
                {
                    Toast.makeText(parentActivity, (String) map.get("errorMsg"), Toast.LENGTH_SHORT).show();
                } else
                {
                    infoList = (List<Map<String, String>>) map.get("fileExpList");
                    SimpleAdapter sa = new SimpleAdapter(parentActivity, infoList, R.layout.listview_item, new String[]{"TITLE", "LEADERID", "SENDTIME"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
                    list.setAdapter(sa);
                }
            }
        });
    }

    public interface ItemClickCallBack {
        void onItemClick(String id);
    }
}
