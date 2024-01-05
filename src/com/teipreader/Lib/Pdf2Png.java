package com.teipreader.Lib;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Pdf2Png {
    /**
     * 使用pdfbox将整个pdf转换成图片
     *
     * @param filename PDF文件名不带后缀名
     * @param type     图片类型 png 和jpg
     */
    public static StringBuilder pdf2png(String filename, String type, String SaveAs) {
        long startTime = System.currentTimeMillis();
        // 将文件地址和文件名拼接成路径 注意：线上环境不能使用\\拼接
        File file = new File(filename);
        PDDocument doc;
        try {
            // 写入文件
            doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            StringBuilder Blist = new StringBuilder();
            for (int i = 0; i < pageCount; i++) {
                // dpi为144，越高越清晰，转换越慢
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // 将图片写出到该路径下
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                //Blist.append("<img src=\"data:image/png;base64,").append(Base64.getEncoder().encodeToString(baos.toByteArray())).append("\">\r\n");
                Blist.append("<img src=\"" + (i + 1) + ".res" + "\">");
                ImageIO.write(image, type, new File(SaveAs + "/" + (i + 1) + ".res"));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("共耗时：" + ((endTime - startTime) / 1000.0) + "秒");  //转化用时
            return Blist;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

