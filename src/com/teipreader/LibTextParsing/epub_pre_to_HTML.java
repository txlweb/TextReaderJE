package com.teipreader.LibTextParsing;

import com.teipreader.Main.Config_dirs;
import com.teipreader.Main.langunges;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import static com.teipreader.LibTextParsing.TextReaderLibVa.StrFixMainText;

public class epub_pre_to_HTML {
    //public static void main(String[] args) {
//        GetList("Xian-Wei-Jing-Xia-De-Da-Ming-Ma-Bo-Yong.epub");
//        GetPage("Xian-Wei-Jing-Xia-De-Da-Ming-Ma-Bo-Yong.epub","1");
    //GetImage("Xian-Wei-Jing-Xia-De-Da-Ming-Ma-Bo-Yong.epub");
    //}
    public static String GetList_HTML_TYPE(String name){
        return GetList(Config_dirs.MainPath+"/"+name+"/main.epub");
    }
    public static String GetMainText_HTML_TYPE(String name, int id) {
        return StrFixMainText(GetMainText_C(name, id), name, id, GetMaxSize(Config_dirs.MainPath+"/"+name+"/main.epub"));
    }
    public static String GetMainText_C(String name, int id) {
        return GetPage(Config_dirs.MainPath+"/"+name+"/main.epub", String.valueOf(id));
    }
    public static String GetList(String FileName){
        //FileName = Config_dirs.MainPath+"/"+FileName+"/main.epub";
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(Config_dirs.MainPath+"/"+FileName+"/main.epub"));
            //System.out.println(book.getContents());
            //System.out.println(book.getTableOfContents());
            List<Resource> ls = book.getContents();
            Metadata metadata = book.getMetadata();
            String LsHTML = "";
            //"\n\n封面图：";
            LsHTML = "\r\n<h1>" + metadata.getTitles().get(0) + "</h1>" +
                    "\n\n<p>作者：" + metadata.getAuthors().get(0) + "</p>" +
                    "\n\n语言：" + metadata.getLanguage();
            if(!metadata.getDescriptions().isEmpty())LsHTML = LsHTML +"\n\n<p>简介：" + metadata.getDescriptions().get(0) + "</p>";
            if(!metadata.getPublishers().isEmpty())LsHTML = LsHTML +"\n\n<p>出版社：" + metadata.getPublishers().get(0) + "</p>";
            for (int i = 0; i < ls.size(); i++) {
                LsHTML = MessageFormat.format("{0}<a class=\"book_list\" href=\"/{1}/{2}.html\" idx=\"{3}\">{4}{5}{6}{7}</a>\r\n", LsHTML, FileName, i + 1, i + 1, langunges.langunges[Config_dirs.LanguageID][4], i + 1, langunges.langunges[Config_dirs.LanguageID][5], ls.get(i).getTitle());
            }
            //System.out.println(LsHTML);
            return LsHTML;
//            TableOfContents tableOfContents = book.getTableOfContents();
//            System.out.println("目录资源数量为："+tableOfContents.size());
//            //获取到目录对应的资源数据
//            List<TOCReference> tocReferences = tableOfContents.getTocReferences();
//            for (TOCReference tocReference : tocReferences) {
//                Resource resource = tocReference.getResource();
//                byte[] data = resource.getData();
//                MediaType mediaType = resource.getMediaType();
//                System.out.println(new String(data, StandardCharsets.UTF_8));
//                System.out.println(mediaType.toString());
//                if(tocReference.getChildren().size()>0){
//                    //获取子目录的内容
//                    GetList(FileName,tocReference.getChildren());
//                }
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "";
    }
    public static int GetMaxSize(String FileName){
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
    public static String GetName(String FileName){
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
    public static String GetPage(String FileName,String ID){
        try {
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
                System.out.println(content);
                //System.out.println(mediaType);
                if(i == Integer.parseInt(ID)) return content;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "不存在的章节!";
    }
    public static byte[] GetImage(String FileName){
        try {
            EpubReader reader = new EpubReader();
            Book book = reader.readEpub(new FileInputStream(FileName));
            Resources spine = book.getResources();
            Resource a = spine.getById("cover");
            if(a != null) return a.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //return "不存在的章节!";
        return null;
    }
    public static void nome(String[] args) {

        File file = new File("Xian-Wei-Jing-Xia-De-Da-Ming-Ma-Bo-Yong.epub");
        InputStream in = null;
        try {
            //从输入流当中读取epub格式文件
            EpubReader reader = new EpubReader();
            in = new FileInputStream(file);
            Book book = reader.readEpub(in);
            //获取到书本的头部信息
            Metadata metadata = book.getMetadata();
            System.out.println("FirstTitle为："+metadata.getFirstTitle());
            //获取到书本的全部资源
            Resources resources = book.getResources();
            System.out.println("所有资源数量为："+resources.size());
            //获取所有的资源数据
            Collection<String> allHrefs = resources.getAllHrefs();
            for (String href : allHrefs) {
                Resource resource = resources.getByHref(href);
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                MediaType mediaType = resource.getMediaType();
            }
            //获取到书本的内容资源
            List<Resource> contents = book.getContents();
            System.out.println("内容资源数量为："+contents.size());
            //获取到书本的spine资源 线性排序
            Spine spine = book.getSpine();
            System.out.println("spine资源数量为："+spine.size());
            //通过spine获取所有的数据
            List<SpineReference> spineReferences = spine.getSpineReferences();
            for (SpineReference spineReference : spineReferences) {
                Resource resource = spineReference.getResource();
                byte[] data = resource.getData();
                String href = resource.getHref();
                String mediaType = resource.getMediaType().toString();
                String content = new String(data, "UTF-8");
                System.out.println(content);
                System.out.println(mediaType);
            }
            //获取到书本的目录资源
            TableOfContents tableOfContents = book.getTableOfContents();
            System.out.println("目录资源数量为："+tableOfContents.size());
            //获取到目录对应的资源数据
            List<TOCReference> tocReferences = tableOfContents.getTocReferences();
            for (TOCReference tocReference : tocReferences) {
                Resource resource = tocReference.getResource();
                //data就是资源的内容数据，可能是css,html,图片等等
                byte[] data = resource.getData();
                // 获取到内容的类型  css,html,还是图片
                MediaType mediaType = resource.getMediaType();
                if(tocReference.getChildren().size()>0){
                    //获取子目录的内容
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //一定要关闭资源
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
