package com.lierda.wificontroller.kapage;


import java.util.Timer;
import java.util.TimerTask;

import com.lierda.udp.UdpExchanger;
import com.lierda.utils.CC3XCmd;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.utils.StringUtils;
import com.lierda.wificontroller.kapage.R;
import com.lierda.wificontroller.kapage.ConfigureServerActivity.ButtonOnClickListener;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfigureOtherActivity extends BaseActivity {
	
	private String targetIp = "";
	private  final String[] modeArr={"AP","STA"};
	private TextView tv_work_mode;
	private Button btn_ap,btn_server,btn_work_mode,btn_setting,btn_reset,btn_restart;
	private ButtonOnClickListener listener_btn = null;
	
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
			ConfigureOtherActivity.this.sendBroadcast(it);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_other);
		
		targetIp = getIntent().getStringExtra("ip");

		initTitleBar(0,R.string.title_more,0);
		initFootBar2();
		setConfigBtn();
		
		listener_btn = new ButtonOnClickListener();
		
		btn_ap = (Button)findViewById(R.id.btn_ap);
		btn_server = (Button)findViewById(R.id.btn_server);
		btn_setting = (Button)findViewById(R.id.btn_setting);
		btn_reset = (Button)findViewById(R.id.btn_reset);
		btn_restart = (Button)findViewById(R.id.btn_restart);
		
		btn_ap.setOnClickListener(listener_btn);
		btn_server.setOnClickListener(listener_btn);
		//tv_work_mode.setOnClickListener(listener_btn);
		//btn_work_mode.setOnClickListener(listener_btn);
		btn_setting.setOnClickListener(listener_btn);
		btn_reset.setOnClickListener(listener_btn);
		btn_restart.setOnClickListener(listener_btn);
		inforBtn.setOnClickListener(listener_btn);
		controllerBtn.setOnClickListener(listener_btn);
		
		
		
		//getMode();
	}
	
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(mResReceiver);
		super.onPause();
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CMD_RESTART);
		filter.addAction(Constants.CMD_FACTORY_SETTING);
		filter.addAction(Constants.ACTION_TIME_OUT);
		registerReceiver(mResReceiver,filter);
		super.onResume();
	}




	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub	
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
				if(action.equals(Constants.CONFIGURE_GET_WORK_MODE)){
					
					String data = it.getStringExtra("result");
					if(data != null){
						//tv_work_mode.setText(data.substring(4,data.length()-4));
					}
				}
				else if(action.equals(Constants.CMD_RESTART)){
					//showToast("success!!!");
					closeProgressDialog();
					cleanAction();
					
					new AlertDialog.Builder(ConfigureOtherActivity.this)
		     		   .setTitle(R.string.hint_info)
		     		   .setMessage(R.string.hint_success_configure)
		     		   .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							ConfigureOtherActivity.this.setResult(Constants.RESULT_QUIT);
							finish();							
						}	   
		     		}).show();
					handler.removeCallbacks(runnable); //停止Timer
					
				}
				else if(action.equals(Constants.CMD_FACTORY_SETTING)){
					//showToast("success!!!");
					new AlertDialog.Builder(ConfigureOtherActivity.this)
		     		   .setTitle(R.string.hint_info)
		     		   .setMessage(R.string.hint_success_configure)
		     		   .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							//finish();							
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

	public void restart(){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		showProgressDialog(getResources().getString(R.string.hint_wait_to_restart),false);
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.restart());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CMD_RESTART).commit();
		UdpExchanger.getInstance().sendPackage(cmd.restart().getBytes(), targetIp,false);
	}
	
	public void recover(){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		showProgressDialog(getResources().getString(R.string.hint_wait_to_restore),false);
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.reset());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CMD_FACTORY_SETTING).commit();
		UdpExchanger.getInstance().sendPackage(cmd.reset().getBytes(), targetIp,false);
	}


	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			/*
			if(v.getId() == R.id.btn_work_mode || v.getId() == R.id.tv_work_mode){
				
				new AlertDialog.Builder(ConfigureOtherActivity.this)
				.setTitle(R.string.msg_please_choose)
				.setIcon(android.R.drawable.ic_dialog_info)                
				.setItems(modeArr, 
				  new DialogInterface.OnClickListener() {
				                            
				     public void onClick(DialogInterface dialog, int which) {
				    	 tv_work_mode.setText(modeArr[which]);
				    	 dialog.dismiss();
				     }
				  }
				)
				.show();
			}
			*/
			
			
			if(v.getId() == R.id.btn_ap){
				
				Intent it = new Intent(ConfigureOtherActivity.this,ConfigureWifiActivity.class);
				it.putExtra("ip", targetIp);
				startActivity(it);
			}
			else if(v.getId() == R.id.btn_server){
				
				Intent it = new Intent(ConfigureOtherActivity.this,ConfigureServerActivity.class);
				it.putExtra("ip", targetIp);
				startActivity(it);
			}
			else if(v.getId() == R.id.btn_setting){
				
				Intent it = new Intent(ConfigureOtherActivity.this,ConfigureModeActivity.class);
				it.putExtra("ip", targetIp);
				startActivity(it);
				
			}
			else if(v.getId() == R.id.btn_reset){
				recover();
			}
			else if(v.getId() == R.id.btn_restart){
				restart();
			}
			else if(v.getId() == R.id.btn_controller){
				finish();
				// Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
			}
			else if(v.getId() == R.id.btn_information){
				setResult(R.id.btn_information);
				finish();
			}
		}
		
	}

}
