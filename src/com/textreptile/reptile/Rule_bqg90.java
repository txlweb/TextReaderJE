package com.textreptile.reptile;

import com.teipreader.Main.Config_dirs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.teipreader.Lib.Download.Dw_File;
import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;
import static com.teipreader.Main.TeipMake.Unzip;
import static com.teipreader.Main.TeipMake.userMake;

public class Rule_bqg90 {
    static String S_host = "https://www.bqg90.com";
    static String p = "{\"title\":\"没有任务\",\"now\":\"1\",\"max\":\"1\",\"m_tit\":\"未获取\",\"an\":\"未获取\",\"in\":\"未获取\",\"im\":\"/imgsrcs/?id=null\"}";

    public static void addToThis(String ID) throws IOException {
        InitSrcCopy(ID);
        Dw_File(GetImage(), "tmp.jpg");
        //1.获取目录列表
        List<List<String>> MainList = GetMainList();
        String Title = GetTitle();
        String Aunthor = GetAuthor();
        String Info = GetInfo();
        String Img = GetImage();
        //2.下载
        File file_txt = new File("./tmp_main_txt.txt");
        if (file_txt.exists()) {
            file_txt.delete();
            file_txt.createNewFile();
        } else file_txt.createNewFile();
        FileWriter fileWriter_txt = new FileWriter(file_txt.getName(), true);
        BufferedWriter bufferWriter_txt = new BufferedWriter(fileWriter_txt);
        long line_num = 0;
        long line_num_last = 0;
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
            index.append(MainList.get(i).get(1)).append("&D&").append(line_num_last).append("\r\n");
            line_num_last = line_num;
            System.out.println(p);
            p = "{\"title\":\""+MainList.get(i).get(1) + "\",\"now\":\"" + i + "\",\"max\":\"" + MainList.size() + "\",\"m_tit\":\""+Title+"\",\"an\":\""+Aunthor+"\",\"in\":\""+Info+"\",\"im\":\""+Img+"\"}";
        }
        bufferWriter_txt.close();
        //3.封装-加入
        userMake("tmp_main_txt.txt", "tmp-dw.zip", Title, "tmp.jpg", index.toString(), Aunthor, Info);
        Unzip("tmp-dw.zip", Config_dirs.MainPath);
        p = "{\"title\":\"任务完成\",\"now\":\"1\",\"max\":\"1\",\"m_tit\":\"未获取\",\"an\":\"未获取\",\"in\":\"未获取\",\"im\":\"/imgsrcs/?id=null\"}";
    }

    public static String Search(String key_word) throws MalformedURLException {
        //逆天!放着接口每个保护.放着让人爬!!
        Dw_File("https://www.bqg90.com/user/hm.html?q=","tmp-dw-req.txt");
        Dw_File("https://www.bqg90.com//user/search.html?q=","tmp-dw-req.txt");
        return "./tmp-dw-req.txt";
    }

//https://www.bqg90.com/user/hm.html?q=%E5%B9%B4
    public static String pst() {
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
                return Arrays.asList(a[1].split("<br />"));
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
        for (String line : lines) {
            if (line.contains("<dd>")) {
                String[] a = line.split("<dd>");
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
                if (url_title != null) url_title.add(S_host + b[0]);
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
