package database;

import com.firebase.client.Firebase;

/**
 * Created by austr on 17/05/2016.
 */
public class FirebaseHelper {
    private Firebase baseKeeper = new Firebase("https://care-connect.firebaseio.com/");


    public FirebaseHelper(){}

    public void newPatient(Patient patient, Firebase firebase){
        firebase.setValue(patient);
    }

    public void newDoctor(Doctor doctor, Firebase firebase){
        firebase.setValue(doctor);
    }


//    public void checkPatientLogin(String email, String password){
//        String email_string = email.replace(".", "");
//        final Firebase firebase = new Firebase("https://care-connect.firebaseio.com/patients/" + email_string);
//        firebase.addValueEventListener(new ValueEventListener() {
//            String fPassword;
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                String patientEmail = firebase.child("email").getValue(String.class);
//                fPassword = getEmailFromHash(dataSnapshot);
//                Log.e("HASHED_PASSWORD", fPassword);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//    }
//
//    private String getEmailFromHash(DataSnapshot data){
//        String fPassword;
//        Object o = data.getValue();
//        if (o instanceof HashMap) {
//            HashMap dataHash = (HashMap) o;
//            if(dataHash.containsKey("password")) {
//                fPassword = (String)dataHash.get("password");
//                return fPassword;
//            }
//        }
//        return "error";
//    }

//    public void getData(String path){
//        Firebase firebase = new Firebase("https://care-connect.firebaseio.com/" + path);
//        firebase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<List<Object>> t = new GenericTypeIndicator<List<Object>>() {};
//                List<Object> objectList = dataSnapshot.getValue(t);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        final List<Object> mRecords = new ArrayList<>();
//        firebase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> record = (Map<String, Object>)dataSnapshot.getValue();
//                mRecords.add(record);
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
//        return mRecords;
//    }

//    public Patient getPatient(String email){
//        List<Map> mPatients = getData("patients");
//        for (Map mPatient : mPatients) {
//            if (mPatient.get("email") == email){
//                String name = (String)mPatient.get("name");
//                String surname = (String)mPatient.get("surname");
//                String password = (String)mPatient.get("password");
//                String gender = (String)mPatient.get("gender");
//                Patient returnPatient = new Patient(name, surname, email, password, gender);
//                return returnPatient;
//            }
//        }
//        return null;
//    }

//    public boolean checkPatientLogin(String email, String password){
//        Patient patient = getPatient(email);
//        if (patient != null && patient.getPassword().equals(password)){
//            return true;
//        }else{
//            return false;
//        }
//    }
}
