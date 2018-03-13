package com.xinstars.ads.utils;

public interface ITaskListener {
    void onTaskFinish(String result);
    void onTaskFail(int errorCode);
}
