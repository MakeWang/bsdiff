# bsdiff
Android增量更新，通过Service拿到差分包，在Android端合并，减少用户下载流量，很多大型BTA公司使用的Android更新方案。</br>
# C/C++库编译</br>
下载库的地址<a>http://www.daemonology.net/bsdiff/</a></br>
在我的github里面的地址<a>https://github.com/MakeWang/bsdiff/tree/master/%E4%BD%BF%E7%94%A8%E7%9A%84%E5%BA%93</a></br>
    1、使用工具：Visual Studio 2013</br>
    2、编译步骤：</br>
          1）新建工程。</br>
          2）bsdiff4.3-win32-src解压文件，将所以的C/C++和头文件全部复制到新建的工程文件夹中，在源文件添加添加现C/C++文件，在头文件中添加现有项头文件。</br>
          ![](https://github.com/MakeWang/bsdiff/blob/master/images/a1.png)<br>
          3）里面编译会报错，在项目上点反键--->属性</br>
          ![](https://github.com/MakeWang/bsdiff/blob/master/images/a2.png)<br>
          添加命令：-D _CRT_SECURE_NO_WARNINGS -D _CRT_NONSTDC_NO_DEPRECATE <br>
          然后里面有些语法在VS中通不过，还要修改一个地方<br>
          ![](https://github.com/MakeWang/bsdiff/blob/master/images/a3.png)<br>
          这个地方选择否就ok。<br>
    3、打包动态库。<br>
    ![](https://github.com/MakeWang/bsdiff/blob/master/images/a4.png)<br>
    ![](https://github.com/MakeWang/bsdiff/blob/master/images/a5.png)<br>
    ![](https://github.com/MakeWang/bsdiff/blob/master/images/a6.png)<br>
    ![](https://github.com/MakeWang/bsdiff/blob/master/images/a7.png)<br>
 
# 服务端计算差分包</br>
就直接调用JNI的C/C++代码了，这里就直接上代码</br>
目录结构：</br>
![](https://github.com/MakeWang/bsdiff/blob/master/images/a8.png)<br>
<h3>BsdiffTest</h3></br>
```java
public class BsdiffTest {
	
	//路径不能包含中文
	public static final String OLD_APK_PATH = "E:/C_Work/file/testapk/BsdiffUpdate_old.apk";
	public static final String NEW_APK_PATH = "E:/C_Work/file/testapk/BsdiffUpdate_new.apk";
	public static final String PATCH_PATH = "E:/C_Work/file/testapk/apk.patch";
	
	public static void main(String[] args) {
		BsdiffNative.diff(OLD_APK_PATH, NEW_APK_PATH, PATCH_PATH);
	}
}
```
<h3>BsdiffNative</h3></br>
```java
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
```

# Android客户端差分包合并</br>
这里就要使用NDK，NDK的配置这里就演示流程了，百度上面好多。</br>
项目结构：</br>
![](https://github.com/MakeWang/bsdiff/blob/master/images/a9.png)<br>
<h3>MainActivity</h3></br>
```java
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new ApkUpdateTask().execute();
	}
	
	class ApkUpdateTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//1.下载差分包
				Log.d("badiff", "开始下载");
				File patchFile = DownloadUtils.download(Constants.URL_PATCH_DOWNLOAD);
				
				//获取当前应用的apk文件/data/app/app
				String oldfile = ApkUtils.getSourceApkPath(MainActivity.this, getPackageName());
				//2.合并得到最新版本的APK文件
				String newfile = Constants.NEW_APK_PATH;
				String patchfile = patchFile.getAbsolutePath();
				BsPatchNative.patch(oldfile, newfile, patchfile);
				
				Log.d("badiff", "oldfile:"+oldfile);
				Log.d("badiff", "newfile:"+newfile);
				Log.d("badiff", "patchfile:"+patchfile);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//3.安装
			if(result){
				Toast.makeText(MainActivity.this, "您正在进行无流量更新", Toast.LENGTH_SHORT).show();
				ApkUtils.installApk(MainActivity.this, Constants.NEW_APK_PATH);
			}
		}
		
	}
	
}
```

<h3>ApkUtils</h3></br>
```java
public class ApkUtils {

	public static boolean isInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return installed;
	}

	/**
	 * 获取已安装Apk文件的源Apk文件
	 * 如：/data/app/my.apk
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getSourceApkPath(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName))
			return null;

		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(packageName, 0);
			return appInfo.sourceDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 安装Apk
	 * 
	 * @param context
	 * @param apkPath
	 */
	public static void installApk(Context context, String apkPath) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.setDataAndType(Uri.parse("file://" + apkPath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
```

<h3>BsPatchNative</h3></br>
```java
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
```
<h3>Constants</h3></br>
```java
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
```

<h3>DownloadUtils</h3></br>
```java
public class DownloadUtils {

	/**
	 * 下载差分包
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static File download(String url){
		File file = null;
		InputStream is = null;
		FileOutputStream os = null;
		try {
			file = new File(Environment.getExternalStorageDirectory(),Constants.PATCH_FILE);
			if (file.exists()) {
				file.delete();
			}
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoInput(true);
			is = conn.getInputStream();
			os = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = is.read(buffer)) != -1){
				os.write(buffer, 0, len);
			}
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
}

```

# 坑</br>
这里有一个坑，我踩了好 久才走出来的，就是bzip2这个文件夹下面的文件都是GBK格式的，如果你的项目是UTF-8的编码，就必须将这文件夹里面所有的代码都用记事本转码，这里就不说了</br>
