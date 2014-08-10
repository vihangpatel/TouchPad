package com.touchpad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;

public class ActivitySettings extends PreferenceActivity 
{

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.app_settings);
	
		ReadSettings();
	}

	public void ReadSettings()
	{		
		SharedPreferences m_pref = getPreferences(MODE_PRIVATE);
    	MyTouch.IP_ADDRESS = m_pref.getString(MyTouch.IPv4_ADDRESS_FEILD, MyTouch.IP_ADDRESS);
    	MyTouch.PORT_NO = m_pref.getInt(MyTouch.PORT_FEILD, MyTouch.PORT_NO);
  
    	Preference IPpf = getPreferenceScreen().findPreference("IP_ADDRESS_FEILD");
    	IPpf.setSummary(MyTouch.IP_ADDRESS + " is the current client Application IP.");
    	
    	Preference PORTpf = getPreferenceScreen().findPreference("PORT_NUMBER_FEILD");
    	PORTpf.setSummary(MyTouch.PORT_NO  + " is the current client Application Port.");
	}
	
	public boolean onPreferenceTreeClick(PreferenceScreen screen,
			Preference pref) 
	{
		String title = pref.getTitle().toString();
		
		if (title.equals("About"))
		{
			About_Dialog();
		}
		
		if(title.equals("Sensitivity"))
		{
			SetTouchSensitivity();
		}
		
		if (title.equals("Application Port"))
		{
			SetApplicationPort();
		}
		
		if (title.equals("Target IPv4 Address"))
		{
			SetApplicationTargetIPv4();
		}
		return true;
	}

	@SuppressLint("NewApi")
	public void SetApplicationPort()
	{
		AlertDialog.Builder portSettingDialog = new AlertDialog.Builder(
				ActivitySettings.this);
		
		final EditText portEditBox =  new EditText(getApplicationContext());
		portEditBox.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		SharedPreferences m_pref = getPreferences(MODE_PRIVATE);
		int portNo = m_pref.getInt(MyTouch.PORT_FEILD, MyTouch.PORT_NO) ;
		portEditBox.setText( String.valueOf(portNo));
		
		portSettingDialog.setTitle("Set the port of the client machine.");
		portSettingDialog.setMessage("Port number on which the computer is running the client application.");
		portSettingDialog.setView(portEditBox);
		portSettingDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{					
						int portNo = (int)Double.parseDouble(portEditBox.getText().toString());
						
						if(portNo == 80 || portNo > 65535 || portNo < 0 )
							return ;
						
						SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putInt(MyTouch.PORT_FEILD, portNo );
						editor.commit();
						dialog.dismiss();
						ReadSettings();
					}
				});

		portSettingDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
					}
				});		
		portSettingDialog.show();
	}
	
	public void SetTouchSensitivity()
	{
		AlertDialog.Builder SensitivitySettingDialog = new AlertDialog.Builder(
				ActivitySettings.this);
		
		final SeekBar sensitivitySeekBar = new SeekBar(getApplicationContext());
		sensitivitySeekBar.setMax(5);
		
		SensitivitySettingDialog.setView(sensitivitySeekBar);
		
		SharedPreferences m_pref = getPreferences(MODE_PRIVATE);
		int defaultVal = m_pref.getInt(MyTouch.TOUCH_SENSITIVITY_FIELD,MyTouch.TOUCH_SENSITVITY) ;
		sensitivitySeekBar.setProgress(defaultVal);
		
		SensitivitySettingDialog.setTitle("Set sensitivity of the Touch.");
		SensitivitySettingDialog.setMessage("Higher the sensitivity , Better the response.");
		
		SensitivitySettingDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{					
						MyTouch.TOUCH_SENSITVITY = sensitivitySeekBar.getProgress();
						SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putInt(MyTouch.TOUCH_SENSITIVITY_FIELD, sensitivitySeekBar.getProgress()); 
						editor.commit();
						dialog.dismiss();
						ReadSettings();
					}
				});

		SensitivitySettingDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
					}
				});		
		SensitivitySettingDialog.show();
	}
	
	public void SetApplicationTargetIPv4()
	{
		AlertDialog.Builder IPSettingDialog = new AlertDialog.Builder(
				ActivitySettings.this);
		
		final EditText IPEditBox =  new EditText(getApplicationContext());
			
		SharedPreferences m_pref = getPreferences(MODE_PRIVATE);
    	IPEditBox.setText( m_pref.getString(MyTouch.IPv4_ADDRESS_FEILD, MyTouch.IP_ADDRESS));
    	
		IPSettingDialog.setTitle("Set the IP of the client machine.");
		IPSettingDialog.setMessage("This will be the IP address of the Reciever computer");
		
		IPSettingDialog.setView(IPEditBox);
		IPSettingDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{					
						SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putString(MyTouch.IPv4_ADDRESS_FEILD,IPEditBox.getText().toString()); 
						editor.commit();
						dialog.dismiss();
						ReadSettings();
					}
				});

		IPSettingDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
					}
				});		
		IPSettingDialog.show();
	}
	
	public void About_Dialog() {
		AlertDialog.Builder dialog1 = new AlertDialog.Builder(
				ActivitySettings.this);
		dialog1.setTitle("About TouchPad");
		
		dialog1.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		dialog1.show();
	}	
}