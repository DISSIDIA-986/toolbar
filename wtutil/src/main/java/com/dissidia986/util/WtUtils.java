package com.dissidia986.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 公用函数
 * @author dissidia986
 *
 */
public class WtUtils {
	
	private static final AtomicLong incremental = new AtomicLong(System.currentTimeMillis());
	
	private static ThreadLocal<ObjectMapper>  mapper =new ThreadLocal<ObjectMapper>(){
		@Override
		public ObjectMapper initialValue() {
			ObjectMapper _mapper = new ObjectMapper();
			_mapper.getDeserializationConfig().with(DateUtil.getNorm_datetime_format());
			_mapper.getSerializationConfig().with(DateUtil.getNorm_datetime_format());
			//TODO 允许JSON串的key不用双引号包括
			_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			// to allow C/C++ style comments in JSON (non-standard, disabled by default)
			_mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			// to allow (non-standard) unquoted field names in JSON:
			_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			// to allow use of apostrophes (single quotes), non standard
			_mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

			// JsonGenerator.Feature for configuring low-level JSON generation:

			// to force escaping of non-ASCII characters:
			_mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			return _mapper;
		}
	};
	
	public static List<NameValuePair> getQueryMap(String url) throws URISyntaxException {
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		return params;
	}

	/**
	 * 获取Http请求的真实IP
	 * 
	 * @return
	 */
	public static String findRealIpUtil(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// String ip = request.getParameter("ip");
		return ip;
	}

	/**
	 *  TODO 如果字符串超过限制则截取，避免数据库保存时溢出
	 * @param origin 原始信息
	 * @param limit 限制长度
	 * @return 超过则进行截取，否则返回原始信息
	 */
	public static String subIfOutOfLimit(String origin, int limit) {
		String result = origin;
		if (StringUtils.isNotEmpty(origin) && origin.length() > limit) {
			result = origin.substring(0, limit);
		}
		return result;
	}

	public static Long createNonce() {

		return incremental.incrementAndGet();
	}
	/**
	 * 按参数的字母顺序排序后拼接URL的queryString
	 * @param params
	 * @return
	 */
	public static String createSortedUrl(Map<String, Object> params) {

		// use a TreeMap to sort the headers and parameters
		TreeMap<String, String> headersAndParams = new TreeMap<String, String>();
		for(String key : params.keySet()){
			headersAndParams.put(key, params.get(key).toString());
		}
		return createSortedUrl(headersAndParams);
	}


	public static String createSortedUrl(TreeMap<String, String> headersAndParams) {
		// build the url with headers and parms sorted
		String params = "";
		for (String key : headersAndParams.keySet()) {
			if (params.length() > 0) {
				params += "@";
			}
			params += key + "=" + headersAndParams.get(key).toString();
		}
		return params;
	}
	/**
	 * 获取线程安全的JSON序列化与反序列化转换器
	 * @return
	 */
	public static ObjectMapper getMapper(){
		return mapper.get();
	}
	/**
	 * JSON序列化
	 * @param obj 序列化对象
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String writeObjectAsString(Object obj) throws JsonProcessingException{
		return getMapper().writeValueAsString(obj);
	}
	/**
	 * JSON反序列化
	 * @param content JSON字符串
	 * @param valueType 结果的对象类型
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T readValue(String content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException{
		return getMapper().readValue(content, valueType);
	}
	
	/**
	 * JSON反序列化
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static JsonNode readTree(String json) throws JsonProcessingException, IOException{
		return getMapper().readTree(json);
	}
	
	/** 
	 * 功能：把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串 去掉空值与签名参数后的新签名参数组
	 */
	public static StringBuilder CreateLinkString(Map params){
			List keys = new ArrayList(params.keySet());
			Collections.sort(keys);
	
			StringBuilder prestr = new StringBuilder();
			String key="";
			String value="";
			for (int i = 0; i < keys.size(); i++) {
				key=(String) keys.get(i);
				value = (String) params.get(key);
				if("".equals(value) || value == null || 
						key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")){
					continue;
				}
				if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
					prestr.append(key).append("=").append(value);
				} else {
					prestr.append(key).append("=").append(value).append("&");
				}
				
			}
			return prestr.deleteCharAt(prestr.length()-1);
	}
	
	/**
	 * 将融宝支付POST过来反馈信息转换一下
	 * @param requestParams 返回参数信息
	 * @return Map 返回一个只有字符串值的MAP
	 * */
	public static Map transformRequestMap(Map requestParams){
		Map params = null;
		if(requestParams!=null && requestParams.size()>0){
			params = new HashMap();
			String name ="";
			String[] values =null;
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				name= (String) iter.next();
				values= (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				params.put(name, valueStr);
			}
		}
		return params;
	}
}
