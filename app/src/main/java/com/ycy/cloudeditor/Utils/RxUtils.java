package com.ycy.cloudeditor.Utils;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by kimi9 on 2018/2/27.
 */

public class RxUtils {
    public static Observable<File> listFiles(File file) {
        if (file.isDirectory()) {
            return Observable.from(file.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    return listFiles(file);
                }
            });
        } else {
            return Observable.just(file);
        }
    }

}
