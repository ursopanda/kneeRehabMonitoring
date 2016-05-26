package com.careconnectpatient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Drawer_fragments.AddPatientFragment;
import Drawer_fragments.BluetoothFragment;
import Drawer_fragments.DoctorHistoryFragment;
import Drawer_fragments.DoctorViewFragment;
import Drawer_fragments.HistoryPatientFragment;
import Drawer_fragments.PatientListFragment;
import Drawer_fragments.PatientViewFragment;
import Drawer_fragments.PreceptAssignFragment;
import Drawer_fragments.PreceptFragment;
import Drawer_fragments.RehabFragment;
import database.Precept;
import database.RehabSession;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddPatientFragment.addPatientListener, PatientListFragment.patientListListener,
        PreceptAssignFragment.preceptAssignListener, PreceptFragment.preceptListener,
        RehabFragment.rehabListener, HistoryPatientFragment.patientHistoryListener,
        DoctorHistoryFragment.DoctorHistoryListener, PatientViewFragment.patientViewListener,
        DoctorViewFragment.doctorViewListener{

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    String email;
    String name;
    String surname;
    String gender;
    String phone;
    String addPatientEmail;
    String doctor_key;
    String precept;
    int duration_minutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing Firebase
        Firebase.setAndroidContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String user_type = sharedPreferences.getString("user_type", null);

        switch (user_type) {
            case "patient":
                //Set the initial (first) fragment for patient
                PreceptFragment patientInitialFragment = new PreceptFragment();
                android.support.v4.app.FragmentTransaction patientFragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                patientFragmentTransaction.replace(R.id.fragment_container, patientInitialFragment);
                patientFragmentTransaction.commit();

                //Showing patients menu items
                navigationView.getMenu().findItem(R.id.nav_rehab).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_history).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_pac_precept).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_bluetooth).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_patient_view).setVisible(true);

                //Getting patient info from shared preferences
                email = sharedPreferences.getString("patient_email", "default@patient.com");
                name = sharedPreferences.getString("patient_name", null);
                surname = sharedPreferences.getString("patient_surname", null);
                gender = sharedPreferences.getString("patient_gender", null);
                phone = sharedPreferences.getString("patient_phone", null);
                doctor_key = sharedPreferences.getString("doctor_key", "no_doctor");
                precept = sharedPreferences.getString("precept", "no_precept");

                //Setting menu name for patient
                String pFullName = name + " " + surname;
                setText(pFullName, email);
                break;

            case "doctor":
                //Set the initial (first) fragment for patient
                AddPatientFragment doctorInitialFragment = new AddPatientFragment();
                android.support.v4.app.FragmentTransaction doctorFragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                doctorFragmentTransaction.replace(R.id.fragment_container, doctorInitialFragment);
                doctorFragmentTransaction.commit();

                //Showing doctor menu items
                navigationView.getMenu().findItem(R.id.nav_add_patient).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_patient_list).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_doc_precept).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_doctor_view).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_doctor_history).setVisible(true);

                //Getting doctor info from shared preferences
                email = sharedPreferences.getString("doctor_email", "default@patient.com");
                name = sharedPreferences.getString("doctor_name", null);
                phone = sharedPreferences.getString("doctor_phone", null);
                surname = sharedPreferences.getString("doctor_surname", null);

                //Creates list for all doctor patients
                createPatientList();

                //Setting menu name for patient
                String dFullName = "Dr. " + name + " " + surname;
                setText(dFullName, email);
                break;
            default:
        }
    }

    public void setText(String fullName, String email) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView pFullName = (TextView) header.findViewById(R.id.drawer_name);
        TextView pEmail = (TextView) header.findViewById(R.id.drawer_email);
        pFullName.setText(fullName);
        pEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Context context = getBaseContext();
                            Intent intent = new Intent(context, StartActivity.class);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Closing Activity")
