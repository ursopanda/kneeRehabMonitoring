package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "care_connect.db";
    private static final int DATABASE_VERSION = 1;

    //Table names
    private static final String TABLE_PATIENT = "patients";
    private static final String TABLE_DOCTOR = "doctors";
    private static final String TABLE_REHAB_SESSION = "rehab_sessions";
    private static final String TABLE_PRECEPT = "precepts";

    //Patient column names
    private static final String KEY_PATIENT_ID = "id";
    public static final String KEY_PATIENT_NAME = "patient_name";
    public static final String KEY_PATIENT_SURNAME = "patient_surname";
    public static final String KEY_PATIENT_EMAIL = "patient_email";
    private static final String KEY_PATIENT_PASSWORD = "patient_password";
    public static final String KEY_PATIENT_GENDER = "patient_gender";
    private static final String KEY_PATIENT_DOCTOR_ID = "patient_doctor_id";

    //Doctor column names
    //TODO: make columns public (necessary ones)
    private static final String KEY_DOCTOR_ID = "doctor_id";
    private static final String KEY_DOCTOR_NAME = "doctor_name";
    private static final String KEY_DOCTOR_SURNAME = "doctor_surname";
    private static final String KEY_DOCTOR_EMAIL = "doctor_email";
    private static final String KEY_DOCTOR_PASSWORD = "doctor_password";
    private static final String KEY_DOCTOR_PHONE = "doctor_phone";

    //Rehab Session column names
    private static final String KEY_REHAB_ID = "rehab_id";
    private static final String KEY_REHAB_START_TIME = "rehab_start_time";
    private static final String KEY_REHAB_END_TIME = "rehab_end_time";
    private static final String KEY_REHAB_EXCEEDED_MAX = "rehab_exceeded_max";
    private static final String KEY_REHAB_PATIENT_ID = "rehab_patient_id";

    //Precept column names
    private static final String KEY_PRECEPT_ID = "precept_id";
    private static final String KEY_PRECEPT_OPTIMAL_ANGLE = "precept_optimal_angle";
    private static final String KEY_PRECEPT_MAXIMAL_ANGLE = "precept_maximal_angle";
    private static final String KEY_PRECEPT_DURATION = "precept_duration";
    private static final String KEY_PRECEPT_FREQUENCY = "precept_frequency";
    private static final String KEY_PRECEPT_PATIENT_ID = "precept_patient_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Patient table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE "
            + TABLE_PATIENT + "("
            + KEY_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PATIENT_NAME + " VARCHAR NOT NULL,"
            + KEY_PATIENT_SURNAME + " VARCHAR,"
            + KEY_PATIENT_EMAIL + " VARCHAR NOT NULL,"
            + KEY_PATIENT_PASSWORD + " VARCHAR, "
            + KEY_PATIENT_GENDER + " VARCHAR, "
            //TODO: have to check whether FK works or not
            + KEY_PATIENT_DOCTOR_ID + " INTEGER)";
//            + "FOREIGN KEY (" + KEY_PATIENT_DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR + "(" + KEY_DOCTOR_ID + "))";

    //Doctor table create statement
    private static final String CREATE_TABLE_DOCTOR = "CREATE TABLE "
            + TABLE_DOCTOR + "("
            + KEY_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DOCTOR_NAME + " VARCHAR NOT NULL,"
            + KEY_DOCTOR_SURNAME + " VARCHAR,"
            + KEY_DOCTOR_EMAIL + " VARCHAR NOT NULL,"
            + KEY_DOCTOR_PASSWORD + " VARCHAR, "
            + KEY_DOCTOR_PHONE + " VARCHAR)";

    //Doctor table create statement
    private static final String CREATE_TABLE_REHAB_SESSION = "CREATE TABLE "
    + TABLE_REHAB_SESSION + "("
            + KEY_REHAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_REHAB_START_TIME + " INTEGER NOT NULL,"
            + KEY_REHAB_END_TIME + " INTEGER NOT NULL,"
            + KEY_REHAB_EXCEEDED_MAX + " INTEGER, "
            //TODO: have to check whether FK works or not
            + KEY_REHAB_PATIENT_ID + " INTEGER)";
//            + "FOREIGN KEY (" + KEY_REHAB_PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + KEY_PATIENT_ID+ "))";

    //Precept table create statement
    private static final String CREATE_TABLE_PRECEPT = "CREATE TABLE "
            + TABLE_PRECEPT + "("
            + KEY_PRECEPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PRECEPT_OPTIMAL_ANGLE + " INTEGER NOT NULL,"
            + KEY_PRECEPT_MAXIMAL_ANGLE + " INTEGER NOT NULL,"
            //TODO: have to check whether INTEGER is suitable data type
            + KEY_PRECEPT_DURATION + " INTEGER NOT NULL,"
            + KEY_PRECEPT_FREQUENCY + " INTEGER NOT NULL, "
            //TODO: have to check whether FK works or not
            + KEY_PRECEPT_PATIENT_ID + " INTEGER)";
