package com.isanechek.beardycast.utils;

import android.os.Environment;

/**
 * Created by isanechek on 12.09.16.
 */
public class FileManager {

    private static FileManager manager = new FileManager();

    public static FileManager getInstance() {
        return manager;
    }

    private FileManager() {
    }

}
