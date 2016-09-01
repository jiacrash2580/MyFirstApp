package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.DisplayMessageActivity;
import activity.PdfViewActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import util.SmartUtil;
import util.UrlConfigManager;

/**
 * Created by aaa on 2016/7/28.
 */
public class DetailMessage extends Fragment {
    private TextView tvTitle = null;
    private TextView tvYjLeader = null;
    private TextView tvYjTime = null;
    private TextView tvSyPeople = null;
    private TextView tvSyTime = null;
    private TextView tvContent = null;
    private GridView attachGridView = null;
    private List<Map> attachsList = null;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private String token = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.detail_message, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvYjLeader = (TextView) view.findViewById(R.id.tvYjLeader);
        tvYjTime = (TextView) view.findViewById(R.id.tvYjTime);
        tvSyPeople = (TextView) view.findViewById(R.id.tvSyPeople);
        tvSyTime = (TextView) view.findViewById(R.id.tvSyTime);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        attachGridView = (GridView) view.findViewById(R.id.attachGridView);
        token = ((DisplayMessageActivity) getActivity()).sp.getString("token", "");

        loadDataDetail();

//        test12306();

        return view;
    }



    /**
     * 测试访问自签名的https网站12306
     */
    private void test12306()
    {
        try
        {
            final Activity parentActivity = getActivity();
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(final Subscriber<? super String> subscriber)
                {
                    try
                    {
                        InputStream is = parentActivity.getResources().openRawResource(R.raw.kyfw12306);
                        Call call = SmartUtil.okHttpsPost("https://kyfw.12306.cn/otn/", null, is);
                        subscriber.add(new SmartUtil.Subscription(call));
                        call.enqueue(new SmartUtil.Callback(subscriber) {
                            @Override
                            public void onResponse(Response response) throws IOException
                            {
                                subscriber.onNext(response.body().string());
                            }
                        });
                    } catch (Exception e)
                    {
                        subscriber.onError(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
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
                        public void onNext(String body)
                        {
                            //body是返回页面的代码
                            int j = 0;
                        }
                    });
        } catch (Exception e)
        {
            int j = 0;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    private void loadDataDetail()
    {
        final Activity parentActivity = getActivity();
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber)
            {
                try
                {
                    Bundle args = getArguments();
                    Map<String, String> urlData = UrlConfigManager.findURL(parentActivity, "fileExpressView");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("id", args.getString("id"));
                    final Call call = SmartUtil.okHttpPost(urlData.get("url"), params);
                    subscriber.add(new SmartUtil.Subscription(call));
                    call.enqueue(new SmartUtil.Callback(subscriber) {
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
                        Map form = (Map) map.get("fileExpressForm");
                        //表单赋值
                        tvTitle.setText((String) form.get("TITLE"));
                        tvYjLeader.setText((String) form.get("LEADERID"));
                        tvYjTime.setText((String) form.get("READTIME"));
                        tvSyPeople.setText((String) form.get("SENDER"));
                        tvSyTime.setText((String) form.get("SENDTIME"));
                        tvContent.setText((String) form.get("CONTENT"));
                        //生成附件按钮
                        attachsList = (List<Map>) map.get("fileExpressAttachs");

                        attachGridView.setAdapter(new attachButtonAdapter());
                        attachGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                String fileId = (String) attachsList.get(position).get("ATTACHID");
                                String url = UrlConfigManager.findURL(getActivity(), "fileExpressAttachDown").get("url") + "?token=" + token + "&fileId=" + fileId;
                                Intent intent = new Intent(getActivity(), PdfViewActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }
                        });
                    }
                }));
    }

    class attachButtonAdapter extends BaseAdapter {
        @Override
        public int getCount()
        {
            if (attachsList != null)
            {
                return attachsList.size();
            } else
            {
                return 0;
            }
        }

        @Override
        public Object getItem(int position)
        {
            return attachsList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view;
            ViewHolder holder;
            if (convertView == null)
            {
                view = LayoutInflater.from(getContext()).inflate(R.layout.attach_adapter, parent, false);
                holder = new ViewHolder();
                holder.attachmentIcon = (ImageView) view.findViewById(R.id.attachment_icon);
                holder.attachmentName = (TextView) view.findViewById(R.id.attachment_name);
                view.setTag(holder);
            } else
            {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            Map attachment = (Map) getItem(position);
            holder.attachmentName.setText((String) attachment.get("TITLENAME"));

            String extension = (String) attachment.get("EXTENSION");
            if (StringUtils.containsIgnoreCase(extension, "doc"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.doc);
            } else if (StringUtils.containsIgnoreCase(extension, "pdf"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.pdf);
            } else if (StringUtils.containsIgnoreCase(extension, "xls"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.xls);
            } else if (StringUtils.containsIgnoreCase(extension, "ppt"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.ppt);
            } else if (StringUtils.containsIgnoreCase(extension, "bmp"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.bmp);
            } else if (StringUtils.containsIgnoreCase(extension, "jpg"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.jpg);
            } else if (StringUtils.containsIgnoreCase(extension, "png"))
            {
                holder.attachmentIcon.setImageResource(R.drawable.png);
            } else
            {
            }
            return view;
        }

        class ViewHolder {
            ImageView attachmentIcon;
            TextView attachmentName;
        }
    }
}
