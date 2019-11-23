package com.lierda.wificontroller.kapage;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * �첽ͼƬ������
 * 
 * @author huangyu
 * 
 */
public class AsyncImageLoader {
	private static Map<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

//	 static ThreadPool threadPool_image = new ThreadPool(2);
	public static ExecutorService pool = Executors.newFixedThreadPool(2); 

	public static void clearImageMap() {
		// if(imageCache!=null && imageCache.size()>100){//���������100��ʱ��
		imageCache.clear();
		// }
	}

	public static Bitmap loadDrawable(final String imageUrl, final ImageCallback callback) {

		Bitmap drawable = null;
		//1���ӻ�����ȡbitmap
		drawable = imageCache.get(imageUrl);
		if(drawable!=null){
//			LogUtil.printInfo("get bitmap from cache");
			return drawable;
		}
		if (avaiableSdcard()) {// �����sd������ȡsd��
			drawable = getPicByPath(Constants.imageCachePath, imageUrl);
			if (drawable != null) {
				LogUtil.printInfo("get bitmap from disk");
				 imageCache.put(imageUrl, drawable);
				return drawable;
			}
		} else {// ���û��sd������ȡ�ֻ�����
			drawable = getPicByPath(Constants.imageCachePath_data, imageUrl);
			if (drawable != null) {
				LogUtil.printInfo("get bitmap from disk");
				 imageCache.put(imageUrl, drawable);
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl);
			}
		};
		Runnable task = new Runnable() {
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl);
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		};

//		new Thread(task).start();
			pool.execute(task);
		
		return null;
	}

	/**
	 * �ж��Ƿ����sd��
	 * 
	 * @return
	 */
	public static boolean avaiableSdcard() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ȡͼƬ
	 * 
	 * @param picName
	 * @return
	 */
	public static Drawable getPic_Draw_ByPath(String path, String picName) {
		picName = picName.substring(picName.lastIndexOf("/") + 1);
		String filePath = path + picName;
		return Drawable.createFromPath(filePath);
	}

	/**
	 * ��ȡͼƬ
	 * 
	 * @param picName
	 * @return
	 */
	public static Bitmap getPicByPath(String path, String picName) {
		if(!"".equals(picName)&&picName!=null){
			picName = picName.substring(picName.lastIndexOf("/") + 1);
			String filePath = path + picName;
			
			//�ж��ļ��Ƿ����
				File file = new File(filePath);
				if(!file.exists()){//�ļ�������
					return null;
				}
			
			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			return bitmap;
		}else{
			return null;
		}
		
	}

	public static Bitmap loadDrawable(String imageUrl) {
		if (imageCache.containsKey(imageUrl)) {
			Bitmap softReference = imageCache.get(imageUrl);
			if (softReference != null) {
				return softReference;
			} else {
				return null;
			}
		}
		return null;
	}

	protected static Bitmap loadImageFromUrl(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
			conn.disconnect();
			if(bitmap!=null){
				LogUtil.printInfo("get bitmap from net");
				 imageCache.put(imageUrl, bitmap);
				 savePic(bitmap, imageUrl);// ����ͼƬ
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	public static void savePic(Bitmap bitmap, String imageUrl) {
		if (bitmap != null && imageUrl != null && !"".equals(imageUrl)) {
			if (avaiableSdcard()) {// �����sd����������sd��
				savePicToSdcard(bitmap, imageUrl);
			} else {// ���û��sd�����������ֻ�����
				saveToDataDir(bitmap, imageUrl);
			}
		}
	}

	/**
	 * ��ͼƬ������sd��
	 * 
	 * @param bitmap
	 *            ͼƬ
	 * @param picName
	 *            ͼƬ���ƣ�ͬ����id����
	 */
	private static void savePicToSdcard(Bitmap bitmap, String picName) {

		picName = picName.substring(picName.lastIndexOf("/") + 1);
		String path = Constants.imageCachePath;
		File file = new File(path + picName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.close();
			} catch (Exception e) {
				// Log.e("", e.toString());
			}
		}
	}

	/**
	 * �����ļ���Ӧ��Ŀ¼
	 * 
	 * @param bitmap
	 * @param fileName
	 *            �ļ�����
	 */
	static void saveToDataDir(Bitmap bitmap, String fileName) {
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		String path = Constants.imageCachePath_data;
		File file = new File(path + fileName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.close();
			} catch (Exception e) {
				Log.e("", e.toString());
			}
		}
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
