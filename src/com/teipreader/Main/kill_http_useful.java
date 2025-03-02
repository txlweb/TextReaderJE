package com.teipreader.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class kill_http_useful {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static Integer findProcessByPort(int port) {
        String command = isWindows()
                ? "cmd /c netstat -ano | findstr :" + port
                : "sh -c lsof -i :" + port;

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("找到进程: " + line);
                return extractPID(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer extractPID(String line) {
        try {
            if (isWindows()) {
                String[] parts = line.trim().split("\\s+");
                return parts.length > 4 ? Integer.parseInt(parts[parts.length - 1]) : null;
            } else {
                String[] parts = line.trim().split("\\s+");
                return parts.length > 1 ? Integer.parseInt(parts[1]) : null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void killProcessByPID(int pid) {
        String command = isWindows() ? "taskkill /F /PID " + pid : "kill -9 " + pid;
        executeCommand(command);
    }

    public static void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
