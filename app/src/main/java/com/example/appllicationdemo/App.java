package com.example.appllicationdemo;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class App extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        try { System.loadLibrary("pjsua2");
            System.out.println("pjsip============================> Library loaded");}
        catch (UnsatisfiedLinkError e) {
            System.out.println("pjsip============================> Library not loaded");
            Log.e("TAG", "Pjsua2:initNatives " + e.getMessage()); }
        // Required initialization logic here!
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}