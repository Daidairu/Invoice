package com.example.hungjuhsiao.invoice;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by hungju.hsiao on 2017/2/22.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
//        Stetho.initializeWithDefaults(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }
}