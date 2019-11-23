package com.lierda.utils;

/** 
 *
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class StringUtils 
{
	/**
	 * @param input
	 * @return boolean
	 */
	public static boolean isBlank( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}
	
	public static String toHexString(byte[] bytes, String separator) {
		String s1 = "", s2 = "";
		for (int i = 0; i < bytes.length; i++) {
			s1 = Integer.toHexString(0xFF & bytes[i]);
			if (s1.length() == 1) {
				s2 += "0";
			}
			s2 += s1;
			s2 += separator;
		}
		return s2;
	}
	
	public static String toString(byte[] bytes, String separator) {
		String s1 = "", s2 = "";
		for (int i = 0; i < bytes.length; i++) {
			s1 =  String.valueOf((char)bytes[i]);
			s2 += s1;
			s2 += separator;
		}
		return s2;
	}
}