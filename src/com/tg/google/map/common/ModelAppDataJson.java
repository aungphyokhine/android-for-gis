package com.tg.google.map.common;

public class ModelAppDataJson {
	private String email;
    private String token;
    private Boolean sync;
    private int syncdelay;
    private Boolean record;
    private int recorddelay;
    private Boolean fetch;
    private int fetchdelay;
    private String gcmid;
    
    
	public ModelAppDataJson(String email, String token, Boolean sync, int syncdelay, Boolean record, int recorddelay,
			Boolean fetch, int fetchdelay, String gcmid) {
		super();
		this.email = email;
		this.token = token;
		this.sync = sync;
		this.syncdelay = syncdelay;
		this.record = record;
		this.recorddelay = recorddelay;
		this.fetch = fetch;
		this.fetchdelay = fetchdelay;
		this.gcmid = gcmid;
	}

	public String getGcmid() {
		return gcmid;
	}

	public void setGcmid(String gcmid) {
		this.gcmid = gcmid;
	}

	public ModelAppDataJson() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Boolean getSync() {
		return sync;
	}
	public void setSync(Boolean sync) {
		this.sync = sync;
	}
	public int getSyncdelay() {
		return syncdelay;
	}
	public void setSyncdelay(int syncdelay) {
		this.syncdelay = syncdelay;
	}
	public Boolean getRecord() {
		return record;
	}
	public void setRecord(Boolean record) {
		this.record = record;
	}
	public int getRecorddelay() {
		return recorddelay;
	}
	public void setRecorddelay(int recorddelay) {
		this.recorddelay = recorddelay;
	}
	public Boolean getFetch() {
		return fetch;
	}
	public void setFetch(Boolean fetch) {
		this.fetch = fetch;
	}
	public int getFetchdelay() {
		return fetchdelay;
	}
	public void setFetchdelay(int fetchdelay) {
		this.fetchdelay = fetchdelay;
	}
    
    
}
