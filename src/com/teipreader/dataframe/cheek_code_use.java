package com.teipreader.dataframe;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.UUID;

import static com.teipreader.Lib.check_code.generateCaptchaImage;
import static com.teipreader.Lib.check_code.generateCaptchaText;

public class cheek_code_use {
    private String cheek_code = "NOT_INIT_CHEEK_CODE";
    private String P_UUID = "NOT_INIT_UUID";
    private long create_time = -1;
    private boolean first_check = true;
    public String init(){
        //获取一个唯一的UUID
        this.P_UUID = UUID.randomUUID().toString();
        //生成check_code
        this.cheek_code = generateCaptchaText();
        //获取时间戳,便于销毁
        this.create_time = System.currentTimeMillis();
        return this.cheek_code;
    }
    public boolean check(String code){
        if(this.is_timeout()){//检验超时
            return false;
        }
        if(Objects.equals(this.cheek_code, code) //校验验证码
                && !Objects.equals(this.cheek_code, "NOT_INIT_CHEEK_CODE")  //防止被通杀
        ){
            //this.cheek_code = "NOT_INIT_CHEEK_CODE";
            return true;
        }
        return false;
    }
    public void ruin(){
        this.cheek_code = "NOT_INIT_CHEEK_CODE";
    }
    public boolean is_timeout(){
        return this.create_time + 5 * 60 * 1000 < System.currentTimeMillis() || Objects.equals(this.cheek_code, "NOT_INIT_CHEEK_CODE");
    }//检查时间和是否已经被销毁
    public BufferedImage get_code_img(){
        //没有初始化或超时,不能创建
        if(Objects.equals(this.cheek_code, "NOT_INIT_CHEEK_CODE") || this.is_timeout()) return new BufferedImage(0,0,BufferedImage.TYPE_INT_RGB);
        return generateCaptchaImage(this.cheek_code);
    }

    public String getP_UUID() {
        return P_UUID;
    }

    public boolean isFirst_check() {
        return first_check;
    }

    public void checked() {
        this.first_check = false;
    }
}
