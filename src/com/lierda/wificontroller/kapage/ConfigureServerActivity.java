package com.lierda.wificontroller.kapage;


import java.util.Timer;
import java.util.TimerTask;

import com.lierda.udp.UdpExchanger;
import com.lierda.utils.CC3XCmd;
import com.lierda.utils.CC3XParser;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.utils.StringUtils;
import com.lierda.wificontroller.kapage.R;
import com.lierda.wificontroller.kapage.ConfigureWifiActivity.ButtonOnClickListener;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConfigureServerActivity extends BaseActivity {
	
	private EditText et_server_name,et_server_port;
	private Button btn_save;
	private ButtonOnClickListener listener_btn = null;
	private String targetIp = "";
	
	private Handler handler = new Handler( );
	private Runnable runnable = new Runnable( ) {
	public void run ( ) {

		checkTimeOut();
		//postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
	}
	};
	
	private void checkTimeOut(){
		LogUtil.printInfo("timer action");
		long currTime = System.currentTimeMillis();			
		if(currTime - actionTime > 60000 && !StringUtils.isBlank(mSettings.getString(Constants.ACTION_TYPE, ""))){
			LogUtil.printInfo("timer action");
			Intent it = new Intent(Constants.ACTION_TIME_OUT);
			ConfigureServerActivity.this.sendBroadcast(it);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_server);
		
		targetIp = getIntent().getStringExtra("ip");
		listener_btn = new ButtonOnClickListener();
		
		initTitleBar(0,R.string.button_server_configure,0);
		et_server_name = (EditText)findViewById(R.id.et_server_name);
		et_server_name.setHint(getResources().getString(R.string.text_input)+getResources().getString(R.string.text_server_address));
		et_server_port = (EditText)findViewById(R.id.et_server_port);
		et_server_port.setHint(getResources().getString(R.string.text_input)+getResources().getString(R.string.text_server_port));
		
		btn_save = (Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(listener_btn);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CONFIGURE_GET_SERVER_ADDRESS);
		filter.addAction(Constants.CONFIGURE_GET_SERVER_PORT);
		filter.addAction(Constants.CONFIGURE_SET_SERVER_ADDRESS);
		filter.addAction(Constants.CONFIGURE_SET_SERVER_PORT);
		filter.addAction(Constants.ACTION_TIME_OUT);
		registerReceiver(mResReceiver,filter);
		
		showProgressDialog(getResources().getString(R.string.hint_wait_to_load),false);
		getServerName();
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mResReceiver);
		super.onDestroy();
	}

	private BroadcastReceiver mResReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context c, Intent it)
		{
			// TODO Auto-generated method stub
			
			if(it != null)
			{
				String action = it.getAction();
				LogUtil.printInfo("action:" + action);
				if(action.equals(Constants.CONFIGURE_GET_SERVER_ADDRESS)){
					
					String data = it.getStringExtra("result");
					if(data != null){
						
						et_server_name.setText(data.substring(4,data.length()-4));
						getServerPort();
					}
					handler.removeCallbacks(runnable); //停止Timer
				}
				else if(action.equals(Constants.CONFIGURE_GET_SERVER_PORT)){
					String data = it.getStringExtra("result");
					if(data != null){
						et_server_port.setText(data.substring(4,data.length()-4));
					}
					//showToast("success!!!");
					closeProgressDialog();
					cleanAction();
				}
				else if(action.equals(Constants.CONFIGURE_SET_SERVER_ADDRESS)){
					setServerPort(et_server_port.getText().toString());
				}
				else if(action.equals(Constants.CONFIGURE_SET_SERVER_PORT)){
					
					new AlertDialog.Builder(ConfigureServerActivity.this)
		     		   .setTitle(R.string.hint_info)
		     		   .setMessage(R.string.hint_success_configure)
		     		   .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							UdpExchanger.getInstance().sendPackage(Constants.CONFIGURE_ENABLE_SERVER.getBytes(), targetIp,false);
							finish();							
						}	   
		     		}).show();
					closeProgressDialog();
					cleanAction();
					handler.removeCallbacks(runnable); //停止Timer
				}
				else if(action.equals(Constants.ACTION_TIME_OUT)){
					closeProgressDialog();
					showToast(getResources().getString(R.string.hint_error_time_out));
					cleanAction();
				}
			}
		}	
	};


	public void getServerName(){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.getServerAddress());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_GET_SERVER_ADDRESS).commit();		
		UdpExchanger.getInstance().sendPackage(cmd.getServerAddress().getBytes(), targetIp,false);
	}
	
	public void getServerPort(){
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.getServerPort());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_GET_SERVER_PORT).commit();
		UdpExchanger.getInstance().sendPackage(cmd.getServerPort().getBytes(), targetIp,false);
	}
	
	public void setServerName(String serverName){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.setServerAddress(serverName));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_SERVER_ADDRESS).commit();
		UdpExchanger.getInstance().sendPackage(cmd.setServerAddress(serverName).getBytes(), targetIp,false);
	}
	
	public void setServerPort(String port){
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.setServerPort(port));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_SERVER_PORT).commit();
		UdpExchanger.getInstance().sendPackage(cmd.setServerPort(port).getBytes(), targetIp,false);
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.btn_save){
				
				if(StringUtils.isBlank(et_server_name.getText().toString())){
					
					showToast(getResources().getString(R.string.hint_error_ip_address));
					return;
				}
				
				if(StringUtils.isBlank(et_server_port.getText().toString())){					
					showToast(getResources().getString(R.string.hint_error_server_port));
					return;
				}
				
				/*
				if(StringUtils.isBlank(et_local_port.getText().toString())){					
					showToast(getResources().getString(R.string.hint_error_local_port));
					return;
				}
				*/
				
				//save configure
				mSettings.edit().putString(Constants.SERVER_ADDRESS, et_server_name.getText().toString())
				.putInt(Constants.SERVER_PORT, Integer.parseInt(et_server_port.getText().toString()))
				//.putInt(Constants.LOCAL_PORT, Integer.parseInt(et_local_port.getText().toString()))
				.commit();
				
				setServerName(et_server_name.getText().toString());
				
			}	
			/*
			else if(v.getId() == R.id.btn_config_route){
				
				LogUtil.printInfo("onclick btn_config_route");
				Intent it = new Intent(ConfigureServerActivity.this, ConfigureWifiActivity.class);
				it.putExtra("ip", getIntent().getStringExtra("ip"));
				startActivity(it);
	            // Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
	            finish();
	            
			}
			else if(v.getId() == R.id.btn_config_server){

				LogUtil.printInfo("onclick btn_config_server");
				startActivity(new Intent(ConfigureServerActivity.this, ConfigureServerActivity.class));
	            // Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
	            finish();

			}
			else if(v.getId() == R.id.btn_config_other){
				LogUtil.printInfo("onclick btn_config_other");
				Intent it = new Intent(ConfigureServerActivity.this, ConfigureOtherActivity.class);
				it.putExtra("ip", getIntent().getStringExtra("ip"));
				startActivity(it);
	            // Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
	            finish();
			}
			*/
			
		}
		
	}

}
