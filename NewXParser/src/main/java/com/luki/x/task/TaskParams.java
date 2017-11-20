/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
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
 */
package com.luki.x.task;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The basic parameters of the task request<BR>
 * used {@link Builder} to create TaskParams
 *
 * @author Luki
 */
public final class TaskParams<T> {
	private static int R;
	final String TAG = "Task-" + R++;
	final String url;
	TaskCallBack<AsyncResult<T>> listener;
	Type type;
	Method method = Method.POST;
	Map<String, String> headers;
	Map<String, String> params;
	List<Object> dataList;
	boolean isForceRefresh;
	boolean isAllowLoadCache = true;
	boolean isParse = true;
	boolean isParallel;
	long cacheTime = 60 * 60 * 1000;
	String[] exceptKeysForGenerateKey;
	private TaskConfig taskConfig = new TaskConfig();
	int timeOut;

	private TaskParams(String url) {
		this.url = url;
	}

	final Map<String, String> getParams() {
		Map<String, String> params = new HashMap<>();
		addMap(this.params, params);
		addMap(taskConfig.requestExtras, params);
		return params;
	}

	private void addMap(Map<String, String> a, Map<String, String> b) {
		if (a == null)
			return;
		for (String key : a.keySet())
			b.put(key, a.get(key));
	}

	public final String generateKey() {
		String root = url + "?";
		String param = "";
		if (params != null) {
			for (String key : params.keySet()) {
				boolean isAppend = true;
				if (exceptKeysForGenerateKey != null && exceptKeysForGenerateKey.length > 0) {
					for (String eKey : exceptKeysForGenerateKey) {
						if (eKey != null && eKey.equals(key)) {
							isAppend = false;
							break;
						}
					}
				}
				if (isAppend) {
					param += "&" + key + "=" + params.get(key);
				}
			}
		}
		Map<String, String> requestExtras = taskConfig.requestExtras;
		if (requestExtras != null) {
			for (String key : requestExtras.keySet()) {
				boolean isAppend = true;
				if (exceptKeysForGenerateKey != null && exceptKeysForGenerateKey.length > 0) {
					for (String eKey : exceptKeysForGenerateKey) {
						if (eKey != null && eKey.equals(key)) {
							isAppend = false;
							break;
						}
					}
				}
				if (isAppend) {
					param += "&" + key + "=" + requestExtras.get(key);
				}
			}
		}
		if (param.length() > 0 && param.startsWith("&")) {
			param = param.substring(1, param.length());
		}
		root += param;
		return root;
	}

	/**
	 * @return the headers
	 */
	final Map<String, String> getHeaders() {
		return headers;
	}

	/*public*/
	final void setTaskConfig(TaskConfig taskTConfig) {
		this.taskConfig = taskTConfig;
	}

	@Override
	public final String toString() {
		return "Params [url=" + url + ", isAllowLoadCache=" + isAllowLoadCache + ", type=" + type + ", isParse=" + isParse + ", method=" + method + ", params=" + params + "]";
	}

	final List<Object> getDataList() {
		return dataList;
	}

	public enum Method {
		GET,
		POST
	}

	/**
	 * Builder
	 *
	 * @param <T>
	 * @author Luki
	 */
	public static class Builder<T> {
		private String[] exceptKeysForGenerateKey;
		private boolean isParallel;
		private Map<String, String> headers;
		private Map<String, String> params;
		private List<Object> dataList;
		private boolean isAllowLoadCache = true;
		private boolean isForceRefresh;
		private long cacheTime = 60 * 60 * 1000;
		private Type type;
		private boolean isParse = true;
		private Method method = Method.POST;
		private TaskCallBack<AsyncResult<T>> listener;
		private String url;

		public Builder(String url) {
			this.url = url;
		}

		/**
		 * Task back listener.
		 *
		 * @return Builder<T>
		 */
		public Builder<T> listener(TaskCallBack<AsyncResult<T>> listener) {
			this.listener = listener;
			return this;
		}

		/**
		 * default post
		 *
		 * @param method method
		 * @return Builder<T>
		 */
		public Builder<T> method(Method method) {
			this.method = method;
			return this;
		}

		/**
		 * default parse
		 *
		 * @param isParse isParse
		 * @return Builder<T>
		 */
		public Builder<T> parse(boolean isParse) {
			this.isParse = isParse;
			return this;
		}

		/**
		 * parse type
		 *
		 * @param type tye
		 * @return Builder<T>
		 */
		public Builder<T> type(Type type) {
			this.type = type;
			return this;
		}

		/**
		 * cache time. Default 1 hour.
		 *
		 * @param cacheTime cacheTime
		 * @return Builder<T>
		 */
		public Builder<T> cacheTime(long cacheTime) {
			this.cacheTime = cacheTime;
			return this;
		}

		/**
		 * set force refresh enable.
		 *
		 * @param forceRefresh forceRefresh
		 * @return Builder<T>
		 */
		public Builder<T> forceRefresh(boolean forceRefresh) {
			this.isForceRefresh = forceRefresh;
			return this;
		}

		/**
		 * disable load cache.
		 *
		 * @return Builder<T>
		 */
		public Builder<T> disAllowLoadCache() {
			this.isAllowLoadCache = false;
			return this;
		}

		/**
		 * parameter
		 *
		 * @param params params
		 * @return Builder<T>
		 */
		public Builder<T> params(Map<String, String> params) {
			this.params = params;
			return this;
		}


		/**
		 * headers <BR>
		 *
		 * @param map params
		 * @return Builder<T>
		 */
		public Builder<T> headers(Map<String, String> map) {
			this.headers = map;
			return this;
		}

		/**
		 * tasks is parallel
		 *
		 * @return Builder<T>
		 */
		public Builder<T> parallel(boolean isParallel) {
			this.isParallel = isParallel;
			return this;
		}

		public Builder<T> exceptKeysForGenerateKey(String... keys) {
			this.exceptKeysForGenerateKey = keys;
			return this;
		}

		public Builder<T> setDataList(List<Object> dataList) {
			this.dataList = dataList;
			return this;
		}

		/**
		 * build
		 *
		 * @return Builder<T>
		 */
		public TaskParams<T> build() {
			if (this.type == null) {
				throw new IllegalAccessError("task params 's type must be not null! ");
			}
			TaskParams<T> params = new TaskParams<>(url);
			params.cacheTime = cacheTime;
			params.exceptKeysForGenerateKey = exceptKeysForGenerateKey;
			params.headers = headers;
			params.isAllowLoadCache = isAllowLoadCache;
			params.isForceRefresh = isForceRefresh;
			params.isParallel = isParallel;
			params.isParse = isParse;
			params.listener = listener;
			params.params = this.params;
			params.dataList = dataList;
			params.method = method;
			params.type = type;
			return params;
		}
	}
}