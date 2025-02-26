package com.teipreader.Lib;

import com.teipreader.share.Main;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static com.teipreader.Main.TeipMake.CopyFileToThis;

public class Download {
    public static boolean import_certificate(File JKS_FILE){
        try {
            CopyFileToThis(JKS_FILE, new File("/certificate/" + JKS_FILE.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    public static boolean Dw_File(String dw_url, String save_as) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        //加载基本证书(IDSOFT颁发的共享证书)
        try (InputStream keyStoreStream = Main.class.getResourceAsStream("keystore.jks")) {
            keyStore.load(keyStoreStream, "idsoft".toCharArray());
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //加载用户自定义证书
        File file = new File("/certificate/");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.getName().equals(".jks")) {
                        try (InputStream keyStoreStream = Files.newInputStream(value.toPath())) {
                            keyStore.load(keyStoreStream, "idsoft".toCharArray());
                        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
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
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();


        // 创建请求
        Request request = new Request.Builder()
                .url(dw_url)
                .header("User-Agent", "Mozilla/5.0") // 模拟浏览器User-Agent
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                File fp = new File(save_as);
                if (fp.isFile()) {
                    if (!fp.delete()) {
                        return false;
                    }
                }
                if (response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        Files.copy(inputStream, fp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            } else {
                // 请求失败
                System.out.println("请求失败: " + response.code());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}