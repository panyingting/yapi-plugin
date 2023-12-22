package com.qbb.builder;

import com.qbb.builder.encoding.AppTwoFieldConvertStrategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @describe: 针对多版本进行 fieldName 名字转换
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/21 16:17
 */
public class KVFieldNameProcessor {

    private AppTwoFieldConvertStrategy appTwoFieldConvertStrategy = new AppTwoFieldConvertStrategy();

    private String properties = "properties";

    public void process(KV kv) {
        if (Objects.nonNull(kv)) {
            Object val = kv.get(properties);
            if (val instanceof KV) {
                doProcessProperties((KV)val);
            }
        }
    }

    private void doProcessProperties(KV kv) {
        if (Objects.nonNull(kv)) {
            Set keySet = new HashSet(kv.keySet());
            for (Object key : keySet) {
                String fieldName = key.toString();
                Object value = kv.get(key);
                processValue(value);
                String convertName = appTwoFieldConvertStrategy.encoding(fieldName);
                kv.set(convertName, value);
                kv.remove(key);
            }
        }
    }

    private void processValue(Object value) {
        if (value instanceof KV) {
            process((KV)value);
        }
    }
}
