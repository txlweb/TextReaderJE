package com.teipreader.share;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

public class WebServer {
    public static void StartServer() {
        int port = Config_dirs.NormPort;
        String root = "./www/";
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println((char) 27 + "[34m[I]: 分享服务正运行在" + port + "上 (http://127.0.0.1:" + port + ")" + (char) 27 + "[39;49m");
            while (true) {
                Socket client = server.accept();
                RequestHandler handler = new RequestHandler(client, root);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class RequestHandler implements Runnable {
    private final Socket client;
    private final String root;

    public RequestHandler(Socket client, String root) {
        this.client = client;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();
            String requestLine = in.readLine();
            String[] parts = requestLine.split(" ");
            String path = parts[1];
            String fullPath = root + path;
            String RET_HTML = "";
            if(Config_dirs.Use_Server_LOG_DEBUG) System.out.println((char) 27 + "[33m[Server]:" + path + (char) 27 + "[39;49m");
            if (path.contains("/list.json")) {
                RET_HTML = TeipIO.PathScan();
            }
            if(path.contains("/dl/")){
                String[] a = path.split("\\?");
                if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]:为小说建立文档@"+ URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"@zip" + (char) 27 + "[39;49m");
                if(new File(Config_dirs.MainPath+"/"+URLDecoder.decode(a[1], StandardCharsets.UTF_8)).exists()) {
                    TeipIO.GetZip(URLDecoder.decode(a[1], StandardCharsets.UTF_8));
                    sendFile(out, new File("tmp.zip"));//goto end
                    in.close();
                    out.close();
                    client.close();
                    return;
                }else{
                    if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]: [E]: 不能为不存在的小说建立文档." + (char) 27 + "[39;49m");
                    RET_HTML = "[E]: 没有这个文章可以下载!";
                }
            }
            if(RET_HTML.isEmpty()){
                RET_HTML = "<meta charset=\"UTF-8\">"+
                        "<h1>Open Teip Share Server</h1>"+
                        "<h2>开放teip共享平台节点为您服务</h2>"+
                        "<p>感谢您为共享开放平台做出的共献! 您打开了这个功能就是在为社区添砖加瓦!</p>"+
                        "<p>正是因为有许多像您一样的具有共享精神的人士参与到社区中,才使得社区具备了活力!</p>"+
                        "<p>独乐乐不如众乐乐,如果您拥有的书源丰富,而且可以较长时间在线且具有公网IP的话可以在github上为项目中的/style/API_list.json添砖加瓦!"+
                        "<p>期待您的加入,在此,IDlike对每一个启用这项功能的人表示感谢!</p>"+
                        "<p>如果您是这项功能的受益者,请不要忘记感谢提供服务的人,或是为他们提供一些帮助,/style/API_list.json内他们的留言.</p>"
                ;
            }
            sendText(out, RET_HTML);
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(OutputStream out, File file) throws IOException {
        String contentType = guessContentType(file.getName());
        byte[] data = readBytes(file);
        sendResponse(out, 200, "OK", contentType, data);
    }

    private void sendText(OutputStream out, String datas) throws IOException {

        byte[] data = datas.getBytes();
        sendResponse(out, 200, "OK", "text/html", data);
    }

    private void sendError(OutputStream out, int statusCode, String statusText) throws IOException {
        String contentType = "text/html";
        String content = "<html><head><title>" + statusCode + " " + statusText + "</title></head><body><h1>"
                + statusCode + " " + statusText + "</h1></body></html>";
        byte[] data = content.getBytes();
        sendResponse(out, statusCode, statusText, contentType, data);
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, String contentType, byte[] data)
            throws IOException {
        String statusLine = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n";
        String header = "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + data.length + "\r\n"
                +"Access-Allow-Control-Origin: * \r\n"+
                "Connection: close\r\n"+
                "\r\n";
        out.write(statusLine.getBytes());
        out.write(header.getBytes());
        out.write(data);
    }

    private String guessContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

    private byte[] readBytes(File file) throws IOException {
        byte[] buffer = new byte[(int) file.length()];
        try (FileInputStream input = new FileInputStream(file)) {
            input.read(buffer);
        }
        return buffer;
    }
}
