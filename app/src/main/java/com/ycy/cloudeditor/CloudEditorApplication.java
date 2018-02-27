package com.ycy.cloudeditor;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by kimi9 on 2018/2/27.
 */

public class CloudEditorApplication extends Application {

    @Override
    public void onCreate() {
        Utils.init(getApplicationContext());
        super.onCreate();
    }

}
