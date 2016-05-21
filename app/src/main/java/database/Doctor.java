package database;

/**
 * Created by austr on 13/05/2016.
 */
public class Doctor {

    int id;
    String name;
    String surname;
    String email;
    String password;
    String phone_number;

    //Constructors
    public Doctor(){}

    public Doctor(String name, String surname, String email, String password, String phone_number){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
    }

    //SETTERS

    public void setId(int id) {
        this.id = id;
    }

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

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    //GETTERS

    public int getId() {
        return id;
    }

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

    public String getPhone_number() {
        return phone_number;
    }
}
