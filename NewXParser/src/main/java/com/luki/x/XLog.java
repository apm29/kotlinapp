package com.luki.x;

/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.util.Log;

import com.luki.x.inject.content.InjectParser;
import com.luki.x.util.InjectUtils;

/**
 * "Less-word" analog of Android {@link Log logger}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.4
 */
public final class XLog {

	private static final String LOG_FORMAT = "%1$s\n%2$s";
	private static volatile boolean DEBUG = false;
	private static boolean DEBUG_DEFAULT_PARSER = false;

	private XLog() {}

	/** Enables logger (if {@link #disableLogging()} was called before) */
	public static void enableLogging() {
		DEBUG = true;
	}

	/** is Enable logger */
	public static boolean isLogging() {
		return DEBUG;
	}

	/** Disables logger, no logs will be passed to LogCat, all log methods will do nothing */
	public static void disableLogging() {
		DEBUG = false;
	}

	public static void enableDefaultParserLogging() {
		DEBUG_DEFAULT_PARSER = true;
	}

	public static void disableDefaultParserLogging() {
		DEBUG_DEFAULT_PARSER = false;
	}

	public static void v(String tag, String message, Object... args) {
		log(tag, Log.VERBOSE, null, message, args);
	}

	public static void d(String tag, String message, Object... args) {
		log(tag, Log.DEBUG, null, message, args);
	}

	public static void i(String tag, String message, Object... args) {
		log(tag, Log.INFO, null, message, args);
	}

	public static void w(String tag, String message, Object... args) {
		log(tag, Log.WARN, null, message, args);
	}

	public static void w(String tag, Throwable ex) {
		log(tag, Log.WARN, ex, ex == null ? "" : ex.toString());
	}

	public static void e(String tag, Throwable ex) {
		log(tag, Log.ERROR, ex, ex == null ? "" : ex.toString());
	}

	public static void e(String tag, String message, Object... args) {
		log(tag, Log.ERROR, null, message, args);
	}

	public static void e(String tag, Throwable ex, String message, Object... args) {
		log(tag, Log.ERROR, ex, message, args);
	}

	public static void start(String tag) {
		v(tag, "");
		v(tag, "------------START--------------");
	}

	public static void start(String tag, String message) {
		v(tag, "");
		v(tag, "------------START " + message + "--------------");
	}

	public static void end(String tag) {
		v(tag, "-------------END---------------");
		v(tag, "");
	}

	public static void end(String tag, String message) {
		v(tag, "-------------END " + message + "---------------");
		v(tag, "");
	}

	private static void log(String tag, int priority, Throwable ex, String message, Object... args) {
		if (!DEBUG) return;
		if (!DEBUG_DEFAULT_PARSER && (InjectParser.class.getSimpleName().equals(tag) || InjectUtils.class.getSimpleName().equals(tag))) return;

		if (args != null && args.length > 0) {
			message = String.format(message, args);
		}

		String log;
		if (ex == null) {
			log = message;
		} else {
			String logMessage = message == null ? ex.getMessage() : message;
			String logBody = Log.getStackTraceString(ex);
			log = String.format(LOG_FORMAT, logMessage, logBody);
		}
		if (tag == null) {
			tag = "XLog";
		}
		Log.println(priority, tag, "X-LOG     " + log);
	}
}