package com.example.henu.step.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/3/18.
 */

public class RunningRecord extends BmobObject {
	private String telephone,start_time, end_time;
	private int duration;
	private float length,consume;
	private String points;
	public RunningRecord() {

	}

	public RunningRecord(String telephone, String start_time, String end_time, int duration, float length, float consume, String points) {
		this.telephone = telephone;
		this.start_time = start_time;
		this.end_time = end_time;
		this.duration = duration;
		this.length = length;
		this.consume = consume;
		this.points = points;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getConsume() {
		return consume;
	}

	public void setConsume(float consume) {
		this.consume = consume;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}
}
