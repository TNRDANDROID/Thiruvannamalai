package com.nic.publictax.utils;


import com.nic.publictax.R;
import com.nic.publictax.application.NICApplication;

/**
 * Created by Achanthi Sundar  on 21/03/16.
 */
public class UrlGenerator {



    public static String getLoginUrl() {
        return NICApplication.getAppString(R.string.LOGIN_URL);
    }

    public static String getServicesListUrl() {
        return NICApplication.getAppString(R.string.BASE_SERVICES_URL);
    }

    public static String getPMAYListUrl() {
        return NICApplication.getAppString(R.string.APP_MAIN_SERVICES_URL);
    }

    public static String getTnrdHostName() {
        return NICApplication.getAppString(R.string.TNRD_HOST_NAME);
    }



}
