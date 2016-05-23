package com.careconnectpatient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Map;

import Drawer_fragments.AddPatientFragment;
import Drawer_fragments.BluetoothFragment;
import Drawer_fragments.HistoryFragment;
import Drawer_fragments.PatientListFragment;
import Drawer_fragments.PreceptAssignFragment;
import Drawer_fragments.PreceptFragment;
import Drawer_fragments.RehabFragment;
import database.Precept;

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

                //Creates list for all doctor patients
                createPatientList();

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

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////
                     CREATING AN INITIAL PATIENT LIST FOR DOCTOR AND OTHER PATIENT LIST METHODS
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    //All patient list with pat_keys
    ArrayList<String> allPatients = new ArrayList<>();

    //All patient list with full names
    ArrayList<String> fullPatientsList = getFullPatientsList(allPatients);

    //Method for creating an initial patient list for doctor signed in
    public void createPatientList(){
        String doc_key = email.replace(".", "");
        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
                + doc_key + "/");

        Firebase childFirebase = firebase.child("patients");
        childFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String patient = (String) dataSnapshot.getValue();
                Log.v("ADDING PATIENT TO LIST", patient);
                if(!allPatients.contains(patient)){
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
    public ArrayList<String> getFullPatientsList(ArrayList<String> arrayList){
        final ArrayList<String> tmp_list = new ArrayList<>();
        for (String mPatient : arrayList){
            Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/" + mPatient);
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> pData = (Map<String, Object>)dataSnapshot.getValue();
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
    public String getPatientFullName(Map<String, Object> map){
        String name = (String)map.get("name");
        String surname = (String)map.get("surname");
        return name + " " + surname;
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
        //Assigning doc_key to patient
        Firebase patFirebase = new Firebase("https://care-connect.firebaseio.com/patients/" + pat_key + "/doctor_key");
        patFirebase.setValue(doc_key);
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
                if(!doctorPatients.contains(patient)){
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
                if(exists(dataSnapshot)){
                    preceptExistsError();
                }
                else{
                    preceptFirebase.setValue(precept);
                    preceptAdded();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void preceptExistsError(){
        preceptFirebase.removeEventListener(precpetListener);
        Toast.makeText(getBaseContext(), "Precept for patient already added!", Toast.LENGTH_SHORT).show();
    }

    public void preceptAdded(){
        preceptFirebase.removeEventListener(precpetListener);
        Toast.makeText(getBaseContext(), "Precept added!", Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void populateSpinner(Spinner spinner) {
//        String doc_key = email.replace(".", "");
//        final ArrayList<String> patients = new ArrayList<>();
//
//        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patients);
////        final ArrayAdapter<String> finalAdapter = new ArrayAdapter<>(
////                this,
////                android.R.layout.simple_spinner_item,
////                updateArray(patients));
//
//        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/doctors/"
//                + doc_key + "/patients/");
//
//        firebase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String patient = (String) dataSnapshot.getValue();
//                patients.add(patient);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//    }

    @Override
    public void populateSpinner(Spinner spinner){
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

    /*////////////////////////////////////////////////////////////////
                      METHODS FOR ASSIGNING PRECEPT
    ////////////////////////////////////////////////////////////////*/

}
