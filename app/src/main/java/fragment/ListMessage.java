package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.DisplayMessageActivity;
import activity.LoginActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import util.SmartUtil;
import util.UrlConfigManager;

/**
 * Created by aaa on 2016/7/28.
 */
public class ListMessage extends Fragment {
    private ListView list = null;
    private List<Map<String, String>> infoList = null;
    private String token = null;
    private CompositeSubscription mCompositeSubscription = null;

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
        mCompositeSubscription = new CompositeSubscription();
        loadDataList();
        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    private void loadDataList()
    {
        final Activity parentActivity = getActivity();
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber)
            {
                try
                {
                    Bundle args = getArguments();
                    Map<String, String> urlData = UrlConfigManager.findURL(parentActivity, "fileExpressList");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("pageNum", args.getString("pageNum"));
                    params.put("pageSize", args.getString("pageSize"));
                    params.put("status", args.getString("status"));
                    final Call call = SmartUtil.okHttpPost(urlData.get("url"), params);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber){
                        @Override
                        public void onResponse(Response response) throws IOException
                        {
                            subscriber.onNext(response.body().string());
                        }
                    });
                } catch (IOException e)
                {
                    subscriber.onError(new Exception("网络无法连接"));
                }
            }
        })
                .map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s)
                    {
                        return JSON.parseObject(s, Map.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map>() {
                    @Override
                    public void onCompleted()
                    {
                    }

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
                }));
    }

    public interface ItemClickCallBack {
        void onItemClick(String id);
    }
}
