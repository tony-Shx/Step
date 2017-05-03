package com.example.henu.step.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/4/18.
 */

public class UserInfo extends BmobObject{
	private String username;
	private String telephone;
	private String sex;
	private int stature,weight;

	public UserInfo() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getStature() {
		return stature;
	}

	public void setStature(int stature) {
		this.stature = stature;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
