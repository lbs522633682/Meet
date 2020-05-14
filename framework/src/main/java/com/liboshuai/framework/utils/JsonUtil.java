package com.liboshuai.framework.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Bob.xu
 * @version 20160120
 */
public class JsonUtil {

    private static String[] litepalExcludes = {"baseObjId", "associatedModelsMapWithFK",
            "associatedModelsMapWithoutFK", "associatedModelsMapForJoinTable",
            "listToClearSelfFK", "listToClearAssociatedFK", "fieldsToSetToDefault"};

    /**
     * 将对象转换成Json字符串
     *
     * @param obj
     * @return json类型字符串
     */
    public static synchronized String toJSON(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        JSONStringer js = new JSONStringer();
        serialize(js, obj);
        return js.toString();
    }

    /**
     * 序列化为JSON
     *
     * @param js json对象
     * @param o  待需序列化的对象
     */
    private static void serialize(JSONStringer js, Object o) {
        if (isNull(o)) {
            try {
                js.value(null);
            } catch (JSONException e) {
                LogUtils.e(e.getMessage());
            }
            return;
        }

        Class<?> clazz = o.getClass();
        if (isArray(clazz)) { // 数组
            serializeArray(js, o);
        } else if (isCollection(clazz)) { // 集合
            Collection<?> collection = (Collection<?>) o;
            serializeCollect(js, collection);
        } else if (isMap(clazz)) { // 集合
            HashMap<?, ?> collection = (HashMap<?, ?>) o;
            serializeMap(js, collection);
        } else if (Date.class.equals(clazz)) { // Date单独拿出
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
            try {
                js.value(sdf.format((Date) o));
            } catch (JSONException e) {
                LogUtils.e(e.getMessage());
            }
        } else if (isSingle(clazz)) { // 单个值
            try {
                js.value(o);
            } catch (JSONException e) {
                LogUtils.e(e.getMessage());
            }
        } else {
            serializeObject(js, o);
        }
    }

