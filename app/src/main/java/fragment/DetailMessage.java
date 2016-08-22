package fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tri.myfirstapp.R;
import activity.PdfViewActivity;
import com.tri.myfirstapp.util.SmartUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ViewGroup fileContainer = null;

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
        fileContainer = (ViewGroup) view.findViewById(R.id.fileContainer);

        Bundle args = getArguments();
        String id = args.getString("id");
        new detailAsyncTask().execute(id);
        return view;
    }

    class detailAsyncTask extends AsyncTask<String, Integer, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... arg)
        {
            try
            {
                Map params = new HashMap();
                params.put("id", arg[0]);
                return SmartUtil.httpPost("http://210.74.194.118:8082/gw-web-manager/gws/fileExpressView?token=bHV5YW58NzZEODAyMjQ2MTFGQzkxOUE1RDU0RjBGRjlGQkE0NDZ8MTQ2NjY2NDUyMzY3MA", params);
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
                String msgStr = result.get("msg");
                if (StringUtils.isNotBlank(msgStr))
                {
                    Map msg = JSON.parseObject(msgStr, Map.class);
                    Map form = (Map) msg.get("fileExpressForm");
                    //表单赋值
                    tvTitle.setText((String) form.get("TITLE"));
                    tvYjLeader.setText((String) form.get("LEADERID"));
                    tvYjTime.setText((String) form.get("READTIME"));
                    tvSyPeople.setText((String) form.get("SENDER"));
                    tvSyTime.setText((String) form.get("SENDTIME"));
                    tvContent.setText((String) form.get("CONTENT"));
                    //生成附件按钮
                    List<Map> attachs = (List<Map>) msg.get("fileExpressAttachs");
                    if (!attachs.isEmpty())
                    {
                        for (Map file : attachs)
                        {
                            String ext = (String) file.get("EXTENSION");
                            if (StringUtils.equalsIgnoreCase(ext, "pdf"))
                            {
                                ImageButton ib = new ImageButton(getActivity());
                                ib.setImageResource(R.drawable.pdf);
                                ib.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) fileContainer.getLayoutParams();
                                lp.width = SmartUtil.dip2px(getActivity(), 80);
                                lp.height = SmartUtil.dip2px(getActivity(), 80);
                                ib.setLayoutParams(lp);
                                ib.setTag(file);
                                ib.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        String fileId = (String) ((Map) v.getTag()).get("ATTACHID");
                                        String url = "http://210.74.194.118:8082/gw-web-manager/gws/fileExpressAttachDown?token=eXVud2VpfDc2RDgwMjI0NjExRkM5MTlBNUQ1NEYwRkY5RkJBNDQ2fDE0NjY2NjQ1ODUwMzI&fileId=" + fileId;
                                        Intent intent = new Intent(getActivity(), PdfViewActivity.class);
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                    }
                                });
                                fileContainer.addView(ib);
                            }
                        }
                    }
                }
            } else if (StringUtils.equals("error", result.get("ret")))
            {
                Toast.makeText(getActivity(), result.get("msg"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
