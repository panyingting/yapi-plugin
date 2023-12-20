package com.qbb.dto;

/**
 * header
 *
 * @author chengsheng@qbb6.com
 * @date 2019/5/9 10:11 PM
 */
public class YapiHeaderDTO {
    /**
     * Ãû³Æ
     */
    private String name;
    /**
     * Öµ
     */
    private String value;
    /**
     * ÃèÊö
     */
    private String desc;
    /**
     * Ê¾Àý
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
