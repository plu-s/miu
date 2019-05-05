package com.corydon.miu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.DiscussComment;
import com.corydon.miu.bean.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    WebView discussShowView = null;
    private Discuss discuss;
    private User user;
    private List<DiscussComment> discussCommentList = new ArrayList<>();
    private Handler handler;
    private CommentAdapter commentAdapter;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_discuss);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        toolbar.setTitle("帖子");
        setSupportActionBar(toolbar);
        handler = new Handler();
        Intent intent = getIntent();
        discuss = (Discuss) intent.getSerializableExtra("discuss");
        user = MainActivity.user;
        commentAdapter = new CommentAdapter(discussCommentList, discuss);
        commentView.setLayoutManager(new LinearLayoutManager(this));
        commentView.setAdapter(commentAdapter);
        commentView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> forms = new HashMap<>();
                forms.put("mail", user.getMail());
                forms.put("passwords", user.getPasswords());
                forms.put("discussId", discuss.getId());
                forms.put("content", commentEdit.getText().toString());
                HttpUtil.doFormPost(Configures.URL_DISCUSS_COMMENT, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toast("请连接网络重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == 200) {
                            try {
                                JSONObject object = new JSONObject(response.body().string());
                                int result = object.getInt("result");
                                toast(object.getString("data"));
                                if (result == 200) {
                                    loadComments();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            toast("请联系管理员");
                        }
                    }
                });
            }
        });
        loadComments();
    }

    private void toast(String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments() {
        Map<String, String> forms = new HashMap<>();
        forms.put("mail", user.getMail());
        forms.put("passwords", user.getPasswords());
        forms.put("discussId", discuss.getId());
        HttpUtil.doFormPost(Configures.URL_DISCUSS_GET_COMMENTS, forms, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                toast("请连接网络重试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        int result = object.getInt("result");
                        if (result == Configures.RESULT_OK) {
                            discussCommentList.clear();
                            discussCommentList.addAll(gson.fromJson(object.getString("data"),
                                    new TypeToken<List<DiscussComment>>() {
                                    }.getType()));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    commentAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
                        } else {
                            toast(object.getString("data"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast("请联系管理员");
                }
            }
        });
    }

    class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_CONTENT = 0;
        private final static int TYPE_TITLEBAR = 1;
        private final static int TYPE_WEBVIEW = 2;
        private List<DiscussComment> discussCommentList;
        private Discuss discuss;

        public CommentAdapter(List<DiscussComment> discussCommentList, Discuss discuss) {
            this.discussCommentList = discussCommentList;
            this.discuss = discuss;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_CONTENT) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_comment, viewGroup, false);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                return viewHolder;
            } else if (viewType == TYPE_WEBVIEW) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_webview, viewGroup, false);
                DiscussViewHolder viewHolder = new DiscussViewHolder(view);
                return viewHolder;
            } else {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_comment_titlebar, viewGroup, false);
                CommentTitlebarViewHolder commentTitlebarViewHolder = new CommentTitlebarViewHolder(view);
                return commentTitlebarViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == TYPE_CONTENT) {
                DiscussComment discussComment = (DiscussComment) discussCommentList.get(i - 2);
                SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                Util.loadImageFromUrl(getApplicationContext(), discussComment.getUserPic(), holder.userPic);
                holder.userName.setText(discussComment.getUserName());
                holder.commentContent.setText(discussComment.getContent());
                holder.commentDate.setText(format.format(discussComment.getCreateDate()));
            } else if (getItemViewType(i) == TYPE_WEBVIEW) {
                if (discussShowView == null) {
                    DiscussViewHolder holder = (DiscussViewHolder) viewHolder;
                    discussShowView = new WebView(getApplicationContext());
                    discussShowView.setWebViewClient(new WebViewClient());
                    discussShowView.loadData(discuss.getContent(), "text/html", "UTF-8");
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    holder.discussShowLayout.addView(discussShowView, 0, lp);
                }
            } else {
                CommentTitlebarViewHolder holder = (CommentTitlebarViewHolder) viewHolder;
                holder.commentTitleBar.setText(String.format("所有评论(%d)", discussCommentList.size()));
            }
        }

        @Override
        public int getItemCount() {
            return discussCommentList.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return TYPE_WEBVIEW;
            if (discussCommentList.size() == 0)
                return TYPE_TITLEBAR;
            else if (position == 1)
                return TYPE_TITLEBAR;
            else return TYPE_CONTENT;
        }

        class DiscussViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.discussShowLayout)
            LinearLayout discussShowLayout;

            public DiscussViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
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
                ButterKnife.bind(this, itemView);
            }

        }

        class CommentTitlebarViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.commentTitleBar)
            TextView commentTitleBar;

            public CommentTitlebarViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
