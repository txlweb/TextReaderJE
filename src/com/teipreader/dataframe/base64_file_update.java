package com.teipreader.dataframe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.teipreader.Main.TeipMake.CopyFileToThis;

public class base64_file_update {
    private int blocks = 0;
    private int size = 0;
    private int now = 0;
    private StringBuffer data = new StringBuffer();
    private List<String> n = new ArrayList<>();
    private String hash = "";
    public void init_data(int block_num, int block_size) {
        this.blocks = block_num;
        this.size = block_size;
        for (int i = 0; i < block_num * block_size; i++) {
            this.data.append("#"); //添加占位符
        }
    }

    public boolean append_data(int index, String push_data) {
        if(this.n.contains(String.valueOf(index))){
            return false;
        }
        this.n.add(String.valueOf(index));
        this.data.replace(index * this.size, (index + 1) * this.size, push_data);
        this.now++;
        System.out.println(push_data.length());
        System.out.println(this.now+"/"+this.blocks);
        return is_final() != "";
    }

    public StringBuffer get_data() {
        return this.data;
    }

    public void clear_all() {
        this.data = null;
    }

    public String is_final() {
        if(this.blocks > this.now){
            return "";
        }
        this.data.delete(0, this.data.indexOf(",") + 1); // 删除第一个逗号及其前面的所有内容
        //System.out.println(this.data.toString());
        //修补尾段无效字节 执行size次检查,末尾是否是#,是则替换
        for (int i = 0; i < this.data.length(); i++) {
            //System.out.println(this.data.substring(this.data.length()-1));
            if(this.data.substring(this.data.length()-1).equals("#")){
                this.data.delete(this.data.length()-1,1);
            }
        }
        String tmp = "update/"+"temp_" + UUID.randomUUID() + ".tmp";
        try (FileOutputStream fileOutputStream = new FileOutputStream(tmp)) {
            int chunkSize = 1280;
            int position = 0;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            while (position < this.data.length()) {
                int currentChunkSize = Math.min(chunkSize, this.data.length() - position);
                String base64Chunk = this.data.substring(position, position + currentChunkSize);
                byte[] decodedBytes = Base64.getDecoder().decode(base64Chunk);
                //同步计算哈希
                digest.update(decodedBytes, 0, decodedBytes.length);
                System.out.println(position);
                System.out.println(this.data.length());
                fileOutputStream.write(decodedBytes,0,decodedBytes.length);
                position += currentChunkSize;
            }
            fileOutputStream.close();
            //cheek hash
            byte[] hash = digest.digest();
            String hash_code = bytesToHex(hash);
            this.hash = hash_code;
            CopyFileToThis(new File(tmp),new File("update/"+hash_code+".upd"));
            new File(tmp).delete();
            return hash_code;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static String bytesToHex(byte[] hash) {
        // 将字节转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getHash() {
        return hash;
    }
}