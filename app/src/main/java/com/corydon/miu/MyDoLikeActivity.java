package com.corydon.miu;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.corydon.miu.bean.Discuss;
import com.corydon.miu.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDoLikeActivity extends AppCompatActivity {
    @BindView(R.id.layout)
    FrameLayout layout;
    private FragmentManager fragmentManager;
    private DiscussFragment discussFragment;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_discuss);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        user = MainActivity.user;

        DiscussFragment.DisCussFilter disCussFilter = new DiscussFragment.DisCussFilter() {
            @Override
            public void filter(List<Discuss> discussList, List<User> authorList, List<String> likedDiscussIdList) {
                for (int i = 0; i < discussList.size(); i++) {
                    Discuss discuss = discussList.get(i);
                    if (!likedDiscussIdList.contains(discuss.getId())) {
                        discussList.remove(i);
                        authorList.remove(i);
                        i--;
                    }
                }
            }
        };

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        discussFragment = new DiscussFragment();
        discussFragment.setDisCussFilter(disCussFilter);
        transaction.add(R.id.layout, discussFragment);
        transaction.show(discussFragment);
        transaction.commit();
    }
}
