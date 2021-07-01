package com.developer.filepicker.utils;

import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

/**
 * @author akshay sunil masram
 */
public class ExtensionFilter implements FileFilter {

    private final String[] validExtensions;
    private final DialogProperties properties;

    public ExtensionFilter(DialogProperties properties) {
        if (properties.extensions != null) {
            this.validExtensions = properties.extensions;
        } else {
            this.validExtensions = new String[]{""};
        }
        this.properties = properties;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() && file.canRead()) {
            return true;
        } else if (properties.selection_type == DialogConfigs.DIR_SELECT) {
            return false;
        } else {
            String name = file.getName().toLowerCase(Locale.getDefault());
            for (String ext : validExtensions) {
                if (name.endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
}