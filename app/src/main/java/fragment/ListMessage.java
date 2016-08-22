package fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.R;
import com.tri.myfirstapp.util.SmartUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String url = args.getString("url");
        String token = args.getString("token");
        String pageNum = args.getString("pageNum");
        String pageSize = args.getString("pageSize");
        String status = args.getString("status");
        new loginAsyncTask().execute(url, token, pageNum, pageSize, status);

        return view;
    }

    class loginAsyncTask extends AsyncTask<String, Integer, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... arg)
        {
            try
            {
                Map params = new HashMap();
                params.put("token", arg[1]);
                params.put("pageNum", arg[2]);
                params.put("pageSize", arg[3]);
                params.put("status", arg[4]);
                return SmartUtil.httpGet(arg[0], params, getActivity());
            } catch (Exception e)
            {
                Map<String, String> result = new HashMap<String, String>();
                result.put("msg", "参数异常！");
                result.put("ret", "error");
                return result;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> result)
        {
            if (StringUtils.equals("ok", result.get("ret")))
            {
                Map reObj = JSON.parseObject(result.get("msg"), Map.class);
                infoList = (List<Map<String, String>>) reObj.get("fileExpList");
                SimpleAdapter sa = new SimpleAdapter(getActivity(), infoList, R.layout.listview_item, new String[]{"TITLE", "LEADERID", "SENDTIME"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
                list.setAdapter(sa);
            } else if (StringUtils.equals("error", result.get("ret")))
            {
                Toast.makeText(getActivity(), result.get("msg"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface ItemClickCallBack {
        void onItemClick(String id);
    }
}
