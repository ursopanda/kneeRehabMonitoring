package database;

/**
 * Created by austr on 13/05/2016.
 */
public class Precept {

    int optimal_angle;
    int maximal_angle;
    int duration;
    int frequency;

    public Precept() {
    }

    public Precept(int optimal_angle, int maximal_angle, int duration, int frequency) {
        this.optimal_angle = optimal_angle;
        this.maximal_angle = maximal_angle;
        this.duration = duration;
        this.frequency = frequency;
    }

    //SETTERS


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

    //GETTERS


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

}
