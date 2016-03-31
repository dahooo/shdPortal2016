package com.shd.websso;

import javax.servlet.http.HttpSession;

public class UserBean {
	private String userId;
	private String httpSessionId;
	private String portletSessionId;
	private String hash;

	public UserBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserBean(String userId, String httpSessionId, String portletSessionId) {
		super();
		this.userId = userId;
		this.httpSessionId = httpSessionId;
		this.portletSessionId = portletSessionId;
	}

	public UserBean(String userId, String httpSessionId, String portletSessionId, String hash) {
		super();
		this.userId = userId;
		this.httpSessionId = httpSessionId;
		this.portletSessionId = portletSessionId;
		this.hash = hash;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public void setSessionId(String httpSessionId) {
		this.httpSessionId = httpSessionId;
	}

	public String getportletSessionId() {
		return portletSessionId;
	}

	public void setportletSessionId(String portletSessionId) {
		this.portletSessionId = portletSessionId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "UserBean [userId=" + userId + ", httpSessionId=" + httpSessionId + ", portletSessionId="
				+ portletSessionId + "]";
	}

}
