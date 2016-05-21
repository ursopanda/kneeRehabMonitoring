package database;

/**
 * Created by Austris on 17.03.2016.
 */
public class Patient {

    String name;
    String surname;
    String email;
    String password;
    String gender;
    String phone_number;
    String doctor_key;

    //constructors
    public Patient() {
    }

    public Patient(String name, String surname, String email, String gender) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
    }

    public Patient(String name, String surname, String email, String password, String gender, String phone_number) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone_number = phone_number;
    }

    public Patient(String name, String surname, String email, String password,
                   String gender, String phone_number, String doctor_key) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phone_number = phone_number;
        this.doctor_key = doctor_key;
    }

    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setDoctor_key(String doctor_key) {
        this.doctor_key = doctor_key;
    }

    //Getters

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getDoctor_key() {
        return doctor_key;
    }
}
