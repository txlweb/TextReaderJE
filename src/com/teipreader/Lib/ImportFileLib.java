package com.teipreader.Lib;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

public class ImportFileLib {
    public static String GetFileList(String path) {
        File file = new File(path);
        String Blist = "{\"code\":\"0\",";
        String Als = "[\"..\"";
        String Bls = "[\"\"";
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        Als = String.format("%s,\"%s\"", Als, value.getName());
                    } else if (value.isFile()) {
                        Bls = String.format("%s,\"%s\"", Bls, value.getName());
                    }
                }
            }
            Blist = String.format("%s\"path\":%s],\"file\":%s],\"rl_path\":\".%s\"}", Blist, Als, Bls, simplifyPath(path));
            if (simplifyPath(path).equals("/") && !path.equals(".")) {
                return (GetFileList("."));
            }
            return Blist;
        }
        return "{\"code\":\"-1\"}";
    }

    public static String simplifyPath(String path) {
        String[] str = path.split("/");
        Deque<String> stack = new ArrayDeque<>();
        for (String name : str) {
            if ("..".equals(name)) {//如果遇到 ".."
                if (!stack.isEmpty()) {// 前提是 栈不为空。要不然，下面的操作会抛出异常。【另外：栈为空，你能删除什么？】
                    stack.pollLast();// 删除 栈底 数据
                }
            } else if (name.length() > 0 && !".".equals(name)) {//  长度为零的字符串 和 "." 不作为存储数据
                stack.offerLast(name);// 将符合条件的数据，放入栈底
            }
        }
        StringBuilder sb = new StringBuilder();
        if (stack.isEmpty()) {
            sb.append("/");
        } else {
            while (!stack.isEmpty()) {
                sb.append("/");
                sb.append(stack.pollFirst());
            }
        }
        return sb.toString();// 类型转换为 String 类型
    }

}
