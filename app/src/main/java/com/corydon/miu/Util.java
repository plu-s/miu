package com.corydon.miu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import pub.devrel.easypermissions.EasyPermissions;

public class Util {
    private static final String TAG = "Util";
    public static Dialog loadingDialog(Context context){
        Dialog dialog=new Dialog(context);
        View view=View.inflate(context,R.layout.progress_bar,null);
        dialog.setContentView(view);
        dialog.show();
        return dialog;
    }

    public static void closeDialog(Dialog dialog){
        if(dialog!=null&&dialog.isShowing())
            dialog.dismiss();
    }

    @TargetApi(19)
    public static String handleImageFromAlbumAboveApi19(Context context,Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(context,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if(uri.getAuthority().equals("com.android.providers.media.documents")){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if(uri.getAuthority().equals("com.android.providers.downloads.documents")){
                Uri contentUri=ContentUris
                        .withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(context,contentUri,null);
            }
            else if(uri.getScheme().equalsIgnoreCase("content")){
                imagePath=getImagePath(context,uri,null);
            }
            else if(uri.getScheme().equalsIgnoreCase("file")){
                imagePath=uri.getPath();
            }
        }
        Log.d(TAG, "handleImageFromAlbumAboveApi19: "+imagePath);
        return imagePath;
    }

    public static void loadImageFromUrl(Context context, String url, ImageView view){
        RequestOptions options=new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(view);
    }

    public static String handleImageFromAlbumBeforeApi19(Context context,Intent data){
        Uri uri=data.getData();
        return getImagePath(context,uri,null);
    }

    private static String getImagePath(Context context,Uri uri,String selection){
        String path=null;
        String[] proj={MediaStore.Images.Media.DATA};
        Cursor cursor=context.getContentResolver().query(uri, null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                int index=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                Log.d(TAG, "getImagePath: "+index);
                path=cursor.getString(index);
            }
            cursor.close();
        }
        return path;
    }

    public static void requestPermissions(Activity activity, String[] perms,int requestCode){
        if(!EasyPermissions.hasPermissions(activity,perms)){
            EasyPermissions.requestPermissions(activity,"选取图片需要外部存储设备读写权限",requestCode, perms);
        }
    }




}
