package com.teipreader.Main;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.teipreader.Main.Config_dirs.MainPath;
import static com.teipreader.Main.TextReaderLibVa.IsFile;

public class TeipMakerLib {
    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    public static void allClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
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

    public static void autoMake(String FileName, String saveAs, String Title, String Img_src, String Rule, String Author, String info) throws IOException {
        System.out.println("正在处理小说: " + Title + " | 切章规则: " + Rule + " | 是否有图标: " + IsFile(Img_src));
        if (!new File(FileName).exists()) return;//检查文件是否存在
        //System.out.println(getFileHash256(FileName));
        String md5 = getFileMD5(FileName);
        System.out.println(md5);
        //清理文件
        if (new File(md5).exists()) new File(md5).delete();
        new File(md5).mkdir();
        //复制文件
        CopyFileToThis(new File(FileName), new File(md5 + "/main.txt"));
        if (new File(Img_src).exists()) CopyFileToThis(new File(Img_src), new File(md5 + "/icon.jpg"));
        //处理章节信息
        WriteFileToThis("main.index", preTxt(FileName, Rule));
        if (new File("main.index").exists()) CopyFileToThis(new File("main.index"), new File(md5 + "/main.index"));
        //添加ini配置项
//        [conf]
//        icon=
//        title=
//        by=
//        ot=
        new File("resource.ini").delete();
        WriteFileToThis("resource.ini", "[conf]\r\nicon = icon.jpg\r\ntitle = " + Title + "\r\nby = " + Author + "\r\not = " + info);
        CopyFileToThis(new File("resource.ini"), new File(md5 + "/resource.ini"));
        //        SetThing("./"+Title+"/resource.ini","conf","icon","icon.jpg");
//        SetThing("./"+Title+"/resource.ini","conf","title",Title);
//        SetThing("./"+Title+"/resource.ini","conf","by",Author);
//        SetThing("./"+Title+"/resource.ini","conf","ot",info);
        //打包
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveAs))) {
            compressFolder(md5, md5, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFileByIO(md5);
        if (new File(md5).exists()) new File(md5).delete();
    }

    public static void deleteFileByIO(String filePath) {
        File file = new File(filePath);
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteFileByIO(temp.getAbsolutePath());
            }
        }
        file.delete();
    }

    public static void Unzip(String from, String to) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(from))) {
            unzipFiles(zipInputStream, to);
            //System.out.println("Files unzipped successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String preTxt(String txt, String Rule) {
        if (Objects.equals(Rule, "")) Rule = ".*第.*章.*";
        List<String> List = ReadCFGFile(txt);
        StringBuilder T_LIST = new StringBuilder();
        for (int i = 0; i < List.size(); i++) {
            Pattern compile = Pattern.compile(Rule);//正则方法
            java.util.regex.Matcher matcher = compile.matcher(List.get(i));
            if (matcher.matches()) {
                //if(List.get(i).contains("第") & List.get(i).contains("章")){//关键字方法
                T_LIST.append(List.get(i)).append("&D&").append(i).append("\r\n");
                //System.out.println(List.get(i) + "&D&" + i);
            }
        }
        T_LIST.append("\r\n");
        System.out.println("共计: " + T_LIST.length() + "章");
        return T_LIST.toString().replace(",", "");//防逗号
    }

    public static void V1ToV2(String V1Name, String V2Save) throws IOException {
        StringBuilder mainTXT = new StringBuilder();
        StringBuilder ListTXT = new StringBuilder();
        List<String> List = ReadCFGFile(MainPath + "/" + V1Name + "/list.info");//读列表
        String LsHTML = "";
        int longer = 0;
        for (int i = 0; i < List.size(); i++) {
            mainTXT.append(List.get(i)).append("\r\n");
            ListTXT.append(List.get(i)).append("&D&").append(longer).append("\r\n");
            List<String> tl = ReadCFGFile((MainPath + "/" + V1Name + "/" + (i + 1) + ".txt").replace(",", ""));//读列表
            for (String s : tl) {
                mainTXT.append(s).append("\r\n");
                longer++;
            }
            longer++;
        }
        //格式化完毕
        //清理
        if (new File(V2Save).exists()) new File(V2Save).delete();
        new File(V2Save).mkdir();
        if (new File("main.index").exists()) new File(V2Save).delete();
        if (new File("main.txt").exists()) new File(V2Save).delete();
        //写入
        WriteFileToThis("main.index", String.valueOf(ListTXT));
        WriteFileToThis("main.txt", String.valueOf(mainTXT));
        //复制
        if (new File("main.index").exists()) CopyFileToThis(new File("main.index"), new File(V2Save + "/main.index"));
        if (new File("main.index").exists()) CopyFileToThis(new File("main.txt"), new File(V2Save + "/main.txt"));
        //清理
        if (new File("main.index").exists()) new File(V2Save).delete();
        if (new File("main.txt").exists()) new File(V2Save).delete();
        if (new File(V2Save + ".teip2").exists()) new File(V2Save).delete();
        //打包
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(V2Save + ".teip2"))) {
            compressFolder(V2Save, V2Save, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (new File(V2Save).exists()) new File(V2Save).delete();
    }

    public static void GetTextWithThis(String Name) throws IOException {
        if (IsFile(MainPath + "/" + Name + "/main.txt")) {
            if (new File("main.txt").exists()) new File("main.txt").delete();
            List<String> List = ReadCFGFile(MainPath + "/" + Name + "/main.txt");
            StringBuilder T_LIST = new StringBuilder();
            for (String s : List) {
                T_LIST.append(s);
            }
            WriteFileToThis("main.txt", String.valueOf(T_LIST));
            return;
        }
        StringBuilder mainTXT = new StringBuilder();
        List<String> List = ReadCFGFile(MainPath + "/" + Name + "/list.info");//读列表
        for (int i = 0; i < List.size(); i++) {
            mainTXT.append(List.get(i)).append("\r\n");
            List<String> tl = ReadCFGFile((MainPath + "/" + Name + "/" + (i + 1) + ".txt").replace(",", ""));//读列表
            for (String s : tl) {
                mainTXT.append(s).append("\r\n");
            }
        }
        if (new File("main.txt").exists()) new File("main.txt").delete();
        WriteFileToThis("main.txt", String.valueOf(mainTXT));
    }

    public static void WriteFileToThis(String file_name, String data) {
        try {
            File file = new File(file_name);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(data);
            bufferWriter.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void CopyFileToThis(File source, File dest) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
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

    public static void unzipFiles(ZipInputStream zipInputStream, String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            File outputFile = new File(outputFolder + "/" + fileName);

            // 创建文件夹
            if (entry.isDirectory()) {
                outputFile.mkdirs();
            } else {
                // 创建文件并写入内容
                new File(outputFile.getParent()).mkdirs();
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            zipInputStream.closeEntry();
        }
    }

    /**
     * 计算文件的MD5
     *
     * @param fileName 文件的绝对路径
     * @return
     */
    public static String getFileMD5(String fileName) {
        File file = new File(fileName);
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * 计算文件的Hash256值
     *
     * @param fileName 文件的绝对路径
     * @return
     */
    public static String getFileHash256(String fileName) {
        File file = new File(fileName);
        FileInputStream fis = null;
        String sha256 = "";
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();
            sha256 = byte2hexLower(digest);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return sha256;
    }

    private static String byte2hexLower(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
