package com.lierda.wificontroller.kapage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import com.lierda.model.DeviceInfo;
import com.lierda.udp.UdpExchanger;
import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;



import com.lierda.wificontroller.kapage.R;
import com.lierda.wificontroller.kapage.AsyncImageLoader.ImageCallback;
import com.lierda.wificontroller.kapage.DeviceManageActivity.ViewOnClickListener;
import com.lierda.wificontroller.kapage.DeviceManageActivity.actionThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ViewFlipper;

public class DeviceDetailActivity extends BaseActivity {
	
	private static final int REQUEST_CAMERA = 1;       // 拍照
	private static final int REQUEST_PHOTOGRAPH = 2;   // 相册
	//private static final int REQUEST_PHOTO_CUT = 3;    // 裁剪

	private Button btn_take_photo,btn_save;
	private ImageView img_device;
	private EditText et_name;
	
	private DeviceInfo deviceInfo = null;
	private ViewOnClickListener view_listener = null;
	private Bitmap myBitmap = null;
	
	private LayoutInflater layoutInflater;
	private ViewFlipper viewFilpper;
	private PopupWindow popup;
	private View popView;
	

    
    public static final String IMAGE_UNSPECIFIED = "image/*";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		ContentResolver resolver = getContentResolver(); 
		if(data!=null){
			if (requestCode == REQUEST_PHOTOGRAPH){
				//if(data!=null)
					//startPhotoZoom(data.getData());
				try  
	            {  
	            	if(myBitmap!=null && !myBitmap.isRecycled())
	            		myBitmap.recycle();
	            	System.gc();
	                // 获得图片的uri  
	                Uri originalUri = data.getData();  
	                // 将图片内容解析成字节数组  
	                //mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));  
	                // 将字节数组转换为ImageView可调用的Bitmap对象  
	                // myBitmap = getPicFromBytes(mContent, null);  
	                // //把得到的图片绑定在控件上显示 
	                //myBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
	                //ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	                //myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); 
	                
	                String[] proj = { MediaStore.Images.Media.DATA };
	                Cursor actualimagecursor = managedQuery(originalUri,proj,null,null,null);
	                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	                actualimagecursor.moveToFirst();
	                String img_path = actualimagecursor.getString(actual_image_column_index);
	                
	                BitmapFactory.Options opts = new BitmapFactory.Options();
	        		opts.inJustDecodeBounds = true;
	        		LogUtil.printInfo(img_path);
	        		BitmapFactory.decodeFile(img_path, opts);
	        		opts.inSampleSize = computeSampleSize(opts, -1, 128*128);
	        		opts.inJustDecodeBounds = false;
	        		myBitmap = BitmapFactory.decodeFile(img_path, opts);
	    			
	                                
	                img_device.setImageBitmap(myBitmap); 
	                
	            } catch ( Exception e )  
	            {  
	                //System.out.println(e.getMessage());  
	            }
			}
	        else if (requestCode == REQUEST_CAMERA){  
	        	
	        	// 设置文件保存路径这里放在跟目录下
	            //File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
	            //startPhotoZoom(Uri.fromFile(picture));
	            
	        		
		            try  
		            {  
		                Bundle extras = data.getExtras();  
		                if(myBitmap != null && !myBitmap.isRecycled())
		                	myBitmap.recycle();
		                
		                myBitmap = (Bitmap) extras.get("data");  
		                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		                img_device.setImageBitmap(myBitmap);
		                //deviceInfo.saveImg(myBitmap);
		            } 
		            catch ( Exception e )  
		            {  
		                e.printStackTrace();  
		            }  
		           
	        }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private int computeSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
	    } 
		else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;
		double h = options.outHeight;
		
