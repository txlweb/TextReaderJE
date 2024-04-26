package com.teipreader.Lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class url_get {
    private List<String> l1 = new ArrayList<>();
    private List<String> l2 = new ArrayList<>();
    private List<String> l3 = new ArrayList<>();

    public void press(String url){
        int v1 = url.indexOf("?");
        String adds = "";
        if(v1!=-1){
            adds = url.substring(v1+1);
            String[] vl1 = adds.split("&");
            for (String s : vl1) {
                int v2 = s.indexOf("=");
                if (v2 != -1) {
                    this.l1.add(s.substring(0, v2));
                    this.l2.add(s.substring(v2+1));
                } else {
                    this.l3.add(s);
                }
            }
        }
    }
    public String get(String key){
        for (int i = 0; i < this.l1.size(); i++) {
            if(Objects.equals(this.l1.get(i), key)){
                return this.l2.get(i);
            }
        }
        return "";
    }
    public String get(int index){
        if(this.l2.size()>index){
            return this.l2.get(index);
        }
        if(this.l3.size()+ this.l2.size()>index){
            return this.l3.get(index-this.l2.size());
        }
        return "";
    }
    public int get_size(){
        return this.l3.size()+ this.l2.size();
    }
}
