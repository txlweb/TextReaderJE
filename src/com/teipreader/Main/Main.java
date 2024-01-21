package com.teipreader.Main;

import com.teipreader.Lib.IniLib;
import com.teipreader.LibTextParsing.CartoonMake;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static com.teipreader.Main.TeipMake.*;

/*
+~~~~~~+~~~~~~+~~~~~~~~~~~+
|  fg  |  bg  |  color    |
+~~~~~~+~~~~~~+~~~~~~~~~~~+
|  30  |  40  |  black    |
|  31  |  41  |  red      |
|  32  |  42  |  green    |
|  33  |  43  |  yellow   |
|  34  |  44  |  blue     |
|  35  |  45  |  magenta  |
|  36  |  46  |  cyan     |
|  37  |  47  |  white    |
|  39  |  49  |  default  |
+~~~~~~+~~~~~~+~~~~~~~~~~~+
*/
public interface Main {
    static void main(String[] args) throws IOException {
        //配置位置
        String Cheek_code = "f5b371099e8856ba175d8b0ccd58facc";
        String version = "1.3.2";
        String build = "2195b-240120";

        boolean is_debug = RunShare.class.getClassLoader().getResource("debug.lock") != null || new File("./debug.lock").isFile();
        //is_debug=true;
        if (is_debug) {
            System.out.println("** DEBUG MODE **");
        }
        System.setProperty("file.encoding", "UTF-8");
        System.out.println((char) 27 + "[33m  TextReader " + (char) 27 + "[31mBeta" + (char) 27 + "[39;49m " + version + "-" + build + "+Res-" + Cheek_code);
        System.out.println("作者: IDlike    GitHub:https://github.com/txlweb/TextReaderJE/");
        System.out.println("如果需要帮助请查看jar包内的readme.md");
        System.out.println();
        System.out.println();
        if (args.length > 0) {
            System.out.println((char) 27 + "[36m- Command Help -");
            System.out.println("获取帮助 -h -help (共计1个参数)");
            System.out.println("导入txt文件 -m, -make txt文件名 保存的压缩包 小说标题 小说图片 切章规则(建议为.*第.*章.*) 作者 简介 (共计8个参数)");
            System.out.println("导入pdf文件 -p, -pdfmake pdf文件名 索引(-为自动索引) 保存的压缩包 标题 图片 作者 简介 (共计8个参数)");
            System.out.println("导入epub文件 -b, -epubmake epub文件名 (共计2个参数)");
            System.out.println("导出txt文件 -o, -out 小说名 导出的txt名 (共计3个参数)");
            System.out.println("导入teip文件(V1&V2) -a, -add 文件名 (共计2个参数)");
            System.out.println("更改设置 -c, -config 键 值 (共计3个参数)");
            System.out.println("[可配置列表(下面给出了默认值及意义)]");
            System.out.println(
                    "    MainPath=./rom   #主路径    \n" +
                            "    Port=8080        #端口\n" +
                            "    UseShare=enable  #启用/禁止分享服务\n" +
                            "    LogRank=1        #日志等级 0=禁止日志 1=用户级日志 2=调试级日志");
            System.out.println("- Page 1 of 1 -");
            System.out.println(Arrays.toString(args));
            if (Objects.equals(args[0], "-make") || Objects.equals(args[0], "-m") & args.length >= 6) {
                TeipMake.autoMake(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-pdfmake") || Objects.equals(args[0], "-p") & args.length >= 6) {
                CartoonMake.MakeCartoon_by_pdf(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-mobimake") || Objects.equals(args[0], "-b") & args.length >= 2) {
                EpubMake(args[1]);
                System.out.println("复制完成.");
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-out") || Objects.equals(args[0], "-o") & args.length >= 3) {
                TeipMake.GetTextWithThis(args[1]);
                TeipMake.CopyFileToThis(new File("main.txt"), new File(args[2]));
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-add") || Objects.equals(args[0], "-a") & args.length >= 3) {
                if (new File("tmp").exists()) new File("tmp").delete();
                new File("tmp").mkdir();
                TeipMake.Unzip(args[1], "tmp");
                if (new File("tmp/resource.ini").isFile()) {//v1文件特征
                    String title = IniLib.GetThing("tmp/resource.ini", "conf", "title");
                    System.out.println((char) 27 + "[31m[I]: 版本=v1 标题=" + title + (char) 27 + "[39;49m");
                    TeipMake.Unzip(args[1], Config_dirs.MainPath + "/" + title);
                } else {
                    System.out.println((char) 27 + "[31m[I]: 版本=v2 " + (char) 27 + "[39;49m");
                    TeipMake.Unzip(args[1], Config_dirs.MainPath);
                }
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-config") || Objects.equals(args[0], "-c") & args.length >= 3) {
                System.out.println((char) 27 + "[39;49m");
                IniLib.SetThing("./config.ini", "settings", "LogRank", "1");
                return;
            }
            System.out.println((char) 27 + "[39;49m");
            return;
        }//含参处理
        System.out.println((char) 27 + "[33m[I]: 正在读取配置文件..." + (char) 27 + "[39;49m");
        Config_dirs.init_configs();
        File file = new File(Config_dirs.MainPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.out.println((char) 27 + "[31m[E]: 无法创建目录." + (char) 27 + "[39;49m");
            }
        }
        File file1 = new File(Config_dirs.StylePath);
        if (!file1.exists()) {
            if (!file1.mkdir()) {
                System.out.println((char) 27 + "[31m[E]: 无法创建目录." + (char) 27 + "[39;49m");
            }
        }
        System.out.println((char) 27 + "[33m[I]: 正在检查文件完整性..." + (char) 27 + "[39;49m");
        File file2 = new File(Config_dirs.StylePath + "/index.html");
        if (!file2.exists()) {
            System.out.println((char) 27 + "[32m[I]: 正在尝试释放资源..." + (char) 27 + "[39;49m");
            System.out.println((char) 27 + "[32m" + Objects.requireNonNull(RunShare.class.getClassLoader().getResource("style.zip")).getPath() + (char) 27 + "[39;49m");
            //写出到外部在解压
            InputStream in = Objects.requireNonNull(RunShare.class.getClassLoader().getResource("style.zip")).openStream();
            try (OutputStream ot = new FileOutputStream("./style.zip")) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
            TeipMake.Unzip("./style.zip", "./style/");
            if (!new File(Config_dirs.StylePath + "/index.html").isFile()) {
                System.out.println((char) 27 + "[31m[E]: 资源释放失败." + (char) 27 + "[39;49m");
                System.out.println((char) 27 + "[31m 进程已结束." + (char) 27 + "[39;49m");
                return;
            } else {
                new File("style.zip").delete();
                System.out.println((char) 27 + "[32m[I]: 释放完成!" + (char) 27 + "[39;49m");
            }
            //恢复备份的配置文件
            if(new File("./back1.json").isFile() && new File("./back2.json").isFile()){
                new File("./style/API_list.json").delete();
                new File("./style/config.json").delete();
                CopyFileToThis(new File("./back1.json"),new File("./style/API_list.json"));
                CopyFileToThis(new File("./back2.json"),new File("./style/config.json"));
                new File("./back1.json").delete();
                new File("./back2.json").delete();
                System.out.println((char) 27 + "[32m[I]: 恢复配置文件完成!" + (char) 27 + "[39;49m");
            }
        }
        System.out.println((char) 27 + "[33m[I]: 正在准备库文件..." + (char) 27 + "[39;49m");
        if ((!new File("./epublib-core-4.0-complete.jar").isFile() || !new File("./pdfbox-app-2.0.30.jar").isFile()) && !is_debug) {
            System.out.println((char) 27 + "[31m[I]: 始化库文件,请在完成后重启程序." + (char) 27 + "[39;49m");
            System.out.println((char) 27 + "[32m[T]: 所用的库开源地址: " + (char) 27 + "[39;49m");
            System.out.println((char) 27 + "[32m[T]: lib-epublib: https://github.com/psiegman/epublib/" + (char) 27 + "[39;49m");
            System.out.println((char) 27 + "[32m[T]: lib-pdfbox: https://github.com/apache/pdfbox" + (char) 27 + "[39;49m");
            if (RunShare.class.getClassLoader().getResource("epublib-core-4.0-complete.jar") != null) {
                InputStream in = Objects.requireNonNull(RunShare.class.getClassLoader().getResource("epublib-core-4.0-complete.jar")).openStream();
                try (OutputStream ot = new FileOutputStream("./epublib-core-4.0-complete.jar")) {
                    byte[] bytes = new byte[1024];
                    int byteread;
                    while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
                }
                in.close();
            } else {
                System.out.println((char) 27 + "[32m[E]: 没有内置库 lib-epublib" + (char) 27 + "[39;49m");
            }
            if (RunShare.class.getClassLoader().getResource("pdfbox-app-2.0.30.jar") != null) {
                InputStream in = Objects.requireNonNull(RunShare.class.getClassLoader().getResource("pdfbox-app-2.0.30.jar")).openStream();
                try (OutputStream ot = new FileOutputStream("./pdfbox-app-2.0.30.jar")) {
                    byte[] bytes = new byte[1024];
                    int byteread;
                    while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
                }
                in.close();
            } else {
                System.out.println((char) 27 + "[32m[E]: 没有内置库 lib-pdfbox" + (char) 27 + "[39;49m");
            }
            System.out.println((char) 27 + "[32m[I]: 释放完成!应用将会重启." + (char) 27 + "[39;49m");
            main(args);
            return;
        }
        //检查style文件夹更新

        File filea = new File("./style/");
        StringBuilder Blist = new StringBuilder();
        if (file.isDirectory()) {
            File[] files = filea.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isFile()) {
                        //排除列表
                        if (!value.getName().equals("API_list.json") && !value.getName().equals("config.json")) {
                            Blist.append(getFileMD5("./style/" + value.getName()));
                        }
                    }
                }
            }
        }
        Blist = new StringBuilder(getTextMD5(String.valueOf(Blist)));
        if (!is_debug) {
            if (!Blist.toString().equals(Cheek_code)) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("[I]: 资源版本不一致!");
                System.out.println("若要取消这个步骤,请在程序运行目录下创建文件 debug.lock");
                System.out.println("目前资源版本: S-" + getMD5(Blist.toString()) + "(旧)");
                System.out.println((char) 27 + "[32m   ↓ 更新" + (char) 27 + "[39;49m");
                System.out.println("内置资源版本: S-" + Cheek_code + "(新)");
                System.out.println("回车键继续,这次的配置文件结构已经改变,建议运行配置文件清理工具,否则配置器可能闪退.");
                scanner.nextLine();
                System.out.println("正在清除原资源..");
                CopyFileToThis(new File("./style/API_list.json"),new File("./back1.json"));
                CopyFileToThis(new File("./style/config.json"),new File("./back2.json"));
                deleteFileByIO("./style/");
                System.out.println((char) 27 + "[31m[I]: 资源修复完成,应用将会重启." + (char) 27 + "[39;49m");
                main(args);
                return;
            }
        } else {
            if (!Blist.toString().equals(Cheek_code)) {
                System.out.println((char) 27 + "[31m[I]: 资源版本有差异,调试模式下已忽略." + (char) 27 + "[39;49m");
            }
            System.out.println((char) 27 + "[34m[DEBUG]: Cheek code: " + Blist);
        }


        System.out.println((char) 27 + "[33m[I]: 完成." + (char) 27 + "[39;49m");
        System.out.println((char) 27 + "[36m[T]: Ctrl+c结束进程" + (char) 27 + "[39;49m");
//        String os = System.getProperty("os.name").toLowerCase();//启动浏览器,但是很烦人
//        if (os.startsWith("windows")) {
//            URI uri = URI.create("http://127.0.0.1:" + Config_dirs.NormPort + "/");
//            try {
//                Desktop desktop = Desktop.getDesktop();
//                desktop.browse(uri);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        if (Config_dirs.Use_Share) {
            RunShare a = new RunShare();
            a.start();
        }
        WebServer b = new WebServer();
        b.start();

    }

    static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    void runShare() throws IOException;

    void runServer() throws IOException;
}
