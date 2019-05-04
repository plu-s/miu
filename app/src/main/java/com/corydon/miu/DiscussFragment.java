package com.corydon.miu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.DiscussImage;
import com.corydon.miu.bean.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DiscussFragment extends Fragment {
    private static final String TAG = "DiscussFragment";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.include_error_page)
    View errorPage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    private DiscussAdapter discussAdapter;
    private User user;
    private List<Discuss> discussList=new ArrayList<>();
    private List<User> authorList=new ArrayList<>();
    private List<String> likedDiscussIdList=new ArrayList<>();
    private Handler handler;
    private Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    // DiscussFragment 在三个地方被使用，分别是讨论区，我的点赞，我的帖子
    // 这三个地方的菜单栏、工具栏标题以及对帖子数据的筛选都应该是不同的
    // 下面是相关设置的方法或接口

    private boolean enableMenu = true;
    private String toolbarTitle = "miu";
    private DisCussFilter disCussFilter;

    public void setToolbarTitle(String title)
    {
        this.toolbarTitle = title;
    }

    public void setEnableMenu(boolean enableMenu) {
        this.enableMenu = enableMenu;
    }

    interface DisCussFilter{
        void filter(List<Discuss> discussList,List<User> authorList,List<String> likedDiscussIdList);
    }

    public void setDisCussFilter(DisCussFilter disCussFilter) {
        this.disCussFilter = disCussFilter;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.discuss_fragment,container,false);
        ButterKnife.bind(this,view);
        ButterKnife.bind(R.layout.error_page,errorPage);
        user=MainActivity.user;
        init();
        refreshData();
        return view;
    }

    private void init(){
        setHasOptionsMenu(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        handler=new Handler();
        toolbar.setTitle(toolbarTitle);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        errorPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        discussAdapter=new DiscussAdapter(discussList,authorList,likedDiscussIdList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(discussAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        refreshLayout.setRefreshing(true);
    }

    private void refreshData(){
        Map<String,String> forms=new HashMap<>();
        forms.put("mail",user.getMail());
        forms.put("passwords",user.getPasswords());
        HttpUtil.doFormPost(Configures.URL_PUSH_DISCUSS, forms, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        errorPage.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    try{
                        JSONObject object=new JSONObject(response.body().string());
                        int result=object.getInt("result");
                        if(result==200){
                            JSONObject data=object.getJSONObject("data");
                            discussList.clear();
                            authorList.clear();
                            likedDiscussIdList.clear();
                            discussList.addAll(gson.fromJson(data.getString("discussList"),
                                    new TypeToken<List<Discuss>>(){}.getType()));
                            authorList.addAll(gson.fromJson(data.getString("authorList"),
                                    new TypeToken<List<User>>(){}.getType()));
                            likedDiscussIdList.addAll(gson.fromJson(data.getString("likedDiscussIdList"),
                                    new TypeToken<List<String>>(){}.getType()));
                            Log.d(TAG, "onResponse: "+discussList.size());
                            if(disCussFilter!=null)
                                disCussFilter.filter(discussList,authorList,likedDiscussIdList);
                            Log.d(TAG, "onResponse: "+discussList.size());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(errorPage.getVisibility()==View.VISIBLE)
                                        errorPage.setVisibility(View.INVISIBLE);
                                    discussAdapter.notifyDataSetChanged();
                                    if(refreshLayout.isRefreshing())
                                        refreshLayout.setRefreshing(false);
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

    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (enableMenu)
        {
            menu.clear();
            inflater.inflate(R.menu.discuss,menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.write_discuss){
            Intent intent=new Intent(getContext(),WriteDiscussActivity.class);
            intent.putExtra("user",user);
            startActivityForResult(intent,100);
        }
        return super.onOptionsItemSelected(item);
    }

    class DiscussAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final static int TYPE_CONTENT=0;
        private final static int TYPE_FOOTER=1;
        List<Discuss> discussList;
        List<User> authorList;
        List<String> likedDiscussId;
        FootViewHolder footViewHolder;
        DiscussAdapter(List<Discuss> discussList,List<User> authorList,List<String> likedDiscussId){
            this.discussList=discussList;
            this.authorList=authorList;
            this.likedDiscussId=likedDiscussId;
        }

        public void setOver(){
            footViewHolder.setOver();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if(viewType==TYPE_CONTENT){
                View view=LayoutInflater.from(getContext())
                        .inflate(R.layout.item_discuss,viewGroup,false);
                ItemViewHolder viewHolder=new ItemViewHolder(view);
                initItemViewHolder(viewHolder);
                return viewHolder;
            }
            else{
                View view=LayoutInflater.from(getContext())
                        .inflate(R.layout.footer_item,viewGroup,false);
                footViewHolder=new FootViewHolder(view);
                return footViewHolder;
            }

        }
        private void initItemViewHolder(ItemViewHolder viewHolder){
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(),ShowDiscussActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra("discuss",discussList.get(viewHolder.getAdapterPosition()));
                    startActivity(intent);
                }
            });
            viewHolder.likeCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,String> forms=new HashMap<>();
                    forms.put("userMail",user.getMail());
                    forms.put("passwords",user.getPasswords());
                    forms.put("discussId",discussList.get(viewHolder.getAdapterPosition()).getId());
                    HttpUtil.doFormPost(Configures.URL_DISCUSS_DO_LIKE, forms, new Callback() {
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
                                    if(result==200){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshData();
                                            }
                                        });
                                    }
                                    else{
                                        toast(object.getString("data"));
                                    }
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(getItemViewType(i)==TYPE_FOOTER){

            }
            else{
                ItemViewHolder itemViewHolder=(ItemViewHolder)viewHolder;
                List<ImageView> imageViewList=new ArrayList<>();
                Discuss discuss=discussList.get(i);
                User author=authorList.get(i);
                if(author.getPicUrl()!=null&&!author.getPicUrl().equals("")){
                    Util.loadImageFromUrl(getContext(),author.getPicUrl(),itemViewHolder.pic);
                }
                itemViewHolder.userName.setText(author.getName());
                itemViewHolder.discussTitle.setText(discuss.getTitle());

                Document document=Jsoup.parse(discuss.getContent());
                if(document.getElementsByTag("p").size()>0){
                    Element p=document.getElementsByTag("p").first();
                    itemViewHolder.discussContent.setText(p.text());
                }
                imageViewList.add(itemViewHolder.image1);
                imageViewList.add(itemViewHolder.image2);
                imageViewList.add(itemViewHolder.image3);
                List<DiscussImage> discussImageList=discuss.getDiscussImageList();
                if(discussImageList!=null&&discussImageList.size()!=0){
                    for(int j=0;j<discussImageList.size();j++){
                        if(j==3)
                            break;
                        DiscussImage discussImage=discussImageList.get(j);
                        if(j<discussImageList.size()){
                            Util.loadImageFromUrl(getContext(),discussImage.getImageUrl(),imageViewList.get(j));
                        }
                        else{
                            imageViewList.get(j).setVisibility(View.GONE);
                        }


                    }
                }
                else{
                    for(ImageView imageView:imageViewList){
                        imageView.setVisibility(View.GONE);
                    }
                }
                itemViewHolder.likeCount.setText(discuss.getLikeCount()+"");
                itemViewHolder.commentCount.setText(discuss.getCommentCount()+"");
                itemViewHolder.likeCountView.setImageResource(R.mipmap.like);
                for(String di:likedDiscussId){
                    if(discuss.getId().equals(di)){
                        itemViewHolder.likeCountView.setImageResource(R.mipmap.liked);
                        break;
                    }
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            /*if(position==discussList.size())
                return TYPE_FOOTER;*/
            return TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            return discussList.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.user_pic)
            RoundedImageView pic;
            @BindView(R.id.user_name)
            TextView userName;
            @BindView(R.id.discuss_title)
            TextView discussTitle;
            @BindView(R.id.discuss_content)
            TextView discussContent;
            @BindView(R.id.image1)
            ImageView image1;
            @BindView(R.id.image2)
            ImageView image2;
            @BindView(R.id.image3)
            ImageView image3;
            @BindView(R.id.like_count)
            TextView likeCount;
            @BindView(R.id.comment_count)
            TextView commentCount;
            @BindView(R.id.icon_like)
            ImageView likeCountView;
            @BindView(R.id.layout)
            ConstraintLayout layout;
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