//                .setMessage("Are you sure you want to close this activity?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rehab) {
            //Set the fragment initially
            RehabFragment fragment = new RehabFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_history) {
            HistoryPatientFragment fragment = new HistoryPatientFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_add_patient) {
            AddPatientFragment fragment = new AddPatientFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_patient_list) {
            PatientListFragment fragment = new PatientListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_pac_precept) {
            PreceptFragment fragment = new PreceptFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_patient_view) {
            PatientViewFragment fragment = new PatientViewFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_doctor_view) {
            DoctorViewFragment fragment = new DoctorViewFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_doc_precept) {
            PreceptAssignFragment fragment = new PreceptAssignFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_doctor_history) {
            DoctorHistoryFragment fragment = new DoctorHistoryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_bluetooth) {
            BluetoothFragment fragment = new BluetoothFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////
                     CREATING AN INITIAL PATIENT LIST FOR DOCTOR AND OTHER PATIENT LIST METHODS
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    //All patient list with pat_keys
    ArrayList<String> allPatients = new ArrayList<>();

    //All patient list with full names
    ArrayList<String> fullPatientsList = getFullPatientsList(allPatients);

    //Method for creating an initial patient list for doctor signed in
    public void createPatientList() {
        String doc_key = email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/");

        Firebase childFirebase = firebase.child("patients");
        childFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("ADDING PATIENT TO LIST", patient);
                if (!allPatients.contains(patient)) {
                    allPatients.add(patient);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Method for getting full name list of patients
    public ArrayList<String> getFullPatientsList(ArrayList<String> arrayList) {
        final ArrayList<String> tmp_list = new ArrayList<>();
        for (String mPatient : arrayList) {
            Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/" + mPatient);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> pData = (Map<String, Object>) dataSnapshot.getValue();
                    tmp_list.add(getPatientFullName(pData));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        return tmp_list;
    }

    //Returning patient full names from database
    public String getPatientFullName(Map<String, Object> map) {
        String name = (String) map.get("name");
        String surname = (String) map.get("surname");
        return name + " " + surname;
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR ADDING A PATIENT
    ////////////////////////////////////////////////////////////////*/

    Firebase addPatFirebase;
    ValueEventListener mListener;

    @Override
    public void isPatient(String tmp_email) {
        addPatientEmail = tmp_email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                + addPatientEmail);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (exists(dataSnapshot)) {
                    checkPatient();
                } else {
                    patientDoesNotExistError();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Method for checking whether there is a patient already added for doctor
    public void checkPatient() {
        String doc_key = email.replace(".", "");
        addPatFirebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/patients/"
                + addPatientEmail);
        mListener = addPatFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (exists(dataSnapshot)) {
                    patientAlreadyAddedError();
                } else {
                    addPatient(addPatientEmail);
                    patientAdded();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
//        firebase.removeEventListener(mListener);
    }

    //Method for adding a patient for the doctor
    public void addPatient(String patientEmail) {
        //Getting doctor id_key
        String doc_key = email.replace(".", "");
        //Getting patient id_key
        String pat_key = patientEmail.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key
                + "/patients/" + pat_key);
        firebase.setValue(pat_key);
        //Assigning doc_key to patient
        Firebase patFirebase = new Firebase("https://care-connect.firebaseio.com/patients/" + pat_key + "/doctor_key");
        patFirebase.setValue(doc_key);
    }

    //Method returning is there such a patient added or not
    public boolean exists(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue() != null;
    }

    public void patientAlreadyAddedError() {
        Toast.makeText(getBaseContext(), "Patient is already added!", Toast.LENGTH_SHORT).show();
    }

    public void patientDoesNotExistError() {
        Toast.makeText(getBaseContext(), "Patient does not exist!", Toast.LENGTH_SHORT).show();
    }

    public void patientAdded() {
        addPatFirebase.removeEventListener(mListener);
        Toast.makeText(getBaseContext(), "Patient added!", Toast.LENGTH_SHORT).show();
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR PATIENT LIST
    ////////////////////////////////////////////////////////////////*/

    ChildEventListener cListener;
    Firebase childFirebase;
    final ArrayList<String> doctorPatients = new ArrayList<>();

    @Override
    public void populateList(ListView listView) {
//        final ArrayList<String> patients = new ArrayList<>();
        String doc_key = email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/");

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doctorPatients);
        listView.setAdapter(adapter);

        childFirebase = firebase.child("patients");
        cListener = childFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("ADDING PATIENT TO LIST", patient);
                if (!doctorPatients.contains(patient)) {
                    doctorPatients.add(patient);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("REMOVING PATIENT", patient);
                doctorPatients.remove(patient);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //childFirebase.removeEventListener(cListener);
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR ASSIGNING PRECEPT
    ////////////////////////////////////////////////////////////////*/

    Firebase preceptFirebase;
    ValueEventListener precpetListener;

    @Override
    public void assignPrecept(String patient_email, int o_angle, int m_angle, int duration, int frequency) {
        preceptFirebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                + patient_email + "/precept");
        final Precept precept = new Precept(o_angle, m_angle, duration, frequency);

        precpetListener = preceptFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (exists(dataSnapshot)) {
                    preceptExistsError();
                } else {
                    preceptFirebase.setValue(precept);
                    preceptAdded();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void preceptExistsError() {
        preceptFirebase.removeEventListener(precpetListener);
        Toast.makeText(getBaseContext(), "Precept for patient already added!", Toast.LENGTH_SHORT).show();
    }

    public void preceptAdded() {
        preceptFirebase.removeEventListener(precpetListener);
        Toast.makeText(getBaseContext(), "Precept added!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void populateSpinner(Spinner spinner) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                allPatients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void removeAssignment(final String patient_key, Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Precept")
                .setMessage("Are you sure you want to remove the precept?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                                + patient_key + "/precept");
                        Log.v("REMOVING PRECEPT FOR", patient_key);
                        firebase.removeValue();
                        Toast.makeText(getBaseContext(), "Precept removed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    /*/////////////////////////////////////////////////////////////////////////
                      METHODS FOR SHOWING PRECEPT FOR PATIENT
    /////////////////////////////////////////////////////////////////////////*/

    public static boolean isNotEmptyString(final String string) {
//        return !Strings.isNullOrEmpty(string) && !string.trim().isEmpty();
        return string != null && !string.isEmpty() && !string.trim().isEmpty();
    }

    @Override
    public void isPrecept(final View view) {
        String no_precept = "no_precept";
        String no_doctor = "no_doctor";
        Log.v("DOC_KEY", doctor_key);
        if (!doctor_key.equals(no_doctor)) {
            Firebase docFirebas = new Firebase("https://care-connect.firebaseio.com/doctors/" + doctor_key);
            docFirebas.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Long> pData = (Map<String, Long>) dataSnapshot.getValue();
                    populatePreceptDoc(pData, view);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } else {
            noDoctor();
            return;
        }

        Log.v("PRECEPT", precept);
//        if (precept.trim().equals(no_precept.trim())) {
        if (!precept.equals(no_precept)) {
            String pat_key = email.replace(".", "");
            Firebase patFirebase = new Firebase("https://care-connect.firebaseio.com/patients/" + pat_key + "/precept");
            patFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Long> pData = (Map<String, Long>) dataSnapshot.getValue();
                    populatePreceptInfo(pData, view);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        } else {
            populatePreceptError();
        }
    }


    private void populatePreceptDoc(Map<String, Long> map, View view) {
        String fullName = String.valueOf(map.get("name")) + " " + String.valueOf(map.get("surname"));
        String doc_phone = String.valueOf(map.get("phone_number"));


        TextView nameview = (TextView) view.findViewById(R.id.pac_precept_doc_name);
        TextView phoneView = (TextView) view.findViewById(R.id.pac_precept_doc_nr);

        nameview.setText(fullName);
        phoneView.setText(doc_phone);
    }

    private void populatePreceptInfo(Map<String, Long> map, View view) {
        String o_angle = String.valueOf(map.get("optimal_angle"));
        String m_angle = String.valueOf(map.get("maximal_angle"));
        String duration = String.valueOf(map.get("duration"));
        String frequency = String.valueOf(map.get("frequency"));
        duration_minutes = Integer.parseInt(duration);

        TextView oAngleView = (TextView) view.findViewById(R.id.pac_precept_optimal);
        TextView mAngleView = (TextView) view.findViewById(R.id.pac_precept_maximal);
        TextView durationView = (TextView) view.findViewById(R.id.pac_precept_duration);
        TextView frequencyView = (TextView) view.findViewById(R.id.pac_precept_frequency);

        oAngleView.setText(o_angle);
        mAngleView.setText(m_angle);
        durationView.setText(duration);
        frequencyView.setText(frequency);

    }

    private void populatePreceptError() {
        Toast.makeText(getBaseContext(), "No precept assigned!", Toast.LENGTH_SHORT).show();
    }

    private void noDoctor() {
        Toast.makeText(getBaseContext(), "No doctor!", Toast.LENGTH_SHORT).show();
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR REHAB FRAGMENT
    ////////////////////////////////////////////////////////////////*/

    //Global variable for count down timer
    CountDownTimer countDownTimer;
    long millisLeft;
    String rehab_duration;
    //Converting duration minutes to milliseconds
    long full_duration_millis;
    String rehab_comment;
    @Override
    public void timer(final View view) {
        full_duration_millis = TimeUnit.MINUTES.toMillis(duration_minutes);
        final Button startButton = (Button) view.findViewById(R.id.start_button);
        final Button stopButton = (Button) view.findViewById(R.id.stop_button);
        final Button finishButton = (Button) view.findViewById(R.id.finish_button);
        final Button resetButton = (Button) view.findViewById(R.id.reset_button);
        final EditText comment = (EditText) view.findViewById(R.id.rehab_comment);
        comment.setFocusableInTouchMode(false);
        resetButton.setEnabled(false);
        finishButton.setEnabled(false);
        stopButton.setEnabled(false);
        //Setting full duration TextView 00:00:00 to patients assigned one
        final String full_duration = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(full_duration_millis),
                TimeUnit.MILLISECONDS.toMinutes(full_duration_millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(full_duration_millis)),
                TimeUnit.MILLISECONDS.toSeconds(full_duration_millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(full_duration_millis)));

        final TextView countdownTimerText = (TextView) view.findViewById(R.id.countdownText);
        countdownTimerText.setText(full_duration);

        if(duration_minutes != 0){
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    finishButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    startTimer(full_duration_millis, countdownTimerText);
                }
            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long time_spent = full_duration_millis - millisLeft;
                    rehab_duration = String.format("%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(time_spent),
                            TimeUnit.MILLISECONDS.toSeconds(time_spent) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time_spent))
                    );
                    Log.v("REHAB DURATION", rehab_duration);
                    comment.setFocusableInTouchMode(true);
                    resetButton.setEnabled(true);
                    finishButton.setEnabled(true);
                    countDownTimer.cancel();
                }
            });

            //On click listener for reset button
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    millisLeft = 0;
                    comment.setText("");
                    countdownTimerText.setText(full_duration);
                    startButton.setEnabled(true);
                    finishButton.setEnabled(false);
                }
            });
        }
        else{
            //No precept found for patient
            //Disables all buttons nad throws Toast message
            resetButton.setEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
            finishButton.setEnabled(false);
            populatePreceptError();
        }
    }

    public void startTimer(long millis, final TextView timerText){
        countDownTimer = new CountDownTimer(millis + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                timerText.setText(hms);
                millisLeft = millisUntilFinished;
                Log.v("Millis left", String.valueOf(millisLeft));
            }

            @Override
            public void onFinish() {
                timerText.setText("TIME'S UP!!");
            }
        }.start();
    }

    @Override
    public void finishRehab(Context context) {
        final EditText comment = (EditText) findViewById(R.id.rehab_comment);
        rehab_comment = comment.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String strDate = sdf.format(new Date());
        Log.v("REHAB DATE", strDate);
        Log.v("REHAB COMMENT", rehab_comment);

        new AlertDialog.Builder(context)
                .setTitle("Finish Rehab Session")
                .setMessage("Do you want to finish your rehab session?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: add exceeded max
                        //For testing purposes
                        RehabSession session = new RehabSession(rehab_comment, strDate, rehab_duration, 0);
//                        RehabSession session = new RehabSession(rehab_comment, strDate, rehab_duration);

                        String patient_key = email.replace(".", "");
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                        + patient_key + "/rehab");
                        firebase.push().setValue(session);
                        Toast.makeText(getBaseContext(), "Rehab session saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Rehab session not saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR PATIENT HISTORY FRAGMENT
    ////////////////////////////////////////////////////////////////*/
    HistoryListAdapter historyAdapter;
    List<RehabSession> patientHistoryList;


    @Override
    public void populateHistoryList(ListView listview) {
        patientHistoryList = new ArrayList<>();
        historyAdapter = new HistoryListAdapter(getApplicationContext(), patientHistoryList);
        String pat_key = email.replace(".","");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                + pat_key + "/rehab");
        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> pData = (Map<String, Object>) dataSnapshot.getValue();
                addSessionToHistory(pData);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        listview.setAdapter(historyAdapter);
    }

    private void addSessionToHistory(Map<String, Object> map) {
        String comment = (String) map.get("comment");
        String date = (String) map.get("date");
        String duration = (String) map.get("duration");
        Long exceeded = (Long) map.get("exceeded_max");
        int exceeded_max = Ints.checkedCast(exceeded);
        RehabSession rSession = new RehabSession(comment,date,duration,exceeded_max);
//        RehabSession rSession = new RehabSession(comment,date,duration);
        patientHistoryList.add(rSession);
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR DOCTOR HISTORY FRAGMENT
    ////////////////////////////////////////////////////////////////*/

    @Override
    public void populateDoctorHistoryList(ListView listview, String patient_key) {


        patientHistoryList = new ArrayList<>();
        historyAdapter = new HistoryListAdapter(getApplicationContext(), patientHistoryList);
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                + patient_key + "/rehab");
        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> pData = (Map<String, Object>) dataSnapshot.getValue();
                addSessionToHistory(pData);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        listview.setAdapter(historyAdapter);
    }

    @Override
    public void populateHistorySpinner(Spinner spinner) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                allPatients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR PATIENT VIEW FRAGMENT
    ////////////////////////////////////////////////////////////////*/

    Firebase changeFirebase;
    ValueEventListener changeListener;

    @Override
    public void deleteAccount(Context context) {
        final String patient_key = email.replace(".","");
        new AlertDialog.Builder(context)
                .setTitle("Delete Account")
                .setMessage("Do you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                                + patient_key);
                        Log.v("DELETING ACCOUNT", patient_key);
                        firebase.removeValue();
                        Toast.makeText(getBaseContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    @Override
    public void changePassword(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Change Password");
        final EditText oldPass = new EditText(context);
        final EditText newPass = new EditText(context);
        final EditText confirmPass = new EditText(context);

        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll=new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40,0,40,0);

        ll.addView(oldPass);

        ll.addView(newPass);
        ll.addView(confirmPass);
        alertDialog.setView(ll);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String patient_key = email.replace(".","");
                        final String mOldPass = oldPass.getText().toString();
                        final String mNewPass = newPass.getText().toString();
                        final String mConfirmPass = confirmPass.getText().toString();
                        changeFirebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                                + patient_key);
                        changeListener = changeFirebase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> pData = (Map<String, Object>) dataSnapshot.getValue();
                                if(initialPassMatches(pData, mOldPass)){
                                    if(mNewPass.equals(mConfirmPass)){
                                        changePass(mNewPass, patient_key);
                                    }
                                    else{
                                        newPAssError();
                                    }
                                }
                                else{
                                    initialPassError();
                                }
                            }

                            private boolean initialPassMatches(Map<String, Object> map, String oldPass) {
                                String password = (String) map.get("password");
                                final HashCode hashPassword = Hashing.sha1().hashString(oldPass, Charset.defaultCharset());
                                String hPass = hashPassword.toString();
                                return hPass.equals(password);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }

                    private void changePass(String mNewPass, String user) {
                        final HashCode hashPassword = Hashing.sha1().hashString(mNewPass, Charset.defaultCharset());
                        String hPass = hashPassword.toString();
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/");
                        Firebase passwordRef = firebase.child(user);
                        Map<String, Object> new_password = new HashMap<String, Object>();
                        new_password.put("password", hPass);
                        passwordRef.updateChildren(new_password);
                        Toast.makeText(getBaseContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                        changeFirebase.removeEventListener(changeListener);
                    }

                    private void newPAssError() {
                        Toast.makeText(getBaseContext(), "New passwords don't match!", Toast.LENGTH_SHORT).show();
                    }

                    private void initialPassError() {
                        Toast.makeText(getBaseContext(), "Invalid old password!", Toast.LENGTH_SHORT).show();
                        changeDocFirebase.removeEventListener(changeDocListener);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }

    @Override
    public void populatePatientView(View view) {
        String full_name = name + " " + surname;
        TextView fullNameView = (TextView) view.findViewById(R.id.pat_view_name_surname);
        TextView genderView = (TextView) view.findViewById(R.id.pat_view_gender);
        TextView emailView = (TextView) view.findViewById(R.id.pat_view_email);
        TextView phoneView = (TextView) view.findViewById(R.id.pat_view_phone_number);
        phoneView.setText(phone);
        fullNameView.setText(full_name);
        genderView.setText(gender);
        emailView.setText(email);
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR DOCTOR VIEW FRAGMENT
    ////////////////////////////////////////////////////////////////*/

    Firebase changeDocFirebase;
    ValueEventListener changeDocListener;

    @Override
    public void deleteDocAccount(Context context) {
        final String doctor_key = email.replace(".","");
        new AlertDialog.Builder(context)
                .setTitle("Delete Account")
                .setMessage("Do you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                                + doctor_key);
                        Log.v("DELETING ACCOUNT", doctor_key);
                        firebase.removeValue();
                        Toast.makeText(getBaseContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    @Override
    public void changeDocPassword(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Change Password");
        final EditText oldPass = new EditText(context);
        final EditText newPass = new EditText(context);
        final EditText confirmPass = new EditText(context);

        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll=new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40,0,40,0);

        ll.addView(oldPass);

        ll.addView(newPass);
        ll.addView(confirmPass);
        alertDialog.setView(ll);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String doctor_key = email.replace(".","");
                        final String mOldPass = oldPass.getText().toString();
                        final String mNewPass = newPass.getText().toString();
                        final String mConfirmPass = confirmPass.getText().toString();
                        changeDocFirebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                                + doctor_key);
                        changeDocListener = changeDocFirebase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> pData = (Map<String, Object>) dataSnapshot.getValue();
                                if(initialPassMatches(pData, mOldPass)){
                                    if(mNewPass.equals(mConfirmPass)){
                                        changePass(mNewPass, doctor_key);
                                    }
                                    else{
                                        newPAssError();
                                    }
                                }
                                else{
                                    initialPassError();
                                }
                            }

                            private boolean initialPassMatches(Map<String, Object> map, String oldPass) {
                                String password = (String) map.get("password");
                                final HashCode hashPassword = Hashing.sha1().hashString(oldPass, Charset.defaultCharset());
                                String hPass = hashPassword.toString();
                                return hPass.equals(password);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }

                    private void changePass(String mNewPass, String user) {
                        final HashCode hashPassword = Hashing.sha1().hashString(mNewPass, Charset.defaultCharset());
                        String hPass = hashPassword.toString();
                        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/");
                        Firebase passwordRef = firebase.child(user);
                        Map<String, Object> new_password = new HashMap<>();
                        new_password.put("password", hPass);
                        passwordRef.updateChildren(new_password);
                        Toast.makeText(getBaseContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                        changeDocFirebase.removeEventListener(changeDocListener);
                    }

                    private void newPAssError() {
                        Toast.makeText(getBaseContext(), "New passwords don't match!", Toast.LENGTH_SHORT).show();
                    }

                    private void initialPassError() {
                        Toast.makeText(getBaseContext(), "Invalid old password!", Toast.LENGTH_SHORT).show();
                        changeDocFirebase.removeEventListener(changeDocListener);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }

    @Override
    public void populateDoctorView(View view) {
        String full_name = name + " " + surname;
        TextView fullNameView = (TextView) view.findViewById(R.id.doc_view_name_surname);
        TextView emailView = (TextView) view.findViewById(R.id.doc_view_email);
        TextView phoneView = (TextView) view.findViewById(R.id.doc_view_phone_number);
        phoneView.setText(phone);
        fullNameView.setText(full_name);
        emailView.setText(email);
    }
}
