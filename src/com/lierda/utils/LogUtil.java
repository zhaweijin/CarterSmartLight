package com.lierda.utils;

import java.io.File;
import java.io.FileOutputStream;


import android.os.Environment;
import android.util.Log;


/**
 * ��־���߰�
 * @author huangyu
 *
 */
public class LogUtil {

	/**
	 * ��ӡ��ͨ��Ϣ
	 * @param tag
	 * @param msg
	 */
	public static void printInfo(String msg){
		if(Constants.DEBUG){
			//�ж��Ƿ����sd��
			if(Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED))
				//������־��sd����
				//recordLog(Constants.SDPATH, Constants.LOGFILENAME, msg+"\n", true);
			Log.i("test", msg);
			//System.out.println("message==="+msg);

		}
	}
	
	/**
	 * ��ӡ������Ϣ
	 * @param tag
	 * @param msg
	 */
	public static void printError(String msg){
		if(Constants.DEBUG){
			Log.e("", msg);
			System.out.println("error=="+msg);
		}
	}
	
	/**
	 * ���ܣ���¼��־<br>
	 * @param savePathStr ������־·��
	 * @param saveFileNameS ������־�ļ���
	 * @param saveDataStr ������־����
	 * @param saveTypeStr �������ͣ�falsΪ���Ǳ��棬trueΪ��ԭ���ļ�����ӱ���
	 */
	public static void recordLog(String savePathStr,String saveFileNameS,String saveDataStr,boolean saveTypeStr) {

		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;
			String saveData = saveDataStr;
			boolean saveType =saveTypeStr;

			// ׼����Ҫ������ļ�
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath +File.separator+ saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// ���������ļ�
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				//saveFile.createNewFile();
				if(saveFile.length()>1024*1024){//���ļ�����1Mʱ��ɾ���ļ������´�����־�ļ�
					saveFile.delete();
					saveFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}



		} catch (Exception e) {
			saveDataStr = e.getMessage();
			recordLog(savePathStr, saveFileNameS, saveDataStr, saveTypeStr);
			
			e.printStackTrace();
		}


	}
}
