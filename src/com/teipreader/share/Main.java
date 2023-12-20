package com.teipreader.share;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config_dirs.init_configs();
        WebServer.StartServer();
    }
    public static void Start() throws IOException {
        System.out.println((char) 27 + "[33mTextReader Share Lib  1.0.0 JavaEdition(Build 10000)" + (char) 27 + "[39;49m");
        System.out.println("作者: IDlike");
        Config_dirs.init_configs();
        WebServer.StartServer();
    }
}
