package com.qbb.dto;

/**
 * PathVariable �������
 *
 * @author chengsheng@qbb6.com
 * @date 2019/5/24 2:24 PM
 */
public class YapiPathVariableDTO {
    /**
     * ����
     */
    private String name;
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


    public YapiPathVariableDTO() {
    }

    public YapiPathVariableDTO(String name, String desc, String example) {
        this.name = name;
        this.desc = desc;
        this.example = example;
    }
}
