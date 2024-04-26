package com.teipreader.share;

import java.io.IOException;

import static com.teipreader.share.Config_dirs.Use_HTTPS;

public class Main {
    public static void main(String[] args) throws IOException {
        Config_dirs.init_configs();
        if(Use_HTTPS){
            try{
                HTTPS_SERVER.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            WebServer.StartServer();
        }

    }

    public static void Start() throws IOException {
        Config_dirs.init_configs();
        if(Use_HTTPS){
            try{
                HTTPS_SERVER.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            WebServer.StartServer();
        }
    }
}
