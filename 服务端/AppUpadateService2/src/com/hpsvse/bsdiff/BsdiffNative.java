package com.hpsvse.bsdiff;

public class BsdiffNative {
	
	/**
	 * ���������
	 * @param oldFile   �ϰ汾��apk�ļ�·��
	 * @param newFile   �µ�apk�ļ�·��
	 * @param patchFile ��ֺ���ļ�·��
	 */
	public native static void diff(String oldFile,String newFile,String patchFile);
	
	static{
		System.loadLibrary("Bsdiff");
	}
	
}
