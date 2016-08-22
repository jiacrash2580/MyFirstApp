package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.infrastructure.net.DefaultThreadPool;
import com.infrastructure.net.HttpRequest;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.net.URLData;
import com.infrastructure.net.UrlConfigManager;
import com.tri.myfirstapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.DisplayMessageActivity;

/**
 * Created by aaa on 2016/7/28.
 */
public class ListMessage extends Fragment {
    private ListView list = null;
    private List<Map<String, String>> infoList = null;

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

        Bundle args = getArguments();
        String token = args.getString("token");
        String pageNum = args.getString("pageNum");
        String pageSize = args.getString("pageSize");
        String status = args.getString("status");

        //发请求，接收结果并处理返回
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("status", status);
        final DisplayMessageActivity parentActivity = ((DisplayMessageActivity)getActivity());
        final URLData urlData = UrlConfigManager.findURL(parentActivity, "fileExpressList");
        HttpRequest request = parentActivity.getRequestManager().createRequest(urlData, params, parentActivity.new AbstractRequestCallback() {
            @Override
            public void onSuccess(String content)
            {
                Map result = JSON.parseObject(content, Map.class);
                infoList = (List<Map<String, String>>) result.get("fileExpList");
                SimpleAdapter sa = new SimpleAdapter(getActivity(), infoList, R.layout.listview_item, new String[]{"TITLE", "LEADERID", "SENDTIME"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
                list.setAdapter(sa);
            }
        }, true);
        DefaultThreadPool.getInstance().execute(request);
        return view;
    }

    public interface ItemClickCallBack {
        void onItemClick(String id);
    }
}
