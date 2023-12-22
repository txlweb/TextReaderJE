package com.teipreader.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ServerLibVa {
    public static String StylePath = Config_dirs.StylePath;

    public static String AddTitle(String Old) throws UnsupportedEncodingException {
        List<String> List = ReadCFGFile(StylePath + "/index.html");// 读列表
        int n = List.size();
        StringBuilder Final = new StringBuilder();
        for (String s : List) {
            if (s.contains("#textbara")) {
                Final.append(Old); //替换 #textbara 为正文内容!!
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
            return TextReaderLibVa.PathScan(false);
        }
        return "";
    }

    public static List<String> ReadCFGFile(String strFilePath) {
        List<String> str = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(strFilePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                str.add(line);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

}