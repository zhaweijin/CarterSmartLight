package com.lierda.wificontroller.kapage;

import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.lierda.model.DeviceInfo;
import com.lierda.udp.TCPExchanger;
import com.lierda.udp.UdpExchanger;
import com.lierda.udp.UdpSender;
import com.lierda.utils.CC3XCmd;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.utils.StringUtils;
import com.lierda.widget.CircularSeekBar;
import com.lierda.widget.CircularSeekBar.OnSeekChangeListener;
import com.lierda.wificontroller.kapage.R;

public class ControllerMode1Activity extends BaseActivity {
	
	private Button btn_on,btn_off,btn_speed_plus,btn_speed_minus,btn_program_plus,btn_program_minus;
	private CircularSeekBar seekBar;
	private DeviceInfo device = null;
	
	private int progress = 0;
	private byte[] cmdBytes = {0x00,0x00};
	private boolean isRunning = false;
	
	private ButtonOnClickListener listener_btn;
	private ButtonOnLongClickListener longlistener_btn;
	private ButtonOnTouchListener  touchlistener_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller_mode1);
		
		device = (DeviceInfo) getIntent().getSerializableExtra("device");
		if(device != null)
			initTitleBar(device.getDeviceName());
		
		listener_btn = new ButtonOnClickListener();
		longlistener_btn = new ButtonOnLongClickListener();
		touchlistener_btn = new ButtonOnTouchListener();
		
		btn_on = (Button)findViewById(R.id.btn_on);
		btn_off = (Button)findViewById(R.id.btn_off);
		btn_speed_plus = (Button)findViewById(R.id.btn_speed_plus);
		btn_speed_minus = (Button)findViewById(R.id.btn_speed_minus);
		btn_program_plus = (Button)findViewById(R.id.btn_program_plus);
		btn_program_minus = (Button)findViewById(R.id.btn_program_minus);
		
		btn_on.setOnClickListener(listener_btn);
		btn_off.setOnClickListener(listener_btn);
		btn_speed_plus.setOnClickListener(listener_btn);
		btn_speed_minus.setOnClickListener(listener_btn);
		btn_program_plus.setOnClickListener(listener_btn);
		btn_program_minus.setOnClickListener(listener_btn);
		
		btn_on.setOnLongClickListener(longlistener_btn);
		btn_off.setOnLongClickListener(longlistener_btn);
		btn_speed_plus.setOnLongClickListener(longlistener_btn);
		btn_speed_minus.setOnLongClickListener(longlistener_btn);
		btn_program_plus.setOnLongClickListener(longlistener_btn);
		btn_program_minus.setOnLongClickListener(longlistener_btn);
		
		btn_on.setOnTouchListener(touchlistener_btn);
		btn_off.setOnTouchListener(touchlistener_btn);
		btn_speed_plus.setOnTouchListener(touchlistener_btn);
		btn_speed_minus.setOnTouchListener(touchlistener_btn);
		btn_program_plus.setOnTouchListener(touchlistener_btn);
		btn_program_minus.setOnTouchListener(touchlistener_btn);
				
		seekBar = (CircularSeekBar)findViewById(R.id.circularSeekBar1);		
		seekBar.setMaxProgress(255);
		seekBar.setSeekBarChangeListener(new OnSeekChangeListener(){

			@Override
			public void onProgressChange(CircularSeekBar view, int newProgress) {
				// TODO Auto-generated method stub
				LogUtil.printInfo("progress="+Integer.toString(newProgress));
				progress = newProgress;
				cmdBytes[0] = 0x10;
				cmdBytes[1] = (byte)(progress&0xFF);
				sendCmd();
			}
			
		});
				
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isRunning = false;
		seekBar.clear();
		super.onDestroy();
	}


	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			cmdBytes[1] = 0;
			
			if(v.getId() == R.id.btn_on){
				cmdBytes[0] = 0x12;
			}
			else if(v.getId() == R.id.btn_off){
				cmdBytes[0] = 0x11;
			}
			else if(v.getId() == R.id.btn_speed_plus){
				cmdBytes[0] = 0x13;
			}
			else if(v.getId() == R.id.btn_speed_minus){
				cmdBytes[0] = 0x14;
			}
			else if(v.getId() == R.id.btn_program_plus){
				cmdBytes[0] = 0x15;
			}	
			else if(v.getId() == R.id.btn_program_minus){
				cmdBytes[0] = 0x16;
			}

			sendCmd();
		}
		
	}
	
	class ButtonOnLongClickListener implements OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			cmdBytes[1] = 0;
			
			//short command first
			if(v.getId() == R.id.btn_on){
				cmdBytes[0] = 0x12;
			}
			else if(v.getId() == R.id.btn_off){
				cmdBytes[0] = 0x11;
			}
			else if(v.getId() == R.id.btn_speed_plus){
				cmdBytes[0] = 0x13;
			}
			else if(v.getId() == R.id.btn_speed_minus){
				cmdBytes[0] = 0x14;
			}
			else if(v.getId() == R.id.btn_program_plus){
				cmdBytes[0] = 0x15;
			}	
			else if(v.getId() == R.id.btn_program_minus){
				cmdBytes[0] = 0x16;
			}

			sendCmd();
			
			//long command then
			if(v.getId() == R.id.btn_on){
				cmdBytes[0] = (byte) 0x92;
			}
			else if(v.getId() == R.id.btn_off){
				cmdBytes[0] = (byte) 0x91;
			}
			else if(v.getId() == R.id.btn_speed_plus){
				cmdBytes[0] = (byte) 0x93;
			}
			else if(v.getId() == R.id.btn_speed_minus){
				cmdBytes[0] = (byte) 0x94;
			}
			else if(v.getId() == R.id.btn_program_plus){
				cmdBytes[0] = (byte) 0x95;
			}	
			else if(v.getId() == R.id.btn_program_minus){
				cmdBytes[0] = (byte) 0x96;
			}
			
			isRunning = true;
			Thread rThread = new Thread(new repeatThread());
			rThread.start();
			
			
			return true;
		}		
	}
	
	class ButtonOnTouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction() == MotionEvent.ACTION_UP){
				isRunning = false;
			}
			return false;
		}
		
	}
	
	public void sendCmd(){
		if(device!=null){
			if(device.getStatus() == Constants.STATUS_ONLINE){
				
				LogUtil.printInfo("targetIp = "+device.getIpStr2()+",cmd="+Integer.toHexString(cmdBytes[0])+Integer.toHexString(cmdBytes[1]));
				UdpSender.getInstance().sendPackage(cmdBytes, device.getIpStr2());
			}
			else if(device.getStatus() == Constants.STATUS_REMOTE){
				
				CC3XCmd cmd = CC3XCmd.getInstance();
				String cmdStr = cmd.remoteControl(device.getMacStr(), StringUtils.toHexString(cmdBytes, ""));				
				TCPExchanger.getInstance().sendPackage(cmdStr.getBytes());
				LogUtil.printInfo(cmdStr);
			}
			else{
				//
				//showToast(getResources().getString(R.string.hint_error_data));
			}
		}
		else{
			//showToast(getResources().getString(R.string.hint_error_data));
		}
	}
	
	class repeatThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			while(isRunning){
				try{
					//
					sendCmd();
					Thread.sleep(250);
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
