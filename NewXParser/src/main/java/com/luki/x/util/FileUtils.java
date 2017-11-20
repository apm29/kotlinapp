/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luki.x.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import com.luki.x.XLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();
	private static String SDPATH;

	private static File mImageCacheDir;
	private static File mPackageDir;
	private static FileUtils mFileUtils = new FileUtils();
	private static AssetManager assets;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		if (SDPATH == null) {
			SDPATH = Environment.getExternalStorageDirectory() + "/";
		}
	}

	/**
	 *
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		file.createNewFile();
		return file;
	}

	/**
	 *
	 * @param dirName dirName
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 */
	public File write2SDFromInput(File file, InputStream input) {
		OutputStream output = null;
		try {
			if (file.exists()) {
				file.mkdirs();
				file.createNewFile();
			}
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
				buffer = new byte[4 * 1024];
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * @param context context
	 * @return file
	 */
	public static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				XLog.w(TAG, "Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				XLog.i(TAG, "Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	/**
	 * move asset to /sdcard/Android/data/package/...
	 */
	public static void copyAsset2AndroidData(Context context) {

		mImageCacheDir = getExternalCacheDir(context);
		mPackageDir = new File(mImageCacheDir.getParent());
		if (mPackageDir != null || !mPackageDir.exists()) {
			mPackageDir.mkdirs();
		}
		assets = context.getResources().getAssets();

		listAssets("");
	}

	private static void listAssets(String path) {
		try {
			String[] files = assets.list(path);
			for (String pathname : files) {
				if (pathname.contains(".")) {
					String path2 = path + File.separator;
					if (TextUtils.isEmpty(path)) {
						path2 = path;
					}
					createTypeFile(path2 + pathname);
				} else {
					if (/*pathname.equals("images") ||*/pathname.equals("sounds") || pathname.equals("webkit") || pathname.equals("cfg") || pathname.equals("place")) {
					} else {
						String path2 = path + File.separator;
						if (TextUtils.isEmpty(path)) {
							path2 = path;
						}
						listAssets(path2 + pathname);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createTypeFile(String child) {
		File file = new File(mPackageDir, child);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				boolean b = file.createNewFile();
				if (b) {
					mFileUtils.write2SDFromInput(file, assets.open(child));
				}
			} catch (Exception e) {
				try {
					file.delete();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void downloadFile(String remoteUrl, String filePath, String fileName) {
		HttpURLConnection httpURLConnection;
		URL url;
		BufferedInputStream bis;
		byte[] buf = new byte[10240];
		int size;
		RandomAccessFile rndFile;
		File file = new File(filePath + "/" + fileName);
		long remoteFileSize = getRemoteFileSize(remoteUrl);
		long nPos = 0;

		if (file.exists()) {
			long localFileSize = file.length();
			if (localFileSize < remoteFileSize) {
				XLog.d(TAG, "continue.");
				nPos = localFileSize;
			} else {
				XLog.d(TAG, "delete...");
				file.delete();
				try {
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			url = new URL(remoteUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("User-Agent", "Net");
			httpURLConnection.setRequestProperty("Range", "bytes=" + nPos + "-");
			bis = new BufferedInputStream(httpURLConnection.getInputStream());
			rndFile = new RandomAccessFile(filePath + "\\" + fileName, "rw");
			rndFile.seek(nPos);
			while ((size = bis.read(buf)) != -1) {
				//if (i > 500) break;
				rndFile.write(buf, 0, size);
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long getRemoteFileSize(String url) {
		long size = 0;
		try {
			HttpURLConnection httpUrl = (HttpURLConnection) (new URL(url)).openConnection();
			size = httpUrl.getContentLength();
			httpUrl.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
}