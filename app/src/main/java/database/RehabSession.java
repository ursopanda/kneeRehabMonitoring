package database;

/**
 * Created by austr on 13/05/2016.
 */
public class RehabSession {

    int id;
    int start_time;
    int end_time;
    int exceeded_max;
    int patient_id;

    public RehabSession() {
    }

    public RehabSession(int start_time, int end_time, int exceeded_max, int patient_id) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.exceeded_max = exceeded_max;
        this.patient_id = patient_id;
    }

    //SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public void setExceeded_max(int exceeded_max) {
        this.exceeded_max = exceeded_max;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    //GETTERS

    public int getId() {
        return id;
    }

    public int getStart_time() {
        return start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public int getExceeded_max() {
        return exceeded_max;
    }

    public int getPatient_id() {
        return patient_id;
    }
}
