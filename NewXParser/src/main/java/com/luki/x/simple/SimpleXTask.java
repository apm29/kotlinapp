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
package com.luki.x.simple;

import android.util.Log;

import com.luki.x.XParser;
import com.luki.x.XTask;
import com.luki.x.task.AsyncResult;
import com.luki.x.task.AsyncResult.LoadFrom;
import com.luki.x.task.AsyncResult.ResultStatus;
import com.luki.x.task.TaskCallBack;
import com.luki.x.task.TaskParams;
import com.luki.x.task.TaskStatusListener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Universal asynchronous task paradigm
 * 
 * @author Luki
 */
@SuppressWarnings("unchecked")
public class SimpleXTask<T extends Serializable> implements TaskStatusListener {
	private boolean isTasking;
	protected String TAG;
	private XTask<T> mCurrentTask;

	public SimpleXTask() {
		TAG = getClass().getSimpleName();
	}

	/**
	 * cancel task
	 */
	public synchronized void cancel() {
		mCurrentTask.cancel(true);
		mCurrentTask.getListener().onCancel();
	}

	/**
	 *  composed with itself params for request.
	 * 
	 * @param params params
	 */
	public void task(TaskParams<T> params) {
		if (!isTasking()) {
			isTasking = true;
			mCurrentTask = XParser.INSTANCE.getXTask(this);
			mCurrentTask.execute(params);
		} else
			Log.v(TAG, "tasking");
	}

	/**
	 * <LI> default method is POST</LI><BR>
	 *
	 * @param url url
	 * @param params params
	 * @param type type
	 * @param listener callback
	 */
	public void task(String url, Map<String, String> params, Type type, SimpleTaskBack<T> listener) {
		TaskParams<T> p = new TaskParams.Builder<T>(url).listener(listener).params(params).type(type).build();
		task(p);
	}

	public synchronized boolean isTasking() {
		return isTasking;
	}

	public void onEnd() {
		isTasking = false;
	}

	public static abstract class SimpleTaskBack<T> implements TaskCallBack<AsyncResult<T>> {

		public void onResult(AsyncResult<T> result) {
			if (result.status == ResultStatus.SUCCESS) {
				onSuccess(result, result.loadedFrom);
			} else
				onFailed(result, result.loadedFrom);
		}

		public void onCancel() {
		}

		public abstract void onSuccess(AsyncResult<T> result, LoadFrom loadFrom);

		public void onFailed(AsyncResult<T> result, LoadFrom loadFrom) {

		}
	}

	public void onStart() {

	}

	public void onCancel() {

	}
}
