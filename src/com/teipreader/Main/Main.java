package com.teipreader.Main;

import com.teipreader.Lib.IniLib;
import com.teipreader.LibTextParsing.CartoonMake;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static com.teipreader.Main.TeipMakerLib.*;

//+~~~~~~+~~~~~~+~~~~~~~~~~~+
//|  fg  |  bg  |  color    |
//+~~~~~~+~~~~~~+~~~~~~~~~~~+
//|  30  |  40  |  black    |
//|  31  |  41  |  red      |
//|  32  |  42  |  green    |
//|  33  |  43  |  yellow   |
//|  34  |  44  |  blue     |
//|  35  |  45  |  magenta  |
//|  36  |  46  |  cyan     |
//|  37  |  47  |  white    |
//|  39  |  49  |  default  |
//+~~~~~~+~~~~~~+~~~~~~~~~~~+
public interface Main {
    static void main(String[] args) throws IOException {
        //配置位置
        String Cheek_code = "ef23f9bcc14d79e1fa3ee45485c28879c91612802bb064597dce11b415ec084bdfe6d300c779ba2070e8bb2f668d0494f8262d3aaea8a4f9ec70a31ebf064eabe711558c29a14e482eab008283ee9072fdc7a5a19196098cf5c6ebd2b750e53eb7187bf4d337c16e0d64cfdef67d59f0c9c9633e5237efd734d1e8c1207e9bdb3fcf44d428f8045313c9c3fee78054aca3de6623ddb44a48ab6f8558e5269f30367241c347f7a82a2b024c70fccb21f7539d2eb089149c970141a3e43ab573fac6169644097d1719dc3f3fe43d288fd4";
        String version = "1.2.9";
        String build = "20500";

        boolean is_debug = RunShare.class.getClassLoader().getResource("debug.lock")!=null;
        //is_debug=true;
        if(is_debug){
            System.out.println("调试模式已开启");
        }
        //在windows下进行调试,小说目录有乱码是正常的,需要编译了之后再启动!!
        //编码问题很大,linux下则没有这种问题.
        System.setProperty("file.encoding", "UTF-8");
        System.out.println((char) 27 + "[33mTextReader " + (char) 27 + "[31mBeta" + (char) 27 + "[39;49m "+version+"-"+build+"");
        System.out.println("");
        System.out.println("");
        System.out.println("作者: IDlike    GitHub:https://github.com/txlweb/TextReaderJE/");
        //System.out.println("编译JDK版本: 11.0.16.1 你的JDK版本:" + System.getProperty("java.version"));
        System.out.println("如果需要帮助请查看jar包内的readme.md");
        if (false) {
            TeipMakerLib.autoMake("测试转换.txt", "test.zip", "测试1", "-", ".*第.*章.*", "unk", "unk");
            TeipMakerLib.Unzip("test.zip", Config_dirs.MainPath);
            return;
        }
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
                TeipMakerLib.autoMake(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
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
                TeipMakerLib.GetTextWithThis(args[1]);
                TeipMakerLib.CopyFileToThis(new File("main.txt"), new File(args[2]));
                System.out.println((char) 27 + "[39;49m");
                return;
            }
            if (Objects.equals(args[0], "-add") || Objects.equals(args[0], "-a") & args.length >= 3) {
                if (new File("tmp").exists()) new File("tmp").delete();
                new File("tmp").mkdir();
                TeipMakerLib.Unzip(args[1], "tmp");
                if (new File("tmp/resource.ini").isFile()) {//v1文件特征
                    String title = IniLib.GetThing("tmp/resource.ini", "conf", "title");
                    System.out.println((char) 27 + "[31m[I]: 版本=v1 标题=" + title + (char) 27 + "[39;49m");
                    TeipMakerLib.Unzip(args[1], Config_dirs.MainPath + "/" + title);
                } else {
                    System.out.println((char) 27 + "[31m[I]: 版本=v2 " + (char) 27 + "[39;49m");
                    TeipMakerLib.Unzip(args[1], Config_dirs.MainPath);
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
        System.out.println((char) 27 + "[33m[I]: 检查文件..." + (char) 27 + "[39;49m");
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
            TeipMakerLib.Unzip("./style.zip", "./style/");
            if (!new File(Config_dirs.StylePath + "/index.html").isFile()) {
                System.out.println((char) 27 + "[31m[E]: 资源释放失败." + (char) 27 + "[39;49m");
                System.out.println((char) 27 + "[31m 进程已结束." + (char) 27 + "[39;49m");
                return;
            } else {
                new File("style.zip").delete();
                System.out.println((char) 27 + "[32m[I]: 释放完成!" + (char) 27 + "[39;49m");
            }
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

        if(!is_debug){
            if(!Blist.toString().equals(Cheek_code)){
                Scanner scanner = new Scanner(System.in);
                System.out.println("[I]: 资源版本不一致!");
                System.out.println("若要取消这个步骤,请在程序运行目录下创建文件 debug.lock");
                System.out.println("内置资源版本: S-"+getMD5(Cheek_code)+"(新)");
                System.out.println((char) 27 + "[32m   ↓ 替换" + (char) 27 + "[39;49m");
                System.out.println("目前资源版本: S-"+getMD5(Blist.toString())+"(旧)");
                System.out.println("回车键继续,建议先备份style文件夹再继续,因为这一步会清理配置文件.");
                scanner.nextLine();
                System.out.println("正在清除原资源..");
                deleteFileByIO("./style/");
                System.out.println((char) 27 + "[31m[I]: 资源修复完成,请重启应用." + (char) 27 + "[39;49m");
                return;

            }
        }else {
            if(!Blist.toString().equals(Cheek_code)){
                System.out.println((char) 27 + "[31m[I]: 资源版本有差异,调试模式下已忽略." + (char) 27 + "[39;49m");
            }
            System.out.println((char) 27 + "[34m[DEBUG]: Cheek code: "+Blist);
        }


        System.out.println((char) 27 + "[33m[I]: 完成." + (char) 27 + "[39;49m");
        System.out.println((char) 27 + "[36m[T]: Ctrl+c结束进程" + (char) 27 + "[39;49m");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            URI uri = URI.create("http://127.0.0.1:" + Config_dirs.NormPort + "/");
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (Config_dirs.Use_Share) {
            RunShare a = new RunShare();
            a.start();
        }
        WebServer b = new WebServer();
        b.start();

    }

    void runShare() throws IOException;

    void runServer() throws IOException;
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
