package com.lierda.wificontroller.kapage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lierda.model.WifiInfo;
import com.lierda.udp.UdpExchanger;
import com.lierda.utils.CC3XCmd;
import com.lierda.utils.CC3XParser;
import com.lierda.utils.CC3XWifiManager;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.utils.StringUtils;
import com.lierda.wificontroller.kapage.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ConfigureWifiActivity extends BaseActivity {
	
	private String ssidStr,chiperModeStr,chiperAlgorithm,passwordStr;
	private ListView listView;
	private MyListAdapter listAdapter;
	private Button btn_search;
	private EditText et_password;
	private String targetIp = "";
	private ButtonOnClickListener listener_btn = null;
	
	private int curIndex = -1;
	private ArrayList<WifiInfo> wifiList = new ArrayList<WifiInfo>();
	
	private LayoutInflater layoutInflater;
	private ViewFlipper viewFilpper;
	private PopupWindow popup;
	private View popView;
	
	private WifiAdmin mWifiAdmin;  
    private WifiManager mWifiManager;
    // ɨ�����б�  
    private List<ScanResult> list;  
    private ScanResult mScanResult;  
    private StringBuffer sb=new StringBuffer();
	
	private Bitmap bmp_wifi1,bmp_wifi2,bmp_wifi3;
	//private int tryTime = 2;
	
	private String broadcastIp = "";
	/**
	 * Wifi Manager instance which gives the network related information like
	 * Wifi ,SSID etc.
	 */
	private CC3XWifiManager mCC3XWifiManager = null;
	WifiManager wifi;


	private int timeout_retry = 0;
	private int MAX_TIME = 2;
	
	private Handler handler = new Handler( );
	private Runnable runnable = new Runnable( ) {
	public void run ( ) {

		checkTimeOut();
		//postDelayed(this,1000)��������һ��Runnable�������̶߳�����
	}
	};
	
	private void checkTimeOut(){
		LogUtil.printInfo("timer action");
		long currTime = System.currentTimeMillis();
		if(mSettings.getString(Constants.CONFIGURE_SCAN, "").equals(Constants.CONFIGURE_SCAN)){
			if(currTime - actionTime > 18000 && !StringUtils.isBlank(mSettings.getString(Constants.ACTION_TYPE, ""))){
				LogUtil.printInfo("timer action");
				Intent it = new Intent(Constants.ACTION_TIME_OUT);
				ConfigureWifiActivity.this.sendBroadcast(it);
			}			
		}
		else{
			if(currTime - actionTime > 30000 && !StringUtils.isBlank(mSettings.getString(Constants.ACTION_TYPE, ""))){
				LogUtil.printInfo("timer action");
				Intent it = new Intent(Constants.ACTION_TIME_OUT);
				ConfigureWifiActivity.this.sendBroadcast(it);
			}			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_router);
		
		initTitleBar(0,R.string.title_wifi_setting,R.string.button_search);
		timeout_retry = 0;
		getBroadcastIp();
		mWifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdmin(ConfigureWifiActivity.this);
        
		bmp_wifi1 = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.wifi_1), null, null);
		bmp_wifi2 = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.wifi_2), null, null);
		bmp_wifi3 = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.wifi_3), null, null);
		
		listener_btn = new ButtonOnClickListener();
		
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_search.setOnClickListener(listener_btn);
		
		listView = (ListView)findViewById(R.id.listView1);
		listAdapter = new MyListAdapter(this,wifiList);
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				LogUtil.printInfo("onItemClick");
				curIndex = arg2;
				listAdapter.notifyDataSetChanged();
				listView.setAdapter(listAdapter);
				
				if(wifiList.get(curIndex).getChiperMode().equals(Constants.AUTH_MODE_OPEN)){
					passwordStr = "";
					setSSID();
				}
				else{
					showPasswordWindow();
				}
			}
			
		});
		//et_password = (EditText)findViewById(R.id.et_password);

		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CONFIGURE_SCAN);
		filter.addAction(Constants.CONFIGURE_SET_SSID);
		filter.addAction(Constants.CONFIGURE_SET_WSKEY);
		filter.addAction(Constants.CONFIGURE_SET_WORK_MODE);
		filter.addAction(Constants.CMD_RESTART);
		filter.addAction(Constants.ACTION_TIME_OUT);
		//filter.addAction(Constants.ACTION_CONNETED);
		registerReceiver(mResReceiver,filter);
		
		showProgressDialog(getResources().getString(R.string.hint_wait_to_load),true);
		Thread scanThread = new Thread(new scanThread());
		scanThread.start();

		//scanWifi();
		/*
		Intent it = new Intent(Constants.ACTION_CONNET);
		it.putExtra("ip", getIntent().getStringExtra("ip"));
		ConfigureWifiActivity.this.sendBroadcast(it);
		showProgressDialog(getResources().getString(R.string.hint_wait_to_load),false);
		*/
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bmp_wifi1!=null && !bmp_wifi1.isRecycled())
			bmp_wifi1.recycle();
		if(bmp_wifi2!=null && !bmp_wifi2.isRecycled())
			bmp_wifi2.recycle();
		if(bmp_wifi3!=null && !bmp_wifi3.isRecycled())
			bmp_wifi3.recycle();
		unregisterReceiver(mResReceiver);
		
		System.gc();
		super.onDestroy();
	}
	
	public void scan(){
		LogUtil.printInfo(".....scan.....");
		//tryTime = 2;
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,15000); // ��ʼTimer		
		targetIp = getIntent().getStringExtra("ip");		
		final CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.scan());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SCAN).commit();

		UdpExchanger.getInstance().sendPackage(cmd.scan().getBytes(), targetIp,false);


	}
	
	public void scanWifi(){
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����  
    	
    	//��ʼɨ������
    	mWifiAdmin.startScan();
    	list=mWifiAdmin.getWifiList();
    	if(list!=null){
    		
    		curIndex = -1;
    		wifiList.clear();
    		
    		for(int i=0;i<list.size();i++){
    			//�õ�ɨ����
    			mScanResult=list.get(i);
    			
    			if(sb!=null){
    	    		sb=new StringBuffer();
    	    	}
    			sb=sb.append(mScanResult.SSID+",")
    			.append(mScanResult.capabilities+",")
    			.append(mScanResult.level);
    			
    			WifiInfo wifi = new WifiInfo();
    			wifi.setSSID(mScanResult.SSID);
    			
    			String capabilities = mScanResult.capabilities;
    			if(capabilities.indexOf("WPA-PSK") != -1 || capabilities.indexOf("WPA1-PSK") != -1 ){
    				wifi.setChiperMode(Constants.AUTH_MODE_WPAPSK);
				}
				else if(capabilities.indexOf("WPA2-PSK") != -1){
					wifi.setChiperMode(Constants.AUTH_MODE_WPA2PSK);
				}
				else if(capabilities.indexOf("WEP") != -1){
					wifi.setChiperMode(Constants.AUTH_MODE_SHARED);
					wifi.setChiperAlgorithm(Constants.ENCRYPT_WEP_A);
				}
				else{
					wifi.setChiperMode(Constants.AUTH_MODE_OPEN); 
					wifi.setChiperAlgorithm(Constants.ENCRYPT_NONE);
				}
    			
    			if(capabilities.indexOf("CCMP") != -1  && capabilities.indexOf("TKIP") != -1){
    				wifi.setChiperAlgorithm("TKIPAES");
    			}
    			else if(capabilities.indexOf("CCMP") != -1){
    				wifi.setChiperAlgorithm(Constants.ENCRYPT_AES);
    			}
    			else if(capabilities.indexOf("TKIP") != -1){
    				wifi.setChiperAlgorithm(Constants.ENCRYPT_TKIP);
    			}
    			wifi.setSignal(mScanResult.level);
    			wifiList.add(wifi);
    		}
    	}	
    	
    	listAdapter.notifyDataSetChanged();
		listView.setAdapter(listAdapter);
	}
	
	public void setSSID(){
		//tryTime = 2;
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,15000); // ��ʼTimer
		
		showProgressDialog(getResources().getString(R.string.hint_wait_to_configure),false);
		ssidStr = wifiList.get(curIndex).getSSID();
				
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo(cmd.configureSETSSID(ssidStr));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_SSID).commit();
		UdpExchanger.getInstance().sendPackage(cmd.configureSETSSID(ssidStr).getBytes(), targetIp,false);
				
	}
	
	public void setWSKEY(){
		//tryTime = 2;
		chiperModeStr = wifiList.get(curIndex).getChiperMode();
		chiperAlgorithm = wifiList.get(curIndex).getChiperAlgorithm();
	
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo(cmd.configureSETWSKEY(chiperModeStr, chiperAlgorithm, passwordStr));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_WSKEY).commit();
		UdpExchanger.getInstance().sendPackage(cmd.configureSETWSKEY(chiperModeStr, chiperAlgorithm, passwordStr).getBytes(), targetIp,false);
	}
	
	public void setMode(String workMode){
		//tryTime = 2;
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.setMode(workMode));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_WORK_MODE).commit();
		UdpExchanger.getInstance().sendPackage(cmd.setMode(workMode).getBytes(), targetIp,false);
		
	}
	
	public void restart(){
		//tryTime = 2;
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.restart());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CMD_RESTART).commit();
		UdpExchanger.getInstance().sendPackage(cmd.restart().getBytes(), targetIp,false);
	}
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v.getId() == R.id.btn_ok){	
				if(popup!=null && popup.isShowing())
		    		popup.dismiss();
				passwordStr = et_password.getText().toString();
				setSSID();
			}
			else if(v.getId() == R.id.btn_cancel){
				if(popup!=null && popup.isShowing())
		    		popup.dismiss();
			}
			else if(v.getId() == R.id.btn_search){
				timeout_retry = 0;
				scan();
				//scanWifi();
			}
		}
		
	}

	private BroadcastReceiver mResReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context c, Intent it)
		{
			// TODO Auto-generated method stub
			
			if(it != null)
			{
				String action = it.getAction();
				LogUtil.printInfo("config wifi="+action);
				//if(action.equals(Constants.ACTION_CONNETED)){
				//	scan();
				//}
				if(action.equals(Constants.CONFIGURE_SCAN)){
					LogUtil.printInfo("recv broadcast CONFIGURE_SCAN");
					String data = it.getStringExtra("result");
					if(data != null){
						
						LogUtil.printInfo("get result="+data);
						curIndex = -1;
			    		wifiList.clear();
						wifiList.addAll(CC3XParser.getInstance().parseWifi(data));
						LogUtil.printInfo("size="+wifiList.size());

						listView.setAdapter(listAdapter);
					}
					handler.removeCallbacks(runnable); //ֹͣTimer
					closeProgressDialog();
					cleanAction();
				}
				else if(action.equals(Constants.CONFIGURE_SET_SSID)){
					
					setWSKEY();
				}
				else if(action.equals(Constants.CONFIGURE_SET_WSKEY)){
					setMode("STA");
				}
				else if(action.equals(Constants.CONFIGURE_SET_WORK_MODE)){
					restart();
				}
				else if(action.equals(Constants.CMD_RESTART)){
					handler.removeCallbacks(runnable); //ֹͣTimer
					new AlertDialog.Builder(ConfigureWifiActivity.this)
		     		   .setTitle(R.string.hint_info)
		     		   .setMessage(R.string.hint_success_configure)
		     		   .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							finish();							
						}	   
		     		}).show();
					
					
					closeProgressDialog();
					cleanAction();
				}
				else if(action.equals(Constants.ACTION_TIME_OUT)){
					timeout_retry++;
					if(timeout_retry<MAX_TIME){
						scan();
					}else{
						timeout_retry=0;
						closeProgressDialog();
						showToast(getResources().getString(R.string.hint_error_time_out));
						cleanAction();
					}
				}
			}
		}	
	};
	
	class MyListAdapter extends BaseAdapter {

		private Context context;
		private List<WifiInfo> arrayList;

		public MyListAdapter(Context context, List<WifiInfo> arrayList) {
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
			RadioButton rd_tick,rd_lock;
			TextView tv_wifi;
			ImageView wifi_status;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item_wifi, null);
				
				viewHolder.rd_tick = (RadioButton) convertView.findViewById(R.id.rd_tick);
				if(curIndex == position){
					viewHolder.rd_tick.setChecked(true);
				}
				else{
					viewHolder.rd_tick.setChecked(false);
				}
				
				viewHolder.tv_wifi = (TextView) convertView.findViewById(R.id.tv_wifi);
				viewHolder.tv_wifi.setText(wifiList.get(position).getSSID());
				//viewHolder.tv_wifi.setText(Integer.toString(position)+":"+wifiList.get(position).getSSID()+","+wifiList.get(position).getChiperMode()+"/"+wifiList.get(position).getChiperAlgorithm());
				LogUtil.printInfo((Integer.toString(position)+":"+wifiList.get(position).getSSID()+","+wifiList.get(position).getChiperMode()+"/"+wifiList.get(position).getChiperAlgorithm()+"/"+wifiList.get(position).getSignal()));
				
				viewHolder.rd_lock = (RadioButton)convertView.findViewById(R.id.rd_lock);
				
				
				if(wifiList.get(position).getChiperMode().equals(Constants.AUTH_MODE_OPEN)){
					//LogUtil.printInfo(Integer.toString(position));
					viewHolder.rd_lock.setChecked(false);
				}
				else{
					viewHolder.rd_lock.setChecked(true);
				}
				viewHolder.wifi_status = (ImageView)convertView.findViewById(R.id.img_wifi);
				int signal = wifiList.get(position).getSignal();
				
				if( signal >= 60){
					viewHolder.wifi_status.setImageBitmap(bmp_wifi3);
				}
				else if(signal >= 30){
					viewHolder.wifi_status.setImageBitmap(bmp_wifi2);
				}
				else{
					viewHolder.wifi_status.setImageBitmap(bmp_wifi1);
				}
				/*
				if( signal < -90){
					viewHolder.wifi_status.setImageBitmap(bmp_wifi1);
				}
				else if(signal < -66){
					viewHolder.wifi_status.setImageBitmap(bmp_wifi2);
				}
				else{
					viewHolder.wifi_status.setImageBitmap(bmp_wifi3);
				}
				*/
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			return convertView;
		}			
	}

	   void showPasswordWindow(){
	    	LogUtil.printError("show input showPasswordWindow");
	    	
	    	if(popup!=null && popup.isShowing())
	    		popup.dismiss();
	    	
			layoutInflater = getLayoutInflater();	
			popView = layoutInflater.inflate(R.layout.popwindow_password, null);
			popup= new PopupWindow(popView,LayoutParams.FILL_PARENT,
		                LayoutParams.FILL_PARENT, true);
			
			
			viewFilpper = (ViewFlipper) popView.findViewById(R.id.viewFlipper1);
			viewFilpper.setInAnimation(AnimationUtils.loadAnimation(ConfigureWifiActivity.this,R.anim.animation_in_from_bottom));
			viewFilpper.setFlipInterval(600000);
			
			popup.setFocusable(true);
			popup.setWidth(LayoutParams.FILL_PARENT);
			popup.setHeight(LayoutParams.FILL_PARENT);
			popup.setBackgroundDrawable(new BitmapDrawable());
			popup.showAtLocation(findViewById(R.id.linearLayout1), Gravity.BOTTOM, 0, 0);
			viewFilpper.startFlipping();	
			
			((TextView)popView.findViewById(R.id.tvppw_id_content)).setText(wifiList.get(curIndex).getSSID());
			et_password = null;
			et_password = (EditText)popView.findViewById(R.id.etppw_pwd);
					
			popView.findViewById(R.id.btn_cancel).setOnClickListener(listener_btn);
			popView.findViewById(R.id.btn_ok).setOnClickListener(listener_btn);
	   }
	
		class scanThread implements Runnable{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try{
					//
					getBroadcastIp();
					CC3XCmd cmd = CC3XCmd.getInstance();
					UdpExchanger.getInstance().sendPackage(cmd.configureQuit().getBytes(), broadcastIp,true);
					UdpExchanger.getInstance().sendPackage(cmd.configureBroadcastID().getBytes(), broadcastIp,true);
					
					Thread.sleep(250);
					UdpExchanger.getInstance().sendPackage(cmd.configureBroadcastID().getBytes(), broadcastIp,true);
					Thread.sleep(250);
					UdpExchanger.getInstance().sendPackage(cmd.configureBroadcastID().getBytes(), broadcastIp,true);
					Thread.sleep(1500);
					scan();
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
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
				mCC3XWifiManager = new CC3XWifiManager(ConfigureWifiActivity.this);
			}
			return mCC3XWifiManager;
		}
}
