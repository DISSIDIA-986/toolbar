package com.dissidia986.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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

	// TODO 如果字符串超过限制则截取
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
	
	public static ObjectMapper getMapper(){
		return mapper.get();
	}
	
	public String writeObjectAsString(Object obj) throws JsonProcessingException{
		return getMapper().writeValueAsString(obj);
	}
	
	public <T> T readValue(String content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException{
		return getMapper().readValue(content, valueType);
	}
	
}
