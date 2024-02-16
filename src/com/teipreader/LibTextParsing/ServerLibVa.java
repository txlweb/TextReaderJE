package com.teipreader.LibTextParsing;

import com.teipreader.Main.Config_dirs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;

public class ServerLibVa {
    public static String StylePath = Config_dirs.StylePath;
    public static String getPluginCode(){
        //加载插件
        //扫plugin目录
        String buffer_plugin = "";
        File file = new File("./plugin/");
        int vid = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {

                for (File value : files) {
                    if (value.isFile()&&!value.getPath().contains(".encode")&value.getPath().contains(".plugin") && !value.getPath().contains(".nop")) {
                        System.out.println("Load plugin: "+value.getPath());
                        boolean b1 = true,b2 = true,b3 = false;
                        List<String> Plugin_ = ReadCFGFile(value.getPath());// 读列表
                        buffer_plugin = buffer_plugin + "<script>\r\nif(plugin_enables["+vid+"]){\r\n";
                        vid++;
                        for (int i = 0; i < Plugin_.size(); i++) {
                            if(b3&!Plugin_.get(i).equals("@JS-END")){
                                buffer_plugin = buffer_plugin + Plugin_.get(i)+"\r\n";
                            }
                            if(Plugin_.get(i).contains("NAME")&b1){
                                buffer_plugin = buffer_plugin + "//"+Plugin_.get(i)+"\r\nplugin_list.push(\""+Plugin_.get(i)+"\")\r\n";
                                b1=false;
                            }
                            if(Plugin_.get(i).contains("BY")&b2){
                                buffer_plugin = buffer_plugin + "//"+Plugin_.get(i)+"\r\n";
                                b2=false;
                            }
                            if(Plugin_.get(i).equals("@JS-START")&!b1&!b2){
                                b3=true;
                            }
                            if(Plugin_.get(i).equals("@JS-END")){
                                b3=false;
                            }

                        }
                        buffer_plugin = buffer_plugin + "}plugin_list[plugin_list.length-1]=\"[OK]\"+plugin_list[plugin_list.length-1];</script>\r\n";

                    }
                }
            }
        }
        String ff =  "<script>var plugin_enables = [true";
        for (int i = 0; i < vid; i++) {
            ff = ff + ",true";
        }
        ff = ff + "];var plugin_list = [];</script>\r\n";
        return ff+buffer_plugin;
    }
    public static String AddTitle(String Old) throws UnsupportedEncodingException {
        List<String> List = ReadCFGFile(StylePath + "/index.html");// 读列表
        int n = List.size();
        StringBuilder Final = new StringBuilder();
        for (String s : List) {
            if (s.contains("#textbara")) {

                //System.out.println(buffer_plugin);
                Final.append(getPluginCode());//插入
                Final.append(Old); //替换 #textbara 为正文内容!!
            } else {
                Final.append(s).append("\n");
            }
        }
        return Final.toString();
    }

    public static String AddTitle_index(String Old) throws UnsupportedEncodingException {
        List<String> List = ReadCFGFile(StylePath + "/index.html");// 读列表
        List<String> List1 = ReadCFGFile(StylePath + "/add-search-index.html");// 读列表
        StringBuilder addx = new StringBuilder();
        for (String a : List1) addx.append(a);
        int n = List.size();
        StringBuilder Final = new StringBuilder();
        for (String s : List) {
            if (s.contains("#textbara")) {
                Final.append(getPluginCode());//插入插件
                Final.append(addx + Old); //替换 #textbara 为正文内容!!
            } else {
                Final.append(s).append("\n");
            }
        }
        return Final.toString();
    }

    public static String PageIniter(String Url) {

        return "";
    }

    public static String ConnectReturn(String url) {
        @SuppressWarnings("Annotator") String[] A = url.split("\\?");
        String Path = A[0];
        if (Objects.equals(Path, "/")) {
            return TextReaderLibVa.PathScan(false, A[1]);
        }
        return "";
    }



}