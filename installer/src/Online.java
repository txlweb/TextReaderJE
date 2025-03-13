import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public class Online {
    public static void download_jdk() {
        String fileUrl = "https://aka.ms/download-jdk/microsoft-jdk-21.0.5-windows-x64.zip";
        String zipFileName = "microsoft-jdk-21.0.5-windows-x64.zip";

        try {
            // 下载文件并显示进度
            downloadFileWithProgress(fileUrl, zipFileName);

            // 解压文件并显示进度
            unzipFileWithProgress(zipFileName, System.getProperty("user.home") + "\\AppData\\Roaming\\idlike\\textreader\\");
            if(new File("./jdk-21.0.5+11/bin/java.exe").isFile()){
                System.out.println("\n操作完成。");
            }else {
                System.out.println("\n失败！自动下载jdk失败！");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadFileWithProgress(String fileUrl, String outputFile) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true); // 处理重定向
        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // 设置User-Agent

        int fileSize = connection.getContentLength();
        if (fileSize == -1) {
            throw new IOException("无法获取文件大小");
        }

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            long totalRead = 0;
            int lastProgress = -1;

            System.out.println("开始下载...");
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1) break;

                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                // 计算进度百分比（0-100）
                int progress = (int) ((totalRead * 100) / fileSize);

                // 每2.5%更新一次进度（每个点代表2.5%）
                if (progress > lastProgress) {
                    // 构建进度条字符串
                    StringBuilder sb = new StringBuilder("\r[");
                    int dots = progress / 10 * 4; // 每10%显示4个点
                    int partial = (progress % 10) * 4 / 10; // 计算中间的点

                    for (int i = 0; i < dots + partial; i++) {
                        sb.append("=");
                    }
                    for (int i = dots + partial; i < 40; i++) {
                        sb.append("-");
                    }
                    sb.append("] ").append(progress).append("%");

                    System.out.print(sb.toString());
                    lastProgress = progress;
                }
            }
            System.out.println(); // 换行结束下载进度
        } finally {
            connection.disconnect();
        }
    }

     static void unzipFileWithProgress(String zipFile, String outputDir) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            int totalEntries = zip.size();
            int processed = 0;

            System.out.println("开始解压...");
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File destFile = new File(outputDir, entry.getName());

                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    destFile.getParentFile().mkdirs();
                    try (InputStream in = zip.getInputStream(entry);
                         OutputStream out = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                }

                // 更新解压进度
                processed++;
                int progress = (processed * 100) / totalEntries;

                // 构建解压进度条
                StringBuilder sb = new StringBuilder("\r[");
                int dots = progress / 10 * 4; // 与下载保持一致的显示风格
                int partial = (progress % 10) * 4 / 10;

                for (int i = 0; i < dots + partial; i++) {
                    sb.append("=");
                }
                for (int i = dots + partial; i < 40; i++) {
                    sb.append("-");
                }
                sb.append("] ").append(progress).append("%");

                System.out.print(sb.toString());
            }
            System.out.println(); // 换行结束解压进度
        }
    }
}