		int lowerBound = (maxNumOfPixels == -1) ? 1 :
			(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		
		int upperBound = (minSideLength == -1) ? 128 :
	    	(int) Math.min(Math.floor(w / minSideLength),
	    			Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		
		if ((maxNumOfPixels == -1) &&(minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}		
	} 
	
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_detail);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
		initTitleBar(0,R.string.title_device_detail,R.string.button_save);
		
		initFootBar2();
		setInfoBtn();
		
		
		
		view_listener = new ViewOnClickListener();
		btn_save = (Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(view_listener);
		controllerBtn.setOnClickListener(view_listener);
		configBtn.setOnClickListener(view_listener);
		
		btn_take_photo = (Button)findViewById(R.id.btn_take_photo1);
		btn_take_photo.setOnClickListener(view_listener);
		
		img_device = (ImageView)findViewById(R.id.img_device);
		et_name = (EditText)findViewById(R.id.et_device_name);

		
		deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
		if(deviceInfo != null){
			et_name.setText(deviceInfo.getDeviceName());
			myBitmap = deviceInfo.getImg();
			if(myBitmap != null)
				img_device.setImageBitmap(myBitmap);
		}
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(myBitmap != null && !myBitmap.isRecycled())
        	myBitmap.recycle();
		super.onDestroy();
	}

	public String macToString(byte[] macBytes){
		String value = "";
		  for(int i = 0;i < macBytes.length; i++){
		   String sTemp = Integer.toHexString(0xFF &  macBytes[i]);
		   value = value+sTemp+":";
		  }
		     
		  value = value.substring(0,value.lastIndexOf(":"));
		  return value;
	}

	class ViewOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.btn_take_photo){
				
				 Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");  
                 startActivityForResult(getImageByCamera, REQUEST_CAMERA); 
                 popup.dismiss();
			}
			else if(v.getId() == R.id.btn_take_photo_from_local){
				Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);  
                getImage.addCategory(Intent.CATEGORY_OPENABLE);  
                getImage.setType("image/jpeg");  
                startActivityForResult(getImage, REQUEST_PHOTOGRAPH); 
                popup.dismiss();
			}
			else if(v.getId() == R.id.btn_cancel){
				popup.dismiss();
			}
			else if(v.getId() == R.id.btn_take_photo1){
				showFun();
			}
			else if(v.getId() == R.id.btn_save){
				
				deviceInfo.setDeviceName(et_name.getText().toString());
				
				showProgressDialog(getResources().getString(R.string.hint_wait_to_save),false);
				Thread saveThread = new Thread(new saveThread());
                saveThread.start();
			}
			else if(v.getId() == R.id.btn_controller){
				finish();
				// Supply a custom animation.  This one will just fade the new
	            // activity on top.  Note that we need to also supply an animation
	            // (here just doing nothing for the same amount of time) for the
	            // old activity to prevent it from going away too soon.
	            overridePendingTransition(R.anim.fade, R.anim.hold);
			}
			else if(v.getId() == R.id.btn_configure){
				
				if(deviceInfo.getStatus() == Constants.STATUS_ONLINE && deviceInfo.getIpStr2().indexOf("10.10.100.") == 0){
					setResult(R.id.btn_configure);
					finish();
				}
				else{
					inforBtn.setChecked(true);
					showToast(getResources().getString(R.string.hint_error_not_allow_configure));
				}
			}
		}
		
	}
	
	public static Bitmap getPicFromBytes ( byte[] bytes , BitmapFactory.Options opts )  
    {  
		BitmapFactory.Options options=new BitmapFactory.Options();
	    options.inJustDecodeBounds = false;
	    options.inSampleSize = 2;
	    
        if (bytes != null)  
            if (opts != null)  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
            else  
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);  
        return null;  
    }  
    public byte[] readStream ( InputStream inStream ) throws Exception  
    {  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        while ((len = inStream.read(buffer)) != -1)  
        {  
            outStream.write(buffer, 0, len);  
        }  
        byte[] data = outStream.toByteArray();  
        outStream.close();  
        inStream.close(); 
        
        return data;  
    }
    
	public void showFun(){
	    	if(popup!=null && popup.isShowing())
	    		popup.dismiss();
	    	
			layoutInflater = getLayoutInflater();	
			popView = layoutInflater.inflate(R.layout.popwindow_detail, null);
			popup= new PopupWindow(popView,LayoutParams.FILL_PARENT,
		                LayoutParams.FILL_PARENT, true);
			
			
			viewFilpper = (ViewFlipper) popView.findViewById(R.id.viewFlipper1);
			viewFilpper.setInAnimation(AnimationUtils.loadAnimation(DeviceDetailActivity.this,R.anim.animation_in_from_bottom));
			viewFilpper.setFlipInterval(600000);
			
			popup.setFocusable(true);
			popup.setWidth(LayoutParams.FILL_PARENT);
			popup.setHeight(LayoutParams.WRAP_CONTENT);
			popup.setBackgroundDrawable(new BitmapDrawable());
			popup.showAtLocation(findViewById(R.id.linearLayout1), Gravity.BOTTOM, 0, 0);
			viewFilpper.startFlipping();	
			
			popView.findViewById(R.id.btn_take_photo).setOnClickListener(view_listener);
			popView.findViewById(R.id.btn_take_photo_from_local).setOnClickListener(view_listener);
			popView.findViewById(R.id.btn_cancel).setOnClickListener(view_listener);
			
	   }
	
	class saveThread implements Runnable{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub	
			LogUtil.printInfo("save");
			deviceInfo.saveImg(myBitmap);
			Message msg = new Message();
            msg.what = 0;            
            myMessageHandler.sendMessage(msg);
		}	
	}
	
	Handler myMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			DeviceDetailActivity.this.closeProgressDialog();
			
			//save
			Intent it = getIntent();
			it.putExtra("device", deviceInfo);
			setResult(RESULT_OK,it);
			finish();
			
		}
	};
	
	/*
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 180);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_PHOTO_CUT);
    }
    */
}
