package org.egov.employee.application;

import android.app.Application;
import android.content.Context;

import org.egov.employee.config.AppConfigReader;

import java.util.Properties;

/**
 * Created by egov on 21/1/16.
 */
public class EgovApp extends Application {

    private static EgovApp appinstance;
    private Context context;
    private static Properties appConfig;

    private String KEY_MULTICITY="app.type.ismulticity";
    private String KEY_URL_RESOURCE="app.resource.serverurl";
    private String KEY_URL_TIMEOUT="app.refresh.serverurl.days";

    @Override
    public void onCreate() {
        super.onCreate();
        this.context= getApplicationContext();
        initializeInstance();
    }

    public void initializeInstance()
    {
        if(appinstance==null)
        {
            appinstance=new EgovApp();
            AppConfigReader appConfigReader = new AppConfigReader(context);
            appConfig = appConfigReader.getProperties("app.conf");
        }
    }

    public static EgovApp getInstance()
    {
        return appinstance;
    }

    public Boolean isMultiCitySupport()
    {
        return Boolean.valueOf(appConfig.get(KEY_MULTICITY).toString());
    }

    public String getCityResourceUrl()
    {
        return appConfig.get(KEY_URL_RESOURCE).toString();
    }

    public Integer getUrlTimeOutDays()
    {
        return Integer.valueOf(appConfig.get(KEY_URL_TIMEOUT).toString());
    }

}
