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

package com.luki.x.net;

import java.util.List;
import java.util.Map;

/**
 * which does post/get method and return the response string.
 *
 * @author Luki
 *
 */
public interface RequestHandler {

	/**
	 * POST 
	 * @param url url
	 * @param params request params
	 *
	 * @return respond String
	 * @throws Exception
	 */
	String post(String url, RequestParams params) throws Exception;

	/**
	 * GET 
	 *
	 * @param url url
	 * @param params request params
	 *
	 * @return respond String
	 * @throws Exception
	 */
	String get(String url, RequestParams params) throws Exception;


	class RequestParams{
		public Map<String, String> params;
		public Map<String, String> headers;
		public List<Object> dataList;
		public int retryTimes =2;
		public int timeOut = 15 * 1000;
	}
}
