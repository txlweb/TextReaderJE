package com.teipreader.Lib;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class post_file_mgr {
    private List<byte[]> FileList = new ArrayList<>();//filename,data
    private List<String> FileNameList = new ArrayList<>();//k,v
    public List<byte[]> getFileData(){
        return this.FileList;
    }
    public List<String> getFileList(){
        return this.FileNameList;
    }
    public boolean press(HttpExchange t){
        String method = t.getRequestMethod();
        String contentType;
        try {
            if (!"POST".equalsIgnoreCase(method)) {
                t.sendResponseHeaders(405, -1); // Method Not Allowed
                return false;
            }

            // 检查Content-Type是否为multipart/form-data
            contentType = t.getRequestHeaders().getFirst("Content-Type");
            if (contentType == null || !contentType.startsWith("multipart/form-data")) {
                t.sendResponseHeaders(415, -1); // Unsupported Media Type
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean isFilePart = false;
        String headerName = null;
        String fileName = null;
        try{
            InputStream requestBody = t.getRequestBody();
            //从客户端读取请求体
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            ByteArrayOutputStream file_save = new ByteArrayOutputStream();

            String boundary = "--" + contentType.substring(contentType.indexOf('=') + 1);
            System.out.println(boundary);
            String last_line = "";
            while ((bytesRead = requestBody.read(buffer)) != -1) {
                String lines = new String(buffer,0,bytesRead, StandardCharsets.UTF_8);
                String[] lx = lines.split("\r\n");
                System.out.println(lx.length);
                for (String line : lx) {

                    //System.out.println(line);
                    //System.out.println("--");
                    if (line.startsWith("Content-Disposition: form-data; name=\"file\"; filename=\"")) {
                        // 找到文件部分的开始
                        isFilePart = true;
                        fileName = line.substring(line.indexOf("filename=\"") + 10, line.lastIndexOf("\""));
                        System.out.println(line);
                        file_save = new ByteArrayOutputStream();
                    }
                    if (line.equals("")) {
                        System.out.println(line);
                        // 如果前面是文件部分的开始，现在开始读取文件内容
                        if (isFilePart) {
                            file_save.reset();
                            continue;
                        }
                    } else if (isFilePart) {
                        // 读取文件内容
                        if(!Objects.equals(line, boundary + "--")) {
                            file_save.write(buffer, 0, buffer.length);
                        }
                        //file_save.reset();
                    }
                    //System.out.println(Objects.equals(line, boundary + "--"));
                    if (isFilePart && Objects.equals(line, boundary + "--")) {
                        // 文件部分结束
                        isFilePart = false;
                        this.FileList.add(file_save.toByteArray());
                        this.FileNameList.add(fileName);
                    }
                    //req_save.write(buffer, 0, bytesRead);
                    last_line = line;
                }
                //file_save.write(("\r").getBytes(), 0, ("\r").getBytes().length);
            }
            if (isFilePart) {
                // 文件部分结束
                isFilePart = false;
                this.FileList.add(file_save.toByteArray());
                this.FileNameList.add(fileName);

            }

            //RequestBody = req_save.toByteArray();
            //解析请求
            //先转成文本(发送的头是utf-8,只要前1024字节内的为解析头)
            //String Header =
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
            //return false;
        }
        //return false;
    }
}
