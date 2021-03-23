package com.example.appllicationdemo;

import android.app.Activity;
import android.widget.Toast;

import org.pjsip.pjsua2.pjsip_status_code;

public interface Observer {
    static void notifyRegState(Activity activity, String regstatus){
        Toast.makeText(activity.getApplicationContext(),regstatus,Toast.LENGTH_LONG).show();
    }
    public void notifyIncomingCall(String calstatus);
}
