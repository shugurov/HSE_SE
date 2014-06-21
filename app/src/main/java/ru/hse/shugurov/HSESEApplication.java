package ru.hse.shugurov;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;

public class HSESEApplication extends Application {
	 private static HSESEApplication instance;
	    public HSESEApplication() {
	        instance = this;
	    }
	    public static HSESEApplication getInstance() {
	        return instance;
	    }
	public void onCreate() {
		super.onCreate();
        SharedPreferences prefs = this.getSharedPreferences("ru.hse.se", Context.MODE_PRIVATE);
		if (this.getSharedPreferences("ru.hse.se",
				Context.MODE_PRIVATE).getBoolean(
				"first_launch", true)) {
			HSETracker.sendEvent(getApplicationContext(),
					"first_launch");
			Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean("first_launch", false);
			prefsEditor.putString("firstLaunchDate",
					new SimpleDateFormat("yyyyMMdd").format(System
							.currentTimeMillis()));
			prefsEditor.putString("installID",
					getInstallId());
			prefsEditor.commit();

		}
		if (this.getSharedPreferences("ru.hse.se",
				Context.MODE_PRIVATE)
				.getString("installID", "").equals("")) {
			Editor prefsEditor = prefs.edit();
			prefsEditor.putString("installID",
					getInstallId());
			prefsEditor.commit();
		}
	};


	private String getEmail(){
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
		String possibleEmails="";
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        possibleEmails = account.name;
		        return possibleEmails;
		    }
		}
		return "";
	}

	private String getInstallId() {
		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
	

    
}
