package com.wangfuhe.wfh.second_shop.user;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wfh on 2016/5/20.
 */
public class Muser extends BmobUser {
    private Boolean gender;
    private String userqq;
    private BmobFile toppic;
    private String useraddress;

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getUserqq() {
        return userqq;
    }

    public void setUserqq(String userqq) {
        this.userqq = userqq;
    }

    public BmobFile getToppic() {
        return toppic;
    }

    public void setToppic(BmobFile toppic) {
        this.toppic = toppic;
    }

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress;
    }
}
