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
package com.luki.x.task;

import com.luki.x.util.NetStatusUtils.NetType;

import java.io.Serializable;

/**
 * task callback bean
 * 
 * @author Luki
 * @param <T>
 */
public final class AsyncResult<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8895005939818095491L;
	public String resultStr;
	public T t;

	public LoadFrom loadedFrom = LoadFrom.NET;
	/** net type */
	public NetType netType = NetType.NONE;
	/** exception */
	public Throwable e;
	/** task status */
	public ResultStatus status = ResultStatus.SUCCESS;
	public TaskParams<T> params;

	@Override
	public String toString() {
		return "AsyncResult [t=" + t + ", resultStr=" + resultStr + ", loadedFrom=" + loadedFrom + ", netType=" + netType + ", e=" + e
				+ ", resultStatus=" + status + "]";
	}

	/**
	 * result status
	 * 
	 * @author Luki
	 */
	public enum ResultStatus {
		/** success */
		SUCCESS,
		/** failed */
		FAILED,
		/** error */
		ERROR,

	}

	public enum LoadFrom {
		NET,
		CACHE
	}
}
