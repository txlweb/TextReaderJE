package com.teipreader.reptile;

import com.teipreader.Main.Config_dirs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.teipreader.Main.Download.Dw_File;
import static com.teipreader.Main.TeipMakerLib.Unzip;
import static com.teipreader.Main.TeipMakerLib.autoMake;
import static com.teipreader.Main.TextReaderLibVa.ReadCFGFile;

public class Rule_biquzw789 {
    public static void addToThis(String ID) throws IOException {
        InitSrcCopy(ID);
        Dw_File(GetDownloadURL(ID),"tmp-dw.txt");
        Dw_File(GetImage(),"tmp.jpg");
        autoMake("tmp-dw.txt","tmd-dw.zip",GetTitle(),"tmp.jpg","",GetAuthor(),GetInfo());
        Unzip("tmd-dw.zip", Config_dirs.MainPath);
        //System.out.println(GetImage("470814"));
    }
    public static String Search(String key_word){
        return "http://www.biquzw789.net/s.php?searchkey="+key_word;
    }
    public static void InitSrcCopy(String ID) throws MalformedURLException {
        Dw_File(GetURL(ID),"tmp-hb.txt");
    }
    public static String GetTitle() throws MalformedURLException {//例如 470814
        //<div id="fmimg"><img
        List<String> lines =  ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<div id=\"fmimg\"><img") && line.contains("</div>")) {
                String[] a = line.split("\"");
                System.out.println(a[3]);
                return a[3];//3
            }
        }
        return "-";
    }
    public static String GetImage() throws MalformedURLException {//例如 470814
        //<div id="fmimg"><img
        List<String> lines =  ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<div id=\"fmimg\"><img") && line.contains("</div>")) {
                String[] a = line.split("\"");
                return a[5];//5
            }
        }
        return "-";
    }
    public static String GetAuthor() throws MalformedURLException {//例如 470814
        //<p>作&nbsp;&nbsp;&nbsp;&nbsp;者：
        List<String> lines =  ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<p>作&nbsp;&nbsp;&nbsp;&nbsp;者：") && line.contains("</p>")) {
                String[] a = line.split("：");
                String[] b = a[1].split("<");
                return b[0];//1
            }
        }
        return "-";
    }
    public static String GetInfo() throws MalformedURLException {//例如 470814
        //<p>作&nbsp;&nbsp;&nbsp;&nbsp;者：
        List<String> lines =  ReadCFGFile("tmp-hb.txt");
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            String line = lines.get(i);
            if (line.contains("<p style=\"text-align: center;font-size: 16px;color: red;\">笔趣阁原域名已被污染，请记住新域名http://www.biquzw789.net</p>")) {
                String[] a = lines.get(i+1).split(">");
                String[] b = a[1].split("<");
                return b[0];//1
            }
        }
        return "-";
    }
    public static String GetDownloadURL(String ID){//例如 470814
        //前三位提取,作为前部470814 -> 470/470814
        return "http://www.biquzw789.net/download/txt/"+ID+".txt";
    }
    public static String GetURL(String ID){//例如 470814
        //前三位提取,作为前部470814 -> 470/470814
        return "http://www.biquzw789.net/"+ID.substring(0,2)+"/"+ID+"/";
    }
}
