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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * cache manager <BR>
 * <Li> please execute {@link #init(Context)} before use</LI><BR>
 * <Li>is init {@link #isInit()}
 * 
 * @author Luki
 */
public class CacheUtil {

	private Context mContext;
	private static CacheUtil mInstance;

	private CacheUtil(Context context) {
		this.mContext = context;
	}

	/**
	 * init
	 * 
	 * @param context context
	 */
	public static void init(Context context) {
		synchronized (CacheUtil.class) {
			if (mInstance == null) {
				mInstance = new CacheUtil(context);
			}
		}
	}

	/**
	 *
	 * @return is init
	 */
	public static boolean isInit() {
		return mInstance != null && mInstance.mContext != null;
	}

	public static CacheUtil getInstance() {
		if (mInstance == null) {
			throw new IllegalArgumentException("please invoke CacheUtil.init(Context) before used");
		}
		return mInstance;
	}

	/**
	 * save object
	 * 
	 * @param ser ser
	 * @param file file
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (Exception ignored) {
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 *
	 * @param file file
	 * @return object
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException ignored) {
		} catch (Exception e) {
			// e.printStackTrace();
			if (e instanceof InvalidClassException || e instanceof EOFException) {
				try {
					File data = mContext.getFileStreamPath(file);
					data.delete();
				} catch (Exception e1) {
					// e1.printStackTrace();
				}
			}
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (Exception ignored) {
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	private boolean isExistDataCache(String cacheFile) {
		boolean exist = false;
		File data = mContext.getFileStreamPath(cacheFile);
		if (data.exists())
			exist = true;
		return exist;
	}

	public boolean isCacheDataFailure(String cacheFile, long cacheTime) {
		boolean failure = false;
		File data = mContext.getFileStreamPath(cacheFile);
		if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > cacheTime)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}
}
