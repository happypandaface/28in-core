package com.mygdx.sheep;

import com.badlogic.gdx.Gdx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;

public class NetUtil
{
	public static int CREATE_ACCOUNT = 1;
	public static int LOGIN = 2;
	public static int SAVE_LEVEL = 3;
	public static int PLAY_RAND = 4;
	public static int GET_USR_LVL = 5;
	public static int CHANGE_PASS = 6;
	public static int GET_LEVEL = 7;
	public static int GET_POP = 8;
	public static int GET_NEW = 9;
	
	public static String bytesToHex(byte[] b) {
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		StringBuffer buf = new StringBuffer();
		for (int j=0; j<b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}
	public static String encode(String s) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(new String(s+"km").getBytes());// add salt
		String hash = bytesToHex(md.digest());
		Gdx.app.log("hash", hash);
		return hash;
	}
	public static void sendRequest(int type, String data, HttpResponseListener listener)
	{
		HttpRequest httpGet = new HttpRequest(HttpMethods.POST);
		httpGet.setUrl("http://scottgriffy.com/sheep/sheep.php");
		httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String contentString = new String();
		if (type == CREATE_ACCOUNT)
			contentString += "type=createAccount&";
		else if (type == LOGIN)
			contentString += "type=login&";
		else if (type == SAVE_LEVEL)
			contentString += "type=saveLevel&";
		else if (type == PLAY_RAND)
			contentString += "type=playRandom&";
		else if (type == GET_USR_LVL)
			contentString += "type=getUsersLevels&";
		else if (type == CHANGE_PASS)
			contentString += "type=changePassword&";
		else if (type == GET_LEVEL)
			contentString += "type=getLevel&";
		else if (type == GET_POP)
			contentString += "type=getPop&";
		else if (type == GET_NEW)
			contentString += "type=getNew&";
		contentString += data;
		httpGet.setContent(contentString);
		//httpGet.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		//...
		Gdx.net.sendHttpRequest (httpGet, listener);
	}
}
