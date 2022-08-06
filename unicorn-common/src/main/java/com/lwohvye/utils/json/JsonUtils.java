/*
 *    Copyright (c) 2021-2022.  lWoHvYe(Hongyan Wang)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.lwohvye.utils.json;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.lwohvye.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Json工具类
 *
 * @author Hongyan Wang
 * @date 2021/11/10 11:42 下午
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class JsonUtils {
    // 加载速度太慢了，放在静态代码块中
    // private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper objectMapper;

    /**
     * 设置一些通用的属性
     */
    static {
        /*
        // Option 1, modifying when constructing JsonFactory
        JsonFactory f = JsonFactory.builder().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS).build();
        // Option 2, modifying when constructing JsonMapper or base type ObjectMapper
        JsonMapper m = JsonMapper.builder().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS).build();
        ObjectMapper m = JsonMapper.builder().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS).build();
        // Option 3: defining when creating ObjectReader instance
        ObjectReader r = mapper.readerFor(MyType.class).with(JsonReadFeature.ALLOW_JAVA_COMMENTS);
         */
        objectMapper = JsonMapper.builder()
                // 如果json中有新增的字段并且是实体类类中不存在的，不报错。即允许json串中有，而pojo中没有的属性
                // ones that do not map to a property, and there is no "any setter" or handler that can handle it
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 允许key没有双引号
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                // 允许key有单引号
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                // 允许整数以0开头
                .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
                // 允许字符串中存在回车换行控制符
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                // 允许有Java注释
                .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
                .build();
    }

    // region   toJSONString

    public static String toJSONString(Object obj) {
        return toJSONString(obj, () -> "", false);
    }

    public static String toFormatJSONString(Object obj) {
        return toJSONString(obj, () -> "", true);
    }

    /**
     * toJSONString 底层实现。简单类型直接返回
     *
     * @param obj             要转json的对象，若是String类型，不做处理
     * @param defaultSupplier 回调
     * @param format          是否格式化
     * @return java.lang.String
     * @date 2021/11/10 9:37 下午
     */
    public static String toJSONString(Object obj, Supplier<String> defaultSupplier, boolean format) {
        try {
            if (Objects.isNull(obj))
                return defaultSupplier.get();

            if (obj instanceof String str)
                return str;

            if (obj instanceof Number)
                return obj.toString();

            if (format)
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error(String.format("toJSONString %s", !Objects.isNull(obj) ? obj.toString() : "null"), e);
        }
        return defaultSupplier.get();
    }

    // endregion

    // region   toJavaObject
    public static <T> T toJavaObject(Object obj, Class<T> tClass) {
        return toJavaObject(obj, tClass, () -> null);
    }

    public static <T> T toJavaObject(InputStream inputStream, Class<T> tClass) {
        Reader reader = new InputStreamReader(inputStream);
        try {
            return objectMapper.readValue(reader, tClass);
        } catch (IOException e) {
            log.error(String.format("toJavaObject exception: %n %s%n %s", inputStream.getClass().getSimpleName(), tClass), e);
        }
        return null;
    }

    public static <T> T toJavaObject(String value, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(value, valueTypeRef);
        } catch (IOException e) {
            log.error(String.format("toJavaObject exception: %n %s%n %s", value, valueTypeRef), e);
        }
        return null;
    }

    /**
     * toJavaObject底层实现
     *
     * @param obj             要转的对象，不是String类型时，会先toJSONString
     * @param tClass          要转成的类型
     * @param defaultSupplier 回调
     * @return T
     * @date 2021/11/10 9:39 下午
     */
    public static <T> T toJavaObject(Object obj, Class<T> tClass, Supplier<T> defaultSupplier) {
        try {
            if (Objects.isNull(obj))
                return defaultSupplier.get();

//            如果obj本身就是type<T>，就没必要转了
            if (tClass.isInstance(obj))
                return tClass.cast(obj);

            var str = toJSONString(obj);
            return objectMapper.readValue(str, tClass);
        } catch (Exception e) {
            log.error(String.format("toJavaObject exception: %n %s%n %s", obj, tClass), e);
        }
        return defaultSupplier.get();
    }

    // endregion

    // region   toJavaObjectList

    public static <T> List<T> toJavaObjectList(Object obj, Class<T> tClass) {
        return toJavaObjectList(obj, tClass, Collections::emptyList);
    }

    /**
     * toJavaObjectList底层实现
     *
     * @param obj             要转的对象，不是String类型时，会先toJSONString
     * @param tClass          要转成的类型
     * @param defaultSupplier 回调
     * @return java.util.List<T>
     * @date 2021/11/10 9:45 下午
     */
    public static <T> List<T> toJavaObjectList(Object obj, Class<T> tClass, Supplier<List<T>> defaultSupplier) {
        try {
            if (Objects.isNull(obj))
                return defaultSupplier.get();

            var str = toJSONString(obj);
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, tClass);
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.error(String.format("toJavaObjectList exception %n%s%n%s", obj, tClass), e);
        }
        return defaultSupplier.get();
    }

    // endregion

    // 简单地直接用json复制或者转换(Cloneable)
    public static <T> T jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObject(obj, tClass) : null;
    }

    // region   toCollection

    public static Map<String, Object> toMap(Object obj) {
        return toJavaObject(obj, Map.class, Collections::emptyMap);
    }

    public static List toList(Object obj) {
        return toJavaObject(obj, List.class, Collections::emptyList);
    }

    // endregion

    // region 从map中获取指定类型的数据

    public static String getString(Map<String, Object> map, String key) {
        if (map.isEmpty())
            return "";

        var valueStr = String.valueOf(map.get(key));
        return StringUtils.isNotEmpty(valueStr) ? valueStr : "";
    }

    public static long getLong(Map<String, Object> map, String key) {
        if (map.isEmpty())
            return 0L;

        var valueStr = String.valueOf(map.get(key));
        if (StringUtils.isBlank(valueStr) || !StringUtils.isNumeric(valueStr))
            return 0L;
        return Long.parseLong(valueStr);
    }

    public static int getInt(Map<String, Object> map, String key) {
        if (map.isEmpty())
            return 0;

        var valueStr = String.valueOf(map.get(key));
        if (StringUtils.isBlank(valueStr) || !StringUtils.isNumeric(valueStr))
            return 0;
        return Integer.parseInt(valueStr);
    }

    // endregion
}


