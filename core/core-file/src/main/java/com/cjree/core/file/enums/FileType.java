package com.cjree.core.file.enums;

import java.util.HashMap;
import java.util.Map;

public enum FileType {

    // TODO imageCode计划用于返回固定的文件图标
    PICTURE("PICTURE"),
    COMPRESS("COMPRESS"),
    OFFICE("OFFICE"),
    OTHER("OTHER"),
    DMG("DMG");


    private static final String[] OFFICE_TYPES = {"docx", "doc", "docm", "xls", "xlsx", "ppt", "pptx", "pdf", "ofd", "txt"};
    private static final String[] PICTURE_TYPES = {"jpg", "jpeg", "png"};
    private static final String[] ARCHIVE_TYPES = {"rar", "zip", "jar", "7-zip", "tar", "gzip", "7z"};
    private static final Map<String, FileType> FILE_TYPE_MAPPER = new HashMap<>();

    static {
        for (String office : OFFICE_TYPES) {
            FILE_TYPE_MAPPER.put(office, FileType.OFFICE);
        }
        for (String office : PICTURE_TYPES) {
            FILE_TYPE_MAPPER.put(office, FileType.PICTURE);
        }
        for (String archive : ARCHIVE_TYPES) {
            FILE_TYPE_MAPPER.put(archive, FileType.COMPRESS);
        }
        FILE_TYPE_MAPPER.put("dmg", FileType.DMG);
    }

    private static FileType to(String fileType) {
        return FILE_TYPE_MAPPER.getOrDefault(fileType, OTHER);
    }

    public static FileType typeFromFileName(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        String lowerCaseFileType = fileType.toLowerCase();
        return FileType.to(lowerCaseFileType);
    }

    private final String imageCode;

    FileType(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getImageCode() {
        return imageCode;
    }

}
