package com.teipreader.share;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.teipreader.Lib.url_get;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.Objects;

import static com.teipreader.Main.Config_dirs.MainPath;

public class HTTPS_SERVER {
    public static void main(String[] args) throws Exception {
        start();
    }
    public static void start() throws Exception {

        // 加载密钥库
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream keyStoreStream = Main.class.getResourceAsStream("/keystore.jks")) {
            if (keyStoreStream == null) {
                throw new Exception("Keystore not found");
            }
            keyStore.load(keyStoreStream, "idsoft".toCharArray());
        }

        // 初始化密钥工厂
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "idsoft".toCharArray());

        // 初始化信任工厂（对于自签名证书，通常使用相同的密钥库）
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        // 初始化 SSL 上下文
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // 创建 HTTPS 服务器
        HttpsServer server = HttpsServer.create(new InetSocketAddress(Config_dirs.NormPort), 1);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // 获取并设置密码
                    SSLParameters sslParams = SSLContext.getDefault().getDefaultSSLParameters();
                    sslParams.setEndpointIdentificationAlgorithm("HTTPS");
                    params.setSSLParameters(sslParams);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 创建一个上下文处理器并添加到服务器
        server.createContext("/", tt -> {
            String path = tt.getRequestURI().getPath();
            url_get adds = new url_get();
            adds.press(tt.getRequestURI().toString());
            System.out.println(path);
            if (Config_dirs.Use_Server_LOG_DEBUG)
                System.out.println((char) 27 + "[33m[Server]:" + path + (char) 27 + "[39;49m");
            if (Objects.equals(path, "/list.json")) {

                String r = TeipIO.PathScan();
                System.out.println(r);
                send_text(tt,r);
                return;
            }
            if (Objects.equals(path, "/dl/")) {
                String[] a = tt.getRequestURI().toString().split("\\?");
                if (Config_dirs.Use_Server_LOG)
                    System.out.println((char) 27 + "[33m[Server]:为小说建立文档@" + URLDecoder.decode(a[1], String.valueOf(StandardCharsets.UTF_8)) + "@zip" + (char) 27 + "[39;49m");
                if (new File(MainPath + "/" + URLDecoder.decode(a[1], String.valueOf(StandardCharsets.UTF_8))).exists()) {
                    TeipIO.GetZip(URLDecoder.decode(a[1], String.valueOf(StandardCharsets.UTF_8)));
                    File file = new File("tmp.zip");
                    // 检查文件是否存在
                    if (file.exists()) {
                        // 文件存在，发送文件内容
                        tt.sendResponseHeaders(200, file.length());
                        Files.copy(file.toPath(), tt.getResponseBody());
                        tt.getResponseBody().close();
                        return;
                    }else {
                        send_text(tt,"ERROR: NOT SUCH FILE IN SERVER");
                    }
                    return;
                } else {
                    if (Config_dirs.Use_Server_LOG)
                        System.out.println((char) 27 + "[33m[Server]: [E]: 不能为不存在的小说建立文档." + (char) 27 + "[39;49m");
                    send_text(tt,"[E]: 没有这个文章可以下载!");
                    return;
                }
            }
            if(Objects.equals(path, "/")){
                send_text(tt,"<p></p><meta charset=\"UTF-8\">" +
                        "<h1>Open Teip Share Server</h1>" +
                        "<h2>开放teip共享平台节点为您服务</h2>" +
                        "<p>感谢您为共享开放平台做出的共献! 您打开了这个功能就是在为社区添砖加瓦!</p>" +
                        "<p>正是因为有许多像您一样的具有共享精神的人士参与到社区中,才使得社区具备了活力!</p>" +
                        "<p>独乐乐不如众乐乐,如果您拥有的书源丰富,而且可以较长时间在线且具有公网IP的话可以在github上为项目中的/style/API_list.json添砖加瓦!" +
                        "<p>期待您的加入,在此,IDlike对每一个启用这项功能的人表示感谢!</p>" +
                        "<p>如果您是这项功能的受益者,请不要忘记感谢提供服务的人,或是为他们提供一些帮助,/style/API_list.json内他们的留言.</p>"
                );
                return;
            }

        });
        //server.setExecutor(null); // 如果需要自定义线程池，可以在这里设置
        server.start();
    }
    public static void send_text(HttpExchange t, String text){
        try{
            t.sendResponseHeaders(200, text.getBytes().length);
            t.getResponseBody().write(text.getBytes());
            t.getResponseBody().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
