package org.egov.android;

import java.io.IOException;
import java.io.InputStream;

import org.egov.android.common.ReflectionUtil;
import org.egov.android.conf.Config;
import org.egov.android.data.SQLiteHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AndroidApp extends Application {

	private Config config = null;
	private SharedPreferences session = null;

	public void configure(String configFile) {
		try {
			InputStream is = getAssets().open(configFile);
			config = new Config(is);
			is.close();
			ReflectionUtil.setFieldData(AndroidLibrary.getInstance(), "config",
					config);
			ReflectionUtil.setFieldData(
					AndroidLibrary.getInstance(),
					"session",
					getSharedPreferences(config.getString("app.name"),
							Context.MODE_PRIVATE));
			SQLiteHelper.newInstance(getApplicationContext()).initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		session = getSharedPreferences(config.getString("app.name"),
				Context.MODE_PRIVATE);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.configure("alib.conf");
	}

	public Config getConfig() {
		return this.config;
	}

	public SharedPreferences getSession() {
		return this.session;
	}
}