//            + "FOREIGN KEY (" + KEY_PRECEPT_PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + KEY_PATIENT_ID+ "))";


    @Override
    public void onCreate(SQLiteDatabase db) {

//        if (!db.isReadOnly()) {
//            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
//        }
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_DOCTOR);
        db.execSQL(CREATE_TABLE_REHAB_SESSION);
        db.execSQL(CREATE_TABLE_PRECEPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REHAB_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRECEPT);
    }


    // ------------------------ "Patient" table CRUD methods ----------------//

    private Firebase mPatientKeeper = new Firebase("https://care-connect.firebaseio.com/patients");
    public List<Patient> mPatients = new ArrayList<>();

    public void addPatient(Patient patient){
        mPatientKeeper.push().setValue(patient);
    }


    public long createPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATIENT_NAME, patient.getName());
        values.put(KEY_PATIENT_SURNAME, patient.getSurname());
        values.put(KEY_PATIENT_EMAIL, patient.getEmail());
        values.put(KEY_PATIENT_PASSWORD, patient.getPassword());
        values.put(KEY_PATIENT_GENDER, patient.getGender());
        values.put(KEY_PATIENT_DOCTOR_ID, patient.getDoctor_key());

        return db.insert(TABLE_PATIENT, null, values);
    }

    //PATIENT LOGIN CHECK
    public Boolean checkPatientLogin(String email, String hpass) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT *  FROM "
                + TABLE_PATIENT + " WHERE patient_email = \""
                + email + "\" AND patient_password=\""
                + hpass + "\";";

        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            do{
                count++;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return count == 1;
    }

    public ArrayList<Patient> getPatients() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Patient> patientList = new ArrayList<>();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_PATIENT, new String[]{});

        if (mCursor.moveToFirst()) {
            do {
                Patient patientDetail = new Patient();
                patientDetail.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_NAME)));
                patientDetail.setSurname(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_SURNAME)));
                patientDetail.setEmail(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_EMAIL)));
                patientDetail.setPassword(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_PASSWORD)));
                patientDetail.setGender(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_GENDER)));
                //Need to add Doctor FK
                patientList.add(patientDetail);
            } while (mCursor.moveToNext());
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
            db.close();
        }
        db.close();
        return patientList;
    }

//    public Cursor getPatient(String email) throws SQLException {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT *  FROM "
//                + TABLE_PATIENT + " WHERE "
//                + KEY_PATIENT_EMAIL + " =?", new String[] {email});
//    }

    public Patient getPatient(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_PATIENT + " WHERE "
                + KEY_PATIENT_EMAIL + " = \"" +  email + "\"";

        Log.e("LOG", SELECT_QUERY);

        Cursor mCursor = db.rawQuery(SELECT_QUERY, new String[]{});

        Patient patient = new Patient();
        if (mCursor.moveToFirst()) {
            patient.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_NAME)));
            patient.setSurname(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_SURNAME)));
            patient.setEmail(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_EMAIL)));
            patient.setPassword(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_PASSWORD)));
            patient.setGender(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PATIENT_GENDER)));
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
            db.close();
        }

        return patient;
    }

//    public int updatePatient(Patient patient) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_PATIENT_NAME, patient.getName());
//        values.put(KEY_PATIENT_SURNAME, patient.getSurname());
//        values.put(KEY_PATIENT_EMAIL, patient.getEmail());
//        values.put(KEY_PATIENT_PASSWORD, patient.getPassword());
//        values.put(KEY_PATIENT_GENDER, patient.getGender());
//
//        return db.update(TABLE_PATIENT, values, KEY_PATIENT_ID + " =?",
//                new String[] { String.valueOf(patient.getId())});
//    }

    public void deletePatient(long patient_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PATIENT, KEY_PATIENT_ID + " =?",
                new String[]{String.valueOf(patient_id)});
    }


    // ------------------------ "Doctor" table CRUD methods ----------------//

    public long createDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DOCTOR_NAME, doctor.getName());
        values.put(KEY_DOCTOR_SURNAME, doctor.getSurname());
        values.put(KEY_DOCTOR_EMAIL, doctor.getEmail());
        values.put(KEY_DOCTOR_PASSWORD, doctor.getPassword());
        values.put(KEY_DOCTOR_PHONE, doctor.getPhone_number());

        //insert row
        return db.insert(TABLE_DOCTOR, null, values);
    }

