package com.hpsvse.bsdiff;

public class BsdiffNative {
	
	/**
	 * 增量包拆分
	 * @param oldFile   老版本的apk文件路劲
	 * @param newFile   新的apk文件路劲
	 * @param patchFile 拆分后的文件路劲
	 */
	public native static void diff(String oldFile,String newFile,String patchFile);
	
	static{
		System.loadLibrary("Bsdiff");
	}
	
}
