package com.teipreader.LibTextParsing;

import com.teipreader.Lib.EncodingDetect;
import com.teipreader.Lib.IniLib;
import com.teipreader.Main.Config_dirs;
import com.teipreader.Main.langunges;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.teipreader.LibTextParsing.TextReaderLibVa.IsFile;
import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;

public class TextReaderLibVb {
    public static String MainPath = Config_dirs.MainPath;



    public static String GetList_HTML_TYPE(String name) {
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/main.index");//读列表
        String[] TList = null;
        int n = List.size();
        String LsHTML = "";
        if (IsFile(MainPath + "/" + name + "/resource.ini")) {
            String ot = IniLib.GetThing(MainPath + "/" + name + "/resource.ini", "conf", "ot");
            String by = IniLib.GetThing(MainPath + "/" + name + "/resource.ini", "conf", "by");
            String tit = IniLib.GetThing(MainPath + "/" + name + "/resource.ini", "conf", "title");
            if (ot != null && by != null && tit != null) {
                LsHTML = "<h1>" + tit + "</h1><p>作者: " + by + "</p><p>简介: " + ot + "</p>";
            }
        }
        for (int i = 0; i < n; i++) {
            TList = List.get(i).split("&D&");//标题&D&起始行&D&结束行
            if (TList.length == 2) {
                LsHTML = MessageFormat.format("{0}<a class=\"book_list\" href=\"/{1}/{2}.html\" idx=\"{3}\">{4}{5}{6}{7}</a>", LsHTML, name, i + 1, i + 1, langunges.langunges[Config_dirs.LanguageID][4], i + 1, langunges.langunges[Config_dirs.LanguageID][5], TList[0]);
            }
        }
        return LsHTML.replace(",", "");
    }

    public static String GetMainText_HTML_TYPE(String name, int id) {//id只吃int
        return TextReaderLibVa.StrFixMainText(GetMainText_C(name, id), name, id, GetMaxTexts(name));
    }

    public static String GetMainText_C(String name, int id) {//id只吃int
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/main.txt");
        List<String> List1 = ReadCFGFile(MainPath + "/" + name + "/main.index");
        if (List1.size() < id) return "";
        String[] TList = List1.get(id - 1).split("&D&");//标题&D&起始行
        String[] TList2 = null;
        if (List1.size() > id + 1) {
            TList2 = List1.get(id).split("&D&");
        } else {
            TList2 = new String[2];
            TList2[1] = String.valueOf(List.size());
        }
        StringBuilder LsHTML = new StringBuilder("<h2>"+TList[0]+"</h2>");
        //添加换行
        System.out.println(Arrays.toString(TList2));
        for (int i = Integer.parseInt(TList[1]); i < Integer.parseInt(TList2[1]); i++) {
            String s = List.get(i);
            LsHTML.append("<p>").append(s).append("</p>");
        }
        return LsHTML.toString();
    }

    public static int GetMaxTexts(String name) {
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/main.index");//读列表
        return List.size();//没有证明文件损坏
    }
}
