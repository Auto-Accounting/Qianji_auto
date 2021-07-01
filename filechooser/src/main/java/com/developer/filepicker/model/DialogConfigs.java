package com.developer.filepicker.model;

/**
 * @author akshay sunil masram
 */
public abstract class DialogConfigs {

    public static final int SINGLE_MODE = 0;
    public static final int MULTI_MODE = 1;
    public static final int FILE_SELECT = 0;
    public static final int DIR_SELECT = 1;
    public static final int FILE_AND_DIR_SELECT = 2;

    /*  PARENT_DIRECTORY*/
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String STORAGE_DIR = "mnt";

    /*  DEFAULT_DIR is the default mount point of the SDCARD. It is the default
     *  mount point.
     */
    public static final String DEFAULT_DIR = DIRECTORY_SEPARATOR + STORAGE_DIR;
}