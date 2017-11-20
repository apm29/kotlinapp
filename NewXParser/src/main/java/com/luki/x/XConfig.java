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

package com.luki.x;

import android.content.Context;
import android.view.WindowManager;

import com.luki.x.inject.content.IParser;
import com.luki.x.inject.content.InjectParser;
import com.luki.x.net.RequestHandler;
import com.luki.x.net.XRequestHandler;
import com.luki.x.task.DataParser;
import com.luki.x.util.CacheUtil;
import com.luki.x.util.NetStatusUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Presents configuration for {@link XParser}
 * 
 * @author Luki
 * @see XParser
 * @see IParser
 * @see RequestHandler
 * @since 1.1.1
 */
@SuppressWarnings("deprecation")
public class XConfig {
	public final int HOLDER_KEY;
	public final int HOLDER_POSITION;
	public final int HOLDER_PARSER_KEY;

	public static int SCREEN_WIDTH;
	public static Context sContext;
	public Map<String, String> requestExtras;
	public Map<String, String> requestHeaders;
	Type errorType;
	IParser userParser;
	RequestHandler requestHandler;
	boolean cacheInDB;
	int timeout;
	int times;
	DataParser dataParser;
	boolean enableDefaultParserLogging;

	/**
	 * Builder for {@link XConfig}
	 *
	 * @author Luki
	 */
	@SuppressWarnings("unused")
	public static class Builder {

		private Map<String, String> requestHeaders;
		private Map<String, String> requestExtras;
		private Context context;
		private Type errorType;
		private boolean cacheInDB = true;
		private IParser userParser;
		private RequestHandler requestHandler;
		private int timeout = 15 * 1000;
		private int times = 1;
		private DataParser dataParser;
		private boolean enabledDefaultParserLogging;

		public Builder(Context context) {
			this.context = context.getApplicationContext();
		}


		/** Additional parameters for each request */
		public Builder requestExtras(Map<String, String> extras) {
			this.requestExtras = extras;
			return this;
		}

		/** It' will be used this analysis when parsing failed */
		public Builder errorType(Type errorType) {
			this.errorType = errorType;
			return this;
		}

		/**
		 * True, cache in the inside of the DB but efficiency low. False, cache in the inside of the File but efficiency
		 * high
		 */
		public Builder cacheInDB(boolean cacheInDB) {
			this.cacheInDB = cacheInDB;
			return this;
		}

		/**
		 * request header
		 */
		public Builder requestHeaders(Map<String, String> headers) {
			this.requestHeaders = headers;
			return this;
		}

		/**
		 * Set up your own parser
		 */
		public Builder userParser(InjectParser parser) {
			this.userParser = parser;
			return this;
		}

		/**
		 * Set up your own net handler.
		 */
		public Builder requestHandler(RequestHandler handler) {
			this.requestHandler = handler;
			return this;
		}

		/**
		 * Set up task timeout. Default is 15 sec.
		 */
		public Builder taskTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		/**
		 * Set up task retry times. Default is 2.
		 */
		public Builder taskRetryTimes(int times) {
			this.times = times;
			return this;
		}

		public Builder taskDataParser(DataParser dataParser) {
			this.dataParser = dataParser;
			return this;
		}
		
		public Builder enabledDefaultParserLogging(boolean b){
			this.enabledDefaultParserLogging = b;
			return this;
		}

		/** Builds configured {@link XConfig} object */
		public XConfig build() {
			check();
			return new XConfig(this);
		}

		/**
		 * 
		 */
		private void check() {
			if (dataParser == null) {
				throw new IllegalArgumentException("DataParser can't be null");
			}
		}

	}

	/**
	 * create default mConfig.
	 * 
	 * @param context context
	 * @return XConfig
	 *//*
	public static XConfig createDefaultConfig(Context context) {
		return new Builder(context).taskDataParser(new DataParser() {
			private com.google.gson.Gson gson = new com.google.gson.Gson();
			@Override
			public Object from(String result, Type clazz) throws Exception {
				return gson.fromJson(result, clazz);
			}
		}).build();
	}*/

	private XConfig(final Builder builder) {
		HOLDER_KEY = R.integer.holder_key;
		HOLDER_POSITION = R.integer.holder_position;
		HOLDER_PARSER_KEY = R.integer.holder_parser_key;
		sContext = builder.context;
		this.requestExtras = builder.requestExtras;
		this.errorType = builder.errorType;
		this.cacheInDB = builder.cacheInDB;
		this.requestHeaders = builder.requestHeaders;
		this.userParser = builder.userParser;
		this.requestHandler = builder.requestHandler;
		this.timeout = builder.timeout;
		this.times = builder.times;
		this.dataParser = builder.dataParser;
		this.enableDefaultParserLogging = builder.enabledDefaultParserLogging;
		

		if (enableDefaultParserLogging) {
			XLog.enableDefaultParserLogging();
		} else {
			XLog.disableDefaultParserLogging();
		}
		if (requestHandler == null) {
			requestHandler = new XRequestHandler();
		}
		init(sContext);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		CacheUtil.init(context);
		NetStatusUtils.init(context);
		sContext = context.getApplicationContext();
		WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
		SCREEN_WIDTH = wm.getDefaultDisplay().getWidth();
	}
}
