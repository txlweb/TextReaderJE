package com.teipreader.share;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config_dirs.init_configs();
        WebServer.StartServer();
    }

    public static void Start() throws IOException {

        Config_dirs.init_configs();
        WebServer.StartServer();
    }
}
