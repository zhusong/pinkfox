package co.xiaowangzi.debug.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import co.xiaowangzi.debug.log.Log;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonPrinter {

    private static ObjectMapper mapper = new ObjectMapper(new JsonFactory());

    static {
        // 设置序列化配置，为null的属性不加入到json中
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.registerModule(new JSR310Module());
//        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false); // map对象序列化的时候忽略值为null的key
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE).
//                setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    /**
     * 将对象转换成json字符串,如果转换失败则返回null
     *
     * @param o 需要转换为json的对象
     * @return 转换后的json字符串
     */
    public static String write2JsonStr(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json转换为对象 如果对象模版为内部类会出现问题，所以不要使用内部类
     *
     * @param json  要转换的json
     * @param clazz 要映射的类型
     * @return 转换成的目标对象，如果转换失败返回null
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)){
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json字符串转换为Map
     *
     * @param json 需要转换为Map的json字符串 {}开头结尾的
     * @return 转换后的map 如果转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String json) {
        try {
            if (StringUtils.isEmpty(json)) {
                return new HashMap<String, Object>();
            }
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json数组转换为List<Map<String,Object>> json数组格式[{},{}]
     *
     * @param jsonArray 需要转换的json数组
     * @return 转换后的列表 如果转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> jsonArray2List(String jsonArray) {
        try {
            return mapper.readValue(jsonArray, List.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json array 转换成list对象
     */
    public static <T> T jsonStr2Object(String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return (T) mapper.readValue(jsonString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Map<?, ?> json2MapObject(String json, Class<?> keyClazz, Class<?> valueClazz) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class, keyClazz, valueClazz);
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode json2Node(String json) {
        try {
            return mapper.readValue(json, JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void node2Writer(Writer writer, JsonNode node) {
        try {
            mapper.writeValue(writer, node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
