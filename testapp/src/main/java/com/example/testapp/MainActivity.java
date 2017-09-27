package com.example.testapp;

import android.accounts.AccountManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.accountmodule.ApmManager;
import com.example.accountmodule.base.IAccountManager;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        ApmManager apmManager=ApmManager.getInstance();
        UserBean userBean = apmManager.setAccountBehavior(IAccountManager.AccountBehavior.LOGIN)
                .setup(UserBean.class);


    }

    private void doLogin() {
        try {

            Callable<UserBean> callable = new Callable<UserBean>() {
                @Override
                public UserBean call() throws Exception {
                    Thread.sleep(3000);
                    //Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                    return new UserBean();
                }
            };
            Future<UserBean> beanFuture = Executors.newSingleThreadExecutor().submit(callable);
            UserBean userBean = beanFuture.get();
            if (userBean!=null){
                Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show();
            }

            System.out.println("finish");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class UserBean{

    }
}
