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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Locale;

/**
 *
 * 
 * @author Luki
 */
public class NetStatusUtils {

	private static ConnectivityManager connectivityManager;

	/**
	 * new Type<BR/>
	 * <B> <LI>NONE</LI><BR/>
	 * <LI>WIFI</LI> <BR/>
	 * <LI>CMWAP</LI><BR/>
	 * <LI>CMNET</LI><BR/>
	 * </B>
	 * 
	 * @author Luki
	 */
	public enum NetType {
		NONE,
		WIFI,
		CMWAP,
		NetType,
		CMNET
	}

	public static void init(Context context) {
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * get current net type
	 * 
	 * @return <LI>NONE ：not network</LI><BR>
	 *         <LI>WIFI ：WIFI</LI><BR>
	 *         <LI>CMWAP：WAP</LI><BR>
	 *         <LI>CMNET：NET</LI>
	 */
	public static NetType getNetworkType() {
		check();
		NetType netType = NetType.NONE;
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo != null && !extraInfo.equals("")) {
				if (extraInfo.toLowerCase(Locale.CHINA).equals("cmnet")) {
					netType = NetType.CMNET;
				} else {
					netType = NetType.CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NetType.WIFI;
		}
		return netType;
	}

	/**
	 *
	 * @return is network connected
	 */
	public static boolean isNetworkConnected() {
		check();
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	private static void check() {
		if (connectivityManager == null) {
			throw new IllegalArgumentException("please invoke NetStatusUtils.init");
		}
	}
}
