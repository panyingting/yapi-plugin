package com.qbb.dto;

import java.io.Serializable;

/**
 * �����б�
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/1 10:30 AM
 */
public class YapiCatResponse implements Serializable {
    /**
     * id
     */
    private Integer _id;
    /**
     * ����
     */
    private String name;
    /**
     * ��Ŀid
     */
    private Integer project_id;
    /**
     * ����
     */
    private String desc;
    /**
     * uid
     */
    private Integer uid;
    /**
     * ����
     */
    private Integer index;


    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
