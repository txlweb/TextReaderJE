import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        // 获取目标目录
        String destDir = System.getProperty("user.home") + "\\AppData\\Roaming\\idlike\\textreader";

        // 检查当前目录是否是目标目录
        File currentDir = new File(System.getProperty("user.dir"));
        if (currentDir.getAbsolutePath().equals(new File(destDir).getAbsolutePath())) {
            // 如果在目标目录中，则运行 TeipReaderJavaEdition.jar

            runTeipReaderJavaEdition(args);
            return;
        }

        // 否则，继续安装流程
        System.out.println((char) 27 + "[33m  TextReader " + (char) 27 + "[31mBeta" + (char) 27 + "[39;49m 1.4.3-3953b-250312+Res-f677d8fda1d0b6489cbde999af550705 安装程序");
        System.out.println((char) 27 + "[33m *如果安装失败，请尝试以管理员身份运行。");

        Scanner scanner = new Scanner(System.in);
        System.out.println("是否继续安装 Text Reader? (Y/N)");
        String input = scanner.nextLine().trim().toLowerCase();
        if (!input.equals("y")) {
            System.out.println("安装已取消。");
            Thread.sleep(2000);
            return;
        }

        install();
        System.out.println("安装完成. [2s 后退出]");
        Thread.sleep(2000);

    }

    private static void runTeipReaderJavaEdition(String[] args) {
        try {
            // 构建 Java 命令
            String destDir = System.getProperty("user.home") + "\\AppData\\Roaming\\idlike\\textreader";
            String javaPath = new File(destDir+"\\jdk-21.0.5+11\\bin\\java.exe").getAbsolutePath();
            String jarPath = new File("TeipReaderJavaEdition.jar").getAbsolutePath();

            // 构建完整的命令
            StringBuilder command = new StringBuilder();
            command.append("\"").append(javaPath).append("\" -jar \"").append(jarPath).append("\"");
            for (String arg : args) {
                command.append(" \"").append(arg).append("\"");
            }

            // 构建 cmd 命令
            String cmdCommand = "cmd.exe /c start \"TeipReader Java Edition\" " + command.toString();
            // 执行 cmd 命令
            Process process = Runtime.getRuntime().exec(cmdCommand);

            // 等待进程启动
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void install() throws IOException {
        String destDir = System.getProperty("user.home") + "\\AppData\\Roaming\\idlike\\textreader";
        System.out.println("目标路径: " + destDir);

        File targetDir = new File(destDir);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            System.out.println("创建目录失败");
            return;
        }

        // 下载 JDK
        File jdkFile = new File(destDir+"\\jdk-21.0.5+11\\bin\\java.exe");
        if (!jdkFile.isFile()) Online.download_jdk();
        if (!jdkFile.isFile()) {
            System.out.println("JDK 下载失败！尝试手动安装？");
            return;
        }

        // 复制 TeipReaderJavaEdition.jar 到目标目录
        try (InputStream is = Main.class.getResourceAsStream("TeipReaderJavaEdition.jar");
             FileOutputStream fos = new FileOutputStream(new File(destDir, "TeipReaderJavaEdition.jar"))) {
            if (is == null) {
                System.out.println("未找到 TeipReaderJavaEdition.jar");
                return;
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("TeipReaderJavaEdition.jar 复制成功");
        } catch (IOException e) {
            System.out.println("复制 TeipReaderJavaEdition.jar 失败: " + e.getMessage());
            return;
        }
        Online.unzipFileWithProgress(new File(destDir, "TeipReaderJavaEdition.jar").getPath(),destDir);
        // 复制自身到目标目录
        try {
            File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            new File(destDir, "main.exe").delete();
            Files.copy(currentJar.toPath(), new File(destDir, "main.exe").toPath());
            System.out.println("程序自身复制成功");
        } catch (Exception e) {
            System.out.println("复制程序自身失败: " + e.getMessage());
            return;
        }

        // 创建快捷方式
        try {
            String setupExePath = destDir + "\\main.exe";
            File setupExe = new File(setupExePath);
            if (setupExe.exists()) {
                String shortcutPath = System.getProperty("user.home") + "\\Desktop\\TextReader.lnk";
                String psCommand = "powershell -Command \""
                        + "$WshShell = New-Object -ComObject WScript.Shell; "
                        + "$Shortcut = $WshShell.CreateShortcut('" + shortcutPath + "'); "
                        + "$Shortcut.TargetPath = '" + setupExePath + "'; "
                        + "$Shortcut.WorkingDirectory = '" + new File(setupExePath).getParent() + "'; "
                        + "$Shortcut.IconLocation = '" + destDir + "\\style\\favicon.ico'; "
                        + "$Shortcut.Save();\"";

                // 执行 PowerShell 脚本
                Process process = Runtime.getRuntime().exec(psCommand);

                // 等待命令执行完成
                process.waitFor();

                // 输出执行结果
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                System.out.println("快捷方式已创建： " + shortcutPath);
            } else {
                System.out.println("安装失败 （无法初始化）！");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}