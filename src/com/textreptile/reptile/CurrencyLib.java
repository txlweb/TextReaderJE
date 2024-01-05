package com.textreptile.reptile;

import java.io.File;
import java.io.IOException;

import static com.teipreader.Lib.Download.Dw_File;
import static com.teipreader.LibTextParsing.TextReaderLibVa.IsFile;
import static com.teipreader.Main.TeipMake.CopyFileToThis;
import static com.teipreader.Main.TeipMake.autoMake;

public class CurrencyLib {
    public static void main(String[] args) throws IOException {// PS:受害者是http://www.biquzw789.net/  (已被我爬破防了,他现在加了个下载需要登录)
        String DW_to = "dw";
        new File(DW_to).mkdir();
        String ID = "";
        String tit = "";
        String by = "";
        String ot = "";
        for (int i = 4101; i < 1000000; i++) {
            System.out.println("DOWNLOAD-TASK: " + String.format("%06d", i) + "/" + 1000000);
            ID = String.format("%06d", i);
            if (new File("tmp-dw.txt").isFile()) new File("tmp-dw.txt").delete();
            Rule_biquzw789.InitSrcCopy(ID);
            System.out.println(Rule_biquzw789.GetDownloadURL(String.valueOf(i)));
            Dw_File(Rule_biquzw789.GetDownloadURL(String.valueOf(i)), "tmp-dw.txt");
            System.out.println(Rule_biquzw789.GetImage());
            Dw_File(Rule_biquzw789.GetImage(), "tmp.jpg");
            tit = Rule_biquzw789.GetTitle();
            by = Rule_biquzw789.GetAuthor();
            ot = Rule_biquzw789.GetInfo();
            CopyFileToThis(new File("tmp-hb.txt"), new File(DW_to + "/" + ID + ".html"));
            autoMake("tmp-dw.txt", "tmd-dw.zip", tit, "tmp.jpg", ".*第.*章.*", by, ot);
            if (!tit.isEmpty() && IsFile("tmd-dw.zip")) {
                CopyFileToThis(new File("tmd-dw.zip"), new File("dw/" + ID + ".zip"));
                new File("tmd-dw.zip").delete();
            }
        }
    }
}
