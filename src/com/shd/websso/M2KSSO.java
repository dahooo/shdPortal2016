package com.shd.websso;

import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class M2KSSO {
	private static M2KSSO instance = null;
	private String id = null;
	private String key = null;
	
	private HashMap<String, UserBean> userMap = new HashMap<String, UserBean>();

	protected M2KSSO() {
		// Exists only to defeat instantiation.
	}

	public static M2KSSO getInstance() {
		if (instance == null) {
			instance = new M2KSSO();
		}
		return instance;
	}

	public void registerUser(String userId, UserBean userBean) {
		userMap.put(userId, userBean);
	}
	public UserBean getUserBean(String userId){
		UserBean ub = userMap.get(userId);
		return ub;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
	
}
