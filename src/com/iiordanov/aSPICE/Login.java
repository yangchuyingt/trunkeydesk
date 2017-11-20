package com.iiordanov.aSPICE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.iiordanov.aSPICE.user.APIQueryVmInstanceReply;
import com.iiordanov.aSPICE.user.ApiInventory;
import com.iiordanov.aSPICE.user.ApiUserPortMsg;
import com.iiordanov.aSPICE.user.HttpPost;
import com.iiordanov.aSPICE.user.UserMessageDatabase;
import com.iiordanov.domain.UserMsg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends MainConfiguration implements OnClickListener, OnItemClickListener {
     private EditText edt_Password;
	private EditText edt_UserName;
	private ArrayList<UserMsg> usernames;
	private ListView lv_use_name;
	private ImageView iv_select;
	private PopupWindow pop;
	private Boolean select=false;
	private ConnectionBean connection;
	private Mydialog dialog;
	private Button bt_login;
	private UserMessageDatabase userMessageDatabase;
	private static final String SERVER_IP="manageServerip";
    private static final String SESSION="387073a8c548480a94517427d04d1d82";
    private static final int WHAT_REMOTE_SERVER=9;
    private static final int WHAT_USER_MSG=8;
    private static final int WHAT_ERROR=10;
    private String hostUuid;
    private String kdpPort;
 	private String serverIp;
 	private String password;
 	private String username;
 	private String hostIp;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	layoutID=R.layout.login;
    	super.onCreate(savedInstanceState);
    	//setContentView(R.layout.login);
    	bt_login = (Button)this.findViewById(R.id.bt_login);
    	edt_UserName = (EditText)this.findViewById(R.id.edt_user_name);
    	edt_Password = (EditText)this.findViewById(R.id.edt_password);
    	bt_login.setOnClickListener(this);
    	iv_select = (ImageView)this.findViewById(R.id.iv_select);
    	TextView tv_manage_server=(TextView)this.findViewById(R.id.tv_manage_server);
    	
    	tv_manage_server.setOnClickListener(this);
    	iv_select.setOnClickListener(this);
    	init();
    	
    }
	@Override
	protected void onStart() {
		super.onStart();
		 updateUserNames();
			
			edt_UserName.setText(username);
			edt_Password.setText(password);
	};
   Handler handler=new Handler(){
	

	

	public void handleMessage(android.os.Message msg) {
		   switch (msg.what) {
		case WHAT_REMOTE_SERVER:
			saveserverIP((String)msg.getData().getCharSequence("res"));
			break;
        case WHAT_USER_MSG:
        	getKdpport(msg.getData().getCharSequence("res"));
        	break;
        case WHAT_ERROR:
        	Toast.makeText(Login.this, "无法连接管理地址 请检查是否有误",Toast.LENGTH_SHORT).show();
        	break;
		default:
			break;
		}
		System.out.println("msg:"+msg.getData().getCharSequence("res"));
	   }

	private void saveserverIP(String resp) {
          Gson gson=new Gson();
          Map<String,String> result1=gson.fromJson(resp,Map.class);
          System.out.println("result"+result1.get("result"));
          Map<String,LinkedTreeMap<String, ArrayList<LinkedTreeMap<String, String>>>> result2=gson.fromJson(result1.get("result"), Map.class);
          hostIp = result2.get("org.zstack.header.host.APIQueryHostReply").get("inventories").get(0).get("name");
          System.out.println("hostIP:"+hostIp);
          savemsgIntodataBase();
          startAspiceConnection();
	}

	

	private void startAspiceConnection() {
		   // Toast.makeText(Login.this, "登陆成功",Toast.LENGTH_SHORT).show();
		    Intent intent = new Intent(Login.this, RemoteCanvasActivity.class);
		    System.out.println("username:"+username+",password:"+password+",kdpPort:"+kdpPort+",hostIp"+hostIp);
		    connection=new ConnectionBean(username, password, kdpPort, hostIp, Login.this);
	        intent.putExtra(Constants.CONNECTION, connection.Gen_getValues());
	        System.out.println("ycy content value:"+selected.Gen_getValues().toString());
	        isConnecting=true;
	        startActivity(intent);
		
	}

	private void getKdpport(CharSequence res) {
		Gson gson=new Gson();
		ApiUserPortMsg portMsg=gson.fromJson((String) res, ApiUserPortMsg.class);
		try{
			Map<String ,LinkedTreeMap<String, ArrayList<LinkedTreeMap<String, String>>>> map=gson.fromJson(portMsg.getResult(), HashMap.class);
			kdpPort = map.get("org.zstack.header.vm.APIQueryVmInstanceReply").get("inventories").get(0).get("kdpPort");
			hostUuid=map.get("org.zstack.header.vm.APIQueryVmInstanceReply").get("inventories").get(0).get("hostUuid");
			System.out.println("kdpport:"+kdpPort);
		}catch(Exception e){
			Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
			return;
		}
		Map<String ,LinkedTreeMap<String, ArrayList<LinkedTreeMap<String, String>>>> map=gson.fromJson(portMsg.getResult(), HashMap.class);
		kdpPort = map.get("org.zstack.header.vm.APIQueryVmInstanceReply").get("inventories").get(0).get("kdpPort");
		hostUuid=map.get("org.zstack.header.vm.APIQueryVmInstanceReply").get("inventories").get(0).get("hostUuid");
		System.out.println("kdpport:"+kdpPort);
		getHostserver(serverIp);
		
	};
   };

