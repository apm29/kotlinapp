/**
 * Copyright (C) 2015 Luki(liulongke@gmail.com)
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

import android.text.TextUtils;

import com.luki.x.XLog;
import com.luki.x.XParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 */
public class HttpRequest {

    private static final String TAG = HttpRequest.class.getName();
    private static final String CERTIFICATE_FILE_NAME = "api.stlc.cn.crt";
    private static SSLContext sslContext;

	/**
	 * do get
	 *
	 * @param url           url
	 * @param requestParams params
	 * @return URL response
	 */
	public static String sendGet(String url, RequestHandler.RequestParams requestParams) throws IOException {
		String result = "";
		BufferedReader in = null;
		try {
			if (!url.contains("?")) {
				url += "?";
			}
            for (String key : requestParams.params.keySet()) {
                String strValue = requestParams.params.get(key);
                strValue = TextUtils.isEmpty(strValue)?"":strValue;
                url += key + "=" + URLEncoder.encode(strValue, "UTF-8") + "&";
            }
			url = url.substring(0, url.length() - 1);

			URL realUrl = new URL(url);
			// open connection
            HttpURLConnection connection = HttpRequest.getConnection(realUrl);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(requestParams.timeOut);
			// set request headers
			connection.setRequestProperty("Connection", "Keep-Alive");
			for (String key : requestParams.headers.keySet()) {
				connection.setRequestProperty(key, requestParams.headers.get(key));
			}
			// connect
			connection.connect();
			// get response
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} finally {// close input steam
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * do post
	 *
	 * @param url           URL
	 * @param requestParams param
	 * @return response
	 *
	 * @throws IOException
	 */
	@SuppressWarnings("ConstantConditions")
	public static String sendPost(String url, RequestHandler.RequestParams requestParams) throws IOException {
		Map<String, String> params = requestParams.params;
		Map<String, String> headers = requestParams.headers;
		List<Object> dataList = requestParams.dataList;
		params = params == null ? new HashMap<String, String>() : params;
		headers = headers == null ? new HashMap<String, String>() : headers;
		dataList = dataList == null ? new ArrayList<>() : dataList;

		DataOutputStream dos = null;
		BufferedReader in = null;
		PrintWriter out = null;
		String result = "";

		String boundary = System.currentTimeMillis() + "";
		String end = "\r\n";
		String twoHyphens = "--";
		try {
			URL realUrl = new URL(url);
			// open connection
            HttpURLConnection connection = HttpRequest.getConnection(realUrl);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(requestParams.timeOut);
			connection.setReadTimeout(requestParams.timeOut);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			// set header
			connection.setRequestProperty("Connection", "Keep-Alive");
			for (String key : headers.keySet()) {
				connection.setRequestProperty(key, headers.get(key));
			}

			StringBuilder param = new StringBuilder();
			if (!dataList.isEmpty()) {
				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				connection.connect();
				dos = new DataOutputStream(connection.getOutputStream());
				for (int i = 0; i < dataList.size(); i++) {
					Object obj = dataList.get(i);
					if (obj instanceof File || obj instanceof InputStream) {
						InputStream fis;
						if (obj instanceof File) {
							fis = new FileInputStream((File) obj);
						} else {
							fis = (InputStream) obj;
						}
						dataList.set(i, fis);
						String p = twoHyphens + boundary + end + "Content-Type: application/octet-stream" + end + "Content-Disposition: form-data; filename=\"file" + i + "\"; name=\"file" + i + "\"" + end + end;

						byte[] data = new byte[fis.available()];
						if (fis.read(data) != -1) {
							dos.writeBytes(p);
							dos.write(data);
						}

					} else if (obj instanceof String) {
						dos.write(((String) obj).getBytes());
					}
				}

				if (!params.isEmpty()) {
					for (String key : params.keySet()) {
						String strKeyValue = TextUtils.isEmpty(params.get(key))?"":params.get(key);
						param.append(end).append(twoHyphens).append(boundary).append(end).append("Content-Type: text/plain").append(end).append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(end).append(end).append(URLEncoder.encode(strKeyValue, "UTF-8")).append(end).append(twoHyphens).append(boundary).append(twoHyphens);
					}
				}
				dos.writeBytes(param.toString());
				dos.flush();
			} else {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				dos = new DataOutputStream(connection.getOutputStream());

				out = new PrintWriter(connection.getOutputStream());
				for (String key : params.keySet()) {
					String svalue=TextUtils.isEmpty(params.get(key))?"":params.get(key);
					param.append(key).append("=").append(URLEncoder.encode(svalue, "UTF-8")).append("&");
				}
				if (param.length() > 0) {
					param.deleteCharAt(param.length() - 1);
				}
				out.print(param.toString());
				out.flush();
			}
			if (connection.getResponseCode() == 200) {
				InputStream is = connection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				result = br.readLine();
				is.close();
			} else {
				result = String.valueOf(connection.getResponseCode());
			}
		} finally {// close input steam
			try {
				if (dos != null) {
					dos.close();
				}
				if (dataList.isEmpty()) {
					for (Object fis : dataList) {
						if (fis instanceof InputStream) {
							((InputStream) fis).close();
						}
					}
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}


    private static HttpURLConnection getConnection(URL url){
        HttpURLConnection connection = null;
        try {
            if (url.getProtocol().toLowerCase().equals("https")) {
                SSLContext context = HttpRequest.getSSLContext();
                if (context != null) {
                    HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
                    httpsCon.setSSLSocketFactory(context.getSocketFactory());
                    connection = httpsCon;
                } else {
                    XLog.e(TAG, "Consider https as http since  create SSLContext failed");
                    connection = (HttpURLConnection) url.openConnection();
                }
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
        }catch (Exception ex){
            XLog.e(TAG, "failed to get Connection, info:"+ex.getMessage());
        }

        return connection;
    }


    public static SSLContext getSSLContext(){

        if(sslContext == null){
            synchronized (HttpRequest.class) {
                if(sslContext == null){
                    sslContext = HttpRequest.createSSlContext();
                }
            }
        }

        return sslContext;
    }


    private static SSLContext createSSlContext(){
        SSLContext context = null;
        Certificate ca = null;
        InputStream caInputStream = null;
        try{
            try {
                caInputStream= XParser.INSTANCE.getXConfig().sContext.getClassLoader().getResourceAsStream("assets/"+CERTIFICATE_FILE_NAME);
            }catch (Exception e){
                e.printStackTrace();
            }
            //InputStream caInputStream = getAssets().open("load-der.crt");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInputStream);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keystore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        }catch (Exception ex){
            ex.printStackTrace();
            XLog.e(TAG, "failed to create SSLContext, info:"+ex.getMessage());
        }finally{
            if (caInputStream!=null){
                try {
                    caInputStream.close();
                }catch (Exception ex){
                }
            }
        }

        return context;
    }

}