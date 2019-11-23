package com.lierda.wificontroller.kapage;

import com.lierda.model.DeviceInfo;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;
import com.lierda.wificontroller.kapage.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RadioButton;

public class ChooseModeActivity extends BaseActivity {
	
	private boolean isStart1,isStart2,isStart3,isStart4,isStart5,isStart6;
	private RadioButton rd_mode1,rd_mode2,rd_mode3,rd_mode4,rd_mode5,rd_mode6;
	private ButtonOnTouchListener touchlistener_btn = null;
	private ButtonOnClickListener listener_btn = null;

	private DeviceInfo device = null;
	
	
	private void reset(boolean flag){
		isStart1 = flag;
		isStart2 = flag;
		isStart3 = flag;
		isStart4 = flag;
		isStart5 = flag;
		isStart6 = flag;
		rd_mode1.setChecked(false);
		rd_mode2.setChecked(false);
		rd_mode3.setChecked(false);
		rd_mode4.setChecked(false);
		rd_mode5.setChecked(false);
		rd_mode6.setChecked(false);
	}
	
	private boolean isAllFalse(){
		if(isStart1 == false && isStart2 == false &&
				isStart3 == false && isStart4 == false &&
				isStart5 == false && isStart6 == false )
			return true;
		else 
			return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == R.id.btn_information){
			Intent it = new Intent(ChooseModeActivity.this,DeviceDetailActivity.class);
			it.putExtra("device", device);
			startActivityForResult(it,0);
			// Supply a custom animation.  This one will just fade the new
            // activity on top.  Note that we need to also supply an animation
            // (here just doing nothing for the same amount of time) for the
            // old activity to prevent it from going away too soon.
            overridePendingTransition(R.anim.fade, R.anim.hold);
		}
		else if(resultCode == R.id.btn_configure){
			Intent it = new Intent(ChooseModeActivity.this,ConfigureOtherActivity.class);
			it.putExtra("ip", device.getIpStr2());
			startActivityForResult(it,0);
			// Supply a custom animation.  This one will just fade the new
            // activity on top.  Note that we need to also supply an animation
            // (here just doing nothing for the same amount of time) for the
            // old activity to prevent it from going away too soon.
            overridePendingTransition(R.anim.fade, R.anim.hold);
		}
		else if(resultCode == Constants.RESULT_QUIT){
			Intent it = getIntent();
			it.putExtra("device", device);
			setResult(RESULT_OK,it);
			finish();
		}
		else{
			if(data != null){
				DeviceInfo d = (DeviceInfo) data.getSerializableExtra("device");
				if(d != null){
					LogUtil.printInfo("device Name change="+d.getDeviceName());
					device = null;
					device = d;
				}
			}
		}
			
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_mode);
		
		device = (DeviceInfo) getIntent().getSerializableExtra("device");
		initTitleBar(0,R.string.title_choose_controller,0);
		initFootBar2();
		
		
		listener_btn = new ButtonOnClickListener();
		touchlistener_btn = new ButtonOnTouchListener();
		rd_mode1 = (RadioButton)findViewById(R.id.rd_mode1);
		rd_mode2 = (RadioButton)findViewById(R.id.rd_mode2);
		rd_mode3 = (RadioButton)findViewById(R.id.rd_mode3);
		rd_mode4 = (RadioButton)findViewById(R.id.rd_mode4);
		rd_mode5 = (RadioButton)findViewById(R.id.rd_mode5);
		rd_mode6 = (RadioButton)findViewById(R.id.rd_mode6);
		
		rd_mode1.setOnTouchListener(touchlistener_btn);
		rd_mode2.setOnTouchListener(touchlistener_btn);
		rd_mode3.setOnTouchListener(touchlistener_btn);
		rd_mode4.setOnTouchListener(touchlistener_btn);
		rd_mode5.setOnTouchListener(touchlistener_btn);
		rd_mode6.setOnTouchListener(touchlistener_btn);
		
		rd_mode1.setOnClickListener(listener_btn);
		rd_mode2.setOnClickListener(listener_btn);
		rd_mode3.setOnClickListener(listener_btn);
		rd_mode4.setOnClickListener(listener_btn);
		rd_mode5.setOnClickListener(listener_btn);
		rd_mode6.setOnClickListener(listener_btn);
		inforBtn.setOnClickListener(listener_btn);
		configBtn.setOnClickListener(listener_btn);
		
		mSettings.edit().putString(Constants.MODE3_1, device.getModeParam(3, 1)).commit();
		mSettings.edit().putString(Constants.MODE3_2, device.getModeParam(3, 2)).commit();
		mSettings.edit().putString(Constants.MODE3_3, device.getModeParam(3, 3)).commit();
		mSettings.edit().putString(Constants.MODE3_4, device.getModeParam(3, 4)).commit();
		
		mSettings.edit().putString(Constants.MODE4_1, device.getModeParam(4, 1)).commit();
		mSettings.edit().putString(Constants.MODE4_2, device.getModeParam(4, 2)).commit();
		mSettings.edit().putString(Constants.MODE4_3, device.getModeParam(4, 3)).commit();
		mSettings.edit().putString(Constants.MODE4_4, device.getModeParam(4, 4)).commit();
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//save
			Intent it = getIntent();
			it.putExtra("device", device);
			setResult(RESULT_OK,it);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setControllerBtn();
		//update mode param
		reset(false);
		device.setModeParam(3, 1, mSettings.getString(Constants.MODE3_1, "1"));
		device.setModeParam(3, 2, mSettings.getString(Constants.MODE3_2, "2"));
		device.setModeParam(3, 3, mSettings.getString(Constants.MODE3_3, "3"));
		device.setModeParam(3, 4, mSettings.getString(Constants.MODE3_4, "4"));

		device.setModeParam(4, 1, mSettings.getString(Constants.MODE4_1, "1"));
		device.setModeParam(4, 2, mSettings.getString(Constants.MODE4_2, "2"));
		device.setModeParam(4, 3, mSettings.getString(Constants.MODE4_3, "3"));
		device.setModeParam(4, 4, mSettings.getString(Constants.MODE4_4, "4"));
	
		super.onResume();
	}



	class ButtonOnTouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				rd_mode1.setChecked(false);
				rd_mode2.setChecked(false);
				rd_mode3.setChecked(false);
				rd_mode4.setChecked(false);
				rd_mode5.setChecked(false);
				rd_mode6.setChecked(false);
			}
			return false;
		}
		
	}
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v.getId() == R.id.rd_mode1){
				if(isAllFalse()){
					Intent it = new Intent(ChooseModeActivity.this,ControllerMode1Activity.class);
					it.putExtra("device", device);
					startActivity(it);
				}
				isStart1 = true;
			}
			else if(v.getId() == R.id.rd_mode2){
				if(isAllFalse()){
					Intent it = new Intent(ChooseModeActivity.this,ControllerMode2Activity.class);
					it.putExtra("device", device);
					startActivity(it);
				}
				isStart2 = true;
			}
			else if(v.getId() == R.id.rd_mode3){
				if(isAllFalse()){
					Intent it = new Intent(ChooseModeActivity.this,ControllerMode3Activity.class);
					it.putExtra("device", device);
					startActivity(it);
				}
				isStart3 = true;
			}
			else if(v.getId() == R.id.rd_mode4){
				if(isAllFalse()){
					Intent it = new Intent(ChooseModeActivity.this,ControllerMode4Activity.class);
					it.putExtra("device", device);
					startActivity(it);
				}
				isStart4 = true;
			}
			else if(v.getId() == R.id.btn_information){
				Intent it = new Intent(ChooseModeActivity.this,DeviceDetailActivity.class);
				it.putExtra("device", device);	            
				startActivityForResult(it,0);
				// Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
			}
			else if(v.getId() == R.id.btn_configure){
				
				if(device.getStatus() == Constants.STATUS_ONLINE && device.getIpStr2().indexOf("10.10.100.") == 0){
					Intent it = new Intent(ChooseModeActivity.this,ConfigureOtherActivity.class);
					it.putExtra("ip", device.getIpStr2());
					startActivityForResult(it,0);
					// Supply a custom animation.  This one will just fade the new
		            // activity on top.  Note that we need to also supply an animation
		            // (here just doing nothing for the same amount of time) for the
		            // old activity to prevent it from going away too soon.
		            overridePendingTransition(R.anim.fade, R.anim.hold);
				}
				else{
					configBtn.setChecked(true);
					showToast(getResources().getString(R.string.hint_error_not_allow_configure));
				}
			}
			
		}
		
	}

}
