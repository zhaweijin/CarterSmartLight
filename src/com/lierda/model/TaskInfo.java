package com.lierda.model;

import java.io.Serializable;

public class TaskInfo  implements Serializable {
	
	private int index;
	private String date;
	private int hours=0,mins=0;
	private boolean isOpen;
	
	public void setIndex(int index){
		this.index = index;
	}
	public int getIndex(){
		return this.index;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	public String getDate(){
		return this.date;
	}
	
	public void setHours(int hours){
		this.hours = hours;
	}
	public int getHours(){
		return this.hours;
	}
	
	public void setMins(int mins){
		this.mins = mins;
	}
	public int getMins(){
		return this.mins;
	}
	
	public void setAction(boolean action){
		this.isOpen = action;
	}
	public boolean getAction(){
		return this.isOpen;
	}

}
