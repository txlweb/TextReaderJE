package com.teipreader.Main;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

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
        //在windows下进行调试,小说目录有乱码是正常的,需要编译了之后再启动!!
        //编码问题很大,linux下则没有这种问题.
        System.setProperty("file.encoding", "UTF-8");
        System.out.println((char) 27 + "[33mTextReader   " + (char) 27 + "[31mBeta" + (char) 27 + "[39;49m 1.2.6 JavaEdition(Build 19560)");
        System.out.println("作者: IDlike    GitHub:https://github.com/txlweb/T-e-i-p-R-e-a-d-e-r-J-a-v-a-E-d-i-t-i-o");
        System.out.println("编译JDK版本: 11.0.16.1 你的JDK版本:" + System.getProperty("java.version"));
        System.out.println("如果需要帮助请查看jar包内的readme.md");
        if (false) {
            TeipMakerLib.autoMake("测试转换.txt", "test.zip", "测试1", "-", ".*第.*章.*", "unk", "unk");
            TeipMakerLib.Unzip("test.zip", Config_dirs.MainPath);
            return;
        }
        if (args.length > 0) {
            System.out.println((char) 27 + "[36m- Command Help -");
            System.out.println("获取帮助 -h -help (共计1个参数)");
            System.out.println("导入txt文件 -m, -make txt文件名 保存的压缩包 小说标题 小说图片 切章规则(建议为.*第.*章.*) (共计6个参数)");
            System.out.println("导出txt文件 -o, -out 小说名 导出的txt名 (共计3个参数)");
            System.out.println("导入teip文件(V1&V2) -a, -add 文件名 (共计2个参数)");
            System.out.println("更改设置 -c, -config 键 值 (共计3个参数)");
            System.out.println("[可配置列表(下面给出了默认值及意义)]");
            System.out.println(
                    "    MainPath=./rom   #主路径    \n" +
                            "    Port=8080        #端口\n" +
                            "    UseShare=enable  #启用/禁止分享服务\n" +
                            "    LogRank=1        #日志等级 0=禁止提示 1=用户级日志 2=调试级提示");
            System.out.println("- Page 1 of 1 -");
            System.out.println(Arrays.toString(args));
            if (Objects.equals(args[0], "-make") || Objects.equals(args[0], "-m") & args.length >= 6) {
                TeipMakerLib.autoMake(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
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
        //WebServer.StartServer();
    }

    void runShare() throws IOException;

    void runServer() throws IOException;
}
