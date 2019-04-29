package com.corydon.miu;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class HttpUtil {
    private static final String TAG="HttpUtil";
    private static OkHttpClient client;
    private static final Map<String,List<Cookie>> cookieStore=new HashMap<>();

    private static String generateGetParams(String url,Map<String,String> params){
        if(params.size()==0)
            return url;
        StringBuilder stringBuilder=new StringBuilder(url+"?");
        for(Map.Entry<String,String> entry:params.entrySet()){
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        stringBuilder.delete(stringBuilder.lastIndexOf("&"),stringBuilder.length());
        return stringBuilder.toString();
    }

    public static void doGet(String url, Map<String,String> params, Callback callback){
        client=getInstant();
        if(params==null)
            params=new HashMap<>();

        url=generateGetParams(url, params);
        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void doImageFormPost(String url, Map<String, String> formParams,String filesKey,
                               List<File> fileList, Callback callback){
        client=getInstant();
        if(formParams==null)
            formParams=new HashMap<>();
        if(fileList==null)
            fileList=new ArrayList<>();
        if(filesKey ==null)
            filesKey="images";
        MultipartBody.Builder multipartBodyBuilder=new MultipartBody.Builder();
        for(String key:formParams.keySet()){
            multipartBodyBuilder.addFormDataPart(key,formParams.get(key));
        }
        for(File file:fileList){
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            multipartBodyBuilder.addFormDataPart(filesKey,file.getName(),requestBody);
        }
        Request request=new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void doFormPost(String url,Map<String, String> formParams, Callback callback){
        client=getInstant();
        if(formParams==null)
            formParams=new HashMap<>();
        MultipartBody.Builder builder=new MultipartBody.Builder();
        for(Map.Entry<String,String> entry:formParams.entrySet()){
            builder.addFormDataPart(entry.getKey(),entry.getValue());
        }
        Request request=new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static OkHttpClient getInstant(){
        if(client==null){
            synchronized(HttpUtil.class){
                client=new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        String host=url.host();
                        String key=host;
                        if(host.split("\\.").length>2){
                            key=host.substring(host.indexOf(".")+1,host.length());
                            Log.d(TAG, "saveFromResponses: "+key);
                        }
                        for(Cookie cookie:cookies){
                            Log.d(TAG, "saveFromResponse: "+cookie.name()+":"+cookie.value());
                        }
                        cookieStore.put(key,cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        String host=url.host();
                        String key=host;
                        if(host.split(".").length>2){
                            key=host.substring(host.indexOf(".")+1,host.length());
                        }
                        List<Cookie> cookies=cookieStore.get(key);
                        return cookies==null?new ArrayList<>():cookies;
                    }
                })
                .build();
            }
        }
        return client;
    }
}
