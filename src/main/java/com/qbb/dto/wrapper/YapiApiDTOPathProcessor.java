package com.qbb.dto.wrapper;

import com.qbb.dto.YapiApiDTO;

import java.util.Optional;

/**
 * @describe:
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/21 15:50
 */
public class YapiApiDTOPathProcessor {

    private static final String VERSION_SUFFIX = "_V002";

    public static String getPath(YapiApiDTO yapiApiDTO) {
        return Optional.ofNullable(yapiApiDTO.getPath()).orElse("") + VERSION_SUFFIX;
    }

    public static String getTitle(YapiApiDTO yapiApiDTO) {
        return Optional.ofNullable(yapiApiDTO.getTitle()).orElse("") + VERSION_SUFFIX;
    }
}
