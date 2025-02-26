package com.teipreader.Lib;

import com.teipreader.LibTextParsing.TextReaderLibVa;
import com.teipreader.Main.Config_dirs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AI_summary extends Thread{
    private String name="";
    public static String model=null;
    private int MaxThreadNumber = 1;
    private static class tread_ask extends Thread{
        private int code = -2;
        private int time = 0;
        private String body_text;
        private File fp;

        private tread_ask(String body_text_,File fp_){
            this.body_text=body_text_;
            this.fp=fp_;
        }

        @Override
        public void run() {
            code = -1;
            OllamaSummarizer tsk = new OllamaSummarizer("总结故事梗概（尽可能简洁），并分析文中出现的人物形象（仅主要人物），不要续写！"+body_text,model);
            tsk.start();
            while (tsk.getStreamState()!=0 && tsk.getStreamState()==-1){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                time += 10;
            }
            if(tsk.getStreamState()==-2){
                code = -2;
                return;
            }
            String summary = tsk.getStreamTextOutput();
            //System.out.println("章节总结: " + summary);

            try {
                if (!fp.delete()) {
                    if(!fp.createNewFile()){
                        System.out.println((char) 27 + "[31m[E]: 无法清理缓存/创建缓存！");
                    }
                }
            } catch (IOException e) {
                code = -2;
                throw new RuntimeException(e);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fp, true))) {
                writer.write(summary);
                writer.newLine();
            }catch (IOException e) {
                code = -2;
                throw new RuntimeException(e);
            }
            code = 0;
        }

        public int getCode() {
            return code;
        }

        public long getTime() {
            return time;
        }
    }
    private void find_cont_task(List<tread_ask> tasks){
        int cont_tasks = 0;
        System.out.println("AI 生成进度&统计");
        int i=0;
        for (tread_ask treadAsk : tasks) {
            //cont_tasks
            if (treadAsk.getCode() == -1) {
                cont_tasks++;
                System.out.println("["+i+"]: <"+treadAsk.getTime()/1000+"s>"+treadAsk.fp+" | code:-1 (正在运行)");
            }
            //retry
            if (treadAsk.getCode() == -2) {
                treadAsk.run();
                System.out.println("["+i+"]: <"+treadAsk.getTime()/1000+"s>"+treadAsk.fp+" | code:-2 (等待重启)");
            }
            if(treadAsk.getCode() == 0){
                tasks.remove(treadAsk);
                System.out.println("["+i+"]: <"+treadAsk.getTime()/1000+"s>"+treadAsk.fp+" | code:0 (已完成)");
                System.out.println("[*] 正在清理...");
                find_cont_task(tasks);
                break;
            }
            i++;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(cont_tasks>=MaxThreadNumber) find_cont_task(tasks);
    }
    public void run(){

        String body_text;
        int i = 0;
        List<File> csAddr = new ArrayList<>();
        if(Objects.equals(body_text = TextReaderLibVa.GetMainText_C(name, i), "ERROR")) {
            i++;
            body_text = TextReaderLibVa.GetMainText_C(name, i);

        }
        //System.out.println(body_text);
        int tasks = 0;
        List<tread_ask> task_list = new ArrayList<>();
        while (!Objects.equals(body_text, "ERROR")){

            File fp = new File(Config_dirs.TempPath+"/"+name+"_"+i+".txt");

            csAddr.add(fp);
            if(!fp.isFile()) {
                task_list.add(new tread_ask(body_text, fp));
                task_list.get(task_list.size() - 1).start();
            }
            body_text = TextReaderLibVa.GetMainText_C(name, i);
            i++;
            find_cont_task(task_list);

        }
        find_cont_task(task_list);
        csAddr.remove(csAddr.size()-1);
        StringBuilder fullSummary = new StringBuilder();
        for (File file : csAddr) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null) {
                    fullSummary.append(line).append("\n");
                    //System.out.println(line);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        OllamaSummarizer tsk = new OllamaSummarizer("总结故事情节大纲，并分析文中出现的人物形象（仅主要人物），不要续写！"+fullSummary.toString(),model);
        tsk.run();

        String finalOutline = tsk.getStreamTextOutput();
        System.out.println("最终的大纲: " + finalOutline);
        File fp = new File(Config_dirs.TempPath+"/"+name+"_final.txt");
        try {
            if (!fp.delete()) {
                if(!fp.createNewFile()){
                    System.out.println((char) 27 + "[31m[E]: 无法清理缓存/创建缓存！");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fp, true))) {
            writer.write(finalOutline);
            writer.newLine();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public AI_summary(String name_,String model_) {
        this.name=name_;
        model = model_;
    }

}
