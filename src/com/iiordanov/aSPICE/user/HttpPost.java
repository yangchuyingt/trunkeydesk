package com.iiordanov.aSPICE.user;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.trilead.ssh2.log.Logger;

import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HttpPost {
	String posturl;
	String parameter;
	Handler handler;
	int what;
	public HttpPost(String posturl,String parameter,Handler handler,int what){
		this.posturl=posturl;
		this.parameter=parameter;
		this.handler=handler;
		this.what=what;
	}
	public void post(){
		URL url;
		try {
			url = new URL(posturl);
			HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
			DataOutputStream ot;
			openConnection.setRequestMethod("POST");
			try{
				ot = new DataOutputStream(openConnection.getOutputStream());
			}catch(Exception e){
				handler.sendEmptyMessage(10);
				return;
			}
		
			
			
			ot.write(parameter.getBytes());
			ot.flush();
			openConnection.connect();
			int responseCode=openConnection.getResponseCode();
			System.out.println("post url:"+url+",parameter:"+parameter);
			System.out.println("return code:"+openConnection.getResponseCode());
			if(responseCode==200){
				String res=getStringFromNet(openConnection.getInputStream());
				Message msg=handler.obtainMessage();
				msg.what=what;
				
				Bundle bundle=new Bundle();
				bundle.putCharSequence("res", res);
				msg.setData(bundle);
				handler.sendMessage(msg);
				
			}
			
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException:"+e.getMessage());
			e.printStackTrace();
			return;
		}catch (IOException e) {
			System.out.println("MalformedURLException:"+e.getMessage());
			e.printStackTrace();
			return;
			// TODO: handle exception
		}
		
		
	}
	 private String getStringFromNet(InputStream in) throws IOException {
	        StringBuilder strBd = new StringBuilder();
	        int position = 0;
	        byte[] buffer = new byte[1024];
	        while ((position = in.read(buffer)) != -1) {
	            strBd.append(new String(buffer, 0, position));
	        }
	        in.close();
	        return strBd.toString();
	    }


}
