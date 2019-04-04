package com.github.exportexcel.constant;

import java.text.SimpleDateFormat;

public interface CommonConstant {

    SimpleDateFormat DATE_TIME_FORMAT_WITHOUT_SPACE = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    SimpleDateFormat DATE_TIME_FORMAT_WITH_SPACE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    int XLS_MAX_LINE_NUMBER = 65534;
}
