package com.tm.recycleritemdrager;

/**
 * Created by Tian on 2017/6/7.
 */

public class DataBean {

    public DataBean(int img) {
        this.imgRes = img;
    }

    private String name;
    private int imgRes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}
