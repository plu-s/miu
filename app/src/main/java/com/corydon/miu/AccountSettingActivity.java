package com.corydon.miu;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.corydon.miu.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountSettingActivity extends AppCompatActivity {
    @BindView(R.id.nameInput)
    EditText nameInput;
    @BindView(R.id.passwordsInput)
    EditText passwordsInput;
    @BindView(R.id.submitButton)
    Button submitButton;
    private Handler handler=new Handler();
    User user=MainActivity.user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        ButterKnife.bind(this);
        init();
    }

    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init(){
        nameInput.setText(user.getName());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName=nameInput.getText().toString();
                String newPasswords=passwordsInput.getText().toString();
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",user.getMail());
                forms.put("name",newName);
                forms.put("oldPasswords",user.getPasswords());
                forms.put("newPasswords",newPasswords);
                HttpUtil.doFormPost(Configures.URL_ACCOUNT_SETTING, forms, new Callback() {
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
                                    user.setName(newName);
                                    user.setPasswords(newPasswords);
                                    toast(object.getString("data"));
                                    finish();
                                }
                                else
                                    toast(object.getString("data"));
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
        });
    }
}
