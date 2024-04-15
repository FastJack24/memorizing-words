package com.fastjack;

import java.io.File;
import java.nio.file.FileSystems;

public class Utils {

    public static final String APP_NAME = "memorizing-words";
    private static final String CACHE_DIR_LINUX = ".cache";
    public static final String INFO_FILE_NAME = "INFO.txt";

    public static String getFileSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    public static String constructDataPath() {
        String fileSeparator = getFileSeparator();
        StringBuilder pathToDataBuilder = new StringBuilder(System.getProperty("user.home"));
        if (!System.getProperty("user.home").endsWith(fileSeparator)) {
            pathToDataBuilder.append(fileSeparator);
        }
        return pathToDataBuilder
                .append(CACHE_DIR_LINUX).append(fileSeparator)
                .append(APP_NAME).toString();
    }
}
