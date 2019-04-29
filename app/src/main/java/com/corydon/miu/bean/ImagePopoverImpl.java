package com.corydon.miu.bean;

import android.os.Handler;
import android.os.Looper;

import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.entity.Image;
import com.github.mr5.icarus.popover.Popover;
import com.google.gson.Gson;

public class ImagePopoverImpl implements Popover {
    protected Icarus icarus;
    protected Handler handler;
    private String src;
    private String imagePathFormat="{\"src\":\"%s\",\"alt\":\"Image\"}";
    Gson gson=new Gson();
    public ImagePopoverImpl(Icarus icarus){
        this.icarus=icarus;
        handler=new Handler(Looper.getMainLooper());
    }
    @Override
    public void show(String params, String callbackName) {
        if(src!=null&&!src.equals("")){
            Image image=gson.fromJson(String.format(imagePathFormat,src),Image.class);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    icarus.jsCallback(callbackName,image,Image.class);
                }
            });
        }
    }

    @Override
    public void hide() {

    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }
}
