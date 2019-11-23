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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ConfigureModeActivity extends BaseActivity {
	
	private RadioButton apBtn,staBtn;
	private ButtonOnClickListener listener_btn;
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
			ConfigureModeActivity.this.sendBroadcast(it);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_mode);
		
		targetIp = getIntent().getStringExtra("ip");
		listener_btn = new ButtonOnClickListener();
		
		initTitleBar(0,R.string.button_work_mode_configure,0);
		apBtn = (RadioButton)findViewById(R.id.rd_ap);
		staBtn = (RadioButton)findViewById(R.id.rd_sta);
		apBtn.setOnClickListener(listener_btn);
		staBtn.setOnClickListener(listener_btn);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CONFIGURE_GET_WORK_MODE);
		filter.addAction(Constants.CONFIGURE_SET_WORK_MODE);
		filter.addAction(Constants.ACTION_TIME_OUT);
		registerReceiver(mResReceiver,filter);
		
		getMode();
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
				if(action.equals(Constants.CONFIGURE_GET_WORK_MODE)){
					
					String data = it.getStringExtra("result");
					if(data != null){
						String mode = data.substring(4,data.length()-4);
						if(mode.equals("ap") || mode.equals("AP") || mode.equals("aP") || mode.equals("Ap")){
							apBtn.setChecked(true);
							apBtn.setTextColor(Color.BLACK);
							staBtn.setTextColor(Color.WHITE);
							
						}
						else{
							staBtn.setChecked(true);
							apBtn.setTextColor(Color.WHITE);
							staBtn.setTextColor(Color.BLACK);
						}
					}
					
					closeProgressDialog();
					cleanAction();
					handler.removeCallbacks(runnable); //停止Timer
				}
				else if(action.equals(Constants.CONFIGURE_SET_WORK_MODE)){
					
					new AlertDialog.Builder(ConfigureModeActivity.this)
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

	public void getMode(){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		showProgressDialog(getResources().getString(R.string.hint_wait_to_load),false);
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.getMode());
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_GET_WORK_MODE).commit();
		UdpExchanger.getInstance().sendPackage(cmd.getMode().getBytes(), targetIp,false);
	}
	
	public void setMode(String workMode){
		actionTime = System.currentTimeMillis();
		handler.postDelayed(runnable,30000); // 开始Timer
		showProgressDialog(getResources().getString(R.string.hint_wait_to_configure),false);
		CC3XCmd cmd = CC3XCmd.getInstance();
		LogUtil.printInfo("targetIp = "+targetIp+",cmd="+cmd.setMode(workMode));
		mSettings.edit().putString(Constants.ACTION_TYPE, Constants.CONFIGURE_SET_WORK_MODE).commit();
		UdpExchanger.getInstance().sendPackage(cmd.setMode(workMode).getBytes(), targetIp,false);
		
	}
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.rd_ap || v.getId() == R.id.rd_sta){
				
				if(apBtn.isChecked()){
					apBtn.setTextColor(Color.BLACK);
					staBtn.setTextColor(Color.WHITE);
					setMode("AP");
				}
				else{
					apBtn.setTextColor(Color.WHITE);
					staBtn.setTextColor(Color.BLACK);
					setMode("STA");
				}
			} 
		}
		
	}

}
