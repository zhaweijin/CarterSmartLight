package com.lierda.wificontroller.kapage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lierda.wificontroller.kapage.R;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WifiControllerActivity extends Activity {
    /** Called when the activity is first created. */
	  private TextView allNetWork;  
	    private Button scan;  
	    private Button start;  
	    private Button stop;  
	    private Button check;  
	    private WifiAdmin mWifiAdmin;  
	    private WifiManager mWifiManager;
	    // ɨ�����б�  
	    private List<ScanResult> list;  
	    private ScanResult mScanResult;  
	    private StringBuffer sb=new StringBuffer();  
	    
	    private ListView  listView;
	    private SimpleAdapter adapter = null;
	    private ArrayList<Map<String,Object>> listHashMap = new ArrayList<Map<String,Object>>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mWifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdmin(WifiControllerActivity.this);  
        init();  
    }
    public void init(){
        allNetWork = (TextView) findViewById(R.id.allNetWork);  
        scan = (Button) findViewById(R.id.scan);  
        start = (Button) findViewById(R.id.start);  
        stop = (Button) findViewById(R.id.stop);  
        check = (Button) findViewById(R.id.check); 
        scan.setOnClickListener(new MyListener());  
        start.setOnClickListener(new MyListener());  
        stop.setOnClickListener(new MyListener());  
        check.setOnClickListener(new MyListener());  
        listView = (ListView)findViewById(R.id.listView1);
        adapter = new SimpleAdapter(this,listHashMap,  
        		R.layout.list_item_wifi,  
        		      new String[] { "wifiText"},   
        		new int[] { R.id.tv_wifi}); 
        		       //������ɰ�һ���ź����ݵĲ����ļ����뱾������  
        listView.setAdapter(adapter);   
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//addNetwork("TP-LINK_8888B2", "", 1);
				addNetwork("Linda", "87654321", 3);
			}

        	
        });
    }
    private class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.scan://ɨ������
				//getAllNetWorkList();  
				updateWifiList();
				break;
           case R.id.start://��Wifi
        		mWifiAdmin.openWifi();
				Toast.makeText(WifiControllerActivity.this, "��ǰwifi״̬Ϊ��"+mWifiAdmin.checkState(), 1).show();
				break;
           case R.id.stop://�ر�Wifi
				mWifiAdmin.closeWifi();
				Toast.makeText(WifiControllerActivity.this, "��ǰwifi״̬Ϊ��"+mWifiAdmin.checkState(), 1).show();
				break;
           case R.id.check://Wifi״̬
        	   Toast.makeText(WifiControllerActivity.this, "��ǰwifi״̬Ϊ��"+mWifiAdmin.checkState(), 1).show();
				break;
			default:
				break;
			}
		}
    	
    }
    public void getAllNetWorkList(){
    	  // ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����  
    	if(sb!=null){
    		sb=new StringBuffer();
    	}
    	//��ʼɨ������
    	mWifiAdmin.startScan();
    	list=mWifiAdmin.getWifiList();
    	if(list!=null){
    		for(int i=0;i<list.size();i++){
    			//�õ�ɨ����
    			mScanResult=list.get(i);
    			sb=sb.append(mScanResult.BSSID+"  ").append(mScanResult.SSID+"   ")
    			.append(mScanResult.capabilities+"   ").append(mScanResult.frequency+"   ")
    			.append(mScanResult.level+"\n\n");
    		}
    		allNetWork.setText("ɨ�赽��wifi���磺\n"+sb.toString());
    	}
    }
    
    public void updateWifiList(){
    	
    	listHashMap.clear();
    	//��ʼɨ������
    	mWifiAdmin.startScan();
    	list=mWifiAdmin.getWifiList();
    	if(list!=null){
    		for(int i=0;i<list.size();i++){
    			//�õ�ɨ����
    			mScanResult=list.get(i);
    			
    			HashMap<String, Object> map = new HashMap<String,Object>();
    			map.put("wifiText", mScanResult.SSID);
    			listHashMap.add(map);
    		}
    	}
    	
    	
    	adapter.notifyDataSetChanged();
    	listView.setAdapter(adapter);
    }
    
    public void addNetwork(String SSID, String Password, int Type)
    {  
        WifiConfiguration config = new WifiConfiguration();    
         config.allowedAuthAlgorithms.clear();  
         config.allowedGroupCiphers.clear();  
         config.allowedKeyManagement.clear();  
         config.allowedPairwiseCiphers.clear();  
         config.allowedProtocols.clear();  
        config.SSID = "\"" + SSID + "\"";    
         
        WifiConfiguration tempConfig = this.IsExsits(SSID);            
        if(tempConfig != null) {   
            mWifiManager.removeNetwork(tempConfig.networkId);   
        } 
         
        if(Type == 1) //WIFICIPHER_NOPASS 
        {  
             config.wepKeys[0] = "";  
             config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
             config.wepTxKeyIndex = 0;  
        }  
        if(Type == 2) //WIFICIPHER_WEP 
        {  
            config.hiddenSSID = true; 
            config.wepKeys[0]= "\""+Password+"\"";  
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
            config.wepTxKeyIndex = 0;  
        }  
        if(Type == 3) //WIFICIPHER_WPA 
        {  
        config.preSharedKey = "\""+Password+"\"";  
        config.hiddenSSID = true;    
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
        //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
        config.status = WifiConfiguration.Status.ENABLED;    
        } 
        mWifiAdmin.addNetWork(config);  
  }  
   
  private WifiConfiguration IsExsits(String SSID)   
  {   
      List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();   
         for (WifiConfiguration existingConfig : existingConfigs)    
         {   
           if (existingConfig.SSID.equals("\""+SSID+"\""))   
           {   
               return existingConfig;   
           }   
         }   
      return null;    
  } 
}