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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Map;

import database.Doctor;
import database.Patient;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mSurnameView;
    private TextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private Spinner mGenderView;
    private EditText mPhoneNumberView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing Firebase
        Firebase.setAndroidContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameView = (EditText) findViewById(R.id.register_name);
        mSurnameView = (EditText) findViewById(R.id.register_surname);
        mEmailView = (TextView) findViewById(R.id.register_email);
        mGenderView = (Spinner) findViewById(R.id.gender_spinner);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mPhoneNumberView = (EditText) findViewById(R.id.register_phone_number);

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

    }

    private void attemptRegister() {
        final Context context = this;
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String surname = mSurnameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String gender = mGenderView.getSelectedItem().toString();
        String phone_number = mPhoneNumberView.getText().toString();
        String confirm_password = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for valid Name
        if(TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
        }

        //Check for valid Surname
        if(TextUtils.isEmpty(surname)) {
            mSurnameView.setError(getString(R.string.error_field_required));
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(confirm_password)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if(!password.matches(confirm_password)){
            mConfirmPasswordView.setError(getString(R.string.error_confirm_password));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone_number)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            Toast.makeText(getBaseContext(), "Registration failed!", Toast.LENGTH_LONG).show();
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserRegisterTask(name, surname, email, password, gender, phone_number);
        }
    }


    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    //Android matching class "Patterns"
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        String pattern_advanced = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$";
        return password.matches(pattern);
    }


    public class UserRegisterTask {

        private final String mName;
        private final String mSurname;
        private final String mEmail;
        private final String mPassword;
        private final String mGender;
        private final String mPhoneNumber;

        private ValueEventListener patientListener, doctorListener;
        private Firebase patientFirebase, doctorFirebase;

        UserRegisterTask(String name, String surname, String email, String password, String gender, String phone) {
            //Hashing password
            final HashCode hashPassword = Hashing.sha1().hashString(password, Charset.defaultCharset());
            String hPass = hashPassword.toString();
            mName = name;
            mSurname = surname;
            mEmail = email;
            mPassword = hPass;
            mGender = gender;
            mPhoneNumber = phone;

            String email_string = mEmail.replace(".", "");
            Log.e("NEW_EMAIL", email_string);

            //Getting user type
            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
            String user_type = sharedPreferences.getString("user_type", null);

            String user_key;

            if (user_type != null) {
                switch (user_type){
                    case "patient":
                        patientFirebase = new Firebase("https://care-connect.firebaseio.com/patients/" + email_string);
                        patientListener = patientFirebase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> pData = (Map<String, Object>)dataSnapshot.getValue();
                                if(userExists(pData)){
                                    emailExistsError();
                                }
                                else{
                                    addPatient(patientFirebase);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                        break;
                    case "doctor":
                        doctorFirebase = new Firebase("https://care-connect.firebaseio.com/doctors/" + email_string);
                        doctorListener = doctorFirebase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> pData = (Map<String, Object>)dataSnapshot.getValue();
                                if(userExists(pData)){
                                    emailExistsError();
                                }
                                else{
                                    addDoctor(doctorFirebase);
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

        }

        private void emailExistsError() {
            Toast.makeText(getBaseContext(), "Email address is already registered!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
            startActivity(intent);
        }

        private void addPatient(Firebase firebase) {
//            FirebaseHelper fb = new FirebaseHelper();
            Patient patient = new Patient(mName, mSurname, mEmail, mPassword, mGender, mPhoneNumber);
//            fb.newPatient(patient, firebase);
            firebase.setValue(patient);
            patientFirebase.removeEventListener(patientListener);

            Toast.makeText(getBaseContext(), "Registration successful!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        private void addDoctor(Firebase firebase) {
            doctorFirebase.removeEventListener(doctorListener);
//            FirebaseHelper fb = new FirebaseHelper();
            Doctor doctor = new Doctor(mName, mSurname, mEmail, mPassword, mPhoneNumber);
//            fb.newDoctor(doctor, firebase);
            firebase.setValue(doctor);

            Toast.makeText(getBaseContext(), "Registration successful!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        private boolean userExists(Map<String, Object> pData) {
            return pData != null;
        }
    }

    public void RegisterBack(View view) {
        final Context context = this;
        Intent intent = new Intent(context, StartActivity.class);
        startActivity(intent);
    }
}