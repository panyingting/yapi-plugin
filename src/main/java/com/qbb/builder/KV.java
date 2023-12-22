package com.qbb.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description: kv
 * @author: chengsheng@qbb6.com
 * @date: 2018/10/27
 */
public class KV<K, V> extends LinkedHashMap<K, V> {
    public <K, V> KV() {
    }

    public static <K, V> KV by(K key, V value) {
        return new KV().set(key, value);
    }

    public static <K, V> KV<K,V> create() {
        return new KV();
    }

    public KV set(K key, V value) {
        super.put(key, value);
        return this;
    }


    public KV set(KV KV) {
        super.putAll(KV);
        return this;
    }

    public String toPrettyJson() {
        KVFieldNameProcessor kvFieldNameProcessor = new KVFieldNameProcessor();
        kvFieldNameProcessor.process(this);
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public boolean equals(Object KV) {
        return KV instanceof KV && super.equals(KV);
    }


}
