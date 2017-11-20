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

package com.luki.x;

import android.app.Activity;
import android.view.View;

import com.luki.x.db.DBEntryMap;
import com.luki.x.db.DBHelper;
import com.luki.x.inject.content.IParser;
import com.luki.x.inject.content.InjectHolder;
import com.luki.x.inject.content.InjectParser;
import com.luki.x.inject.content.ParserCallBack;
import com.luki.x.task.TaskConfig;
import com.luki.x.task.TaskEngine;
import com.luki.x.task.TaskStatusListener;
import com.luki.x.task.base.AsyncTask;

import java.io.Serializable;

import static com.luki.x.inject.view.InjectEventControl.initListener;
import static com.luki.x.inject.view.InjectEventControl.initView;
import static com.luki.x.inject.view.InjectEventControl.parseWithCDAnnotation;

/**
 * A easy tool to do something with init {@link View} or set data to View, and get a {@link DBHelper} to operate the
 * DB, and do {@link AsyncTask} with {@link XTask}.
 *
 * @author Luki
 * @version 1 Nov 5, 2014 7:45:20 PM
 * @see #parseView(View)
 * @since 1.0
 */
public enum XParser {
	INSTANCE;

	private static final String TAG = "XParser";

	private static final String LOG_DESTROY = "Destroy XParser";

	private static final String LOG_INIT_CONFIG = "Initialize XParser with configuration";
	private static final String WARNING_RE_INIT_CONFIG = "Try to initialize XParser which had already been initialized before. " + "To re-init XParser with new configuration call XParser.destroy() at first.";
	private static final String ERROR_NOT_INIT = "XParser must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "XParser configuration can not be initialized with null";
	private final static IParser mDefaultParser = new InjectParser();
	private XConfig configuration;

