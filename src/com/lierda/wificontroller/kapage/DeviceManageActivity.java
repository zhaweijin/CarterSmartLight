package com.lierda.wificontroller.kapage;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.parser.ParseException;





import com.lierda.model.DeviceInfo;
import com.lierda.udp.TCPExchanger;
import com.lierda.udp.UdpReceiver;
import com.lierda.udp.UdpExchanger;
import com.lierda.utils.CC3XCmd;
import com.lierda.utils.CC3XConstants;
import com.lierda.utils.CC3XParser;
import com.lierda.utils.CC3XWifiManager;
import com.lierda.utils.Constants;
import com.lierda.utils.FileUtils;
import com.lierda.utils.LogUtil;
import com.lierda.wificontroller.kapage.R;
import com.lierda.wificontroller.kapage.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class DeviceManageActivity extends BaseActivity {
	
	private static final int MAX_TIMER_INTERVAL = 5;
	
	private static final int MESSAGE_UDP = 1;
	private static final int MESSAGE_TCP = 2;
	private static final int MESSAGE_TIME_OUT = 4;
	private static final int MESSAGE_TIMER = 15;


	private ListView deviceListView;
	private MyListAdapter deviceListAdapter;

	//private View devicePage;
	private EditText et_name;
	private Button menuBtn,rightBtn;

	private View[] children;
	private LayoutInflater inflater;
	
	private LinearLayout newDeviceLayout = null;
	private ArrayList<DeviceInfo>  deviceInfoList = new ArrayList<DeviceInfo>();
	private ArrayList<DeviceInfo>  deviceManageList = new ArrayList<DeviceInfo>();

	
	private LayoutInflater layoutInflater;
	private ViewFlipper viewFilpper;
	private PopupWindow popup;
	private View popView;
	private ViewOnClickListener view_listener = null;

	
	private boolean isRunning = true,isSearch = true;
	private String data = null;
	private int timeCounter = MAX_TIMER_INTERVAL,serverPort=0,delete_index=0;
	private String broadcastIp = "",serverIp="",configIp = "";
	
	
	
	/**
	 * Wifi Manager instance which gives the network related information like
	 * Wifi ,SSID etc.
	 */
	private CC3XWifiManager mCC3XWifiManager = null;
	WifiManager wifi;
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data != null){
			DeviceInfo device = (DeviceInfo) data.getSerializableExtra("device");
			if(device != null){
				synchronized(deviceManageList){
					for(int i=0;i<deviceManageList.size();i++){
						if( true == isSameDevice(device.getMacAddress(),deviceManageList.get(i).getMacAddress())){
							LogUtil.printError("change!!!");
							if(!device.getDeviceName().equals(deviceManageList.get(i).getDeviceName())){
								deviceManageList.get(i).setDeviceName(device.getDeviceName());
							}
							if(device.getImageContent() != null)
								deviceManageList.get(i).setImageContent(device.getImageContent());
							AsyncImageLoader.clearImageMap();
							updateUI();
							
							//update mode param
							deviceManageList.get(i).setModeParam(3, 1, device.getModeParam(3, 1));
							deviceManageList.get(i).setModeParam(3, 2, device.getModeParam(3, 2));
							deviceManageList.get(i).setModeParam(3, 3, device.getModeParam(3, 3));
							deviceManageList.get(i).setModeParam(3, 4, device.getModeParam(3, 4));

							deviceManageList.get(i).setModeParam(4, 1, device.getModeParam(4, 1));
							deviceManageList.get(i).setModeParam(4, 2, device.getModeParam(4, 2));
							deviceManageList.get(i).setModeParam(4, 3, device.getModeParam(4, 3));
							deviceManageList.get(i).setModeParam(4, 4, device.getModeParam(4, 4));
							save();
							LogUtil.printInfo("info="+mSettings.getString(CC3XConstants.LOCAL_DEVICE, ""));
							break;
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_manage);
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SEARCH);
		//sfilter.addAction(Constants.ACTION_CONNET);
		registerReceiver(mResReceiver,filter);
		
		//test
		//String local = "{\"resultBean\":[{\"mac_address\":\"ACCF2301A558\",\"device_name\":\"??��?��?????\"},{\"mac_address\":\"00956900469C\",\"device_name\":\"??��?��?????\"},{\"mac_address\":\"009569002908\",\"device_name\":\"??��?��?????\"},{\"mac_address\":\"009569004624\",\"device_name\":\"??��?��?????\"}]}";
		//mSettings.edit().putString(CC3XConstants.LOCAL_DEVICE, local).commit();
		
		String info = mSettings.getString(CC3XConstants.LOCAL_DEVICE, "");
		LogUtil.printInfo("info="+info);
		if(!StringUtils.isBlank(info))
			deviceManageList.addAll(FileUtils.getLocalInfo(info));
		
		
		
		view_listener = new ViewOnClickListener();
		initTitleBar(1,R.string.title_device_list,0);
		
		deviceListAdapter = new MyListAdapter(this, deviceManageList);
		deviceListView = (ListView)findViewById(R.id.lv_device);
		deviceListView.setAdapter(deviceListAdapter);
		updateDeviceManageList();
		

		rightBtn = (Button)findViewById(R.id.btn_right);
		rightBtn.setOnClickListener(view_listener);
		
		newDeviceLayout = (LinearLayout)findViewById(R.id.ll_new_device);		
		updateNewDeviceList();
		
		
		
		//UdpExchanger.getInstance().initSocket();		
		Thread actionThread = new Thread(new actionThread());
		actionThread.start();
		
		//remote search
		//Thread tcpThread = new Thread(new tcpThread());
		//tcpThread.start();
		
		Thread timerThread = new Thread(new timerThread());
		timerThread.start();
				
		//Thread heartBeatThread = new Thread(new heartbeatThread());
		//heartBeatThread.start();	
	}
	
	
	
	private BroadcastReceiver mResReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context c, Intent it)
		{
			// TODO Auto-generated method stub
			
			if(it != null)
			{
				String action = it.getAction();
				LogUtil.printInfo(action);
				if(action.equals(Constants.ACTION_SEARCH)){
					LogUtil.printInfo("recv broadcast ACTION_SEARCH");
					String data = it.getStringExtra("result");
					if(data != null){
												
						LogUtil.printInfo("get result");
						DeviceInfo device = CC3XParser.getInstance().parseDevice(data);
						if(device!= null){
							LogUtil.printInfo("get device ip="+device.getIpStr2());	
							UdpExchanger.getInstance().sendPackage("+ok".getBytes(), device.getIpStr2(),false);
							
							checkNewDevice(device);
							updateNewDeviceList();
							
							//if(!StringUtils.isBlank(configIp) && device.getIpStr2().equals(configIp)){
							//	Intent it1 = new Intent(Constants.ACTION_CONNETED);
							//	DeviceManageActivity.this.sendBroadcast(it1);
							//}
								
						}
						
					}
				}
				//else if(action.equals(Constants.ACTION_CONNET)){
				//	configIp = it.getStringExtra("ip");
				//	searchDevice(false);
				//}
			}
		}	
	};
	
	public void getBroadcastIp(){
		broadcastIp = getWiFiManagerInstance().getGatewayIpAddress();
		//LogUtil.printInfo(broadcastIp);
		InetAddress broadcastAddr;	   
	    try {
			broadcastAddr = InetAddress.getByName(broadcastIp);
			byte[] ipBytes = broadcastAddr.getAddress();
			ipBytes[3] = (byte)0xFF;
			
			broadcastAddr = InetAddress.getByAddress(ipBytes);
			broadcastIp = broadcastAddr.getHostAddress();
			LogUtil.printInfo("broadcastIp="+broadcastIp);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the Wifi Manager instance which gives the network related
	 * information like Wifi ,SSID etc.
	 * 
	 * @return Wifi Manager instance
	 */
	public CC3XWifiManager getWiFiManagerInstance() {
		if (mCC3XWifiManager == null)
		{
			mCC3XWifiManager = new CC3XWifiManager(DeviceManageActivity.this);
		}
		return mCC3XWifiManager;
	}


	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		serverIp = Constants.DEFAULT_SERVER_IP;//mSettings.getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_IP);
		serverPort  = Constants.DEFAULT_SERVER_PORT;//mSettings.getInt(Constants.SERVER_PORT, Constants.DEFAULT_SERVER_PORT);
		isSearch = true;
		timeCounter = MAX_TIMER_INTERVAL;
		cleanAction();
		updateUI();
		super.onResume();
	}

	public void searchDevice(boolean isATQ){
		//if(isSearch == true){
			getBroadcastIp();
			//mSettings.edit().putString(Constants.ACTION_TYPE, CC3XConstants.ACTION_SEARCH).commit();
			CC3XCmd cmd = CC3XCmd.getInstance();
			//UdpExchanger.getInstance().initSocket();
			if(isATQ) {
				System.out.println("=================searchDevice============" +broadcastIp);
				UdpExchanger.getInstance().sendPackage(cmd.configureQuit().getBytes(), broadcastIp,true);
			}
			
			
			UdpExchanger.getInstance().sendPackage(cmd.configureBroadcastID().getBytes(), broadcastIp,true);

		//}
	}
	
	public void repeatSearch(){
		getBroadcastIp();
		//mSettings.edit().putString(Constants.ACTION_TYPE, CC3XConstants.ACTION_SEARCH).commit();
		CC3XCmd cmd = CC3XCmd.getInstance();
		UdpExchanger.getInstance().sendPackage(cmd.configureBroadcastID().getBytes(), broadcastIp,true);
	}
	
	public void searchRemoteDevice(String macAddress){
		
		String serverIp = Constants.DEFAULT_SERVER_IP;//mSettings.getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_IP);
		int serverPort = Constants.DEFAULT_SERVER_PORT;//mSettings.getInt(Constants.SERVER_PORT, Constants.DEFAULT_SERVER_PORT);
		CC3XCmd cmd = CC3XCmd.getInstance();
		TCPExchanger.getInstance().initSocket(serverIp, serverPort);
		LogUtil.printInfo("tcp send="+cmd.searchRemoteDevice(macAddress));
		TCPExchanger.getInstance().sendPackage(cmd.searchRemoteDevice(macAddress).getBytes());
		
	}
	
	public void save(){
		synchronized(deviceManageList){
			//save deviceManageList
			mSettings.edit().putString(CC3XConstants.LOCAL_DEVICE, FileUtils.SaveDeviceInfo(deviceManageList)).commit();
		}
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		isSearch = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isRunning = false;
		
		save();
		unregisterReceiver(mResReceiver);
		//UdpExchanger.getInstance().closeSocket();
		TCPExchanger.getInstance().closeSocket();
		super.onDestroy();
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			
			finish();
			System.exit(0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	class timerThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				
				while(isRunning && timeCounter >= 0){
					
					if(isSearch)
					{						
						//LogUtil.printInfo("timer!");
						if(timeCounter == MAX_TIMER_INTERVAL){
							
							//clean deviceManageList online status
							for(int i=0;i<deviceManageList.size();i++){
								deviceManageList.get(i).setPreStatus(Constants.STATUS_OFFLINE);
							}
									
						}
					}
					
					LogUtil.printInfo("searchDevice,count="+Integer.toString(timeCounter)+"!");
					searchDevice(isSearch);
					Thread.sleep(300);
					if(timeCounter == MAX_TIMER_INTERVAL && isSearch){
						searchDevice(isSearch);
						Thread.sleep(300);
					}
					repeatSearch();
					
					Thread.sleep(5000);
					if(isSearch){
						Thread tcpThread = new Thread(new tcpThread());
						tcpThread.start();
					}
					
					//if(!isSearch)
					//	timeCounter = MAX_TIMER_INTERVAL-1;
					
					if(isSearch)
					{
						timeCounter --;
						if(timeCounter == 0){
							synchronized(deviceManageList){
								for(int i=0;i<deviceManageList.size();i++){
									deviceManageList.get(i).setStatus(deviceManageList.get(i).getPreStatus());
								}
							}
							
							//update ui
							Message msg = new Message();
			                msg.what = MESSAGE_TIMER;		                 
			                myMessageHandler.sendMessage(msg);
						}
					}
					
					Thread.sleep(10000);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void heartBeat(String ipAddress){
		
		LogUtil.printInfo("keep alive,ip=" + ipAddress);
		CC3XCmd cmd = CC3XCmd.getInstance();
		//UdpExchanger.getInstance().initSocket();
		UdpExchanger.getInstance().sendPackage(cmd.configureKeetAlive().getBytes(), ipAddress,true);
	}
	
	class heartbeatThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRunning){
				
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//keep heart beat
				synchronized(deviceInfoList){					
					for(int i=0;i<deviceInfoList.size();i++){
						if(deviceInfoList.get(i).getStatus() == Constants.STATUS_ONLINE)
							heartBeat(deviceInfoList.get(i).getIpStr2());
					}					
				}
				
				synchronized(deviceManageList){					
					for(int i=0;i<deviceManageList.size();i++){
						if(deviceManageList.get(i).getStatus() == Constants.STATUS_ONLINE)
							heartBeat(deviceManageList.get(i).getIpStr2());
					}					
				}
			}
		}
		
	}
	
	class tcpThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRunning && isSearch){
				try{
					LogUtil.printInfo("enter tcpThread");
					//
					if(!isSearch)
						Thread.sleep(5000);
					
					if(isSearch){
						Thread.sleep(2000);
						TCPExchanger.getInstance().initSocket(serverIp, serverPort);
												
						//if(timeCounter == MAX_TIMER_INTERVAL-1)
						{
							//tcp
							synchronized(deviceManageList){
								for(int i=0;i<deviceManageList.size();i++){
									if(deviceManageList.get(i).getPreStatus() == Constants.STATUS_OFFLINE){
										searchRemoteDevice(deviceManageList.get(i).getMacStr());
										String tcpBuffer = TCPExchanger.getInstance().receivePackage();
										parseTCPResult(tcpBuffer);
										tcpBuffer = null;
									}
								}
							}
						}
						
						//String tcpBuffer = TCPExchanger.getInstance().receivePackage();
						//if(tcpBuffer != null){
						//	Bundle bundle = new Bundle();
			            //    bundle.putString("msg", tcpBuffer);
			            //    Message msg = new Message();
			            //    msg.what = MESSAGE_TCP;
			            //    msg.setData(bundle);			                 
			            //    myMessageHandler.sendMessage(msg);
						//}
						//tcpBuffer = null;
						
						Message msg = new Message();
						msg.what = MESSAGE_TCP;
						myMessageHandler.sendMessage(msg);
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	class actionThread implements Runnable{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub	
			//LogUtil.printInfo("begin");
			
			while(isRunning){
				try{
					
					//UdpExchanger.getInstance().closeSocket();
					//UdpExchanger.getInstance().initSocket();
					UdpExchanger.getInstance().startSocket();
					
					wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    MulticastLock ml = wifi.createMulticastLock("just some tag text");  
                    ml.acquire(); 
                    
                    //LogUtil.printInfo("recv");
					data = UdpExchanger.getInstance().receivePackage();
					
					  
                    ml.release(); 
					
                   
					if(data != null){
						Bundle bundle = new Bundle();
		                bundle.putString("msg", data);
		                Message msg = new Message();
		                msg.what = MESSAGE_UDP;
		                msg.setData(bundle);
		                 
		                myMessageHandler.sendMessage(msg);
					}
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(e instanceof SocketTimeoutException){
						LogUtil.printError("time out");
						if(!StringUtils.isBlank(mSettings.getString(Constants.ACTION_TYPE, ""))){
							

			                Message msg = new Message();
			                msg.what = MESSAGE_TIME_OUT;
			                myMessageHandler.sendMessage(msg);
						}
					}
					
					//e.printStackTrace();
				}
			}
		}	
	}

	Handler myMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			Thread.currentThread().interrupt();
			if(msg.what == MESSAGE_UDP){
				
				getBroadcastIp();
				LogUtil.printInfo("end");
				String type = mSettings.getString(Constants.ACTION_TYPE, "");
				
				LogUtil.printInfo(type);
				if(data.indexOf("+ok=") == 0){
					
					if(type.equals(Constants.CONFIGURE_SCAN)){
						LogUtil.printInfo("scan wifi");
						//scan wifi list
						int start=0,end=0;
						String subStr = "",result="";
						
						int index = data.indexOf("\n");
						while(index > 0){
							end = index - 1;
							subStr = data.substring(start,end);
							
							LogUtil.printInfo("subStr="+subStr);
							if(subStr.indexOf("+ok") !=-1){
								//do nothing
							}
							else if(subStr.indexOf("SSID") != -1){
								//do nothing
								result += subStr;
								result += "|";
							}
							else{
								//parse
								if(!StringUtils.isBlank(subStr)){
									result += subStr;
									result += "|";
								}
							}
							if(index < data.length()-2){
								data = data.substring(index+2, data.length());
								index = data.indexOf("\n");
							}
							else{
								break;
							}
						}
						
						Intent it = new Intent(Constants.CONFIGURE_SCAN);
						it.putExtra("result", result);
						DeviceManageActivity.this.sendBroadcast(it);
					}
					else if(type.equals(Constants.CONFIGURE_SET_SSID) || type.equals(Constants.CONFIGURE_SET_WSKEY)){
						Intent it = new Intent(type);
						it.putExtra("result", "");
						DeviceManageActivity.this.sendBroadcast(it);
					}
					else {
						Intent it = new Intent(type);
						it.putExtra("result", data);
						DeviceManageActivity.this.sendBroadcast(it);
					}
				}
				else if(data.indexOf("+ok") == 0){
					Intent it = new Intent(type);
					it.putExtra("result", data);
					DeviceManageActivity.this.sendBroadcast(it);
				}
				else if(data.indexOf("+ERR") == 0){					
					Intent it = new Intent(Constants.ACTION_ERROR);
					DeviceManageActivity.this.sendBroadcast(it);
				}
				else{
					int index = data.indexOf(",");
					String ip = "";
					if(index != -1){
						ip = data.substring(0,index);
					}
					
					//device type
					if(ip.indexOf(broadcastIp.substring(0, 6)) == 0){
						LogUtil.printInfo("device list="+data);
						LogUtil.printInfo("CC3XConstants.ACTION_SEARCH");
						Intent it = new Intent(Constants.ACTION_SEARCH);
						it.putExtra("result", data);
						DeviceManageActivity.this.sendBroadcast(it);
					}
					else{
						//discards
					}
				}
				
				//Toast.makeText(DeviceManageActivity.this, "receive", Toast.LENGTH_SHORT).show();
			}
			else if(msg.what == MESSAGE_TIME_OUT){
				Intent it = new Intent(Constants.ACTION_TIME_OUT);
				DeviceManageActivity.this.sendBroadcast(it);
			}
			else if(msg.what == MESSAGE_TCP){
				
				//String tcpBuffer = msg.getData().getString("msg");				
				//parseTCPResult(tcpBuffer);
				updateUI();
			}
			else if(msg.what == MESSAGE_TIMER){
				LogUtil.printInfo("MESSAGE_TIMER update UI");
				timeCounter = MAX_TIMER_INTERVAL;
				updateUI();
				Thread timerThread = new Thread(new timerThread());
				timerThread.start();
			}
		}
	};
	
	public void parseTCPResult(String tcpBuffer){
		
		if(tcpBuffer.indexOf("ok#SV#") == 0){
			
			//ok#SV#00:95:69:00:46:24#ST#1
			String macAddress = tcpBuffer.substring(6,23);
			
			String isOn = tcpBuffer.substring(tcpBuffer.length()-1,tcpBuffer.length());
			LogUtil.printInfo("isOn="+isOn);
			if(isOn.equals("1")){
				LogUtil.printInfo("isOn");
				//synchronized(deviceManageList)
				{					
					for(int i=0;i<deviceManageList.size();i++){
						if(deviceManageList.get(i).getMacStr().equals(macAddress)
								&& (deviceManageList.get(i).getPreStatus() == Constants.STATUS_OFFLINE)){
							deviceManageList.get(i).setStatus(Constants.STATUS_REMOTE);
							deviceManageList.get(i).setPreStatus(Constants.STATUS_REMOTE);
							//updateUI();
							break;
						}
					}					
				}
			}
			else{
				//off
				for(int i=0;i<deviceManageList.size();i++){
					if(deviceManageList.get(i).getMacStr().equals(macAddress)){
						deviceManageList.get(i).setStatus(Constants.STATUS_OFFLINE);
						deviceManageList.get(i).setPreStatus(Constants.STATUS_OFFLINE);
						//updateUI();
						break;
					}
				}
			}
		}
		else if(tcpBuffer.indexOf("ok#1") == 0){
			
		}
		else if(tcpBuffer.indexOf("ok#0")  == 0){
			
		}
		else{
			//error,do nothing
		}
	}
	
	public void updateUI(){
		if(isSearch){
			deviceListAdapter.notifyDataSetChanged();
			deviceListView.setAdapter(deviceListAdapter);
		}
	}
	
	public void updateNewDeviceList(){
				
		newDeviceLayout.removeAllViews();
		if(deviceInfoList == null || deviceInfoList.size() <= 0)
			return;
		
		for(int i=0;i<deviceInfoList.size();i++){
			View deviceInfoView = getLayoutInflater().inflate(R.layout.list_item_new_device, null);
			
			TextView device_info = (TextView)deviceInfoView.findViewById(R.id.tv_device_info);
			device_info.setTag(i);
			device_info.setText(deviceInfoList.get(i).getDeviceName() + "\n"+ deviceInfoList.get(i).getMacStr()); //+"\n" + deviceInfoList.get(i).getTask());
			device_info.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					showRenameWindow(deviceInfoList.get(index).getDeviceName(),index);
				}
				
			});
					
			newDeviceLayout.addView(deviceInfoView, 
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		}	
	}
	
	public void updateDeviceManageList(){
		
		LogUtil.printInfo("update2");
		updateUI();

		
	    int totalHeight = 0; 
	    for (int i = 0; i < deviceListAdapter.getCount(); i++) { 
	        View listItem = deviceListAdapter.getView(i, null, deviceListView); 
	        listItem.measure(0, 0); 
	        totalHeight += listItem.getMeasuredHeight(); 
	    } 
	    
	    LogUtil.printInfo("totalHeight="+Integer.toString(totalHeight));
	    totalHeight = totalHeight*2;

	    ViewGroup.LayoutParams params = deviceListView.getLayoutParams(); 
	    params.height = totalHeight + (deviceListView.getDividerHeight() * (deviceListView.getCount()-1)); 
	    deviceListView.setLayoutParams(params);
	    
	}
	
	public void showRenameWindow(String deviceName,int index){
	    	LogUtil.printError("show input showRenameWindow");
	    	
	    	if(popup!=null && popup.isShowing())
	    		popup.dismiss();
	    	
			layoutInflater = getLayoutInflater();	
			popView = layoutInflater.inflate(R.layout.popwindow, null);
			popup= new PopupWindow(popView,LayoutParams.FILL_PARENT,
		                LayoutParams.FILL_PARENT, true);
			
			
			viewFilpper = (ViewFlipper) popView.findViewById(R.id.viewFlipper1);
			viewFilpper.setInAnimation(AnimationUtils.loadAnimation(DeviceManageActivity.this,R.anim.animation_in_from_bottom));
			viewFilpper.setFlipInterval(600000);
			
			popup.setFocusable(true);
			popup.setWidth(LayoutParams.FILL_PARENT);
			popup.setHeight(LayoutParams.WRAP_CONTENT);
			popup.setBackgroundDrawable(new BitmapDrawable());
			popup.showAtLocation(findViewById(R.id.linearLayout1), Gravity.BOTTOM, 0, 0);
			viewFilpper.startFlipping();	
			
			et_name = null;
			et_name = (EditText)popView.findViewById(R.id.et_device_name);
			et_name.setText(deviceName);
			
			popView.findViewById(R.id.btn_cancel).setTag(index);
			popView.findViewById(R.id.btn_confirm).setTag(index);
			popView.findViewById(R.id.btn_cancel).setOnClickListener(view_listener);
			popView.findViewById(R.id.btn_confirm).setOnClickListener(view_listener);
	   }
	
	   
	class ViewOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LogUtil.printInfo("onclick.....");
			if(v.getId() == R.id.btn_cancel){
				popup.dismiss();
			}
			else if(v.getId() == R.id.btn_confirm){
				int index = (Integer)v.getTag();
				
				deviceInfoList.get(index).setDeviceName(et_name.getText().toString());
				addDevice(index);			
				popup.dismiss();
				
				save();
			}
			else  if(v.getId() == R.id.tv_manage_device_info){
				
				int index = (Integer)v.getTag();
				if(deviceManageList.get(index).getStatus() == Constants.STATUS_OFFLINE){
					Toast.makeText(DeviceManageActivity.this, getResources().getString(R.string.hint_device_is_offline), Toast.LENGTH_SHORT).show();
					//test
					//Intent it = new Intent(DeviceManageActivity.this,ChooseModeActivity.class);
					//it.putExtra("device", deviceManageList.get(index));
					//startActivityForResult(it,0);
				}
				else{
					Intent it = new Intent(DeviceManageActivity.this,ChooseModeActivity.class);
					it.putExtra("device", deviceManageList.get(index));
					startActivityForResult(it,0);
				}
			}
			else if(v.getId() == R.id.btn_right){				
				searchDevice(isSearch);
			}
		}
		
	}
	
	
	public void addDevice(int index){
		
		if(index>=0 && index<deviceInfoList.size() && deviceInfoList.size() >0){
			deviceManageList.add(deviceInfoList.get(index));
			deviceInfoList.remove(index);
			
			updateNewDeviceList();
			updateDeviceManageList();
			
		}
	}
	
	
	class MyListAdapter extends BaseAdapter {

		private Context context;
		private List<DeviceInfo> arrayList;

		public MyListAdapter(Context context, List<DeviceInfo> arrayList) {
			this.context = context;
			this.arrayList = arrayList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (arrayList != null && arrayList.size() > 0) {
				return arrayList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arrayList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		class ViewHolder {
			ImageView icon;
			TextView device_info;
			ImageView device_status;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item_manage_device1, null);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_device);
				
				String url = deviceManageList.get(position).getImgPath();
				if(!StringUtils.isBlank(url)){
					
					Bitmap bitmap = AsyncImageLoader.loadDrawable(url, new ImageCallback() {
						@Override
						public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
							
						}
					});
					if(bitmap!= null)
						viewHolder.icon.setImageBitmap(bitmap);
				}
				
				viewHolder.device_info = (TextView) convertView.findViewById(R.id.tv_manage_device_info);
				viewHolder.device_info.setTag(position);
				viewHolder.device_info.setOnClickListener(view_listener);	
				viewHolder.device_info.setOnLongClickListener(new OnLongClickListener(){

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						delete_index = (Integer)v.getTag();
						
						new AlertDialog.Builder(DeviceManageActivity.this)
			     		   .setTitle(R.string.hint_info)
			     		   .setMessage(R.string.hint_delete)
			     		   .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								synchronized(deviceManageList){
									deviceManageList.remove(delete_index);		
									save();
								}	
								updateUI();							
							}
			     		})
			     		.setNegativeButton(R.string.button_cancel, null)
			     		.show();
						
						
						return true;
					}
					
				});
				viewHolder.device_info.setText(deviceManageList.get(position).getDeviceName() + "\n"
				+deviceManageList.get(position).getMacStr());
			
				viewHolder.device_status = (ImageView) convertView.findViewById(R.id.img_status);
				if(deviceManageList.get(position).getStatus() == Constants.STATUS_ONLINE)
					viewHolder.device_status.setImageResource(R.drawable.online);
				else if(deviceManageList.get(position).getStatus() == Constants.STATUS_REMOTE)
					viewHolder.device_status.setImageResource(R.drawable.remote);
				else
					viewHolder.device_status.setImageResource(R.drawable.offline);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			return convertView;
		}			
	}
	
	
	public void checkNewDevice(DeviceInfo device){
		
		//check deviceManageList
		for(int i=0;i<deviceManageList.size();i++){
			if( true == isSameDevice(device.getMacAddress(),deviceManageList.get(i).getMacAddress())){
				
				String name = deviceManageList.get(i).getDeviceName();
				byte[] img = deviceManageList.get(i).getImageContent();
				
				device.setStatus(Constants.STATUS_ONLINE);
				device.setPreStatus(Constants.STATUS_ONLINE);
				device.setDeviceName(name);
				device.setImageContent(img);
				device.setModeParam(3, 1, deviceManageList.get(i).getModeParam(3, 1));
				device.setModeParam(3, 2, deviceManageList.get(i).getModeParam(3, 2));
				device.setModeParam(3, 3, deviceManageList.get(i).getModeParam(3, 3));
				device.setModeParam(3, 4, deviceManageList.get(i).getModeParam(3, 4));
				device.setModeParam(4, 1, deviceManageList.get(i).getModeParam(4, 1));
				device.setModeParam(4, 2, deviceManageList.get(i).getModeParam(4, 2));
				device.setModeParam(4, 3, deviceManageList.get(i).getModeParam(4, 3));
				device.setModeParam(4, 4, deviceManageList.get(i).getModeParam(4, 4));
				img = null;
				
				deviceManageList.remove(i);
				deviceManageList.add(i, device);	
				
				updateUI();
				return;
			}
		}
		
		//check deviceList
		int j=0;
		for(j=0;j<deviceInfoList.size();j++){
			if( true == isSameDevice(device.getMacAddress(),deviceInfoList.get(j).getMacAddress())){
				return;
			}
		}
		
		if(j == deviceInfoList.size())
			deviceInfoList.add(device);
		
	}
	
	public boolean isSameDevice(byte[]macAddress1,byte[]macAddress2){
		
		for(int i=0;i<6;i++){
			if(macAddress1[i] != macAddress2[i])
				return false;
		}
		
		return true;
	}

}
