package com.example.henu.step.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/1.
 */

public class User extends BmobObject {
	private String username;
	private String telephone;
	private String password;

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public User() {
	}

}
