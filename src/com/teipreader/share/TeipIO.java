package com.teipreader.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class TeipIO {
    public static String MainPath = Config_dirs.MainPath;
    public static String PathScan() {
        File file = new File(MainPath);
        String Blist = "{\"code\":\"0\", \"data\":[{\"name\":\"\",\"pack\":\"THIS_SERVER\"}";
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        if (!IsHidden(value.getName()))
                            Blist = String.format("%s,{\"name\":\"%s\",\"pack\":\"THIS_SERVER\"}", Blist, value.getName());
                    }
                }
            }
            return Blist+"]}";
        }
        return "{\"code\":\"-1\"}";
    }

    public static void GetZip(String name) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("tmp.zip"))) {
            compressFolder(MainPath+"/"+name, name, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean IsHidden(String name) {
        File t = new File(MainPath + "/" + name + "/hidden.info");
        return t.isFile();
    }
    public static int GetMaxTexts(String name) {
        //LibVb
        if(IsFile(MainPath + "/" + name + "/main.index")){
            List<String> List = com.teipreader.Main.TextReaderLibVb.ReadCFGFile(MainPath + "/" + name + "/main.index");
            return List.size();
        }
        //LibVa
        List<String> List = com.teipreader.Main.TextReaderLibVb.ReadCFGFile(MainPath + "/" + name + "/list.info");
        return List.size();
    }
    public static boolean IsFile(String File_name){
        return new File(File_name).exists();
    }
    public static void addToZipFile(String fileName, String fileAbsolutePath, ZipOutputStream zipOutputStream) throws IOException {
        // 创建ZipEntry对象并设置文件名
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);

        // 读取文件内容并写入Zip文件
        try (FileInputStream fileInputStream = new FileInputStream(fileAbsolutePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bytesRead);
            }
        }

        // 完成当前文件的压缩
        zipOutputStream.closeEntry();
    }
    public static void compressFolder(String sourceFolder, String folderName, ZipOutputStream zipOutputStream) throws IOException {
        File folder = new File(sourceFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 压缩子文件夹
                    compressFolder(file.getAbsolutePath(), folderName + "/" + file.getName(), zipOutputStream);
                } else {
                    // 压缩文件
                    addToZipFile(folderName + "/" + file.getName(), file.getAbsolutePath(), zipOutputStream);
                }
            }
        }
    }

}
