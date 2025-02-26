package com.teipreader.Lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AI_Speech {

    //lib - 语句拆分
    //条件:句号拆分,如果超长那从不超长的逗号开始拆分
    //暂定的长度为 400 字
    public static List<String> text_split_by_speech(String In_File){
        List<String> ret = new ArrayList<>();
        //处理异常的HTML内容 PS:这玩意不能读啊!

        List<String> text = Arrays.asList(In_File.split(""));
        int c = 0;
        StringBuilder tret = new StringBuilder();
        for (int i = 0; i < text.size(); i++) {
            c++;
            tret.append(text.get(i));
            if(Objects.equals(text.get(i), "。") || Objects.equals(text.get(i), ".")){//如果他是直接碰上了句号,那就直接输出给朗读.
                if(tret.length()>=400){//超长了,要截短
                    for (int j = 0; j < i; j++) {
                        if(j*400+400<=tret.length()){
                            ret.add(tret.substring(j*400,j*400+400));
                        }else {
                            ret.add(tret.substring(j*400,tret.length()));
                            break;
                        }
                    }
                }else {
                    ret.add(tret.toString());
                }
                tret = new StringBuilder();
                c=0;
            }
        }
        if(c!=0){
            if(tret.length()>=400){//超长了,要截短
                for (int j = 0; j < text.size(); j++) {
                    if(j*400+400<=tret.length()){
                        ret.add(tret.substring(j*400,j*400+400));
                    }else {
                        ret.add(tret.substring(j*400,tret.length()));
                        break;
                    }
                }
            }else {
                ret.add(tret.toString());
            }
        }
        return ret;
    }

}
