package com.teipreader.LibTextParsing;

import com.teipreader.Main.Config_dirs;
import com.teipreader.Main.langunges;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static com.teipreader.LibTextParsing.TextReaderLibVa.StrFixMainText;

public class TextReaderLibVc {
    static int c = 0;

    public static String GetList_HTML_TYPE(String name) {
        return GetList(Config_dirs.MainPath + "/" + name + "/main.epub");
    }
    public static boolean IsEpubFile(String FileName){
        try {
            new EpubReader().readEpub(new FileInputStream(FileName));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static String GetMainText_HTML_TYPE(String name, int id) {
        return StrFixMainText(GetMainText_C(name, id), name, id, GetMaxSize(Config_dirs.MainPath + "/" + name + "/main.epub"));
    }

    public static String GetMainText_C(String name, int id) {
        return GetPage(name, String.valueOf(id));
    }

    public static String GetList(String FileName) {
        //FileName = Config_dirs.MainPath+"/"+FileName+"/main.epub";
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(Config_dirs.MainPath + "/" + FileName + "/main.epub"));
            //System.out.println(book.getContents());
            //System.out.println(book.getTableOfContents());
            List<Resource> ls = book.getContents();
            //System.out.println(book.getTableOfContents().getTocReferences().get(1).getTitle());
            Metadata metadata = book.getMetadata();
            String LsHTML = "";
            //"\n\n封面图：";
            LsHTML = "\r\n<h1>" + metadata.getTitles().get(0) + "</h1>" +
                    "\n\n<p>作者：" + metadata.getAuthors().get(0) + "</p>" +
                    "\n\n语言：" + metadata.getLanguage();
            if (!metadata.getDescriptions().isEmpty())
                LsHTML = LsHTML + "\n\n<p>简介：" + metadata.getDescriptions().get(0) + "</p>";
            if (!metadata.getPublishers().isEmpty())
                LsHTML = LsHTML + "\n\n<p>出版社：" + metadata.getPublishers().get(0) + "</p>";
            if (book.getTableOfContents().getTocReferences() == null || book.getTableOfContents().getTocReferences().isEmpty()) {
                LsHTML = getString(FileName, ls, LsHTML);
            } else {
                c = 0;
                LsHTML = LsHTML + parseMenu(FileName, book.getTableOfContents().getTocReferences(), 0, 0);
                if (c < ls.size() - 1) {
                    LsHTML = "\r\n<h1>" + metadata.getTitles().get(0) + "</h1>" +
                            "\n\n<p>作者：" + metadata.getAuthors().get(0) + "</p>" +
                            "\n\n语言：" + metadata.getLanguage();
                    LsHTML = getString(FileName, ls, LsHTML);
                }
            }

            //System.out.println(LsHTML);
            return LsHTML;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "";
    }

    private static String getString(String FileName, List<Resource> ls, String lsHTML) {
        lsHTML = lsHTML + "<p style=\"color:red;\">[目前正在查看资源列表,因为使用树状列表无法正常阅读!]</p>";
        for (int i = 0; i < ls.size(); i++) {
            lsHTML = MessageFormat.format("{0}<a class=\"book_list\" href=\"/{1}/{2}.html\" idx=\"{3}\">{4}{5}{6}{7}</a>\r\n", lsHTML, FileName, i, i, langunges.langunges[Config_dirs.LanguageID][4], i + 1, langunges.langunges[Config_dirs.LanguageID][5], ls.get(i).getTitle());
        }
        return lsHTML;
    }

    private static String parseMenu(String FileName, List<TOCReference> refs, int ix, int ln) {

        if (refs == null || refs.isEmpty()) {
            return "";
        }
        String ret = "";
        String p = "┝";
        int i;
        for (i = 0; i < refs.size(); i++) {
            c++;
            for (int j = 0; j < ln; j++) {
                p = p + "-- ";
            }
            if (p.equals("┝")) {
                ret = MessageFormat.format("{0}<a class=\"book_list\" idx=\"{1}\" title=\"{3}\">{2}{4}</a>\r\n",//#{5}:
                        ret, c,
                        p, i + 1,
                        refs.get(i).getTitle());
            } else {
                ret = MessageFormat.format("{0}<a class=\"book_list\" href=\"/{1}/{2}.html\" idx=\"{3}\" title=\"{5}\">{4}{6}</a>\r\n",//#{5}:
                        ret, FileName, c, c,
                        p, i + 1,
                        refs.get(i).getTitle());
            }

            p = "┝";
            //System.out.println(i + ix);
            ret = ret + parseMenu(FileName, refs.get(i).getChildren(), ix + i, ln + 1);
        }
        ix = i;
        return ret;
    }

    public static int GetMaxSize(String FileName) {
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            List<Resource> ls = book.getContents();
            return ls.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "";
    }

    public static String GetName(String FileName) {
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            List<Resource> ls = book.getContents();
            return book.getMetadata().getTitles().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "";
    }

    public static String GetPage(String Name, String ID) {
        try {
            String FileName = Config_dirs.MainPath + "/" + Name + "/main.epub";
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            Spine spine = book.getSpine();
            //System.out.println("spine资源数量为："+spine.size());
            //通过spine获取所有的数据
            List<SpineReference> spineReferences = spine.getSpineReferences();
            for (int i = 0; i < spineReferences.size(); i++) {
                SpineReference spineReference = spineReferences.get(i);
                Resource resource = spineReference.getResource();
                byte[] data = resource.getData();
                String href = resource.getHref();
                String mediaType = resource.getMediaType().toString();
                String content = new String(data, book.getContents().get(Integer.parseInt(ID)).getInputEncoding());
                content = content.replace("<style", "<!--");
                content = content.replace("style>", "-->");
                content = content.replace("xlink:href=\"", "xlink:href=\"/EpubRes/?" + Name + "?");

                //System.out.println(mediaType);
                if (i == Integer.parseInt(ID)) return content;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "不存在的章节!";
    }

    public static String GetInfo(String FileName) {
        //FileName = Config_dirs.MainPath+"/"+FileName+"/main.epub";
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(Config_dirs.MainPath + "/" + FileName + "/main.epub"));
            //System.out.println(book.getContents());
            //System.out.println(book.getTableOfContents());
            List<Resource> ls = book.getContents();
            Metadata metadata = book.getMetadata();
            String LsHTML = "";
            LsHTML = String.format(",{\"name\":\"[EPUB]%s\",\"by\":\"%s\"",
                    metadata.getTitles().get(0),
                    metadata.getAuthors().get(0)
            );
            if (!metadata.getDescriptions().isEmpty())
                LsHTML = LsHTML + "\"ot\":\"" + metadata.getDescriptions().get(0) + "\"";
            LsHTML = LsHTML + ",\"md5\":\"" + FileName + "\"}";
            return LsHTML;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "";
    }

    public static byte[] GetTitImg(String FileName) {
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            Resources spine = book.getResources();
            Resource a = spine.getById("cover");
            if (a != null) return a.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "不存在的章节!";
        return null;
    }

    public static byte[] GetImg(String Name, String path) {
        String FileName = Config_dirs.MainPath + "/" + Name + "/main.epub";
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            Resources spine = book.getResources();
            Resource a = spine.getByHref(path);
            if (a != null) return a.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "不存在的章节!";
        return null;
    }
}