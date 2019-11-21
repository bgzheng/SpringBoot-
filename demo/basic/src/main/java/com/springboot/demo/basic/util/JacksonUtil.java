package com.springboot.demo.basic.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * json工具类
 */
public class JacksonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger (JacksonUtil.class);

	private static ObjectMapper objectMapper = new ObjectMapper ()
			.setSerializationInclusion (JsonInclude.Include.NON_NULL)
			// .setSerializationInclusion(JsonInclude.Include.ALWAYS)
			// .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
			// .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
			.disable (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable (SerializationFeature.FAIL_ON_EMPTY_BEANS)
			.setDateFormat (new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"))
			// 允许key不加引号
			.configure (JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
			// 允许单引号
			.configure (JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

	/**
	 * javaBean,list,array convert to json string
	 */
	public static String toJson (Object obj) {
		try {
			return JacksonUtil.objectMapper.writeValueAsString (obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace ();
			throw new RuntimeException ("obj to json failed!!!");
		}
	}

	/**
	 * json string convert to javaBean
	 */
	public static <T> T fromJson (String jsonStr, Class<T> clazz) {
		try {
			return JacksonUtil.objectMapper.readValue (jsonStr, clazz);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		return null;
	}

	public static <T> T fromJson (String jsonString, TypeReference<T> type) {
		if (StringUtils.isEmpty (jsonString)) {
			return null;
		}
		try {
			return (T) JacksonUtil.objectMapper.readValue (jsonString, type);
		} catch (IOException e) {
			e.printStackTrace ();
			return null;
		}
	}

	/**
	 * json string convert to map
	 */
	public static <T> Map<String, Object> json2Map (String jsonStr) {
		try {
			return JacksonUtil.objectMapper.readValue (jsonStr, Map.class);
		} catch (Exception e) {
			LOGGER.warn (jsonStr + " not a JSON string!! Exception:" + e.getMessage ());
		}
		return null;
	}

	/**
	 * json string convert to map with javaBean
	 */
	public static <T> Map<String, T> json2Map (String jsonStr, Class<T> clazz) {
		Map<String, T> map = null;
		try {
			map = JacksonUtil.objectMapper.readValue (jsonStr, new TypeReference<Map<String, T>> () {
			});
		} catch (Exception e) {
			LOGGER.warn (jsonStr + " not a JSON string!! Exception:" + e.getMessage ());
		}
		return map;
	}

	/**
	 * json array string convert to list with javaBean
	 */
	public static <T> List<T> json2List (String jsonArrayStr, Class<T> clazz) {
		List<T> result = new ArrayList<T> ();
		try {
			List<Map<String, Object>> list = JacksonUtil.objectMapper
					.readValue (jsonArrayStr, new TypeReference<List<Map<String, Object>>> () {
					});
			for (Map<String, Object> map : list) {
				result.add (JacksonUtil.map2pojo (map, clazz));
			}
		} catch (JsonParseException e) {
			e.printStackTrace ();
		} catch (JsonMappingException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
		return result;
	}

	/**
	 * map convert to javaBean
	 */
	public static <T> T map2pojo (Map<?, ?> map, Class<T> clazz) {
		return JacksonUtil.objectMapper.convertValue (map, clazz);
	}

	/**
	 * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调.
	 * 如果JSON字符串为null或"null"字符串,返回null. 如果JSON字符串为"[]",返回空集合.
	 */
	@SuppressWarnings ("unchecked")
	public static <T> T fromJson (String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty (jsonString)) {
			return null;
		}
		try {
			return (T) JacksonUtil.objectMapper.readValue (jsonString, javaType);
		} catch (IOException e) {
			return null;
		}
	}

	public static <T> T fromJson (String json, Class<T> clazz, Type type) {
		JavaType javaType = TypeFactory.defaultInstance ()
				.constructParametricType (clazz, TypeFactory.defaultInstance ().constructType (type));
		return JacksonUtil.fromJson (json, javaType);
	}

	public static void main (String[] args) {
		// String s = "[{id:\"\",flowId:\"7ca7bebc9be64b39a941559f6555cc36\"}]";
		String s = "[{id:'',flowId:'7ca7bebc9be64b39a941559f6555cc36'}]";
		List<LinkedHashMap<String, Object>> objs = JacksonUtil.fromJson (s, List.class);
		if (objs != null && objs.size () > 0) {
			for (int i = 0; i < objs.size (); i++) {
				Object object = objs.get (i);
				System.out.println (object.toString ());
			}
		}
		Object[] array = JacksonUtil.fromJson (s, Object[].class);
		if (array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				Object object = array[i];
				System.out.println (object.toString ());
			}
		}
	}

	public static String mapToJson (Map<String, Object> map) {
		ObjectMapper objectMapper = new ObjectMapper ();
		String json = null;
		try {
			json = objectMapper.writeValueAsString (map);
		} catch (JsonProcessingException e) {
			LOGGER.error ("map转json错误");
		}
		json = json.replace ("\\", "");
		return json;
	}

	public static String objectToJson (Object object) {
		ObjectMapper objectMapper = new ObjectMapper ();
		String json = null;
		try {
			json = objectMapper.writeValueAsString (object);
		} catch (JsonProcessingException e) {
			LOGGER.error ("object转json错误");
		}
		json = json.replace ("\\", "");
		return json;
	}

}
