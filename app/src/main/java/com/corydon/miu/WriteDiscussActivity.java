package com.corydon.miu;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.button.Button;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.corydon.miu.bean.ImagePopoverImpl;
import com.corydon.miu.bean.User;


import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class WriteDiscussActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "WriteDiscussActivity";
    @BindView(R.id.title_input)
    EditText titleEdit;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.button_image)
    TextView imageTextView;
    @BindView(R.id.button_align_left)
    TextView alignLeftView;
    @BindView(R.id.button_align_center)
    TextView alignCenterView;
    @BindView(R.id.button_align_right)
    TextView alignRightView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextViewButton imageButton;
    TextViewButton alignLeftButton;
    TextViewButton alignCenterButton;
    TextViewButton alignRightButton;
    protected Icarus icarus;
    private ImagePopoverImpl imagePopover;
    private static final int REQUEST_PERMISSION=2;
    private Handler handler=new Handler();
    private User user;
    private Uri cropFileUri;
    private String[] perms={Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CHOOSE_PHOTO=1;
    private static final int REQUEST_PHOTO_CUT=2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_discuss);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        user=MainActivity.user;
        init();
    }

    private void requestPermission(){
        Util.requestPermissions(this,perms,REQUEST_PERMISSION);
    }

    private void init(){
        toolbar.setTitle("发表帖子");
        setSupportActionBar(toolbar);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "Simditor.ttf");
        webView.addJavascriptInterface(new JavaScriptLocalObj(),"local_obj");
        TextViewToolbar toolbar =new TextViewToolbar();
        Options options=new Options();
        options.setPlaceholder("Placeholder...");
        icarus=new Icarus(toolbar,options,webView);
        imageButton = new TextViewButton(this.imageTextView,icarus);
        imageButton.setName(Button.NAME_IMAGE);
        imagePopover=new ImagePopoverImpl(icarus);
        imageButton.setPopover(imagePopover);
        alignLeftButton=new TextViewButton(this.alignLeftView,icarus);
        alignLeftButton.setName(Button.NAME_ALIGN_LEFT);
        alignCenterButton=new TextViewButton(this.alignCenterView,icarus);
        alignCenterButton.setName(Button.NAME_ALIGN_CENTER);
        alignRightButton=new TextViewButton(this.alignRightView,icarus);
        alignRightButton.setName(Button.NAME_ALIGN_RIGHT);
        imageTextView.setTypeface(iconfont);
        alignLeftView.setTypeface(iconfont);
        alignCenterView.setTypeface(iconfont);
        alignRightView.setTypeface(iconfont);
        toolbar.addButton(imageButton);
        toolbar.addButton(alignLeftButton);
        toolbar.addButton(alignCenterButton);
        toolbar.addButton(alignRightButton);
        imageButton.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                if(EasyPermissions.hasPermissions(getApplicationContext(),perms))
                    openAlbum();
            }
        });
        icarus.render();
    }


    private void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CHOOSE_PHOTO){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    cutImage(data.getData());
                }
            }
        }
        else if(requestCode==REQUEST_PHOTO_CUT){
            if(resultCode==RESULT_OK){
                /*if(Build.VERSION.SDK_INT>=19){
                    imagePath=Util.handleImageFromAlbumAboveApi19(this,data);
                }
                else{
                    imagePath=Util.handleImageFromAlbumBeforeApi19(this,data);
                }*/
                imagePopover.setSrc(cropFileUri.getPath());
                if(imageButton.isEnabled()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageButton.command();
                        }
                    });
                }
            }
        }
    }
    public void cutImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);//剪裁后X的像素
        intent.putExtra("outputY", 250);//剪裁后Y的像素
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        cropFileUri = Uri.fromFile(
                new File(getExternalCacheDir(), new Date().getTime()+".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropFileUri);
        try {
            startActivityForResult(intent, REQUEST_PHOTO_CUT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    final class JavaScriptLocalObj {
        @JavascriptInterface
        public void uploadHtml(String html) throws IOException {
            Log.d(TAG, "uploadHtml: test");
            Map<String,String> forms=new HashMap<>();
            forms.put("authorMail",user.getMail());
            forms.put("passwords",user.getPasswords());
            forms.put("title",titleEdit.getText().toString());
            List<File> imageFileList=new ArrayList<>();
            Document document=Jsoup.parse(html);
            Element content=document.getElementsByClass("simditor-body").first();
            Elements imgs=content.select("img");
            for(Element img:imgs){
                File file=new File(img.attr("src"));
                imageFileList.add(file);
            }
            content.prepend("<h1>"+titleEdit.getText().toString().replace("\n","")+"</h1>");
            String finalContent="<html><body>"+content.html()+"</html></body>";
            forms.put("content",finalContent);
            HttpUtil.doImageFormPost(Configures.URL_UPLOAD_DISCUSS, forms,"images", imageFileList, new Callback() {
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
                                File cache=getExternalCacheDir();
                                for(File file:cache.listFiles()){
                                    file.delete();
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },1000);
                            }
                            else{

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
    }
    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_discuss_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_upload){
            webView.loadUrl("javascript:window.local_obj.uploadHtml(document.getElementsByTagName('html')[0].innerHTML);");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
