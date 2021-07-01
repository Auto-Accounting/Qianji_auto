package com.developer.filepicker.model;

import java.util.Locale;

/**
 * @author akshay sunil masram
 */
public class FileListItem implements Comparable<FileListItem> {

    private String filename, location;
    private boolean directory, marked;
    private long time;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Override
    public int compareTo(FileListItem fileListItem) {
        if (fileListItem.isDirectory() && isDirectory()) {
            return filename.toLowerCase().compareTo(fileListItem.getFilename().toLowerCase(Locale.getDefault()));
        } else if (!fileListItem.isDirectory() && !isDirectory()) {
            return filename.toLowerCase().compareTo(fileListItem.getFilename().toLowerCase(Locale.getDefault()));
        } else if (fileListItem.isDirectory() && !isDirectory()) {
            return 1;
        } else {
            return -1;
        }
    }
}