package com.careconnectpatient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.client.Firebase;

public class UserPickActivity extends AppCompatActivity {

    private String PAT = "patient";
    private String DOC = "doctor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializing Firebase
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick);
    }

    public void StartPatient(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("user_type", PAT);
        editor.apply();

        final Context context = this;
        Intent intent = new Intent(context, StartActivity.class);
        startActivity(intent);
    }

    public void StartDoctor(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_type", DOC);
        editor.apply();

        //TODO: create doctor login/register
        final Context context = this;
        Intent intent = new Intent(context, StartActivity.class);
        startActivity(intent);
    }
}