//    public Cursor getDoctor(String email) throws SQLException {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT *  FROM "
//                + TABLE_DOCTOR + " WHERE "
//                + KEY_DOCTOR_EMAIL + " =?", new String[] {email});
//    }

    public Cursor getTables(){
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY = " SELECT name FROM sqlite_master " + " WHERE type='table'";
        return db.rawQuery(QUERY, new String[]{});
    }

    public Doctor getDoctor(String email) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_DOCTOR + " WHERE "
                + KEY_DOCTOR_EMAIL + " = \"" +  email + "\"";

        Log.e("LOG", SELECT_QUERY);

        Cursor mCursor = db.rawQuery(SELECT_QUERY, new String[]{});

        Doctor doctor = new Doctor();
        if (mCursor.moveToFirst()) {
            doctor.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_ID)));
            doctor.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_NAME)));
            doctor.setSurname(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_SURNAME)));
            doctor.setEmail(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_EMAIL)));
            doctor.setPassword(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_PASSWORD)));
            doctor.setPhone_number(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_PHONE)));
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
            db.close();
        }

        return doctor;

    }

    public ArrayList<Doctor> getDoctors() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Doctor> doctorList = new ArrayList<>();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_DOCTOR, new String[]{});

        if (mCursor.moveToFirst()) {
            do {
                Doctor doctorDetail = new Doctor();
                doctorDetail.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_ID)));
                doctorDetail.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_NAME)));
                doctorDetail.setSurname(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_SURNAME)));
                doctorDetail.setEmail(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_EMAIL)));
                doctorDetail.setPassword(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_PASSWORD)));
                doctorDetail.setPhone_number(mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_DOCTOR_PHONE)));
                doctorList.add(doctorDetail);
            } while (mCursor.moveToNext());
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
        }
        db.close();
        return doctorList;
    }

    public int updateDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DOCTOR_NAME, doctor.getName());
        values.put(KEY_DOCTOR_SURNAME, doctor.getSurname());
        values.put(KEY_DOCTOR_EMAIL, doctor.getEmail());
        values.put(KEY_DOCTOR_PASSWORD, doctor.getPassword());
        values.put(KEY_DOCTOR_PHONE, doctor.getPhone_number());

        return db.update(TABLE_DOCTOR, values, KEY_DOCTOR_ID + " =?",
                new String[] { String.valueOf(doctor.getId())});
    }

    public void deleteDoctor(long doctor_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCTOR, KEY_DOCTOR_ID + " =?",
                new String[]{String.valueOf(doctor_id)});
    }

    //Assigning Doctor to Patient
