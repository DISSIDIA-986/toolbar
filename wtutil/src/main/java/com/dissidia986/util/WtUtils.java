package com.dissidia986.util;

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

public class WtUtils {
	
	private static final AtomicLong incremental = new AtomicLong(System.currentTimeMillis());

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
	
}
