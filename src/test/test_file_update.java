package test;
import com.teipreader.share.Main;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class test_file_update {
    private String api = "https://127.0.0.1:8090";
    public void send_data(String[] args) {
        String cid = "105bfb65-1eeb-4a49-abca-8d6278937028";
        String code = "35FzPZ";
        int stock_speed = 2000;

        File file = new File("Start_up/bd88a21f696cb499b413ddfefab09e3f.zip");
        try {
            StringBuilder buffer_base64 = new StringBuilder();
            try (FileInputStream is = new FileInputStream(file);
                 FileChannel channel = is.getChannel();  ) {
                long fileSize = channel.size();
                ByteBuffer buffer = ByteBuffer.allocate((int) Math.min(fileSize, Integer.MAX_VALUE));
                int bytesRead = channel.read(buffer);
                if (bytesRead == -1) {
                    throw new IOException("无法读取文件");
                }
                buffer.flip();
                byte[] fileBytes = new byte[bytesRead];
                buffer.get(fileBytes);
                buffer_base64.append(Base64.getEncoder().encodeToString(fileBytes));
                System.out.println(buffer_base64.length());
                boot_send_data(cid,code,buffer_base64.length()/stock_speed);
                int xw = 0;
                for (int i = 0; i < buffer_base64.length()/stock_speed; i++) {
                    send_data(cid,code,i,buffer_base64.substring(i*stock_speed,(i+1)*stock_speed));
                    //System.out.println(buffer_base64.substring(i*stock_speed,(i+1)*stock_speed));
                    xw=i;
                }
                if(xw*stock_speed<buffer_base64.length()){
                    send_data(cid,code,xw+1,buffer_base64.substring((xw+1)*stock_speed,buffer_base64.length()));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean send_data(String CID,String CODE,int i,String data){
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        //加载基本证书(IDSOFT颁发的共享证书)
        try (InputStream keyStoreStream = Main.class.getResourceAsStream("/keystore.jks")) {
            keyStore.load(keyStoreStream, "idsoft".toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        TrustManager[] trustManagers;
        SSLSocketFactory sslSocketFactory;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();


        String url = this.api + "/update?cid="+CID+"&code="+CODE+"&i="+i+"&d="+data;

        try {
            //分块
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.dispatcher().executorService().shutdown();
        }
        return true;
    }
    private boolean boot_send_data(String CID,String CODE,int blocks){
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        //加载基本证书(IDSOFT颁发的共享证书)
        try (InputStream keyStoreStream = Main.class.getResourceAsStream("/keystore.jks")) {
            keyStore.load(keyStoreStream, "idsoft".toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        TrustManager[] trustManagers;
        SSLSocketFactory sslSocketFactory;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();


        String url = this.api + "/update?cid="+CID+"&code="+CODE+"&blocks="+blocks;

        try {
            //分块
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.dispatcher().executorService().shutdown();
        }
        return true;
    }
    public boolean cheek_code(){
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        //加载基本证书(IDSOFT颁发的共享证书)
        try (InputStream keyStoreStream = Main.class.getResourceAsStream("/keystore.jks")) {
            keyStore.load(keyStoreStream, "idsoft".toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        TrustManager[] trustManagers;
        SSLSocketFactory sslSocketFactory;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();


        String url = this.api + "/cheek_update";

        try {
            //分块
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.dispatcher().executorService().shutdown();
        }
        return true;
    }
}
