package com.github.zzbbc.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static String getNewFilePathWithTime(String folderPath, String fileName)
            throws IOException {
        String dateString = TimeUtils.now(TimeUtils.DATE_FORMATTER);
        String dateFolderUploadPath =
                new StringBuilder(folderPath).append(File.separator).append(dateString).toString();
        CommonUtils.ensureDirExists(dateFolderUploadPath);

        int seprateExtensionIndex = fileName.lastIndexOf('.');

        String name = fileName.substring(0, seprateExtensionIndex);
        String extensionName = fileName.substring(seprateExtensionIndex + 1);
        String fileNameWithTime = new StringBuilder(name).append("_").append(TimeUtils.time())
                .append(".").append(extensionName).toString();

        String fileDir = new StringBuilder(dateFolderUploadPath).append(File.separator)
                .append(fileNameWithTime).toString();

        return fileDir;
    }
}
