package com.shixian.android.client.activities;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.LoginUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;


public class LoginActivity extends Activity
{
    private String TAG = "LoginActivity";
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button main_login_btn= (Button) findViewById(R.id.main_login_btn);
        main_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mAccessToken=AccessTokenKeeper.readAccessToken(LoginActivity.this);
                if(mAccessToken!=null&&!mAccessToken.isSessionValid())
                    LoginUtil.getToken(LoginActivity.this, new AuthListener());
                else{
                    //说明token还没有过期
                    CommonUtil.logUtil(TAG,mAccessToken.getToken());
                }
            }
        });

        //大哥我先把这里注释掉了 到时候再打开阿
      /*  new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 跳过登录
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }, 1000);*/

    }

    //微薄登陆回调类
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                //从服务器请求cookie？？

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
