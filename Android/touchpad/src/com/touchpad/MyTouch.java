package com.touchpad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MyTouch extends Activity {
	
	private Button btnLeft;
	private Button btnRight;

    public static int APPLICATION_SETTINGS  = 1000;
    public static int TEST_CONNECTION = 1001 ;
	
    public static String PORT_FEILD = "PORT_NUMBER_FEILD";
    public static String IPv4_ADDRESS_FEILD = "IP_ADDRESS_FEILD";
    public static String TOUCH_SENSITIVITY_FIELD = "TOUCH_SENSITIVITY_FEILD";
    
    public static int TOUCH_SENSITVITY = 1 ;
    
	public static int LEFT_BUTTON = 100 ;
	public static int RIGHT_BUTTON = 101;
	
	public static int MOUSE_CLICK = 0;
	public static int MOUSE_MOVED = 1;
	public static int MOUSE_WHEEL = 2;
	
	public static int LONG_HOLD = 11 ;
	public static int SINGLE_TAP = 12 ;
	
	public static String IP_ADDRESS = "192.168.43.8";
	public static int PORT_NO = 8888 ;
	
	class Point
	{
		public int x;
		public int y ;
		
	}
	
	Point m_startPt ;
	Point m_endPt;
	
	SharedPreferences m_pref;
	
	GestureDetector detectTouch  ;
    View touchView ;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        touchView = findViewById(R.id.View01);
        btnLeft = (Button) findViewById(R.id.LeftButton);
		btnRight = (Button) findViewById(R.id.RightButton);
        
        m_startPt = new Point();
        m_endPt = new Point();
        
        ReadSettingsFromFile();
        DisableWidgets(!IsConnected());
        HandleGestures();
        
        btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				SendClicks(SINGLE_TAP, LEFT_BUTTON);
			}
		});
        
        btnLeft.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) 
			{
				SendClicks(LONG_HOLD, LEFT_BUTTON);
				return true;
			}
		});
        
        btnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				SendClicks(SINGLE_TAP, RIGHT_BUTTON);
			}
		});
        
        btnRight.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v)
			{
				SendClicks(LONG_HOLD,RIGHT_BUTTON);
				return true;
			}
		});
        
        touchView.setOnTouchListener(new View.OnTouchListener() 
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) 
            {
            	boolean flag = detectTouch.onTouchEvent(event);
            	Log.d("ON TOUCH DOWN : ","Touch is coming ... ");
            	if(event.getAction() == MotionEvent.ACTION_MOVE && !flag)
            	{
            		int dx = (int)event.getX() -  m_startPt.x;
    				int dy = (int)event.getY() - m_startPt.y;
    				
    				m_startPt.x = (int)event.getX();
    				m_startPt.y = (int)event.getY();
    				Log.d("ON D SCROLL : " , " x : " + dx + " y : " +dy);
    				SendTouches(MOUSE_MOVED, dx,dy);
            	}
            	return true;
            }
        });
    }
    
    public void DisableWidgets(boolean flag)
    {
    	if(flag)
    	{
    		Toast.makeText(MyTouch.this, "Connection is not available",Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(MyTouch.this, "Connection is available",Toast.LENGTH_SHORT).show();
    	}
    		touchView.setEnabled(!flag);
    		btnLeft.setEnabled(!flag);
    		btnRight.setEnabled(!flag);
    	
    }
    
    public void SendClicks(int ClickType ,int bType)
    {
    	try 
 		{
 	          Socket socket = new Socket(IP_ADDRESS,PORT_NO);
			  DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		      String str = String.valueOf(MOUSE_CLICK) + "#" 
		    		   + String.valueOf(ClickType) +"@"+ String.valueOf(bType) ;		  
			  dataOutputStream.writeBytes(str);		   
			  dataOutputStream.close();
 		} 
 		catch (IOException e) 
 		{
 			DisableWidgets(true);
			e.printStackTrace();
		}
    }
       
    
    public void SendTouches(int eventType , int x,int y)
    {
    	try 
 		{
 	          Socket socket = new Socket(IP_ADDRESS,PORT_NO);
			  DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		      String str = String.valueOf(eventType)+"#"+ String.valueOf(x*TOUCH_SENSITVITY) 
		    		  + "@" + String.valueOf(y*TOUCH_SENSITVITY);			  
			  dataOutputStream.writeBytes(str);		   
			  dataOutputStream.close();
 		} 
 		catch (IOException e) 
 		{
 				DisableWidgets(true);
				e.printStackTrace();
		}  
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.add(menu.NONE, APPLICATION_SETTINGS , menu.NONE, "Settings");
		menu.add(menu.NONE, TEST_CONNECTION , menu.NONE , "Test Connection");
		return true;
	}
    
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case 1000 :
				Intent intentSettings = new Intent(this, ActivitySettings.class);
				startActivity(intentSettings);
				break ;

			case 1001 :
				DisableWidgets(!IsConnected());
				break ;
		}
		return false;
	}

    public boolean IsConnected()
    {
    	try 
    	{
			Socket connectionSocket = new Socket(IP_ADDRESS,PORT_NO);
			connectionSocket.close();
			return true ;
			
		}
    	catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return false ;
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
			return false;
		}
    	
    }
    
    public void ReadSettingsFromFile()
    {
    	m_pref = getPreferences(MODE_PRIVATE);
    	IP_ADDRESS = m_pref.getString(IPv4_ADDRESS_FEILD, IP_ADDRESS);
    	PORT_NO = m_pref.getInt(PORT_FEILD, PORT_NO);
    	TOUCH_SENSITVITY = m_pref.getInt(TOUCH_SENSITIVITY_FIELD, 1);
    	Log.d("IP AND PORT :" , IP_ADDRESS + " " + PORT_NO );
    }
    
    @SuppressLint("NewApi") 
    public void HandleGestures()
    {
    	detectTouch = new GestureDetector(this,
    	new OnGestureListener() 
    	{
			
			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{			
				SendClicks(SINGLE_TAP,LEFT_BUTTON);
				return true;
			}
			
			@Override
			public void onShowPress(MotionEvent e) 
			{
			
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) 
			{
				int dx = (int)e2.getX() -  m_startPt.x;
				int dy = (int)e2.getY() - m_startPt.y;
				
				m_startPt.x = (int)e2.getX();
				m_startPt.y = (int)e2.getY();
				Log.d("ON D SCROLL :cv " , " x : " + dx + " y : " +dy);
				Log.d("TOUCH VIEW WIDTH : " , String.valueOf( touchView.getWidth()));
				
				int eventType = 
				(e2.getX() > touchView.getWidth() - 10 ) ? MOUSE_WHEEL : MOUSE_MOVED ;
				
				
				SendTouches(eventType , dx,dy);
				return true;
			}
			
			@Override
			public void onLongPress(MotionEvent e) 
			{
				SendClicks(LONG_HOLD,LEFT_BUTTON);
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) 
			{
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) 
			{
				m_startPt.x = (int) e.getX();
				m_startPt.y = (int) e.getY();
				Log.d("ON D DOWN : " , " x : " + m_startPt.x + " y : " + m_startPt.y);
				return false;
			}
		});
    }
}