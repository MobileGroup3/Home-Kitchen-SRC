package com.backendless.hk3.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.backendless.hk3.login.kitchen.CreateKitchenActivity;

/**
 * Created by zini on 5/26/16.
 */
public class InitializationActivity extends AppCompatActivity {

    private TextView login;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_page);

        login = (TextView) findViewById(R.id.login_initial);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(InitializationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
