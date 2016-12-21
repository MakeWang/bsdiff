package com.hpsvse.bsdiff;

public class BsdiffTest {
	
	//路径不能包含中文
	public static final String OLD_APK_PATH = "E:/C_Work/file/testapk/BsdiffUpdate_old.apk";
	public static final String NEW_APK_PATH = "E:/C_Work/file/testapk/BsdiffUpdate_new.apk";
	public static final String PATCH_PATH = "E:/C_Work/file/testapk/apk.patch";
	
	public static void main(String[] args) {
		BsdiffNative.diff(OLD_APK_PATH, NEW_APK_PATH, PATCH_PATH);
	}
}
