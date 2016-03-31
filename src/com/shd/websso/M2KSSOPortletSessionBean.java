package com.shd.websso;

/**
 *
 * A sample Java bean that stores portlet instance data in portlet session.
 *
 */
public class M2KSSOPortletSessionBean {
	
	/**
	 * Last text for the text form
	 */
	private String formText = "";

	/**
	 * Set last text for the text form.
	 * 
	 * @param formText last text for the text form.
	 */
	public void setFormText(String formText) {
		this.formText = formText;
	}

	/**
	 * Get last text for the text form.
	 * 
	 * @return last text for the text form
	 */
	public String getFormText() {
		return this.formText;
	}

	/**
	 * Start position
	 */
	private int startPosition = 0;

	/**
	 * Get start position
	 * 
	 * @return Start position
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * Set start position
	 * 
	 * @param startPosition Start position
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

}
