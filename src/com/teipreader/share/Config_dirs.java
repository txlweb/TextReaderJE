package com.teipreader.share;

import com.teipreader.Lib.IniLib;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Config_dirs {
    //public static String MainPath = "./rom";//Text root
    public static int NormPort = 8090;//Web port
    public static boolean Use_Server_LOG = true;
    public static boolean Use_Server_LOG_DEBUG = false;
    public static boolean Use_HTTPS = true;
    public static void init_configs() throws IOException {
        //ini lib version
        //[settings]
        //MainPath=./rom
        //Port=8080 #端口
        //LogRank=1 #0=禁止提示 1=仅重要 2=全部提示
        if (!new File("./config_share.ini").isFile()) {
            IniLib.SetThing("./config_share.ini", "settings", "Port", "8090");
            IniLib.SetThing("./config_share.ini", "settings", "LogRank", "1");
        }
        String Gl_Port = IniLib.GetThing("./config_share.ini", "settings", "Port");
        if (!Objects.equals(Gl_Port, "UnknownThing")) {
            if (Integer.parseInt(Gl_Port) > 0 & Integer.parseInt(Gl_Port) < 25565) {
                NormPort = Integer.parseInt(Gl_Port);
            }
        }
        String Gl_HTTPS = IniLib.GetThing("./config.ini", "settings", "HTTPS");
        if (!Objects.equals(Gl_HTTPS, "UnknownThing")) {
            Use_HTTPS = Gl_HTTPS.equals("enable");
        }
        String Gl_LogRank = IniLib.GetThing("./config_share.ini", "settings", "LogRank");
        if (!Objects.equals(Gl_LogRank, "UnknownThing")) {
            Use_Server_LOG = false;
            Use_Server_LOG_DEBUG = false;
            if (Gl_LogRank.equals("1")) {
                Use_Server_LOG = true;
            }
            if (Gl_LogRank.equals("2")) {
                Use_Server_LOG = true;
                Use_Server_LOG_DEBUG = true;
            }
        } else {
            Gl_LogRank = "1";
        }
        //System.out.println("[C]: 服务端口=" + NormPort + " | 日志等级=" + Gl_LogRank);
    }
}
