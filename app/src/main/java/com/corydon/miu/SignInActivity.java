package com.corydon.miu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

public class SignInActivity extends AppCompatActivity {
    @BindView(R.id.mail_input)
    EditText mailInput;
    @BindView(R.id.passwords_input)
    EditText passwordsInput;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.sign_up_link)
    TextView signUpLink;
    @BindView(R.id.rememberPass)
    CheckBox checkBox;
    private Handler handler=new Handler();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPassRemember = pref.getBoolean("remember_password", false);
        if (isPassRemember)
        {
            String usermail = pref.getString("usermail", "");
            String password = pref.getString("password", "");
            mailInput.setText(usermail);
            passwordsInput.setText(password);
            checkBox.setChecked(true);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> forms=new HashMap<>();
                forms.put("mail",mailInput.getText().toString());
                forms.put("passwords",passwordsInput.getText().toString());
                HttpUtil.doFormPost(Configures.URL_SIGN_IN, forms, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toast("请连接网络重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson=new Gson();
                        if(response.code()==200){
                            try{
                                JSONObject object=new JSONObject(response.body().string());
                                int result=object.getInt("result");
                                if(result==Configures.RESULT_OK){

                                    editor = pref.edit();
                                    if (checkBox.isChecked())
                                    {
                                        editor.putBoolean("remember_password", true);
                                        editor.putString("usermail", mailInput.getText().toString());
                                        editor.putString("password", passwordsInput.getText().toString());
                                    }else{
                                        editor.clear();
                                    }
                                    editor.apply();

                                    User user=gson.fromJson(object.getString("data"),User.class);
                                    startMailActivity(user);
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
        });
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUpActivity();
            }
        });
    }

    private void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startMailActivity(User user){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    private void startSignUpActivity(){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}
