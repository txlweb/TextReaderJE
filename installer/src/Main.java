import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
//        if (!requestAdminPrivileges()) {
//            System.out.println((char) 27 + "[33m Text Reader v1.4.0 安装程序  [！请以管理员身份运行！]");
//            System.out.println((char) 27 + "[31m[E]: 我们无权安装TextReader." + (char) 27 + "[39;49m");
//            String currentPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
//            System.out.println((char) 27 + "[33m Current executable path: " + currentPath);
//
//            // PowerShell 脚本命令
//            String psCommand = "powershell -Command \"Start-Process '" + currentPath + "' -Verb RunAs\"";
//            System.out.println((char) 27 + "[33m Exec power shell: "+psCommand);
//
//            try {
//                // 执行 PowerShell 脚本
//                Process process = Runtime.getRuntime().exec(psCommand);
//
//                // 等待命令执行完成
//                process.waitFor();
//
//                // 可选：获取执行结果
//                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                }
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.exit(1);
//        }
        System.out.println((char) 27 + "[33m Text Reader v1.4.1 ");
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

//    private static boolean requestAdminPrivileges() {
//        File testFile = new File("C:\\Windows\\System32\\testingUCA");
//        try {
//            if (testFile.createNewFile()) {
//                testFile.delete();
//                return true;
//            }
//        } catch (IOException e) {
//            return false;
//        }
//        return false;
//    }

    private static void install() {
        String destDir = System.getProperty("user.home") + "\\AppData\\Roaming\\idlike\\textreader";
        System.out.println("目标路径: " + destDir);

        File targetDir = new File(destDir);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            System.out.println("创建目录失败");
            return;
        }

        File archiveFile = new File(".\\archive.zip");
        try (InputStream is = Main.class.getResourceAsStream("/archive.zip");
             FileOutputStream fos = new FileOutputStream(archiveFile)) {
            if (is == null) {
                System.out.println("未找到 archive.zip");
                return;
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("文件释放成功");
        } catch (IOException e) {
            System.out.println("安装失败: " + e.getMessage());
            return;
        }
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archiveFile.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zis.closeEntry();
            }
            System.out.println("解压完成");
        } catch (IOException e) {
            System.out.println("解压失败: " + e.getMessage());
            return;
        }

        try {
            String setupExePath = destDir + "\\setup.exe";
            File setupExe = new File(setupExePath);
            if (setupExe.exists()) {
                String shortcutPath = System.getProperty("user.home") + "\\Desktop\\TextReader.lnk";
                String psCommand = "powershell -Command \""
                        + "$WshShell = New-Object -ComObject WScript.Shell; "
                        + "$Shortcut = $WshShell.CreateShortcut('"+shortcutPath+"'); "
                        + "$Shortcut.TargetPath = '"+setupExePath+"'; "
                        + "$Shortcut.WorkingDirectory = '" + new File(setupExePath).getParent() + "'; "
                        + "$Shortcut.IconLocation = '"+destDir+"\\style\\favicon.ico'; "
                        + "$Shortcut.Save();\"";

                // 执行 PowerShell 脚本
                Process process = Runtime.getRuntime().exec(psCommand);

                // 等待命令执行完成
                process.waitFor();

                // 可选：获取执行结果
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                System.out.println("快捷方式已创建： " + shortcutPath);

//                ProcessBuilder pb = new ProcessBuilder(setupExePath);
//                pb.directory(new File(destDir));
//                pb.start();
//                System.out.println("正在运行 <初始化>...");
            } else {
                System.out.println("安装失败 （无法初始化）！");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
