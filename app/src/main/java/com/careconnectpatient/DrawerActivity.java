package com.careconnectpatient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import Drawer_fragments.AddPatientFragment;
import Drawer_fragments.BluetoothFragment;
import Drawer_fragments.HistoryFragment;
import Drawer_fragments.PatientListFragment;
import Drawer_fragments.PreceptAssignFragment;
import Drawer_fragments.PreceptFragment;
import Drawer_fragments.RehabFragment;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddPatientFragment.addPatientListener, PatientListFragment.patientListListener,
        PreceptAssignFragment.preceptAssignListener{

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    String email;
    String name;
    String surname;
    String addPatientEmail;

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

        switch (user_type){
            case "patient":
                //Set the initial (first) fragment for patient
                RehabFragment patientInitialFragment = new RehabFragment();
                android.support.v4.app.FragmentTransaction patientFragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                patientFragmentTransaction.replace(R.id.fragment_container, patientInitialFragment);
                patientFragmentTransaction.commit();

                //Showing patients menu items
                navigationView.getMenu().findItem(R.id.nav_rehab).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_history).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_pac_precept).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_bluetooth).setVisible(true);

                //Getting patient info from shared preferences
                email = sharedPreferences.getString("patient_email", "default@patient.com");
                name = sharedPreferences.getString("patient_name", null);
                surname = sharedPreferences.getString("patient_surname", null);

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

                //Getting doctor info from shared preferences
                email = sharedPreferences.getString("doctor_email", "default@patient.com");
                name = sharedPreferences.getString("doctor_name", null);
                surname = sharedPreferences.getString("doctor_surname", null);

                //Setting menu name for patient
                String dFullName = "Dr. " + name + " " + surname;
                setText(dFullName, email);
                break;
            default:
        }

//        //TODO: needs to be redone
//        //Sending data to RehabFragment
//        PreceptFragment precept = new PreceptFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("patient_email", email);
//        precept.setArguments(bundle);
//        //Sending via callback
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, precept);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    public void setText(String fullName, String email){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView pFullName = (TextView)header.findViewById(R.id.drawer_name);
        TextView pEmail = (TextView)header.findViewById(R.id.drawer_email);
        pFullName.setText(fullName);
        pEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
            HistoryFragment fragment = new HistoryFragment();
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
        }
        else if (id == R.id.nav_patient_list) {
            PatientListFragment fragment = new PatientListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_pac_precept) {
            PreceptFragment fragment = new PreceptFragment();
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

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR ADDING A PATIENT
    ////////////////////////////////////////////////////////////////*/

    Firebase addPatFirebase;
    ValueEventListener mListener;

    @Override
    public void isPatient(String tmp_email){
        addPatientEmail = tmp_email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/"
                + addPatientEmail);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(exists(dataSnapshot)){
                    checkPatient();
                }
                else {
                    patientDoesNotExistError();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Method for checking whether there is a patient already added for doctor
    public void checkPatient(){
        String doc_key = email.replace(".", "");
        addPatFirebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/patients/"
                + addPatientEmail);
        mListener = addPatFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (exists(dataSnapshot)){
                    patientAlreadyAddedError();
                }
                else {
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
    }

    //Method returning is there such a patient added or not
    public boolean exists(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue() != null;
    }

    public void patientAlreadyAddedError(){
        Toast.makeText(getBaseContext(), "Patient is already added!", Toast.LENGTH_SHORT).show();
    }

    public void patientDoesNotExistError(){
        Toast.makeText(getBaseContext(), "Patient does not exist!", Toast.LENGTH_SHORT).show();
    }
    public void patientAdded(){
        addPatFirebase.removeEventListener(mListener);
        Toast.makeText(getBaseContext(), "Patient added!", Toast.LENGTH_SHORT).show();
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR PATIENT LIST
    ////////////////////////////////////////////////////////////////*/

    ChildEventListener cListener;
    Firebase childFirebase;

    @Override
    public void populateList(ListView listView) {
        final ArrayList<String> patients = new ArrayList<>();
        String doc_key = email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/");

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patients);
        listView.setAdapter(adapter);

        childFirebase = firebase.child("patients");
        cListener = childFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("ADDING PATIENT TO LIST", patient);
                patients.add(patient);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("REMOVING PATIENT", patient);
                patients.remove(patient);
                adapter.notifyDataSetChanged();
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
        //childFirebase.removeEventListener(cListener);
    }

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR ASSIGNING PRECEPT
    ////////////////////////////////////////////////////////////////*/

    @Override
    public void assignPrecept(int o_angle, int m_angle, int duration, int frequency) {

    }

    @Override
    public void populateSpinner(Spinner spinner) {
        String doc_key = email.replace(".", "");
        final ArrayList<String> patients = new ArrayList<>();
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/patients/");

        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                patients.add(patient);
                adapter.notifyDataSetChanged();
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
        spinner.setAdapter(adapter);
    }
}
