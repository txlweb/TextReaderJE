package com.textreptile.reptile;

import com.teipreader.Lib.Download;
import com.teipreader.Main.Config_dirs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.teipreader.Lib.Download.Dw_File;
import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;
import static com.teipreader.Main.TeipMake.Unzip;
import static com.teipreader.Main.TeipMake.userMake;
import static com.textreptile.reptile.Rule_bqg9x.GetMainText;

public class Rule_bqg9x {
    public static float p = 0.00f;

    public static void addToThis(String URL,String Title,String Aunthor,String Info,String img) throws IOException {
        Dw_File(img,"tmp.jpg");
        //1.获取目录列表
        List<List<String>> MainList = GetMainList(URL);
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
            p= (float) i /MainList.size()*100;
            //更新文本
            List<String> MT = GetMainText(MainList.get(i).get(0));
            if (MT != null) {
                line_num++;
                bufferWriter_txt.write(MainList.get(i).get(1) + "\r\n");
                for (String s : MT) {
                    line_num++;
                    bufferWriter_txt.write(s + "\r\n");
                }
            }
            //更新目录

            index.append(MainList.get(i).get(1)).append("&D&").append(line_num_last).append("\r\n");
            line_num_last = line_num;
        }
        bufferWriter_txt.close();
        //3.封装-加入
        userMake("tmp_main_txt.txt", "tmp-dw.zip", Title, "tmp.jpg", index.toString(), Aunthor, Info);
        Unzip("tmp-dw.zip", Config_dirs.MainPath);
    }

    public static String Search(String key_word) throws MalformedURLException {
        //逆天!放着接口每个保护.放着让人爬!!
        Dw_File("https://novel-api.xiaoppkk.com/h5/search?word="+key_word,"tmp-dw-req.txt");
        //先pre
        //开始符 <ul class="weui-flex books-list">
        List <String> prep = ReadCFGFile("tmp-dw-req.txt");
        List <String> l_img = new ArrayList<>();
        List <String> l_tit = new ArrayList<>();
        List <String> l_inf = new ArrayList<>();
        List <String> l_hrf = new ArrayList<>();
        List <List <String>> l_typ = new ArrayList<>();
        boolean is_start = false;
        boolean is_inf_end = false;
        for (String s : prep) {
            if (s.contains("<ul class=\"weui-flex books-list\">")) {
                is_start = !is_start;
            }
            if (is_start) {
                if(s.contains("<a href=\"")){
                    l_hrf.add(s.split("\"")[1]);
                }
                if(s.contains("<img src=\"")){
                    l_img.add(s.split("\"")[1]);
                }
                if(s.contains("<h3 class=\"weui-flex__item\">")){
                    l_tit.add(s.split(">")[1].split("<")[0]);
                }
                if(s.contains("<p class=\"type\">")){
                    String[] t = s.split("<span>");
                    List <String> tl = new ArrayList<>();
                    for (int i = 0; i < t.length; i++) {
                        if(i!=0){
                            tl.add(t[i].split("</span>")[0]);
                        }
                    }
                    l_typ.add(tl);
                }
                if(s.contains("<span>") && !s.contains("<p class=\"type\">")){

                    if(!s.contains("</span>")) is_inf_end = true;
                    l_inf.add(s.split("<span>")[1].split("</span>")[0]);
                }
                if(is_inf_end){
                    l_inf.set(l_inf.size()-1,l_inf.get(l_inf.size()-1)+s);
                    if(s.contains("</span>")) is_inf_end = false;
                }
            }
        }
//        System.out.println(l_img.size());
//        System.out.println(l_hrf.size());//这里数量是正常的2倍,需要隔一个消除一个
//        System.out.println(l_inf.size());
//        System.out.println(l_tit.size());
//        System.out.println(l_typ.size());
        //格式化为json
        StringBuilder buffer_json = new StringBuilder();
        //if(l_img.size() - l_hrf.size() + l_inf.size() - l_tit.size() + l_typ.size() + l_img.size() == 0){//确保都是一样长的.否则是不对的
            for (int i = 0; i < l_img.size(); i++) {
                buffer_json.append("<div class=\"rdblk\" onclick=\"d('https://novel-api.xiaoppkk.com/").append(l_hrf.get(i * 2).replace("-id", "-catalog-id")).append("','").append(l_tit.get(i)).append("','").append(l_typ.get(i).get(0)).append("','").append(l_inf.get(i).replace(",", "")
                        .replace("]", "")
                        .replace("[", "")
                        .replace("\"", "")
                        .replace(":", "")
                        .replace("<", "")
                        .replace(">", "")).append("','").append(l_img.get(i)).append("')\"><img src=\"").append(l_img.get(i)).append("\"><div class=\"rsblk\"><h3>").append(l_tit.get(i)).append("</h3><p>简介:").append(l_inf.get(i).replace(",", "")
                        .replace("]", "")
                        .replace("[", "")
                        .replace("\"", "")
                        .replace(":", "")
                        .replace("<", "")
                        .replace(">", "")).append("</p><p>作者: ").append(l_typ.get(i).get(0)).append("</p></div></div>");
            }
        //}

        System.out.println(buffer_json);
        return buffer_json.toString();
    }


    public static void main(String[] args) throws MalformedURLException {
        //Search("我是");
        System.out.println(GetMainList("https://novel-api.xiaoppkk.com/h5/book-catalog-id-5018"));
    }
    public static String GetTitle() throws MalformedURLException {
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        for (String line : lines) {
            if (line.contains("<h4 class=\"title\">") && line.contains("</h4>")) {
                String[] a = line.split(">");
                String[] b = a[1].split("<");
                System.out.println(b[0]);
                return b[0];//3
            }
        }
        return "-";
    }
    static long mt_id = 0;
    public static List<String> GetMainText(String URL) throws MalformedURLException {
        mt_id++;
        long mt_gl_id = mt_id;
        Dw_File(URL, "tmp-tx.txt");
        List<String> lines = ReadCFGFile("tmp-tx.txt");
        List<String> r = new ArrayList<>();
        boolean b1 = false;
        String v1 = "";
        for (String line : lines) {
            if (line.contains("<section>")) {
                b1 = true;
            }
            if(line.contains("<p class=\"foot_nav\">")){
                b1 = false;
            }
            if(b1){
               r.add(line.replace("<p>","").replace("</p>","").replace("&nbsp;",""));
            }
        }
        return r;
    }

    //<<URL,title>,<URL,title>,...>
    public static List<List<String>> GetMainList(String URL) throws MalformedURLException {
        Dw_File(URL, "tmp-hb.txt");
        List<String> lines = ReadCFGFile("tmp-hb.txt");
        List<List<String>> ret = new ArrayList<>();
        List<String> url_title = new ArrayList<>();
        boolean v1 = false;
        for (String line : lines) {
            if (line.contains("<div class=\"page\" style=\"margin-top: 1.2rem;\">")) {
                v1 = true;
            }
            if(line.contains("</div>")){
                v1 = false;
            }
            if(v1){
                if(line.contains("<li><a href=\"")){
                    url_title = new ArrayList<>();
                    url_title.add("https://novel-api.xiaoppkk.com/"+line.split("\"")[1]);
                    url_title.add(line.split("<span>")[1].split("</span>")[0]);
                    ret.add(url_title);
                }
            }
        }
        //return "-";
        return ret;
    }
}
