package com.wy.bsdiffupdate.util;

import java.io.File;

import android.os.Environment;

public class Constants {
	
	//增量包名称
	public static final String PATCH_FILE = "apk.patch";
	
	//文件下载路劲
	//public static final String URL_PATCH_DOWNLOAD = "http://192.168.31.159:8081/AppUpadateService2/file/"+PATCH_FILE;
	public static final String URL_PATCH_DOWNLOAD = "http://192.168.83.99:8081/AppUpadateService2/file/apk.patch";
	//http://192.168.31.159:8081/AppUpadateService2/file/apk.patch
	//应用包名
	public static final String PACKAGE_NAME = "com.wy.bsdiffupdate";
	
	//下载存放的路劲
	public static final String SD_CARD = Environment.getExternalStorageDirectory() + File.separator;
	
	//新合并的路劲
	public static final String NEW_APK_PATH = SD_CARD+"BsdiffUpdate_new.apk";
	
	//增量包的路劲
	public static final String PATCH_FILE_PATH = SD_CARD+PATCH_FILE;
	
}