    /**
     * 序列化数组
     *
     * @param js    json对象
     * @param array 数组
     */
    private static void serializeArray(JSONStringer js, Object array) {
        try {
            js.array();
            for (int i = 0; i < Array.getLength(array); ++i) {
                Object o = Array.get(array, i);
                serialize(js, o);
            }
            js.endArray();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 序列化集合
     *
     * @param js         json对象
     * @param collection 集合
     */
    private static void serializeCollect(JSONStringer js,
                                         Collection<?> collection) {
        try {
            js.array();
            for (Object o : collection) {
                serialize(js, o);
            }
            js.endArray();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 序列化Map
     *
     * @param js  json对象
     * @param map map对象
     */
    private static void serializeMap(JSONStringer js, Map<?, ?> map) {
        try {
            js.object();
            @SuppressWarnings("unchecked")
            Map<String, Object> valueMap = (Map<String, Object>) map;
            Iterator<Map.Entry<String, Object>> it = valueMap.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
                        .next();
                js.key(entry.getKey());
                serialize(js, entry.getValue());
            }
            js.endObject();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 序列化对象
     *
     * @param js  json对象
     * @param obj 待序列化对象
     */
    private static void serializeObject(JSONStringer js, Object obj) {
        try {
            js.object();
            Class<? extends Object> objClazz = obj.getClass();
            ArrayList<Field> fields = new ArrayList<Field>();
            getDeclaredFields(fields, objClazz);
            for (Field field : fields) {
                if (isFieldStaticFinal(field)) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object fieldVal = field.get(obj);
                    if (fieldVal != null) {
                        js.key(field.getName());
                        serialize(js, fieldVal);
                    }
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
            js.endObject();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    private static void fieldSet(Field field, Object obj, Object value)
            throws Exception {
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    /**
     * 给对象的字段赋值
     *
     * @param obj       类实例
     * @param field     字段方法
     * @param fieldType 字段类型
     * @param value
     * @throws ParseException
     */
    private static void setFiedlValue(Object obj, Field field,
                                      String fieldType, Object value) throws Exception {
        if (null != value) {
            if ("String".equals(fieldType)) {
                fieldSet(field, obj, value.toString());
            } else if ("Date".equals(fieldType)) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
                Date temp = sdf.parse(value.toString());
                fieldSet(field, obj, temp);
            } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                Integer temp = Integer.parseInt(value.toString());
                fieldSet(field, obj, temp);
            } else if ("Character".equals(fieldType)
                    || "char".equals(fieldType)) {
                char temp = value.toString().charAt(0);
                fieldSet(field, obj, temp);
            } else if ("Short".equalsIgnoreCase(fieldType)) {
                short temp = Short.parseShort(value.toString());
                fieldSet(field, obj, temp);
            } else if ("Long".equalsIgnoreCase(fieldType)) {
                Long temp = Long.parseLong(value.toString());
                fieldSet(field, obj, temp);
            } else if ("Double".equalsIgnoreCase(fieldType)) {
                Double temp = Double.parseDouble(value.toString());
                fieldSet(field, obj, temp);
            } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                Boolean temp = Boolean.parseBoolean(value.toString());
                fieldSet(field, obj, temp);
            } else {
                fieldSet(field, obj, value);
            }
        }
    }

    /**
     * 反序列化简单对象
     *
     * @param jo    json对象
     * @param clazz 实体类类型
     * @return 反序列化后的实例
     * @throws JSONException
     */
    private static <T> T parseObject(JSONObject jo, Class<T> clazz)
            throws JSONException {
        if (clazz == null || isNull(jo)) {
            return null;
        }

        T obj = newInstance(clazz);
        if (obj == null) {
            return null;
        }
        ArrayList<Field> fields = new ArrayList<Field>();
        getDeclaredFields(fields, clazz);
        for (Field f : fields) {
            if (!isFieldStaticFinal(f)) {
                setField(obj, f, jo);
            }
        }
        return obj;
    }

    /**
     * 反序列化简单对象
     *
     * @param jsonStr json字符串
     * @param clazz   实体类类型
     * @return 反序列化后的实例
     * @throws JSONException
     */
    public static synchronized <T> T parseObject(String jsonStr, Class<T> clazz) {
        if (clazz == null || jsonStr == null || jsonStr.length() == 0) {
            return null;
        }

        JSONObject jo = null;
        try {
            jo = new JSONObject(jsonStr);
            if (isNull(jo)) {
                return null;
            }
            return parseObject(jo, clazz);
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
        return null;
    }

    /**
     * 解析所有型数组(含int[] long[]等基本型)
     *
     * @param ja    json数组
     * @param clazz 实体类类型
     * @return 反序列化后的数组
     */
    private static Object parseAllKindArray(JSONArray ja, Class<?> clazz) {
        if (clazz == null || isNull(ja)) {
            return null;
        }

        int len = ja.length();
        Object array = Array.newInstance(clazz, len);

        try {
            String clsName = clazz.getSimpleName();
            for (int i = 0; i < len; ++i) {
                // 下面前几个需要强转的，所以单独列出
                if (clsName.equalsIgnoreCase("byte")) {
                    Array.set(array, i, (byte) ja.getInt(i));
                } else if (clsName.equalsIgnoreCase("short")) {
                    Array.set(array, i, (short) ja.getInt(i));
                } else if (clsName.equalsIgnoreCase("float")) {
                    Array.set(array, i, (float) ja.getDouble(i));
                } else if (clazz.equals(char.class)) {
                    Array.set(array, i, (char) ja.getString(i).charAt(0));
                } else if (clazz.equals(Date.class)) {
                    String str = ja.getString(i);
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
                    Date temp = sdf.parse(str);
                    Array.set(array, i, temp);
                } else if (isSingle(clazz)) {
                    Array.set(array, i, ja.get(i));
                } else {
                    Array.set(array, i,
                            parseObject((JSONObject) ja.get(i), clazz));
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }

        return array;
    }

    /**
     * 反序列化数组对象(含int[] long[]等基本型)
     *
     * @param jsonStr json字符串
     * @param clazz   实体类类型
     * @return 序列化后的数组
     */
    public static synchronized Object parseArray(String jsonStr, Class<?> clazz) {
        if (clazz == null || jsonStr == null || jsonStr.length() == 0) {
            return null;
        }
        JSONArray ja = null;
        try {
            ja = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isNull(ja)) {
            return null;
        }

        return parseAllKindArray(ja, clazz);
    }

    /**
     * 反序列化泛型集合
     *
     * @param ja              json数组
     * @param collectionClazz 集合类型
     * @param genericType     实体类类型
     * @return
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private static <T> Collection<T> parseCollection(JSONArray ja,
                                                     Class<?> collectionClazz, Class<T> genericType)
            throws JSONException {

        if (collectionClazz == null || genericType == null || isNull(ja)) {
            return null;
        }

        Collection<T> collection = (Collection<T>) newInstance(collectionClazz);

        for (int i = 0; i < ja.length(); ++i) {
            try {
                Object obj = ja.get(i);
                if (isSingle(obj.getClass())) {
                    collection.add((T) obj);
                } else {
                    T o = parseObject((JSONObject) obj, genericType);
                    collection.add(o);
                }
            } catch (JSONException e) {
                LogUtils.e(e.getMessage());
            }
        }

        return collection;
    }

    /**
     * 反序列化泛型集合
     *
     * @param jsonStr         json字符串
     * @param collectionClazz 集合类型
     * @param genericType     实体类类型
     * @return 反序列化后的数组
     * @throws JSONException
     */
    public static synchronized <T> Collection<T> parseCollection(
            String jsonStr, Class<?> collectionClazz, Class<T> genericType) {
        if (collectionClazz == null || genericType == null || jsonStr == null
                || jsonStr.length() == 0) {
            return null;
        }
        if (!isCollection(collectionClazz)) {
            try {
                throw new JSONException("is Not Collection: " + collectionClazz);
            } catch (JSONException e) {
                LogUtils.e(e.getMessage());
            }
        }
        JSONArray jo = null;
        try {
            // 如果为数组，则此处转化时，需要去掉前面的键，直接后面的[]中的值
            int index = jsonStr.indexOf("[");
            String arrayString = null;

            // 获取数组的字符串
            if (-1 != index) {
                arrayString = jsonStr.substring(index);
            }

            // 如果为数组，使用数组转化
            if (null != arrayString) {
                jo = new JSONArray(arrayString);
            } else {
                jo = new JSONArray(jsonStr);
            }

        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }

        if (isNull(jo)) {
            return null;
        }

        try {
            return parseCollection(jo, collectionClazz, genericType);
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
        return null;
    }

    /**
     * 根据类型创建对象
     *
     * @param clazz 待创建实例的类型
     * @return 实例对象
     * @throws JSONException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> T newInstance(Class<T> clazz) throws JSONException {
        if (clazz == null)
            return null;
        T obj = null;
        if (clazz.isInterface()) {
            if (clazz.equals(Map.class)) {
                obj = (T) new HashMap();
            } else if (clazz.equals(List.class)) {
                obj = (T) new ArrayList();
            } else if (clazz.equals(Set.class)) {
                obj = (T) new HashSet();
            } else {
                throw new JSONException("unknown interface: " + clazz);
            }
        } else {
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                throw new JSONException("unknown class type: " + clazz);
            }
        }
        return obj;
    }

    /**
     * 设定Map的值
     *
     * @param obj 待赋值字段的对象
     * @param jo  json实例
     */
    @SuppressWarnings("unchecked")
    private static void setMapField(Object obj, JSONObject jo,
                                    Class<?> genericType) {
        if (isNull(jo)) {
            return;
        }
        try {
            Iterator<String> keyIter = jo.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = (Map<String, Object>) obj;
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jo.get(key);
                if (isSingle(genericType)) {
                    if (isNull(value))
                        valueMap.put(key, null);
                    else
                        valueMap.put(key, value);
                } else {
                    Object o = parseObject((JSONObject) value, genericType);
                    valueMap.put(key, o);
                }
            }
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 设定字段的值
     *
     * @param obj   待赋值字段的对象
     * @param field 字段
     * @param jo    json实例
     */
    private static void setField(Object obj, Field field, JSONObject jo) {
        String name = field.getName();
        Class<?> clazz = field.getType();
        try {
            if (isArray(clazz)) { // 数组
                JSONArray ja = jo.optJSONArray(name);
                if (!isNull(ja)) {
                    Object array = parseAllKindArray(ja, clazz.getComponentType());
                    setFiedlValue(obj, field, clazz.getSimpleName(), array);
                }
            } else if (isCollection(clazz)) { // 泛型集合
                // 获取定义的泛型类型
                JSONArray ja = jo.optJSONArray(name);
                if (!isNull(ja)) {
                    Class<?> c = null;
                    Type gType = field.getGenericType();
                    if (gType instanceof ParameterizedType) {
                        ParameterizedType ptype = (ParameterizedType) gType;
                        Type[] targs = ptype.getActualTypeArguments();
                        if (targs != null && targs.length > 0) {
                            Type t = targs[0];
                            c = (Class<?>) t;
                        }
                    }
                    Object o = parseCollection(ja, clazz, c);
                    setFiedlValue(obj, field, clazz.getSimpleName(), o);
                }
            } else if (isSingle(clazz)) { // 值类型
                Object o = jo.opt(name);
                if (o != null) {
                    setFiedlValue(obj, field, clazz.getSimpleName(), o);
                }
            } else if (isMap(clazz)) { // Map
                JSONObject j = jo.optJSONObject(name);
                if (!isNull(j)) {
                    Class<?> c = null;
                    Type gType = field.getGenericType();
                    if (gType instanceof ParameterizedType) {
                        ParameterizedType ptype = (ParameterizedType) gType;
                        Type[] targs = ptype.getActualTypeArguments();
                        if (targs != null && targs.length > 0) {
                            Type t = targs[1];
                            c = (Class<?>) t;
                        }
                    }
                    Object o = newInstance(clazz);
                    setMapField(o, j, c);
                    setFiedlValue(obj, field, clazz.getSimpleName(), o);
                }
            } else if (clazz != null) { // 对象
                JSONObject j = jo.optJSONObject(name);
                if (!isNull(j)) {
                    Object o = parseObject(j, clazz);
                    setFiedlValue(obj, field, clazz.getSimpleName(), o);
                }
            } else {
                throw new Exception("unknow type!");
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 实例
     * @return
     */
    private static boolean isNull(Object obj) {
        return obj == null || JSONObject.NULL.equals(obj);
    }

    /**
     * 判断是否是值类型
     *
     * @param clazz
     * @return
     */
    private static boolean isSingle(Class<?> clazz) {
        return isBoolean(clazz) || isNumber(clazz) || isString(clazz)
                || Date.class.equals(clazz) || Object.class.equals(clazz);
    }

    /**
     * 是否布尔值
     *
     * @param clazz
     * @return
     */
    private static boolean isBoolean(Class<?> clazz) {
        return (clazz != null)
                && ((Boolean.TYPE.isAssignableFrom(clazz)) || (Boolean.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 是否数值
     *
     * @param clazz
     * @return
     */
    private static boolean isNumber(Class<?> clazz) {
        return (clazz != null)
                && ((Byte.TYPE.isAssignableFrom(clazz))
                || (Short.TYPE.isAssignableFrom(clazz))
                || (Integer.TYPE.isAssignableFrom(clazz))
                || (Long.TYPE.isAssignableFrom(clazz))
                || (Float.TYPE.isAssignableFrom(clazz))
                || (Double.TYPE.isAssignableFrom(clazz)) || (Number.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 判断是否是字符串
     *
     * @param clazz
     * @return
     */
    private static boolean isString(Class<?> clazz) {
        return (clazz != null)
                && ((String.class.isAssignableFrom(clazz))
                || (Character.TYPE.isAssignableFrom(clazz)) || (Character.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 判断是否是数组
     *
     * @param clazz
     * @return
     */
    private static boolean isArray(Class<?> clazz) {
        return clazz != null && clazz.isArray();
    }

    /**
     * 判断是否是集合
     *
     * @param clazz
     * @return
     */
    private static boolean isCollection(Class<?> clazz) {
        return clazz != null && Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 判断是否是Map
     *
     * @param clazz
     * @return
     */
    private static boolean isMap(Class<?> clazz) {
        return clazz != null && Map.class.isAssignableFrom(clazz);
    }

    private static void getDeclaredFields(ArrayList<Field> fieldlist,
                                          Class<?> cls) {
        if (fieldlist == null || cls == null) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        fieldlist.addAll(Arrays.asList(fields));
        Class<?> superCls = cls.getSuperclass();
        // 避开Object类,规避Android5.0以后google对Object类的修改导致的兼容性错误
        if (superCls != null && !Object.class.equals(superCls)) {
            getDeclaredFields(fieldlist, superCls);
        }
    }

    private static boolean isFieldStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
    }
}