//    public long createPatientDoctor(long patient_id, long doctor_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_PATIENT_ID, patient_id);
//        values.put(KEY_DOCTOR_ID, doctor_id);
//    }

    public void assignPatient(){

    }

    // ------------------------ "RehabSession" table CRUD methods ----------------//

    public long createSession(RehabSession session) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REHAB_START_TIME, session.getStart_time());
        values.put(KEY_REHAB_END_TIME, session.getEnd_time());
        values.put(KEY_REHAB_EXCEEDED_MAX, session.getExceeded_max());
        values.put(KEY_REHAB_PATIENT_ID, session.getPatient_id());

        //insert row
        return db.insert(TABLE_REHAB_SESSION, null, values);
    }

    public RehabSession getLastSession(int patient_id) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_REHAB_SESSION + " WHERE "
                + KEY_REHAB_PATIENT_ID + " = \"" +  patient_id + "\" ORDER BY " + KEY_REHAB_ID + " DESC LIMIT 1";

        Log.e("LOG", SELECT_QUERY);

        Cursor mCursor = db.rawQuery(SELECT_QUERY, new String[]{});

        RehabSession session = new RehabSession();
        if (mCursor.moveToFirst()) {
            session.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_ID)));
            session.setStart_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_START_TIME)));
            session.setEnd_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_END_TIME)));
            session.setExceeded_max(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_EXCEEDED_MAX)));
            session.setPatient_id(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_PATIENT_ID)));
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
        }

        db.close();
        return session;
    }

    public RehabSession getSession(int session_id) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_REHAB_SESSION + " WHERE "
                + KEY_REHAB_ID + " = \"" +  session_id + "\"";

        Log.e("LOG", SELECT_QUERY);

        Cursor mCursor = db.rawQuery(SELECT_QUERY, new String[]{});

        RehabSession session = new RehabSession();
        if (mCursor.moveToFirst()) {
            session.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_ID)));
            session.setStart_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_START_TIME)));
            session.setEnd_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_END_TIME)));
            session.setExceeded_max(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_EXCEEDED_MAX)));
            session.setPatient_id(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_PATIENT_ID)));
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
        }

        db.close();
        return session;
    }

    public ArrayList<RehabSession> getSessions() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<RehabSession> sessionList = new ArrayList<>();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_REHAB_SESSION, new String[]{});

        if (mCursor.moveToFirst()) {
            do {
                RehabSession sessionDetail = new RehabSession();
                sessionDetail.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_ID)));
                sessionDetail.setStart_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_START_TIME)));
                sessionDetail.setEnd_time(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_END_TIME)));
                sessionDetail.setExceeded_max(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_EXCEEDED_MAX)));
                sessionDetail.setPatient_id(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_REHAB_PATIENT_ID)));
                sessionList.add(sessionDetail);
            } while (mCursor.moveToNext());
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
            db.close();
        }
        db.close();
        return sessionList;
    }

    public int updateSession(RehabSession session) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REHAB_START_TIME, session.getStart_time());
        values.put(KEY_REHAB_END_TIME, session.getEnd_time());
        values.put(KEY_REHAB_EXCEEDED_MAX, session.getExceeded_max());
        values.put(KEY_REHAB_PATIENT_ID, session.getPatient_id());

        return db.update(TABLE_REHAB_SESSION, values, KEY_REHAB_ID + " =?",
                new String[] { String.valueOf(session.getId())});
    }

    public void deleteSession(long session_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REHAB_SESSION, KEY_REHAB_ID + " =?",
                new String[]{String.valueOf(session_id)});
    }

    // ------------------------ "Precept" table CRUD methods ----------------//

    public long cretePrecept(Precept precept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRECEPT_PATIENT_ID, precept.getPatient_id());
        values.put(KEY_PRECEPT_DURATION, precept.getDuration());
        values.put(KEY_PRECEPT_FREQUENCY, precept.getFrequency());
        values.put(KEY_PRECEPT_MAXIMAL_ANGLE, precept.getMaximal_angle());
        values.put(KEY_PRECEPT_OPTIMAL_ANGLE, precept.getOptimal_angle());

        //insert row
        return db.insert(TABLE_PRECEPT, null, values);
    }

    public Precept getPrecept(int precept_id) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_PRECEPT + " WHERE "
                + KEY_PRECEPT_ID + " = \"" +  precept_id + "\"";

        Log.e("LOG", SELECT_QUERY);

        Cursor mCursor = db.rawQuery(SELECT_QUERY, new String[]{});

        Precept precept = new Precept();
        if (mCursor.moveToFirst()) {
            precept.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_ID)));
            precept.setPatient_id(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_PATIENT_ID)));
            precept.setDuration(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_DURATION)));
            precept.setFrequency(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_FREQUENCY)));
            precept.setMaximal_angle(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_MAXIMAL_ANGLE)));
            precept.setOptimal_angle(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_OPTIMAL_ANGLE)));
        }

        if (!mCursor.isClosed()) {
            mCursor.close();
        }

        db.close();
        return precept;
    }

    public ArrayList<Precept> getPrecepts() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Precept> preceptList = new ArrayList<>();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_PRECEPT, new String[]{});

        if (mCursor.moveToFirst()) {
            do {
                Precept preceptDetail = new Precept();
                preceptDetail.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_ID)));
                preceptDetail.setPatient_id(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_PATIENT_ID)));
                preceptDetail.setDuration(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_DURATION)));
                preceptDetail.setFrequency(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_FREQUENCY)));
                preceptDetail.setMaximal_angle(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_MAXIMAL_ANGLE)));
                preceptDetail.setOptimal_angle(mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_PRECEPT_OPTIMAL_ANGLE)));
                preceptList.add(preceptDetail);
            } while (mCursor.moveToNext());
        }
        if (!mCursor.isClosed()) {
            mCursor.close();
        }
        db.close();
        return preceptList;
    }

    public int updatePrecept(Precept precept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRECEPT_PATIENT_ID, precept.getPatient_id());
        values.put(KEY_PRECEPT_DURATION, precept.getDuration());
        values.put(KEY_PRECEPT_FREQUENCY, precept.getFrequency());
        values.put(KEY_PRECEPT_MAXIMAL_ANGLE, precept.getMaximal_angle());
        values.put(KEY_PRECEPT_OPTIMAL_ANGLE, precept.getOptimal_angle());

        return db.update(TABLE_PRECEPT, values, KEY_PRECEPT_ID + " =?",
                new String[] { String.valueOf(precept.getId())});
    }

    public void deletePrecept(long precept_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRECEPT, KEY_PRECEPT_ID + " =?",
                new String[]{String.valueOf(precept_id)});
    }
}