private boolean isConnecting;
private SQLiteDatabase db;
private Myadapter myadapter;
private ListView listview;


	private void init() {
		
		userMessageDatabase=new UserMessageDatabase(Login.this);
        db=userMessageDatabase.getWritableDatabase();
//		if(preferecnce==null){
//			preferecnce = getSharedPreferences("aspice", Activity.MODE_PRIVATE);
//		}
//		HashSet<String> login_msg_hashset= (HashSet<String>)preferecnce.getStringSet("login_msg", null);
//		if(login_msg_hashset==null||login_msg_hashset.isEmpty()){
//			return ;
//		}
//		Iterator<String> itera=login_msg_hashset.iterator();
//		String username=null;
//		usernames = new UserMsg[login_msg_hashset.size()];
//		//usernames[0]="";
//		int i=0;
//		while(itera.hasNext()){
//			username=usernames[i]=itera.next();
//			System.out.println(usernames[i]);
//			i++;
//			
//		}
		
		
		
		
	}


	private void updateUserNames() {
		Cursor rawQuery = db.rawQuery("select * from usermsg", null);
		usernames = new ArrayList<UserMsg>();
		while(rawQuery.moveToNext()){
			UserMsg umsg=new UserMsg();
			umsg.setUsername(rawQuery.getString(rawQuery.getColumnIndex("username")));
			umsg.setPassword(rawQuery.getString(rawQuery.getColumnIndex("password")));
			umsg.setHostip(rawQuery.getString(rawQuery.getColumnIndex("hostip")));
			umsg.setManagerip(rawQuery.getString(rawQuery.getColumnIndex("managerip")));
			umsg.setPort(rawQuery.getString(rawQuery.getColumnIndex("port")));
			if(rawQuery.getString(rawQuery.getColumnIndex("updatetime")).equals("1")){
				username=rawQuery.getString(rawQuery.getColumnIndex("username"));
				password=rawQuery.getString(rawQuery.getColumnIndex("password"));
				kdpPort=rawQuery.getString(rawQuery.getColumnIndex("port"));
				serverIp=rawQuery.getString(rawQuery.getColumnIndex("managerip"));
				hostIp=rawQuery.getString(rawQuery.getColumnIndex("hostip"));
			}
			usernames.add(umsg);
		}
	}

	
	private void setadapter() {
		Myadapter adapter=new Myadapter();
		lv_use_name.setAdapter(adapter);
		
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login:
			login();
			break;
       case R.id.iv_select:
    	   if(usernames==null||usernames.size()==0)return;
    	   if(!select){
    		   showPopwindow();
    		   iv_select.setImageResource(R.drawable.select_up);
    		   select=true;
    	   }else{
    		   select=false;
    		   pop.dismiss();
    		   iv_select.setImageResource(R.drawable.select_down);
    	   }
    	   break;
    	case R.id.tv_manage_server:
    		showdialog();
    		break;
		default:
			break;
		}
		
	}

	private void showdialog() {
		if(dialog==null){
			dialog = new Mydialog(Login.this);
			//Window window = dialog.getWindow();
			//window .setGravity(Gravity.TOP);
			//android.view.WindowManager.LayoutParams attributes = window.getAttributes();
			//int [] location=new int[2];
			//bt_login.getLocationOnScreen(location);
			System.out.println("yyy:"+bt_login.getHeight());
			//attributes.y=location[1]+(int)(bt_login.getHeight()*0.1);
			//window.setAttributes(attributes);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
		}
		
		dialog.show();
		
	}



	private void showPopwindow() {
		listview = new ListView(Login.this);
		listview.setDividerHeight(0);
		listview.setDivider(null);
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    listview.setLayoutParams(layoutParams);
	    listview.setDividerHeight(1);
	    if(usernames!=null||usernames.size()>0){
	    	myadapter=new Myadapter();
	    	listview.setAdapter(myadapter);
		}
		
	    
	    pop = new PopupWindow(listview,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    pop.setTouchable(true);
	    //pop.showAsDropDown(listview);
	    pop.showAtLocation(edt_UserName, Gravity.NO_GRAVITY, 0, getlocationY());
	    listview.setOnItemClickListener( this);
		
	}

  private int getlocationY(){
	  int[] location=new int[2];
	  edt_Password.getLocationInWindow(location);
	  System.out.println("位置："+location[1]);
	  return location[1];
  }

	private void login() {
      String username=edt_UserName.getEditableText().toString().trim();
     
      String password=edt_Password.getEditableText().toString().trim();
      if(username.isEmpty()||password.isEmpty()){
    	  Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
    	  return;
      }
    
      this.password=password;
      this.username=username;
      if(serverIp==null||serverIp.isEmpty()){
    	  
    	  showdialog();
    	  return;
      }
     
    	  pushToserver(serverIp,username,password);
     
	}

	private void savemsgIntodataBase() {
		Cursor rawQuery = db.rawQuery("select * from usermsg where username=?", new String[]{username});
		if(rawQuery.moveToFirst()){
			db.execSQL("update usermsg set updatetime=0");
			db.execSQL(String.format("update usermsg set `password`='%s', `hostip`='%s',`managerip`='%s',`port`='%s',`updatetime`='1' where `username`='%s'",password,hostIp,serverIp,kdpPort,username));
			//db.execSQL("update usermsg set password=`?` , hostip=`?`,managerip=`?`,port=`?`,updatetime=`1` where username＝`?`",new String[]{password,hostIp,serverIp,kdpPort,username});
		    
		}else {
			db.execSQL("update usermsg set updatetime=0");
			db.execSQL("insert into usermsg values(?,?,?,?,?,1)",new String[]{username,password,hostIp,serverIp,kdpPort});
		}
		
	}


	private void pushToserver(String host,String username,String password) {
		final String url="http://" + host + ":8080/zstack/api/";
		System.out.println("url:"+url);
		final String parameter="{\"org.zstack.header.vm.APIQueryVmInstanceMsg\":{\"session\": {\"uuid\":\""+
                         SESSION+"\"},\"conditions\": [{\"name\": \"name\", \"value\":\""+username+"\", \"op\": \"=\" },{\"name\": \"kdpPassword\", \"value\":\""+
				         password+"\", \"op\": \"=\" }]}}";
	    System.out.println("body:"+parameter);
		new Thread(new Runnable() {
			
			
			@Override
			public void run() {
				new HttpPost(url, parameter, handler, WHAT_USER_MSG).post();				
			}
		}).start();
		
		
		                 
		
	}
  
   private void getHostserver(String host){
	   final String url="http://" + host + ":8080/zstack/api/";
	   final String parameter="{\"org.zstack.header.host.APIQueryHostMsg\":{\"session\": {\"uuid\":\""+SESSION+"\"},\"conditions\": [{\"name\": \"uuid\", \"value\":\""+
			                   hostUuid+"\", \"op\": \"=\" }]}}";
	   System.out.println("hostServer ip paramter:"+parameter);
	   new Thread(new Runnable() {
		
		@Override
		public void run() {
			new HttpPost(url, parameter, handler, WHAT_REMOTE_SERVER).post();
			System.out.println("gethostServer:"+parameter);
			
		}
	}).start();
	   
   }

//	private void saveUserAndPassword(String username, String password) {
//		preferecnce = getSharedPreferences("aspice", Activity.MODE_PRIVATE);
//		Editor editor = preferecnce.edit();
//		HashSet<String> login_msg_set=(HashSet<String>)preferecnce.getStringSet("login_msg", null);
//		if(login_msg_set==null||login_msg_set.isEmpty()){
//			login_msg_set=new HashSet<String>();
//		}
//	   login_msg_set.add(username);
//       editor.putStringSet("login_msg", login_msg_set);	
//       editor.putString(username, password);
//       editor.commit();
//	}
	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
			
			return usernames.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return usernames.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view =View.inflate(getApplicationContext(), R.layout.pop_adapter, null);
			TextView text1=(TextView)view.findViewById(R.id.tv_list_item);
			ImageView imageview=(ImageView)view.findViewById(R.id.img_delete);
			imageview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteaccount(usernames.get(position));
					
				}

				
			});
			//System.out.println("usernames["+position+"]"+usernames[position]);
			text1.setText(usernames.get(position).getUsername());
			
			return view;
		}

		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String user=usernames.get(position).getUsername();
		String password=usernames.get(position).getPassword();
		edt_UserName.setText(user);
		edt_Password.setText(password);
		iv_select.setImageResource(R.drawable.select_down);
		select=false;
		pop.dismiss();
		
	}
	protected void deleteaccount(UserMsg userMsg) {
		db.execSQL(String.format("delete from usermsg where `username`='%s'",userMsg.getUsername()));
		updateUserNames();
		myadapter.notifyDataSetChanged();
		if(usernames==null||usernames.size()==0){
			pop.dismiss();
 		    iv_select.setImageResource(R.drawable.select_down);
			edt_UserName.setText("");
			edt_Password.setText("");
			this.username="";
			this.password="";
			this.kdpPort="";
			this.serverIp="";
			this.hostIp="";
			
		}
		
		
	}
	
	private class Mydialog extends Dialog implements android.view.View.OnClickListener{

		private EditText edt_server_ip;


		public Mydialog(Context context) {
			super(context, R.style.NoDialogTitle);
			// TODO Auto-generated constructor stub
		}

		
		public Mydialog(Context context, int themeResId) {
			super(context, R.style.NoDialogTitle);
			// TODO Auto-generated constructor stub
		}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_manage_server);
			Button bt_concel=(Button)this.findViewById(R.id.bt_dialog_concel);
			Button bt_dialog_ensure=(Button)this.findViewById(R.id.bt_dialog_ensure);
			edt_server_ip = (EditText)this.findViewById(R.id.edt_server_ip);
			//preferecnce = getSharedPreferences("aspice", Activity.MODE_PRIVATE);
			edt_server_ip.setText(serverIp);
			bt_concel.setOnClickListener(this);
			bt_dialog_ensure.setOnClickListener(this);
			
		}


		@Override
		public void onClick(View v) {
		  switch(v.getId()){
		   case R.id.bt_dialog_ensure:
			   saveIP();
			   break;
		   case R.id.bt_dialog_concel:
			   this.dismiss();
			   
			   break;
		  }
			
		}


		private void saveIP() {
			String ip=edt_server_ip.getEditableText().toString().trim();
			String ipreg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."  
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
	                +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";  
            Pattern pattern = Pattern.compile(ipreg);  
            Matcher matcher = pattern.matcher(ip);  
            if(ip.isEmpty()||!matcher.matches()){
            	Toast.makeText(Login.this, "ip不合法请重新输入", 0).show();
            	return;
            }
            serverIp=ip;
           // preferecnce.edit().putString(SERVER_IP,ip).commit();
            
            this.dismiss();
            
           
			
		}
	}
	@Override
	protected void updateViewFromSelected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void updateSelectedFromView() {
		// TODO Auto-generated method stub
		
	}
 
    
}
