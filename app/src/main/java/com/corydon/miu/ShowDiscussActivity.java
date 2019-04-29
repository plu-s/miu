package com.corydon.miu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.DiscussComment;
import com.corydon.miu.bean.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ShowDiscussActivity extends AppCompatActivity {
    private static final String TAG = "ShowDiscussActivity";
    @BindView(R.id.discussShowLayout)
    LinearLayout discussShowLayout;
    @BindView(R.id.commentEdit)
    EditText commentEdit;
    @BindView(R.id.commentButton)
    Button commentButton;
    @BindView(R.id.commentView)
    RecyclerView commentView;
    WebView discussShowView;
    private Discuss discuss;
    private User user;
    private List<DiscussComment> discussCommentList=new ArrayList<>();
    private Handler handler;
    private CommentAdapter commentAdapter;
    private Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_discuss);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        handler=new Handler();
        Intent intent=getIntent();
        discuss=(Discuss)intent.getSerializableExtra("discuss");
        user=MainActivity.user;
        discussShowView=new WebView(this);
        discussShowView.setWebViewClient(new WebViewClient());
        discussShowView.loadData(discuss.getContent(),"text/html", "UTF-8");
        commentAdapter=new CommentAdapter(discussCommentList);
        commentView.setLayoutManager(new LinearLayoutManager(this));
        commentView.setAdapter(commentAdapter);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        discussShowLayout.addView(discussShowView,0,lp);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",user.getMail());
                forms.put("passwords",user.getPasswords());
                forms.put("discussId",discuss.getId());
                forms.put("content",commentEdit.getText().toString());
                HttpUtil.doFormPost(Configures.URL_DISCUSS_COMMENT, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toast("请连接网络重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.code()==200){
                            try{
                                JSONObject object=new JSONObject(response.body().string());
                                int result=object.getInt("result");
                                toast(object.getString("data"));
                                if(result==200){
                                    loadComments();
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            toast("请联系管理员");
                        }
                    }
                });
            }
        });
        loadComments();
    }

    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments(){
        Map<String,String> forms=new HashMap<>();
        forms.put("mail",user.getMail());
        forms.put("passwords",user.getPasswords());
        forms.put("discussId",discuss.getId());
        HttpUtil.doFormPost(Configures.URL_DISCUSS_GET_COMMENTS, forms, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                toast("请连接网络重试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    try{
                        JSONObject object=new JSONObject(response.body().string());
                        int result=object.getInt("result");
                        if(result==Configures.RESULT_OK){
                            discussCommentList.clear();
                            List<DiscussComment> l=gson.fromJson(object.getString("data"),
                                    new TypeToken<List<DiscussComment>>(){}.getType());
                            discussCommentList.addAll(gson.fromJson(object.getString("data"),
                                    new TypeToken<List<DiscussComment>>(){}.getType()));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    commentAdapter.notifyDataSetChanged();
                                    commentAdapter.setProgressOver();
                                }
                            },1000);
                        }
                        else{
                            toast(object.getString("data"));
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                else{
                    toast("请联系管理员");
                }
            }
        });
    }

    class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_CONTENT=0;
        private final static int TYPE_FOOTER=1;
        private List<DiscussComment> discussCommentList;
        private FootViewHolder footViewHolder;
        public CommentAdapter(List<DiscussComment> discussCommentList){
            this.discussCommentList=discussCommentList;
        }

        public void setProgressOver(){
            footViewHolder.setOver();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if(viewType==TYPE_CONTENT){
                View view=LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_comment,viewGroup,false);
                ItemViewHolder viewHolder=new ItemViewHolder(view);
                return viewHolder;
            }
            else{
                View view=LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.footer_item,viewGroup,false);
                footViewHolder=new FootViewHolder(view);
                return footViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(getItemViewType(i)==TYPE_CONTENT){
                DiscussComment discussComment=(DiscussComment)discussCommentList.get(i);
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ItemViewHolder holder=(ItemViewHolder)viewHolder;
                Util.loadImageFromUrl(getApplicationContext(),discussComment.getUserPic(),holder.userPic);
                holder.userName.setText(discussComment.getUserName());
                holder.commentContent.setText(discussComment.getContent());
                holder.commentDate.setText(format.format(discussComment.getCreateDate()));
            }
        }

        @Override
        public int getItemCount() {
            return discussCommentList.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==discussCommentList.size())
                return TYPE_FOOTER;
            return TYPE_CONTENT;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.userPic)
            RoundedImageView userPic;
            @BindView(R.id.userName)
            TextView userName;
            @BindView(R.id.commentContent)
            TextView commentContent;
            @BindView(R.id.commentDate)
            TextView commentDate;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

        }
        class FootViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.progressBar)
            ContentLoadingProgressBar progressBar;
            @BindView(R.id.footer_tv)
            TextView footerTv;
            public FootViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
            public void setOver(){
                progressBar.setVisibility(View.INVISIBLE);
                footerTv.setVisibility(View.VISIBLE);
            }
        }
    }

}
