package com.textreptile.reptile;

import com.teipreader.Main.Config_dirs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static com.teipreader.Lib.Download.Dw_File;
import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;
import static com.teipreader.Main.TeipMake.*;

public class Rule_bqg90 {
    static String S_host = "https://www.bqg90.com";
    static String p = "";
    public static void addToThis(String ID) throws IOException {
        InitSrcCopy(ID);
        Dw_File(GetImage(),"tmp.jpg");
        //1.获取目录列表
        List<List<String>> MainList = GetMainList();
        //2.下载
        File file_txt = new File("./tmp_main_txt.txt");
        if (file_txt.exists()) {
            file_txt.delete();
            file_txt.createNewFile();
        } else file_txt.createNewFile();
        FileWriter fileWriter_txt = new FileWriter(file_txt.getName(), true);
        BufferedWriter bufferWriter_txt = new BufferedWriter(fileWriter_txt);
        long line_num = 0;
        StringBuilder index = new StringBuilder();
        for (int i = 0; i < MainList.size(); i++) {
            System.out.println("dw: " + i + "/" + MainList.size());
            //更新文本
            List<String> MT = GetMainText(MainList.get(i).get(0));
            if (MT != null) {
                line_num++;
                bufferWriter_txt.write(MainList.get(i).get(1));
                for (String s : MT) {
                    line_num++;
                    bufferWriter_txt.write(s + "\r\n");
                }
            }
            //更新目录
            index.append(MainList.get(i).get(1)).append("&D&").append(line_num).append("\r\n");
            System.out.println(p);
            p = MainList.get(i).get(1) + " (" + i + "/" + MainList.size()+")";
        }
        bufferWriter_txt.close();
        //3.封装-加入
        userMake("tmp_main_txt.txt", "tmp-dw.zip", GetTitle(), "tmp.jpg", index.toString(), GetAuthor(), GetInfo());
        Unzip("tmp-dw.zip", Config_dirs.MainPath);
        p="已经完成.";
    }

    public static String Search(String key_word) throws MalformedURLException {
        //逆天!放着接口每个保护.放着让人爬!!
        return S_host + "/user/search.html?q=" + key_word;
    }
    public static String pst(){
        return p;
    }
    public static void InitSrcCopy(String ID) throws MalformedURLException {
        Dw_File(GetURL(ID), "tmp-hb.txt");
    }

    public static String GetTitle() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<h1>") && line.contains("</h1>")) {
                String[] a = line.split(">");
                String[] b = a[1].split("<");
                System.out.println(b[0]);
                return b[0];//3
            }
        }
        return "-";
    }
    public static List<String> GetMainText(String URL) throws MalformedURLException {
        Dw_File(URL, "tmp-tx.txt");
        List<String> lines = ReadCFGFile("tmp-tx.txt");
        for (String line : lines) {
            if (line.contains("<div id=\"chaptercontent\" class=\"Readarea ReadAjax_content\">")) {
                String[] a = line.split("\">");
                return List.of(a[1].split("<br />"));
            }
        }
        return null;
    }

    public static String GetImage() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<img src=\"") && line.contains("</div>")) {
                String[] a = line.split("\"");
                return a[3];
            }
        }
        return "-";
    }

    public static String GetAuthor() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<span>") && line.contains("</span>")) {
                String[] a = line.split("：");
                String[] b = a[1].split("<");
                return b[0];//1
            }
        }
        return "-";
    }

    public static String GetInfo() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            String line = lines.get(i);
            if (line.contains("<dd>")) {
                String[] a = lines.get(i).split("<dd>");
                String[] b = a[1].split("<");
                return b[0];//1
            }
        }
        return "-";
    }
    //<<URL,title>,<URL,title>,...>
    public static List<List<String>> GetMainList() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        List<List<String>> ret = new ArrayList<>();
        List<String> url_title = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("<dd><a") && !line.contains("<p>")) {
                String[] a = line.split("<dd><a href =\"");
                String[] b = a[1].split("\"");
                if (url_title != null) url_title.add(S_host+b[0]);
                a = line.split(">");
                b = a[2].split("<");
                if (url_title != null) url_title.add(b[0]);
                if (ret != null) ret.add(url_title);
                url_title = new ArrayList<>();
            }
        }
        //return "-";
        return ret;
    }
    public static String GetURL(String ID) {
        return S_host + "/book/" + ID + "/";
    }
}
