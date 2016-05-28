package database;

import com.firebase.client.Firebase;

/**
 * Created by austr on 17/05/2016.
 */
public class FirebaseHelper {

    public FirebaseHelper(){}

    public void newPatient(Patient patient, Firebase firebase){
        firebase.setValue(patient);
    }

    public void newDoctor(Doctor doctor, Firebase firebase){
        firebase.setValue(doctor);
    }

}
