package com.developer.filepicker.model;

import java.util.HashMap;
import java.util.Set;

/**
 * @author akshay sunil masram
 */
public class MarkedItemList {

    private static HashMap<String, FileListItem> ourInstance = new HashMap<>();

    public static void addSelectedItem(FileListItem item) {
        ourInstance.put(item.getLocation(), item);
    }

    public static void removeSelectedItem(String key) {
        ourInstance.remove(key);
    }

    public static boolean hasItem(String key) {
        return ourInstance.containsKey(key);
    }

    public static void clearSelectionList() {
        ourInstance = new HashMap<>();
    }

    public static void addSingleFile(FileListItem item) {
        ourInstance = new HashMap<>();
        ourInstance.put(item.getLocation(), item);
    }

    public static String[] getSelectedPaths() {
        Set<String> paths = ourInstance.keySet();
        String[] strings = new String[paths.size()];
        int i = 0;
        for (String path : paths) {
            strings[i++] = path;
        }
        return strings;
    }

    public static int getFileCount() {
        return ourInstance.size();
    }
}
