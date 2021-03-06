package database;

/**
 * Created by austr on 13/05/2016.
 */
public class RehabSession {

    String date;
    String duration;
    int exceeded_max;
    String comment;

    public RehabSession() {
    }

    public RehabSession(String comment, String date, String duration, int exceeded_max) {
        this.comment = comment;
        this.date = date;
        this.duration = duration;
        this.exceeded_max = exceeded_max;
    }

    public RehabSession(String comment, String date, String duration) {
        this.comment = comment;
        this.date = date;
        this.duration = duration;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setExceeded_max(int exceeded_max) {
        this.exceeded_max = exceeded_max;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public int getExceeded_max() {
        return exceeded_max;
    }
}
