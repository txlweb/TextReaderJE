package com.teipreader.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.teipreader.Main.TextReaderLibVa.IsFile;
import static com.teipreader.Main.TextReaderLibVa.ReadCFGFile;

public class WebServer extends Thread implements Main{
    @Override
    public void run() {
        StartServer();
    }
    /**
     * @throws IOException
     */
    @Override
    public void runShare() throws IOException {
        StartServer();
    }

    /**
     * @throws IOException
     */
    @Override
    public void runServer() throws IOException {
        StartServer();
    }

    public static void StartServer() {
        int port = Config_dirs.NormPort;
        String root = Config_dirs.StylePath;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println((char) 27 + "[34m[I]: 网页服务正运行在" + port + "上 (http://127.0.0.1:" + port + ")" + (char) 27 + "[39;49m");
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
            //String method = parts[0];
            String path = parts[1];
            String fullPath = root + path;
            boolean IsSendData = false;
            StringBuilder RET_HTML = new StringBuilder();
            if(Config_dirs.Use_Server_LOG_DEBUG) System.out.println((char) 27 + "[33m[Server]:" + path + (char) 27 + "[39;49m");
            if (path.contains("/imgsrcs/?id=")) {
                String[] a = path.split("=");
                fullPath = URLDecoder.decode(a[1], StandardCharsets.UTF_8) + "/icon.jpg";
                //System.out.println(fullPath);         
                if (!new File(fullPath).isFile()) {
                    fullPath = URLDecoder.decode(a[1], StandardCharsets.UTF_8) + "/icon.png";
                    //System.out.println(fullPath);
                }
                if (!new File(fullPath).isFile()) {
                    fullPath = root + "/noimg.png";
                }
                //System.out.println(fullPath);
            }
            if (path.contains("/list.html")) {
                String[] a = path.split("/");
                RET_HTML = new StringBuilder("<h1 id=\"title\">章节目录</h1>");
                String xx = "";
                if (IsFile(Config_dirs.MainPath+"/"+URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"/resource.ini")){
                    xx = IniLib.GetThing(Config_dirs.MainPath+"/"+URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"/resource.ini","conf","title");
                }else {
                    xx = URLDecoder.decode(a[1], StandardCharsets.UTF_8);
                }
                RET_HTML.append("<a href=\"/api/dwtxt/").append(xx).append(".txt?").append(URLDecoder.decode(a[1], StandardCharsets.UTF_8)).append("\">[下载本小说]</a><br>");
                RET_HTML.append(TextReaderLibVa.GetList_HTML_TYPE(URLDecoder.decode(a[1], StandardCharsets.UTF_8)));
                if (!path.contains("?NOUI")) {
                    RET_HTML = new StringBuilder(ServerLibVa.AddTitle(RET_HTML.toString()));
                }
                if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]:"+ URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"@List" + (char) 27 + "[39;49m");
                IsSendData = true;
            }
            if (path.contains("/api/openFile/")) {
                String[] a = path.split("\\?");
                RET_HTML = new StringBuilder(ImportFileLib.GetFileList(URLDecoder.decode(a[1], StandardCharsets.UTF_8)));
                IsSendData = true;
            }
            if (path.contains("/api/include/?")) {
                String[] a = path.split("\\?");
                TeipMakerLib.Unzip(URLDecoder.decode(a[1], StandardCharsets.UTF_8),Config_dirs.MainPath);
                RET_HTML = new StringBuilder("Complete to import file.");
                IsSendData = true;
            }
            if (path.contains("/api/mktxt/?")) {
                String[] a = URLDecoder.decode(path, StandardCharsets.UTF_8).split("\\?");
                TeipMakerLib.autoMake(a[1],"tmp.zip",a[2],a[3],a[4],a[5],a[6]);
                TeipMakerLib.Unzip("tmp.zip",Config_dirs.MainPath);
                RET_HTML = new StringBuilder(String.format("Complete to import file. parameters:%s,%s,%s,%s", a[1], a[2], a[3], a[4]));
                IsSendData = true;
            }
            if (path.contains("/api/webReq/?")) {
                String[] a = URLDecoder.decode(path, StandardCharsets.UTF_8).split("\\?");
                Download.Dw_File(a[1],"tmp.json");
                List<String> lines =  ReadCFGFile("tmp.json");
                for (String line : lines) {
                    RET_HTML.append(line);
                }
                System.out.println(RET_HTML);
                IsSendData = true;
            }
            if (path.contains("/api/webDL_biquzw789/?")) {
                String[] a = URLDecoder.decode(path, StandardCharsets.UTF_8).split("\\?");
                com.teipreader.reptile.Rule_biquzw789.addToThis(a[1]);
                RET_HTML = new StringBuilder("Complete to import file.");
                IsSendData = true;
            }
            if (path.contains("/api/web_dl/?")) {
                String[] a = URLDecoder.decode(path, StandardCharsets.UTF_8).split("\\?=");
                Download.Dw_File(a[1],"tmp.teip");
                TeipMakerLib.Unzip("tmp.teip",Config_dirs.MainPath);
                RET_HTML = new StringBuilder("Complete to import file from URL"+a[1]+".");
                IsSendData = true;
            }
            Pattern compile = Pattern.compile(".*/[0-9].*.html.*");
            java.util.regex.Matcher matcher = compile.matcher(path);
            if (matcher.matches()) {
                String[] a = path.split("/");
                String[] b = a[2].split(".html");
                if (path.contains("?")) {
                    RET_HTML = new StringBuilder(TextReaderLibVa.GetMainText_C(URLDecoder.decode(a[1], StandardCharsets.UTF_8), Integer.parseInt(b[0].replace(",", ""))));
                } else {
                    RET_HTML = new StringBuilder(TextReaderLibVa.GetMainText_HTML_TYPE(URLDecoder.decode(a[1], StandardCharsets.UTF_8), Integer.parseInt(b[0].replace(",", ""))));
                    RET_HTML = new StringBuilder(ServerLibVa.AddTitle(RET_HTML.toString()));
                }
                if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]:"+ URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"@" + Integer.parseInt(b[0].replace(",","")) + (char) 27 + "[39;49m");
                IsSendData = true;
            }
            Pattern compile1 = Pattern.compile(".*/.*/.*.res");
            java.util.regex.Matcher matcher1 = compile1.matcher(path);
            if (matcher1.matches()) {
                String[] a = path.split("/");
                fullPath = Config_dirs.MainPath+"/"+URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"/"+URLDecoder.decode(a[2], StandardCharsets.UTF_8);
                if (!new File(fullPath).isFile()) fullPath = root + "/noimg.png";
            }
            if (path.contains("/api/dwtxt/") && !IsSendData) {
                String[] a = path.split("\\?");
                if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]:为小说建立文档@"+ URLDecoder.decode(a[1], StandardCharsets.UTF_8)+"@txt" + (char) 27 + "[39;49m");
                if(new File(Config_dirs.MainPath+"/"+URLDecoder.decode(a[1], StandardCharsets.UTF_8)).exists()){
                    TeipMakerLib.GetTextWithThis(URLDecoder.decode(a[1], StandardCharsets.UTF_8));
                    sendFile(out, new File("main.txt"));//goto end
                    in.close();
                    out.close();
                    client.close();
                    return;
                }else {
                    if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]: [E]: 不能为不存在的小说建立文档." + (char) 27 + "[39;49m");
                    RET_HTML = new StringBuilder("[E]: 没有这个文章可以下载!");
                }
                IsSendData = true;
            }
            if (path.contains("/app") && !IsSendData) {
                RET_HTML = new StringBuilder("<h1 id=\"title\">小说列表</h1>");
                if (path.contains("?")) {
                    RET_HTML.append(TextReaderLibVa.PathScan(true));
                }else{
                    RET_HTML.append(TextReaderLibVa.PathScan(false));
                }
                RET_HTML = new StringBuilder(ServerLibVa.AddTitle(RET_HTML.toString()));
                if(Config_dirs.Use_Server_LOG) System.out.println((char) 27 + "[33m[Server]:"+ " 主页" + (char) 27 + "[39;49m");

                IsSendData = true;
            }
            if (path.contains("false")) {
                RET_HTML = new StringBuilder();
                IsSendData = true;
            }

            File file = new File(fullPath);
            if (file.exists() && !IsSendData) {
                if (file.isFile()) {
                    sendFile(out, file);
                } else if (file.isDirectory()) {
                    //sendDirectory(out, file);
                    sendText(out, "<meta http-equiv=\"refresh\" content=\"0;url=/app\">");
                }
            } else {
                if (IsSendData) {
                    if (Objects.equals(RET_HTML.toString(), "")) {
                        sendText(out, "<h1>404 Error</h1>");
                        return;
                    } else {
                        sendText(out, RET_HTML.toString());
                    }

                } else {
                    sendText(out, "<meta http-equiv=\"refresh\" content=\"0;url=/app\">");
                }

            }
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
        String header = "Content-Type: " + contentType + "\r\n" + "Content-Length: " + data.length + "\r\n"
                + "Connection: close\r\n\r\n";
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
