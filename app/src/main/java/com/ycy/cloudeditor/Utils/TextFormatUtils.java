package com.ycy.cloudeditor.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kimi9 on 2018/2/23.
 */

public class TextFormatUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

}
