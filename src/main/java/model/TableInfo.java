package model;

import java.io.Serializable;

public class TableInfo implements Serializable {
    private int tableId;
    private int sessionId;
    private String sessionStatus;

    public TableInfo() {}

    public TableInfo(int tableId, int sessionId, String sessionStatus) {
        this.tableId = tableId;
        this.sessionId = sessionId;
        this.sessionStatus = sessionStatus;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) {
    	this.tableId = tableId;
    	}

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) {
    	this.sessionId = sessionId;
    	}

    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) {
    	this.sessionStatus = sessionStatus;
    	}
}