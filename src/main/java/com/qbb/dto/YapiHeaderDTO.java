package com.qbb.dto;

/**
 * header
 *
 * @author chengsheng@qbb6.com
 * @date 2019/5/9 10:11 PM
 */
public class YapiHeaderDTO {
    /**
     * ����
     */
    private String name;
    /**
     * ֵ
     */
    private String value;
    /**
     * ����
     */
    private String desc;
    /**
     * ʾ��
     */
    private String example;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
