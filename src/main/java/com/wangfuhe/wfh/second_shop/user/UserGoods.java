package com.wangfuhe.wfh.second_shop.user;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wfh on 2016/5/20.
 */
public class UserGoods extends BmobObject {
    private String category;
    private String describe;
    private Float nodegree;
    private Double maxprice;
    private Double minprice;
    private String wttrade;
    private Muser master;
    public Muser getCollector() {
        return collector;
    }

    public void setCollector(Muser collector) {
        this.collector = collector;
    }

    private Muser collector;
    private BmobFile pic1;
    private BmobFile pic2;
    private BmobFile pic3;
    private Boolean issellde;

    public Muser getMaster() {
        return master;
    }

    public void setMaster(Muser master) {
        this.master = master;
    }

    public Boolean getIssellde() {
        return issellde;
    }

    public void setIssellde(Boolean issellde) {
        this.issellde = issellde;
    }

    public Double getMinprice() {
        return minprice;
    }

    public void setMinprice(Double minprice) {
        this.minprice = minprice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(Double maxprice) {
        this.maxprice = maxprice;
    }

    public Float getNodegree() {
        return nodegree;
    }

    public void setNodegree(Float nodegree) {
        this.nodegree = nodegree;
    }

    public String getWttrade() {
        return wttrade;
    }

    public void setWttrade(String wttrade) {
        this.wttrade = wttrade;
    }

    public BmobFile getPic1() {
        return pic1;
    }

    public void setPic1(BmobFile pic1) {
        this.pic1 = pic1;
    }

    public BmobFile getPic2() {
        return pic2;
    }

    public void setPic2(BmobFile pic2) {
        this.pic2 = pic2;
    }

    public BmobFile getPic3() {
        return pic3;
    }

    public void setPic3(BmobFile pic3) {
        this.pic3 = pic3;
    }
}
