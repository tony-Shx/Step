package com.example.henu.step.Bean;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class Run {
    private String telephone;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    private int id, duration;
    private long start_time, end_time;
    private float length,consume;
    private String points;
    private boolean isUpdate;

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public float getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = (float) length;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getConsume() {
        return consume;
    }

    public void setConsume(double consume) {
        this.consume = (float) consume;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public Run() {

    }

    @Override
    public String toString() {
        return "Run{" +
                "telephone='" + telephone + '\'' +
                ", id=" + id +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", duration=" + duration +
                ", length=" + length +
                ", consume=" + consume +
                ", points='" + points + '\'' +
                '}';
    }

    public Run(String telephone, int start_time, int end_time, float length, int duration, float consume, String points) {
        this.telephone = telephone;
        this.start_time = start_time;
        this.end_time = end_time;
        this.length = length;
        this.duration = duration;
        this.consume = consume;
        this.points = points;
    }
}
