package com.careconnectpatient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.client.Firebase;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing Firebase
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //Login button press
    public void StartLogin(View view) {
        final Context context = this;
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    //Register button press
    public void StartRegister(View view) {
        final Context context = this;
        Intent intent = new Intent(context, RegisterActivity.class);
        startActivity(intent);
    }
}
