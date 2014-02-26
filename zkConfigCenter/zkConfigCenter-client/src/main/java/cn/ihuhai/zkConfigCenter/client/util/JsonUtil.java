package cn.ihuhai.zkConfigCenter.client.util;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类
 * @author huhai
 *
 */
public class JsonUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 反序列化对象
	 * @param src json字符串
	 * @param valueType 目标对象类型
	 * @return
	 */
	public static Object deserialize(String src, Class<?> valueType){
		try {
			return objectMapper.readValue(src, valueType);
		} catch (Exception e) {
			LOGGER.error("serialize json to object error:" + src, e);
		} 
		return null;
	}
	
	/**
	 * 转化对象为字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			LOGGER.error("deserialize object to json error:" + obj, e);
		}
		
		return null;
	}
	
	/**
	 * 序列化对象
	 * @param obj 被序列化的对象
	 * @param os 输出流
	 */
	public static void serialize(Object obj, OutputStream os){
		try {
			objectMapper.writeValue(os, obj);
		} catch (Exception e) {
			LOGGER.error("deserialize object to json error:" + obj, e);
		}
	}
}
