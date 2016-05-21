package database;

/**
 * Created by austr on 13/05/2016.
 */
public class Precept {

    int id;
    int optimal_angle;
    int maximal_angle;
    int duration;
    int frequency;
    int patient_id;

    public Precept() {
    }

    public Precept(int id, int optimal_angle, int maximal_angle, int duration, int frequency, int patient_id) {
        this.id = id;
        this.optimal_angle = optimal_angle;
        this.maximal_angle = maximal_angle;
        this.duration = duration;
        this.frequency = frequency;
        this.patient_id = patient_id;
    }

    public Precept(int optimal_angle, int maximal_angle, int duration, int frequency) {
        this.optimal_angle = optimal_angle;
        this.maximal_angle = maximal_angle;
        this.duration = duration;
        this.frequency = frequency;
    }

    //SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setOptimal_angle(int optimal_angle) {
        this.optimal_angle = optimal_angle;
    }

    public void setMaximal_angle(int maximal_angle) {
        this.maximal_angle = maximal_angle;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    //GETTERS

    public int getId() {
        return id;
    }

    public int getOptimal_angle() {
        return optimal_angle;
    }

    public int getMaximal_angle() {
        return maximal_angle;
    }

    public int getDuration() {
        return duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getPatient_id() {
        return patient_id;
    }
}
