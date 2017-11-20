package com.luki.x.net;

import com.luki.x.XLog;
import com.luki.x.XParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 创建http/https的HttpURLConnection
 *
 * Created by lluo2010 on 17/2/17.
 */



public final class HttpConnectionFactory {

    private static final String TAG = "HttpConnectionFactory";
    private static final String CERTIFICATE_FILE_NAME = "api.stlc.cn.crt";
    private static SSLContext sslContext;

    public static HttpURLConnection getConnection(URL url){
        HttpURLConnection connection = null;
        try {
            if (url.getProtocol().toLowerCase().equals("https")) {
                SSLContext context = HttpConnectionFactory.getSSLContext();
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


    private static SSLContext getSSLContext(){

        if(sslContext == null){
            synchronized (HttpConnectionFactory.class) {
                if(sslContext == null){
                    sslContext = HttpConnectionFactory.createSSlContext();
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
