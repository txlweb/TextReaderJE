package com.teipreader.Lib;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
public class check_code
{
    private static final int WIDTH = 200;
    private static final int HEIGHT = 80;
    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CHINESE_CHARACTERS = ""; // 特殊字符,不加因为太难认
    private static final int LINES_COUNT = 50;
    private static final int DOTS_COUNT = 150;

    public static String generateCaptchaText() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length() + CHINESE_CHARACTERS.length());
            if (index < CHARACTERS.length()) {
                sb.append(CHARACTERS.charAt(index));
            } else {
                sb.append(CHINESE_CHARACTERS.charAt(index - CHARACTERS.length()));
            }
        }
        return sb.toString();
    }

    public static BufferedImage generateCaptchaImage(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.BOLD, 36));
        g.drawString(text, 20, 40);
        drawLines(g);
        drawDots(g);
        g.dispose();
        return image;
    }

    private static void drawLines(Graphics g) {
        Random random = new Random();
        for (int i = 0; i < LINES_COUNT; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private static void drawDots(Graphics g) {
        Random random = new Random();
        for (int i = 0; i < DOTS_COUNT; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawOval(x, y, 1, 1);
        }
    }

    private static void saveCaptchaImage(BufferedImage image, String filePath) throws IOException {
        File file = new File(filePath);
        ImageIO.write(image, "png", file);
    }
}
