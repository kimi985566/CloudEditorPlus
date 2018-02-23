package com.ycy.cloudeditor.Listener;

import android.os.FileObserver;
import android.support.annotation.Nullable;

/**
 * Created by kimi9 on 2018/2/20.
 */

public class fileListener extends FileObserver {

    public fileListener(String path) {
        super(path);
    }

    public fileListener(String path, int mask) {
        super(path, mask);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        switch (event) {
            case FileObserver.CREATE:
                break;
            case FileObserver.ALL_EVENTS:
                break;

        }
    }
}