package com.lierda.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.lierda.utils.LogUtil;


public class UdpReceiver {
	
	private DatagramSocket udpSocket = null;
	private DatagramPacket recvPacket = null;
	
	int DEFAULT_PORT = 48899;
	int MAX_DATA_PACKET_LENGTH = 255;
	byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	private static UdpReceiver instance = null;
	
	public static UdpReceiver getInstance() {
		if (null == instance) {
			instance = new UdpReceiver();
		}
		return instance;
	}
	
	public void init() throws Exception{
		udpSocket = new DatagramSocket(DEFAULT_PORT );	
		recvPacket = new DatagramPacket( buffer, MAX_DATA_PACKET_LENGTH );
		udpSocket.setBroadcast(true);
		//udpSocket.setSoTimeout(3000);
	}

	public byte[] receivePackage()throws Exception{
		
		
		udpSocket.receive(recvPacket);	
		byte[] data = recvPacket.getData();
		int length = recvPacket.getLength();
		
		
		String log = "";
		for(int i =0;i<length;i++)
			log += Integer.toHexString((data[i])&0xFF)+ " ";
		LogUtil.printInfo("recv data0="+log);
		
		//start,end
		//&& buffer[buffer.length-1] == CC3XConstants.CMD_END
		/*
		if(data[0] == CC3XConstants.CMD_START ){
			if(data[8]==(byte)0x80 || data[8]==(byte)0xC1 || data[8]==(byte)0xC2
					|| data[8] == (byte)0xC3 || data[8] == (byte)0xFF){
				
				LogUtil.printInfo("success!");
				byte[] result = new byte[length];
				System.arraycopy(data, 0, result, 0, length);
				Arrays.fill(buffer,(byte)0);
				return result;
				
			}
		}
		*/
				
		LogUtil.printInfo("failed!");
		return null;
	}
	
	public void  close(){
		if(udpSocket!=null)
			udpSocket.close();
		
		udpSocket = null;
		recvPacket = null;
	}
}