	/**
	 * Initializes XParser instance with configuration.<br />
	 * If configurations was set before ( {@link #isInitialized()} == true) then this method does nothing.<br />
	 * To force initialization with new configuration you should {@linkplain #destroy() destroy XParser} at first.
	 *
	 * @param configuration {@linkplain XConfig XParser configuration}
	 * @throws IllegalArgumentException if <b>configuration</b> parameter is null
	 */
	public synchronized void init(XConfig configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null) {
			XLog.d(TAG, LOG_INIT_CONFIG);
			this.configuration = configuration;
		} else {
			XLog.w(TAG, WARNING_RE_INIT_CONFIG);
		}
	}

	/**
	 * Returns <b>true</b> - if XParser {@linkplain #init(XConfig) is initialized with
	 * configuration}; <b>false</b> - otherwise
	 */
	public boolean isInitialized() {
		return configuration != null;
	}

	/**
	 * Traversal the value, format, click, longClick from the InjectHolder.<BR>
	 * see more <BR>
	 * {@link #parseView(View)}
	 *
	 * @param target   The target is an Activity who contains the click or longClick method.
	 * @param callBack callBack
	 */
	public void parse(Activity target, ParserCallBack callBack) {
		parse(target, null, callBack);
	}

	/**
	 * Traversal the value, format, click, longClick from the InjectHolder.<BR>
	 * see more <BR>
	 * {@link #parseView(View)}
	 *
	 * @param target   The target is an Activity who contains the click or longClick method.
	 * @param data     dataSource
	 * @param callBack callBack
	 */
	public void parse(Activity target, Object data, ParserCallBack callBack) {
		View view = target.getWindow().getDecorView();
		parse(target, data, view, callBack);
	}

	/**
	 * Traversal the value and format and click and longClick from the InjectHolder.<BR>
	 * see more <BR>
	 * {@link #parseView(View)}
	 *
	 * @param target   The target is an object who contains the click or longClick method. If you doesn't need click or
	 *                 longClick method, it can be null.
	 * @param data     dataSource
	 * @param view     Which contains contentDescription.
	 * @param callBack callBack
	 */
	public void parse(Object target, Object data, View view, ParserCallBack callBack) {
		checkConfiguration();
		if (view == null || data == null) {
			return;
		}
		InjectHolder holder;
		holder = (InjectHolder) view.getTag(configuration.HOLDER_KEY);
		if (holder == null) {
			holder = parseView(view);
			view.setTag(configuration.HOLDER_KEY, holder);
		}
		Integer position = (Integer) view.getTag(configuration.HOLDER_POSITION);
		if (position == null)
			position = -1;
		holder.position = position;

		mDefaultParser.onParse(target, data, holder, callBack);
		if (configuration.userParser != null) {
			configuration.userParser.onParse(target, data, holder, callBack);
		}
	}

	/**
	 * Checks if XParser's configuration was initialized
	 *
	 * @throws IllegalStateException if configuration wasn't initialized
	 */
	private void checkConfiguration() {
		if (configuration == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

	/**
	 * Parse the view contentDescription for each child, and add the each child to InjectHolder.
	 *
	 * @param view views who contains contentDescription collection.
	 * @return views who contains contentDescription collection.
	 */
	public InjectHolder parseView(View view) {
		checkConfiguration();
		InjectHolder injectHolder;
		if (configuration.userParser != null) {
			injectHolder = configuration.userParser.onParseView(view);
			if (injectHolder != null) {
				return injectHolder;
			}
		}
		return mDefaultParser.onParseView(view);
	}

	/**
	 * initial all views and it's listener method in the activity. see more {@link #inject(Object, View)}
	 *
	 * @param activity activity
	 */
	public void inject(Activity activity) {
		inject(activity, activity.getWindow().getDecorView());
	}

	/**
	 * initial all views and it's listener method in the view. And the listener method should be in the target.
	 * All about the annotation should write in the target <BR>
	 * e.g.<BR>
	 * <pre>
	 * public class Sample {
	 * 		＠ViewInject(value = R.id.test, longClick = "longClick")
	 * 		View v;
	 * 		＠ViewInject(longClick = "longClick")
	 *   		View test;
	 *   		View view;
	 *
	 * 		public Sample(View view) {
	 * 			this.view = view;
	 * 			ViewInjectUtil.initViewInject(this, view);
	 *        }
	 *
	 * 		＠ViewListener(ids = {R.id.test}, type=ListenerType.CLICK)
	 * 		public void click(View v){
	 * 			Toast.makeText(view.getContext(), "click", Toast.LENGTH_SHORT).show();
	 *        }
	 *
	 * 		public boolean longClick(View v){
	 * 			Toast.makeText(view.getContext(), "longClick", Toast.LENGTH_SHORT).show();
	 * 			return true;
	 *        }
	 *
	 * }
	 *     </pre>
	 *
	 * @param target target
	 * @param view   view
	 */
	public void inject(Object target, View view) {
		initView(target, view);
		initListener(target, view);
		parseWithCDAnnotation(target, view);
	}

	/**
	 * Get a XTask instance.
	 *
	 * @param callBack callBack
	 * @return XTask
	 */
	public <T extends Serializable> XTask<T> getXTask(TaskStatusListener callBack) {
		return getXTask(callBack, null);
	}

	/**
	 * Get a XTask instance with taskConfig.
	 *
	 * @param callBack callBack
	 * @return XTask
	 */
	public <T extends Serializable> XTask<T> getXTask(TaskStatusListener callBack, TaskConfig config) {
		checkConfiguration();
		if (config == null) {
			config = getDefaultTaskConfig();
		}
		return new TaskEngine<>(callBack, config);
	}

	/**
	 * init default task configuration
	 *
	 * @return task configuration
	 */
	private TaskConfig getDefaultTaskConfig() {
		TaskConfig config = new TaskConfig();
		config.cacheInDB = configuration.cacheInDB;
		config.errorType = configuration.errorType;
		config.requestExtras = configuration.requestExtras;
		config.requestHeaders = configuration.requestHeaders;
		config.timeOut = configuration.timeout;
		config.retryTimes = configuration.times;
		config.dataParser = configuration.dataParser;
		config.requestHandler = configuration.requestHandler;
		return config;
	}

	/**
	 * returns a DBHelper object who can convenient and unified to manage the data.
	 *
	 * @return DBHelper
	 *
	 * @see DBHelper
	 */
	public DBHelper getDBHelper() {
		return getDBHelper(null);
	}

	/**
	 * When the database does not exist, it will automatically generate a database according to the database name.
	 * And returns a DBHelper object who can convenient and unified to manage the data.
	 *
	 * @param dbName dbName
	 * @return DBHelper
	 *
	 * @see DBHelper
	 */
	public DBHelper getDBHelper(String dbName) {
		checkConfiguration();
		return DBEntryMap.getDBHelper(XConfig.sContext, dbName);
	}

	/**
	 * {@linkplain #inject(Activity) Stops XParser} and clears current configuration. <br />
	 * You can {@linkplain #init(XConfig) init} XParser with new configuration after calling this
	 * method.
	 */
	public void destroy() {
		XLog.d(TAG, LOG_DESTROY);
		configuration = null;
		DBEntryMap.destroy();
	}

	public XConfig getXConfig() {
		return configuration;
	}
}
