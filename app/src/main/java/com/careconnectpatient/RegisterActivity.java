package com.careconnectpatient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.firebase.client.Firebase;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

import database.Doctor;
import database.FirebaseHelper;
import database.Patient;

public class RegisterActivity extends AppCompatActivity {



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mSurnameView;
    private TextView mEmailView;
    private EditText mPasswordView;
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

            /*////////////////// FOR TESTING PURPOSES /////////////////////*/
//            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
//            Patient patient1 = new Patient(name, surname, email, password, gender);
//            long patient_id = db.createPatient(patient1);
//            ArrayList<Patient> allRecords = db.getPatients();
//            db.close();
            /*////////////////// FOR TESTING PURPOSES /////////////////////*/

            mAuthTask = new UserRegisterTask(name, surname, email, password, gender, phone_number);
            mAuthTask.execute((Void) null);
            Toast.makeText(getBaseContext(), "Registration successful!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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

//    private boolean isEmailValid(String email) {
//        Pattern pattern;
//        Matcher matcher;
//        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//        pattern = Pattern.compile(EMAIL_PATTERN);
//        matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    //Android matching class "Patterns"
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mSurname;
        private final String mEmail;
        private final String mPassword;
        private final String mGender;
        private final String mPhoneNumber;

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
        }

        @Override
            protected Boolean doInBackground(Void... params) {
            try {

//                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                String email_string = mEmail.replace(".", "");
                Log.e("NEW_EMAIL", email_string);

                //Firebase implementation
                FirebaseHelper fb = new FirebaseHelper();
                Firebase firebase;

                //Getting user type
                SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                String user_type = sharedPreferences.getString("user_type", null);

                String user_key;

                if (user_type != null) {
                    switch (user_type){
                        case "patient":
                            firebase = new Firebase("https://care-connect.firebaseio.com/patients/" + email_string);
                            Patient patient = new Patient(mName, mSurname, mEmail, mPassword, mGender, mPhoneNumber);
                            fb.newPatient(patient, firebase);
                            user_key = patient.getName();
                            break;
                        case "doctor":
                            firebase = new Firebase("https://care-connect.firebaseio.com/doctors/" + email_string);
                            Doctor doctor = new Doctor(mName, mSurname, mEmail, mPassword, mPhoneNumber);
                            fb.newDoctor(doctor, firebase);
                            user_key = doctor.getName();
                            break;
                        default:
                            user_key = null;
                    }
                }
                else {
                    user_key = null;
                }

//                fb.newPatient(patient, firebase);
//                String patient_key = patient.getName();
//                Log.e("PATIENT KEY", fire);

                if (user_key != null){
                    return true;
                }
                else
                {
                    Thread.sleep(10);
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_connection_issue));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void RegisterBack(View view) {
        final Context context = this;
        Intent intent = new Intent(context, StartActivity.class);
        startActivity(intent);
    }
}
