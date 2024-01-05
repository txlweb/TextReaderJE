package com.teipreader.LibTextParsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

import static com.teipreader.Lib.Pdf2Png.pdf2png;
import static com.teipreader.Main.TeipMake.*;

public class CartoonMake {
    public static void MakeCartoon(String ImagePath, String Index, String SaveAs, String Title, String Img_src, String Author, String info) throws IOException {
        //校验文件夹并获取md5
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("tmp.zip"))) {
            compressFolder(ImagePath, ImagePath, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5 = getFileMD5("tmp.zip");
        if (new File(md5).exists()) new File(md5).delete();
        new File(md5).mkdir();
        deleteFileByIO("tmp.zip");
        //写入index文件
        new File("tmp.index").delete();
        WriteFileToThis("tmp.index", Index);
        CopyFileToThis(new File("tmp.index"), new File(md5 + "main.index"));
        //Index:  [章节名]&D&[开始id]&D&[结束ID]\r\n[章节名]&D&[开始id]&D&[结束ID]
        //图片资源
        if (new File(Img_src).exists()) CopyFileToThis(new File(Img_src), new File(md5 + "/icon.jpg"));
        //写入资源简介
        new File("resource.ini").delete();
        WriteFileToThis("resource.ini", "[conf]\r\nicon = icon.jpg\r\ntitle = " + Title + "\r\nby = " + Author + "\r\not = " + info);
        CopyFileToThis(new File("resource.ini"), new File(md5 + "/resource.ini"));
        //图片转base64直接传
        File file = new File(ImagePath);
        StringBuilder Blist = new StringBuilder();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    Blist.append("<img src=\"data:image/png;base64,").append(encryptToBase64(value.getPath() + "/" + value.getName())).append("\">\r\n");
                }
            }
        }
        //写出资源
        new File("main.txt").delete();
        WriteFileToThis("main.txt", Blist.toString());
        CopyFileToThis(new File("main.txt"), new File(md5 + "/main.txt"));
        WriteFileToThis("type_pdf.lock", "THIS IS A PDF!!");
        CopyFileToThis(new File("type_pdf.lock"), new File(md5 + "/type_pdf.lock"));
        //输出压缩包
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(SaveAs))) {
            compressFolder(md5, md5, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFileByIO(md5);
        if (new File(md5).exists()) new File(md5).delete();
    }

    public static void MakeCartoon_by_pdf(String PDF_File, String Index, String SaveAs, String Title, String Img_src, String Author, String info) throws IOException {
        //校验文件并获取md5

        String md5 = getFileMD5(PDF_File);
        if (new File(md5).exists()) new File(md5).delete();
        new File(md5).mkdir();
        deleteFileByIO("tmp.zip");
        //写入index文件
        new File("tmp.index").delete();
        //Index:  [章节名]&D&[开始id] 没有index就默认的整个文件做一张
        if (Objects.equals(Index, "-")) {
            WriteFileToThis("tmp.index", "整个文件&D&0");
        } else {
            WriteFileToThis("tmp.index", Index);
        }
        CopyFileToThis(new File("tmp.index"), new File(md5 + "/main.index"));

        //图片资源
        if (new File(Img_src).exists()) CopyFileToThis(new File(Img_src), new File(md5 + "/icon.jpg"));
        //写入资源简介
        new File("resource.ini").delete();
        WriteFileToThis("resource.ini", "[conf]\r\nicon = icon.jpg\r\ntitle = " + Title + "\r\nby = " + Author + "\r\not = " + info);
        CopyFileToThis(new File("resource.ini"), new File(md5 + "/resource.ini"));
        //图片转base64直接传
        //写出资源
        new File("main.txt").delete();
        WriteFileToThis("main.txt", Objects.requireNonNull(pdf2png(PDF_File, "jpg", "./" + md5)).toString());
        CopyFileToThis(new File("main.txt"), new File(md5 + "/main.txt"));
        WriteFileToThis("type_pdf.lock", "THIS IS A PDF!!");
        CopyFileToThis(new File("type_pdf.lock"), new File(md5 + "/type_pdf.lock"));

        //输出压缩包
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(SaveAs))) {
            compressFolder(md5, md5, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFileByIO(md5);
        if (new File(md5).exists()) new File(md5).delete();
    }

    public static String encryptToBase64(String filePath) {
        if (filePath == null) {
            return null;
        }
        try {
            byte[] b = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
