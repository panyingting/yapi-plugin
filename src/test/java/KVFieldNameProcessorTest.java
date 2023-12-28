import com.google.gson.GsonBuilder;
import com.qbb.builder.KV;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @describe:
 * @author: pyt email:panyingting220415@credithc.com
 * @create_time: 2023/12/22 11:19
 */
public class KVFieldNameProcessorTest {

    private final String properties = "properties";

    private final String description = "description";
    @Test
    public void test() {

        KV kv = new GsonBuilder().create().fromJson(json, KV.class);
        process(kv);
        System.out.println(kv.toPrettyJson());;

    }

    @Test
    public void testPath() {
        String path = "/sdfsd/sdfsdf\\sdfswe\\sdfsd  sdfsd \\ ";
        System.out.println(path.replaceAll("\\W+", ""));
    }

    private void process(KV kv) {
        try {
            if (kv.containsKey(properties)){
                Object val = kv.get(properties);
                String json = new GsonBuilder().create().toJson(val);
                KV kvVal = new GsonBuilder().create().fromJson(json, KV.class);
                process(kvVal);

                Set keySet = new HashSet<>(kvVal.keySet());
                for (Object o : keySet) {
                    json = new GsonBuilder().create().toJson(kvVal.get(o));
                    KV innerKvVal = new GsonBuilder().create().fromJson(json, KV.class);
                    process(innerKvVal);
                    kvVal.set(o, innerKvVal);
                }


                kv.set(properties, kvVal);
            }
        } catch (Exception ex) {

        }
    }


    private static String json = "{\n" +
            "  \"type\": \"object\",\n" +
            "  \"title\": \"SingleResult<MyResp>\",\n" +
            "  \"required\": [],\n" +
            "  \"description\": \"SingleResult<MyResp> :SingleResult\",\n" +
            "  \"properties\": {\n" +
            "    \"data\": {\n" +
            "      \"type\": \"object\",\n" +
            "      \"description\": \"返回数据信息 ,MyResp\",\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "          \"type\": \"number\",\n" +
            "          \"description\": \"id信息\",\n" +
            "          \"mock\": {\n" +
            "            \"mock\": \"@integer\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"name\": {\n" +
            "          \"type\": \"string\",\n" +
            "          \"description\": \"用户返回名称\",\n" +
            "          \"mock\": {\n" +
            "            \"mock\": \"@string\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"age\": {\n" +
            "          \"type\": \"number\",\n" +
            "          \"description\": \"用户返回年龄\",\n" +
            "          \"mock\": {\n" +
            "            \"mock\": \"@integer\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"phone\": {\n" +
            "          \"type\": \"string\",\n" +
            "          \"description\": \"返回电话\",\n" +
            "          \"mock\": {\n" +
            "            \"mock\": \"@string\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"required\": []\n" +
            "    },\n" +
            "    \"code\": {\n" +
            "      \"type\": \"number\",\n" +
            "      \"description\": \"返回结果码：0-成功，其他失败\",\n" +
            "      \"mock\": {\n" +
            "        \"mock\": \"@integer\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"desc\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"结果描述信息\",\n" +
            "      \"mock\": {\n" +
            "        \"mock\": \"@string\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"i18n\": {\n" +
            "      \"type\": \"boolean\",\n" +
            "      \"description\": \"内部字段，是否是国际化的结果，用于判断是否需要再次国际化\",\n" +
            "      \"mock\": {\n" +
            "        \"mock\": \"@boolean\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
