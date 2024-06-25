package com.teipreader.share;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.teipreader.Lib.check_code;
import com.teipreader.Lib.post_file_mgr;
import com.teipreader.Lib.url_get;
import com.teipreader.dataframe.base64_file_update;
import com.teipreader.dataframe.cheek_code_use;

import javax.imageio.ImageIO;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static com.teipreader.Lib.check_code.generateCaptchaImage;
import static com.teipreader.Lib.check_code.generateCaptchaText;
import static com.teipreader.Main.Config_dirs.MainPath;

public class HTTPS_SERVER {
    public static List<cheek_code_use> cheek_codes = new ArrayList<>();//限制6位
    public static List<base64_file_update> file_update = new ArrayList<>();
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
        server.createContext("/tup", tt -> {
            send_text(tt,"<p></p>    <form action=\"/update\" method=\"post\" enctype=\"multipart/form-data\">  \n" +
                    "        <label for=\"file\">选择文件:</label>  \n" +
                    "        <input type=\"file\" id=\"fileaa\" name=\"file\">  \n" +
                    "        <input type=\"submit\" value=\"上传\">  \n" +
                    "    </form>  ");
        });
        // 创建一个上下文处理器并添加到服务器
        server.createContext("/update", tt -> {
            url_get adds = new url_get();
            adds.press(tt.getRequestURI().toString());
            boolean checked = false;
            int ccid = -1;
            //stock 校验验证码,防止恶意上传
            for (int i = 0, cheekCodesSize = cheek_codes.size(); i < cheekCodesSize; i++) {
                cheek_code_use cheekCode = cheek_codes.get(i);
                System.out.println(adds.get("cid"));
                System.out.println(cheekCode.getP_UUID());
                if (Objects.equals(adds.get("cid"), cheekCode.getP_UUID())) {
                    System.out.println(cheekCode.check(adds.get("code")));
                    if (cheekCode.check(adds.get("code"))) {
                        checked = true;
                        ccid = i;
                    }
                    //cheekCode.ruin();
                }
            }
            clear_code();//垃圾清理
            if(!checked){
                send_text(tt,"-1");
                return;
            }
            //test
            if(cheek_codes.get(ccid) == null){
                send_text(tt,"-1");
                return;
            }
            if(adds.get("t") == "hash"){
                send_text(tt,file_update.get(ccid).getHash());
                return;
            }
            if (cheek_codes.get(ccid).isFirst_check()) {
                base64_file_update n = new base64_file_update();
                int s = file_update.size();
                file_update.add(n);
                file_update.get(s).init_data(Integer.parseInt(adds.get("blocks")), 2000);
                cheek_codes.get(ccid).checked();
                send_text(tt,"2");
                return;
            }
            if(file_update.get(ccid).append_data(Integer.parseInt(adds.get("i")),adds.get("d"))){
                send_text(tt,"0");
            }else {
                send_text(tt,"-1");
            }

        });
        server.createContext("/cheek_update", tt -> {
            String path = tt.getRequestURI().getPath();
            url_get adds = new url_get();
            adds.press(tt.getRequestURI().toString());
            System.out.println(path);
            cheek_code_use ck = new cheek_code_use();
            System.out.println(ck.init());//调试用,生产注释它!
            //ck.init();
            //转换成base64图片
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(ck.get_code_img(), "jpg", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            cheek_codes.add(ck);
            send_text(tt, "{\"CID\": \""+ck.getP_UUID()+"\",\"IM\":\""+base64Image+"\"}");
            //发送完数据,服务器清理已经失效的checker
            clear_code();
        });
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
    public static void clear_code(){
        for (int i = 0; i < cheek_codes.size(); i++) {
            if(cheek_codes.get(i).is_timeout()){
                cheek_codes.remove(i);
                clear_code();
                break;
            }
        }
    }
}
