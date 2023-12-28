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

    private final String properties = "properties";

    private final String arrayProperties = "items";

    private final String description = "description";

    private final String type = "type";
    private final String mock = "mock";


    public void process(KV kv) {
        if (Objects.nonNull(kv)) {
            Object val = kv.get(properties);
            if (val instanceof KV) {
                doProcessProperties((KV)val);
            }
        }
    }

    private void process(KV kv, String originFieldName) {
        if (Objects.nonNull(kv)) {

            KV kvForProperties = kv;
            Object arrayKv = kv.get(arrayProperties);
            if (Objects.nonNull(arrayKv)) {
                kvForProperties = (KV)arrayKv;
            }

            Object val = kvForProperties.get(properties);
            if (val instanceof KV) {
                doProcessProperties((KV)val);
            }
            if (kv.containsKey(description) || (kv.containsKey(type) || kv.containsKey(mock))) {
                kv.set(description, kv.getOrDefault(description, "无描述") + String.format("【%s】", originFieldName));
            }
        }
    }

    private void doProcessProperties(KV kv) {
        if (Objects.nonNull(kv)) {
            Set keySet = new HashSet(kv.keySet());
            for (Object key : keySet) {
                String fieldName = key.toString();
                Object value = kv.get(key);
                processValue(value, fieldName);
                String convertName = appTwoFieldConvertStrategy.encoding(fieldName);
                kv.set(convertName, value);
                kv.remove(key);
            }
        }
    }

    private void processValue(Object value, String originFieldName) {
        if (value instanceof KV) {
            process((KV)value, originFieldName);
        }
    }
}
