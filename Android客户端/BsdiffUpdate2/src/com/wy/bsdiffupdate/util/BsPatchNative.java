package com.wy.bsdiffupdate.util;

public class BsPatchNative {

	/**
	 * 合并
	 * @param oldfile   老版本的apk文件路劲
	 * @param newfile   新的apk文件路劲
	 * @param patchfile 拆分后的文件路劲
	 */
	public native static void patch(String oldfile, String newfile, String patchfile);

	static{
		System.loadLibrary("bspatch");
	}
	
}
