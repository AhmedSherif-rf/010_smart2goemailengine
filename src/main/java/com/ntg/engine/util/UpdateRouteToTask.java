package com.ntg.engine.util;

import java.util.ArrayList;

public class UpdateRouteToTask {
	private ArrayList<Object> param = new ArrayList<>();
	private String routeToTask;
	private String exceptionMessage;
	private String routeLabel;

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public ArrayList<Object> getParam() {
		return param;
	}

	public void setParam(ArrayList<Object> param) {
		this.param = param;
	}

	public String getRouteToTask() {
		return routeToTask;
	}

	public void setRouteToTask(String routeToTask) {
		this.routeToTask = routeToTask;
	}

	public String getRouteLabel() {
		return routeLabel;
	}

	public void setRouteLabel(String routeLabel) {
		this.routeLabel = routeLabel;
	}

}
