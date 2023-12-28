package com.qbb.builder;

import com.qbb.builder.encoding.AppTwoFieldConvertStrategy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @describe: 针对多版本进行 fieldName 名字转换
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/21 16:17
 */
public class KVFieldNameProcessor {

    private final AppTwoFieldConvertStrategy appTwoFieldConvertStrategy = new AppTwoFieldConvertStrategy();

    private static final String properties = "properties";

    private static final String arrayProperties = "items";

    private static final String description = "description";

    private static final String type = "type";
    private static final String mock = "mock";

    KVFieldNameOutputProcessor outputProcessor = new KVFieldNameOutputProcessor();


    public void process(KV kv) {

        outputProcessor.openFileAndRead();

        if (Objects.nonNull(kv)) {
            Object val = kv.get(properties);
            if (val instanceof KV) {
                doProcessProperties((KV)val);
            }
        }

        outputProcessor.writeToFile();
    }

    private void process(KV kv, String originFieldName, String newFieldName) {
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
                String fieldDesc = String.valueOf(kv.getOrDefault(description, "无描述"));
                kv.set(description, fieldDesc + String.format("【%s】", originFieldName));
                outputProcessor.addField(originFieldName, newFieldName, fieldDesc);
            }
        }
    }

    private void doProcessProperties(KV kv) {
        if (Objects.nonNull(kv)) {
            Set keySet = new HashSet(kv.keySet());
            for (Object key : keySet) {
                String fieldName = key.toString();
                Object value = kv.get(key);
                String convertName = appTwoFieldConvertStrategy.encoding(fieldName);

                // 对值进行处理
                processValue(value, fieldName, convertName);

                kv.set(convertName, value);
                kv.remove(key);
            }
        }
    }

    private void processValue(Object value, String originFieldName, String newFieldName) {
        if (value instanceof KV) {
            process((KV)value, originFieldName, newFieldName);
        }
    }


}
