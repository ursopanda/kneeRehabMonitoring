package com.careconnectpatient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing Firebase
        Firebase.setAndroidContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // Set up the login form.
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //Hashing password
        final HashCode hashPassword = Hashing.sha1().hashString(password, Charset.defaultCharset());
        String hPass = hashPassword.toString();

        boolean cancel = false;
        View focusView = null;

        // Validation for email and password fields
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            Toast.makeText(getBaseContext(), "Login failed!", Toast.LENGTH_LONG).show();
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, hPass);
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Public class for login login
     */
    public class UserLoginTask {

        private final String mEmail;
        private final String mPassword;
        private ValueEventListener pListner, dListner;
        private Firebase pFirebase, dFirebase;

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String user_type = sharedPreferences.getString("user_type", null);

        /**
         * Class for handling the login
         * @param email - email parameter
         * @param password - password parameter
         */
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;

            if (user_type == null){
                loginFailed();
            }

            switch (user_type){
                case "patient":
                    String patient_email_string = mEmail.replace(".", "");
                    pFirebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                            + patient_email_string);
                    pListner = pFirebase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> pData = (Map<String, Object>)dataSnapshot.getValue();
                            if(userExists(pData)){
                                String tPass = (String)pData.get("password");
                                String tEmail = (String)pData.get("email");
                                showProgress(true);
                                tryPatientLogin(tPass, tEmail);
                                sharedPatient(pData);
                            }
                            else{
                                noUserError();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    break;
                case "doctor":
                    String doctor_email_string = mEmail.replace(".", "");
                    dFirebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                            + doctor_email_string);
                    dListner = dFirebase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> pData = (Map<String, Object>)dataSnapshot.getValue();
                            if(userExists(pData)){
                                String tPass = (String)pData.get("password");
                                String tEmail = (String)pData.get("email");
                                showProgress(true);
                                tryDoctorLogin(tPass, tEmail);
                                sharedDoctor(pData);
                            }
                            else {
                                noUserError();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    break;
                default:
            }
        }

        /**
         * populates error if email address is invalid
         */
        private void noUserError() {
            Toast.makeText(getBaseContext(), "Invalid email!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        /**
         * Method checks whether user exists in database
         * @param pData - user object from database
         */
        private boolean userExists(Map<String, Object> pData) {
            return pData != null;
        }

        /**
         * Method for trying patient login
         * @param fPass - password from input form
         * @param fEmail - email from input form
         */
        public void tryPatientLogin(String fPass, String fEmail){

            boolean isLogged = (fPass.matches(mPassword));

            if(isLogged){
                pFirebase.removeEventListener(pListner);
                Toast.makeText(getBaseContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), DrawerActivity.class);
                startActivity(intent);
            }
            else {
                mEmailView.setText(fEmail);
                pFirebase.removeEventListener(pListner);
                Toast.makeText(getBaseContext(), "Invalid password!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }

        /**
         * Method for trying doctor login
         * @param fPass - password from input form
         * @param mEmail - email from input form
         */
        public void tryDoctorLogin(String fPass, String mEmail){
            boolean isLogged = (fPass.matches(mPassword));

            if(isLogged){
                dFirebase.removeEventListener(dListner);
                Toast.makeText(getBaseContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), DrawerActivity.class);
                startActivity(intent);
            }
            else{
                mEmailView.setText(mEmail);
                pFirebase.removeEventListener(pListner);
                Toast.makeText(getBaseContext(), "Invalid password!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }

        /**
         * Method for populating login error
         */
        public void loginFailed(){
            Toast.makeText(getBaseContext(), "Login failed!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        /**
         * Method for putting patient data in shared preferences
         * @param map - patient object from database
         */
        public void sharedPatient(Map<String, Object> map){
            String name = (String)map.get("name");
            String surname = (String)map.get("surname");
            String gender = (String)map.get("gender");
            String email = (String)map.get("email");
            String phone = (String)map.get("phone_number");

            boolean has_doctor = map.containsKey("doctor_key");
            boolean has_precept = map.containsKey("precept");

            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("patient_email", email);
            editor.putString("patient_name", name);
            editor.putString("patient_surname", surname);
            editor.putString("patient_gender", gender);
            editor.putString("patient_phone", phone);
            if(has_doctor){
                String doc_key = String.valueOf(map.get("doctor_key"));
                editor.putString("doctor_key", doc_key);
            }
            if(has_precept){
                String precept = String.valueOf(map.get("precept"));
                editor.putString("precept", precept);
            }
            editor.apply();
        }

        /**
         * Method for putting patient data in shared preferences
         * @param map - doctor object from database
         */
        public void sharedDoctor(Map<String, Object> map){
            String name = (String)map.get("name");
            String surname = (String)map.get("surname");
            String gender = (String)map.get("gender");
            String email = (String)map.get("email");
            String phone = (String)map.get("phone_number");

            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("doctor_email", email);
            editor.putString("doctor_name", name);
            editor.putString("doctor_surname", surname);
            editor.putString("doctor_gender", gender);
            editor.putString("doctor_phone", phone);
            editor.apply();
        }
    }

    /**
     * Method for pressing back button
     */
    public void LoginBack(View view) {
        final Context context = this;
        Intent intent = new Intent(context, StartActivity.class);
        startActivity(intent);
    }
}

