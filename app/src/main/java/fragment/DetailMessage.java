package fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.infrastructure.net.DefaultThreadPool;
import com.infrastructure.net.HttpRequest;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.net.URLData;
import com.infrastructure.net.UrlConfigManager;
import com.tri.myfirstapp.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.DisplayMessageActivity;
import activity.PdfViewActivity;

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

        Bundle args = getArguments();
        String id = args.getString("id");

        //发请求，接收结果并处理返回
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", "bHV5YW58NzZEODAyMjQ2MTFGQzkxOUE1RDU0RjBGRjlGQkE0NDZ8MTQ2NjY2NDUyMzY3MA");
        params.put("id", id);
        final DisplayMessageActivity parentActivity = ((DisplayMessageActivity)getActivity());
        final URLData urlData = UrlConfigManager.findURL(parentActivity, "fileExpressView");
        HttpRequest request = parentActivity.getRequestManager().createRequest(urlData, params, parentActivity.new AbstractRequestCallback() {
            @Override
            public void onSuccess(String content)
            {
                if (StringUtils.isNotBlank(content))
                {
                    Map msg = JSON.parseObject(content, Map.class);
                    Map form = (Map) msg.get("fileExpressForm");
                    //表单赋值
                    tvTitle.setText((String) form.get("TITLE"));
                    tvYjLeader.setText((String) form.get("LEADERID"));
                    tvYjTime.setText((String) form.get("READTIME"));
                    tvSyPeople.setText((String) form.get("SENDER"));
                    tvSyTime.setText((String) form.get("SENDTIME"));
                    tvContent.setText((String) form.get("CONTENT"));
                    //生成附件按钮
                    attachsList = (List<Map>) msg.get("fileExpressAttachs");

                    attachGridView.setAdapter(new attachButtonAdapter());
                    attachGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            String fileId = (String) attachsList.get(position).get("ATTACHID");
                            String url = getResources().getString(R.string.server_url) + "fileExpressAttachDown?token=eXVud2VpfDc2RDgwMjI0NjExRkM5MTlBNUQ1NEYwRkY5RkJBNDQ2fDE0NjY2NjQ1ODUwMzI&fileId=" + fileId;
                            Intent intent = new Intent(getActivity(), PdfViewActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                }
            }
        }, false);
        DefaultThreadPool.getInstance().execute(request);
        return view;
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
