package com.teipreader.Lib;

import com.teipreader.Main.TeipMake;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.teipreader.LibTextParsing.TextReaderLibVa.ReadCFGFile;


public class IniLib {
    public static boolean lastLineisCRLF(String filename) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filename, "r");
            long pos = raf.length() - 2;
            if (pos < 0) return false; // too short
            raf.seek(pos);
            return raf.read() == '\r' && raf.read() == '\n';
        } catch (IOException e) {
            return false;
        } finally {
            if (raf != null) try {
                raf.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static String GetThing(String FileName, String Node, String key) {//will return key
        //如果文件尾部没有换行符,就要添加,否则会报错!!!!
        try {
            if (!lastLineisCRLF(FileName))
                Files.write(Paths.get(FileName), "\r\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> lines = ReadCFGFile(FileName);
        boolean getLN = false;
        for (String line : lines) {
            if (line.contains("[" + Node + "]")) {
                getLN = true;
                continue;
            }
            if (getLN & (line.contains(key + "=") || line.contains(key + " ="))) {
                String[] a = line.split("=");
                return a[1];
            }
            if (line.contains("[") & line.contains("]")) {
                getLN = false;
            }
        }
        return "UnknownThing";
    }

    public static void SetThing(String FileName, String Node, String key, String Value) {//will return key
        if (!new File(FileName).isFile()) TeipMake.WriteFileToThis(FileName, "[" + Node + "]");
        List<String> lines = ReadCFGFile(FileName);
        boolean getLN = false;
        boolean changed = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("[" + Node + "]")) {
                getLN = true;
                continue;
            }
            if (getLN & (line.contains(key + "=") || line.contains(key + " ="))) {
                lines.set(i, key + "=" + Value + "\r\n");//存在就覆写上去
                changed = true;
                break;
            }
            if (line.contains("[") & line.contains("]")) {
                if (getLN) {
                    lines.add(i - 1, key + "=" + Value + "\r\n");//如果找到node却没有key则在下一个node前插入k+v
                    changed = true;
                    break;
                }
                getLN = false;
            }
        }
        if (!changed) {//这种情况就是没有node或只有一个node
            if (getLN) {//直接写
                lines.add(key + "=" + Value + "\r\n");
            } else {//没node就创建
                lines.add("[" + Node + "]" + "\r\n");
                lines.add(key + "=" + Value + "\r\n");
            }
        }
        //写回文件
        StringBuilder ln = new StringBuilder();
        for (String line : lines) {
            if (line.contains("=") || line.contains("[") || line.contains("#")) ln.append(line).append("\r\n");
        }
        if (new File(FileName).isFile()) new File(FileName).delete();
        TeipMake.WriteFileToThis(FileName, String.valueOf(ln));
    }
}
