package com.lierda.wificontroller.kapage;

import com.lierda.utils.CC3XConstants;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.wificontroller.kapage.R;
import com.lierda.wificontroller.kapage.DeviceDetailActivity.saveThread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public Button leftBtn,rightBtn;
	public RadioButton wifiConfigBtn,serverConfigBtn,otherConfigBtn;
	public RadioButton controllerBtn,inforBtn,configBtn;
	public TextView tv_title;
	public String ipStr = "";
	
	private ProgressDialog mProDialog = null;
	public SharedPreferences mSettings;
	public long actionTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		WifiManager myWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
		int ipAddress = myWifiInfo.getIpAddress();
		ipStr= Formatter.formatIpAddress(ipAddress);
		LogUtil.printInfo("hostip="+ipStr);
		//Toast.makeText(BaseActivity.this, ipStr, Toast.LENGTH_SHORT).show();
		
		mSettings = getSharedPreferences(CC3XConstants.SETTING_INFOS,0);
	}

	public void initTitleBar(String title){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText(title);
	}

	public void initTitleBar(int left,int title,int right){
		
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText(title);
	}
	
	//controllerBtn,inforBtn,configBtn
	public void initFootBar2(){
		
		controllerBtn = (RadioButton)findViewById(R.id.btn_controller);		
		inforBtn = (RadioButton)findViewById(R.id.btn_information);		
		configBtn = (RadioButton)findViewById(R.id.btn_configure);

	}
	public void setControllerBtn(){
		controllerBtn.setChecked(true);
	}
	public void setInfoBtn(){
		inforBtn.setChecked(true);
	}
	public void setConfigBtn(){
		configBtn.setChecked(true);
	}
	

	
	public void showProgressDialog(String hintString,boolean cancelable){
		
		if(cancelable == true){
			mProDialog = ProgressDialog.show(BaseActivity.this, null, hintString,true,cancelable,
					new DialogInterface.OnCancelListener(){
	
						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							LogUtil.printInfo("thread canceled!!");
							//mHttpsUtil.setCanceled(true);
							//isRunning = false;
							//actionThread.interrupt();
						}
				
			});
		}
		else{
			mProDialog = ProgressDialog.show(BaseActivity.this, null, hintString); //);
		}
	}
	
	protected void closeProgressDialog(){
		
		if(mProDialog != null && mProDialog.isShowing())
			mProDialog.dismiss();
	}

	public void showToast(String hintString){
		Toast.makeText(BaseActivity.this, hintString, Toast.LENGTH_SHORT).show();
	}
	
	public void cleanAction(){
		mSettings.edit().putString(Constants.ACTION_TYPE, "").commit();
	}
	
}
