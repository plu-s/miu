package com.corydon.miu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MyCommentActivity extends AppCompatActivity {

    @BindView(R.id.mycommentView)
    RecyclerView myCommentView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<MyComment> myCommentList = new ArrayList<>();
    CommentAdapter commentAdapter = new CommentAdapter(myCommentList);
    User user = MainActivity.user;
    Handler handler = new Handler();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        toolbar.setTitle("我的评论");
        setSupportActionBar(toolbar);
        myCommentView.setLayoutManager(new LinearLayoutManager(this));
        myCommentView.setAdapter(commentAdapter);
        myCommentView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        loadMyComment();
    }

    void loadMyComment() {
        Map<String, String> forms = new HashMap<>();
        forms.put("mail", user.getMail());
        forms.put("passwords", user.getPasswords());
        HttpUtil.doFormPost(Configures.URL_MY_COMMENTS, forms, new Callback() {
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
                        if (result == 200) {
                            // 刷新适配器关联的数据
                            myCommentList.clear();
                            myCommentList.addAll(gson.fromJson(object.getString("data"),
                                    new TypeToken<List<MyComment>>() {
                                    }.getType()));
                            // 在主线程中提示适配器更新显示列表
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    commentAdapter.notifyDataSetChanged();
                                }
                            }, 1000);
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

    private void toast(String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    class MyComment implements Serializable {
        String comment;
        String date;
        String title;
        String authorName;
        String authorPic;
        String discussId;

        public String getDiscussId() {
            return discussId;
        }

        public void setDiscussId(String discussId) {
            this.discussId = discussId;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public void setAuthorPic(String authorPic) {
            this.authorPic = authorPic;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAuthorPic() {
            return authorPic;
        }

        public String getTitle() {
            return title;
        }

        public String getComment() {
            return comment;
        }

        public String getDate() {
            return date;
        }

        public String getAuthorName() {
            return authorName;
        }
    }

    class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_CONTENT = 0;
        private List<MyComment> myCommentList;

        public CommentAdapter(List<MyComment> myCommentList) {
            this.myCommentList = myCommentList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_mycomment, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 拿到当前点击的评论的帖子数据
                    Map<String, String> forms = new HashMap<>();
                    forms.put("mail", user.getMail());
                    forms.put("passwords", user.getPasswords());
                    forms.put("discussId", myCommentList.get(viewHolder.getAdapterPosition()).getDiscussId());
                    HttpUtil.doFormPost(Configures.URL_DISCUSS_GETDISCUSSBYID, forms, new Callback() {
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
                                    if (result == 200) {
                                        Intent intent = new Intent(MyCommentActivity.this, ShowDiscussActivity.class);
                                        Discuss discuss = gson.fromJson(object.getString("data"), Discuss.class);
                                        intent.putExtra("discuss", discuss);
                                        startActivity(intent);
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
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            MyComment myComment = (MyComment) myCommentList.get(i);
            ViewHolder holder = (ViewHolder) viewHolder;
            Util.loadImageFromUrl(getApplicationContext(), myComment.getAuthorPic(), holder.userPic);
            holder.userName.setText(myComment.getAuthorName());
            holder.commentContent.setText(myComment.getComment());
            holder.commentDate.setText(myComment.getDate());
            String tmpTitle = "[标题] " + myComment.getTitle();
            holder.title.setText(tmpTitle);
        }

        @Override
        public int getItemCount() {
            return myCommentList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_CONTENT;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.userPic)
            RoundedImageView userPic;
            @BindView(R.id.userName)
            TextView userName;
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.commentContent)
            TextView commentContent;
            @BindView(R.id.commentDate)
            TextView commentDate;
            @BindView(R.id.layout)
            ConstraintLayout layout;

            public ViewHolder(@NonNull View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }   // ViewHolder
    }
}
