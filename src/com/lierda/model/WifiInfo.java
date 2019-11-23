package com.lierda.model;

import java.io.Serializable;

public class WifiInfo implements Serializable{
	
	private String ssid;
	private String chiper_mode;
	private String chiper_algorithm;
	private int signal = 0;
	
	public void setSSID(String ssid){
		this.ssid = ssid;
	}
	public String getSSID(){
		return this.ssid;
	}
	
	public void setChiperMode(String mode){
		this.chiper_mode = mode;
	}
	public String getChiperMode(){
		return this.chiper_mode;
	}
	
	public void setChiperAlgorithm(String algorithm){
		this.chiper_algorithm = algorithm;
	}
	public String getChiperAlgorithm(){
		return this.chiper_algorithm;
	}
	
	public void setSignal(int signal){
		this.signal = signal;
	}
	public int getSignal(){
		return this.signal;
	}

}
