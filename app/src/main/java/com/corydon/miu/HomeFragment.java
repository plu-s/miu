package com.corydon.miu;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private static final String[][] websiteUrls=
    {
        {"大麦", "https://m.damai.cn/damai/category/index.html"},
        {"永乐", "https://m.228.cn/category/"},
        {"趣票", "https://m.qupiaowang.com/infor/list-85.html"},
        {"聚橙", "https://m.juooo.com/Show/showsLibrary?cid=-1&caid=35"}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TabLayout websites_tabs = view.findViewById(R.id.websites_tab);
        ViewPager websites_viewpager = view.findViewById(R.id.websites_viewpager);
        websites_viewpager.setOffscreenPageLimit(3);
        List<String> titlesList = new ArrayList<>();
        List<WebsiteFragment> fragmentsList = new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            titlesList.add(websiteUrls[i][0]);
            fragmentsList.add(WebsiteFragment.NewInstance(websiteUrls[i][1]));
        }

        MyAdapter myAdapter = new MyAdapter(getActivity().getSupportFragmentManager(), titlesList, fragmentsList);
        websites_viewpager.setAdapter(myAdapter);

        websites_tabs.setupWithViewPager(websites_viewpager,false);

        super.onViewCreated(view, savedInstanceState);
    }


    class MyAdapter extends FragmentPagerAdapter {
        private List<String> titleList;
        private List<WebsiteFragment> fragmentList;

        public MyAdapter(FragmentManager fm, List<String> titleList, List<WebsiteFragment> fragmentList) {
            super(fm);
            this.titleList = titleList;
            this.fragmentList = fragmentList;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}