package com.tg.google.map.common;

public class ModelResult {
	private Boolean success;
    private String message;
    private String code;
    
	public ModelResult(Boolean success, String message, String code) {
		super();
		this.success = success;
		this.message = message;
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
}
