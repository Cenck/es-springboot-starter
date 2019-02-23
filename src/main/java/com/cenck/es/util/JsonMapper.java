package com.cenck.es.util;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.type.JavaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

/**
 * 简单封装Jackson，实现JSON与Object的Mapper。封装不同的输出风格，使用不同的builder函数创建实例。
 */
public class JsonMapper {


	private ObjectMapper objectMapper;

	private JsonMapper() {
		this(null);
	}

	private JsonMapper(Inclusion inclusion) {
		objectMapper = new ObjectMapper();
		if (inclusion != null) {
			// 设置输出时包含属性的风格
			objectMapper.setSerializationInclusion(inclusion);
		}
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	private static JsonMapper	mapper	= new JsonMapper();

	public static JsonMapper getMapper() {
		return mapper;
	}

	public static JsonMapper builder() {
		return new JsonMapper();
	}

	public static JsonMapper builder(Inclusion... inclusions) {
		if (inclusions == null || inclusions.length == 0) {
			return new JsonMapper();
		}
		return new JsonMapper(inclusions[0]);
	}

	/**
	 * 将对象转换为JSON字符串。
	 * 
	 * @param object
	 *            待转换对象
	 * @return
	 */
	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void toJson(Object object, Writer w) {
		try {
			objectMapper.writeValue(w, object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将JSON字符串转换为Object。
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将JSON字符串转换为Object。
	 * 
	 * @param jsonString
	 * @param javaType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		try {
			return (T) objectMapper.readValue(jsonString, javaType);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 构造泛型的Collection Type<br>
	 * 如: <br>
	 * ArrayList<MyBean>,则调用constructCollectionType(ArrayList.class,MyBean.class) HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
	 */
	public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * 当JSON里只含有Bean的部分属性时，更新一个已存在Bean，只覆盖部分的属性。
	 */
	@SuppressWarnings("unchecked")
	public <T> T update(String jsonString, T object) {
		try {
			return (T) objectMapper.readerForUpdating(object).readValue(jsonString);
		} catch (JsonProcessingException e) {
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 输出JSONP格式数据
	 */
	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}

	/**
	 * 设定是否使用Enum的toString方法来读写Enum，为False时使用Enum的name()方法读写Enum，默认为Flase。<br>
	 * 注意：本方法一定要在Mapper创建后，所有的读写动作之前调用。
	 */
	public void enableEnumUseToString() {
		objectMapper.enable(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING);
		objectMapper.enable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
