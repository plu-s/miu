package com.corydon.miu.bean;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private int itemCount;
    private int lastItem;
    private boolean scrolled=false;

    protected abstract void loadMore(int itemCount,int lastItem);

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if(newState==RecyclerView.SCROLL_STATE_DRAGGING||newState==RecyclerView.SCROLL_STATE_SETTLING)
            scrolled=true;
        else
            scrolled=false;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager layoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
            itemCount=layoutManager.getItemCount();
            lastItem=layoutManager.findLastCompletelyVisibleItemPosition();
        }
        if(scrolled&&lastItem>=itemCount-1){
            loadMore(itemCount,lastItem);
        }
    }
}
