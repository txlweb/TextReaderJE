package com.teipreader.LibTextParsing;

import com.teipreader.Lib.EncodingDetect;
import com.teipreader.Lib.IniLib;
import com.teipreader.Main.Config_dirs;
import com.teipreader.Main.langunges;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextReaderLibVa {
    public static String MainPath = Config_dirs.MainPath;

    public static void allClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> ReadCFGFile(String strFilePath) {
        File file = new File(strFilePath);
        List<String> rstr = new ArrayList<>();
        if (!file.exists() || file.isDirectory()) {
            System.out.println((char) 27 + "[31m[E]: 找不到文件." + (char) 27 + "[39;49m");
        } else {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, EncodingDetect.getJavaEncode(strFilePath));
                bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    rstr.add(str);
                }
                return rstr;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                allClose(bufferedReader, inputStreamReader, fileInputStream);
            }
        }
        return rstr;
    }

    public static boolean IsFile(String File_name) {
        return new File(File_name).exists();
    }

    public static String GetList_HTML_TYPE(String name) {
        //LibVb
        if (IsFile(MainPath + "/" + name + "/main.index")) return TextReaderLibVb.GetList_HTML_TYPE(name);
        //断点-如果是LibEPUB处理则直接返回
        if (IsFile(MainPath + "/" + name + "/main.epub")) return TextReaderLibVc.GetList(name);
        //LibVa
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/list.info");//读列表
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
            //生成列表
            LsHTML = MessageFormat.format("{0}<a class=\"book_list\" href=\"/{1}/{2}.html\" idx=\"{3}\">{4}{5}{6}{7}</a>", LsHTML, name, i + 1, i + 1, langunges.langunges[Config_dirs.LanguageID][4], i + 1, langunges.langunges[Config_dirs.LanguageID][5], List.get(i));
        }
        return LsHTML.replace(",", "");
    }

    public static String GetMainText_HTML_TYPE(String name, int id) {//id只吃int
        //断点-如果是LibVb处理则直接返回
        if (IsFile(MainPath + "/" + name + "/main.index")) return TextReaderLibVb.GetMainText_HTML_TYPE(name, id);
        //断点-如果是LibEPUB处理则直接返回
        if (IsFile(MainPath + "/" + name + "/main.epub")) return TextReaderLibVc.GetMainText_HTML_TYPE(name, id);
        //LibVa处理方法
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/" + id + ".txt");//逐行读(不能用/n,这样显示会没有换行)
        StringBuilder LsHTML = new StringBuilder();
        //添加换行
        for (String s : List) LsHTML.append(s).append("<br/>");
        return StrFixMainText(LsHTML.toString(), name, id, GetMaxTexts(name));
    }


    public static String GetMainText_C(String name, int id) {//id只吃int
        //LibVb
        if (IsFile(MainPath + "/" + name + "/main.index")) return TextReaderLibVb.GetMainText_C(name, id);
        //断点-如果是LibEPUB处理则直接返回
        if (IsFile(MainPath + "/" + name + "/main.epub")) return TextReaderLibVc.GetMainText_C(name, id);

        //LibVa
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/" + id + ".txt");
        StringBuilder LsHTML = new StringBuilder();
        //添加换行
        for (String s : List) LsHTML.append(s).append("<br/>");
        return LsHTML.toString();
    }

    //修补主要文本方式!
    public static String StrFixMainText(String MainText, String name, int id, int maxid) {
        String Fixd;
        Fixd = MessageFormat.format("<br><br><bar><a class=\"book_block\" href=\"/{0}/{1}.html\" id=\"last\">{2}</a>", name, id - 1, langunges.langunges[Config_dirs.LanguageID][1]);
        Fixd = MessageFormat.format("{0}<a class=\"book_block\" onclick=\"vwlist(''{1}/list.html'')\">{2}</a>", Fixd, name, langunges.langunges[Config_dirs.LanguageID][0]);
        //Fixd = MessageFormat.format("{0}<a href=\"/{1}/list.html\">目录</a>", Fixd, name);
        Fixd = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/{2}.html\" id=\"next\">{3}</a></bar>", Fixd, name, id + 1, langunges.langunges[Config_dirs.LanguageID][2]);
        Fixd = MessageFormat.format("{0}<div id=\"maintext\">{1}</div>", Fixd, MainText);
        Fixd = MessageFormat.format("{0}<bar><a class=\"book_block\" href=\"/{1}/{2}.html\">{3}</a>", Fixd, name, id - 1, langunges.langunges[Config_dirs.LanguageID][1]);
        Fixd = MessageFormat.format("{0}<a class=\"book_block\" onclick=\"vwlist(''{1}/list.html'')\">{2}</a>", Fixd, name, langunges.langunges[Config_dirs.LanguageID][0]);
        //Fixd = MessageFormat.format("{0}<a href=\"/{1}/list.html\">目录</a>", Fixd, name);
        Fixd = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/{2}.html\">{3}</a></bar>", Fixd, name, id + 1, langunges.langunges[Config_dirs.LanguageID][2]);
        Fixd = MessageFormat.format("{0}<br><pointer id=\"poin\" now=\"{1}\" max=\"{2}\"></pointer>", Fixd, id, maxid);
        return Fixd.replace(",", "");
    }

    public static String PathScan(boolean is_vh, String key) {
        File file = new File(MainPath);
        String Blist = "";
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        if (!IsHidden(value.getName()) | is_vh) {
                            if (!IsFile(MainPath + "/" + value.getName() + "/resource.ini")) {
                                if (value.getName().contains(key)) {
                                    if (!IsFile(MainPath + "/" + value.getName() + "/main.epub"))
                                        Blist = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/list.html\"><img class=\"ticon\" res=\"/imgsrcs/?id={2}\"><br>{3}</a>", Blist, value.getName(), value.getPath(), value.getName());
                                    else if(TextReaderLibVc.IsEpubFile(MainPath + "/" + value.getName() + "/main.epub"))
                                        Blist = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/list.html\"><img class=\"ticon\" res=\"/imgsrcs/?id={2}\"><br>{3}</a>", Blist, value.getName(), value.getPath(), TextReaderLibVc.GetName(MainPath + "/" + value.getName() + "/main.epub"));

                                }
                            } else {
                                if (Objects.equals(key, "")) {
                                    Blist = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/list.html\"><img class=\"ticon\" res=\"/imgsrcs/?id={2}\"><br>{3}</a>", Blist, value.getName(), value.getPath(), IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title"));
                                } else {
                                    String things = IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title") + IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "by") + IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "ot");
                                    //标题+作者+简介 或 md5真值完全一致
                                    if (things.contains(key) || value.getPath().equals(key)) {
                                        if (IsFile(MainPath + "/" + value.getName() + "/type_pdf.lock"))
                                            Blist = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/list.html\"><img class=\"ticon\" res=\"/imgsrcs/?id={2}\"><br>[PDF]{3}</a>", Blist, value.getName(), value.getPath(), IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title"));
                                        else
                                            Blist = MessageFormat.format("{0}<a class=\"book_block\" href=\"/{1}/list.html\"><img class=\"ticon\" res=\"/imgsrcs/?id={2}\"><br>{3}</a>", Blist, value.getName(), value.getPath(), IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title"));

                                    }
                                }
                                if (Config_dirs.Use_Server_LOG_DEBUG) System.out.println(value.getName());
                            }
                        }
                    }
                }
            }
            return Blist;
        }
        return langunges.langunges[Config_dirs.LanguageID][3];
    }

    public static Boolean IsHidden(String name) {
        File t = new File(MainPath + "/" + name + "/hidden.info");
        return t.isFile();
    }

    public static int GetMaxTexts(String name) {
        //LibVb
        if (IsFile(MainPath + "/" + name + "/main.index")) return TextReaderLibVb.GetMaxTexts(name);
        //LibVa
        List<String> List = ReadCFGFile(MainPath + "/" + name + "/list.info");
        return List.size();
    }
}
