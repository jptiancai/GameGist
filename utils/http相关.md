
```
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;

public class HttpUtil {

	public static String httpPost(String url,Map<String,String> paramMap, int timeout) throws HttpException, IOException {
		
		List<NameValuePair> valueList = new ArrayList<NameValuePair>();
		Iterator<String> it = paramMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = paramMap.get(key);
			NameValuePair nv = new NameValuePair(key,value);
			valueList.add(nv);
		}
			
		NameValuePair[] valueArray = valueList.toArray(new NameValuePair[0]);
		
		String response = null;
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		PostMethod method = new PostMethod(url);

		method.getParams().setContentCharset("UTF-8");
		if(timeout > 0){
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout); 
		}
		method.setRequestBody(valueArray);
		
		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			//logger.error("post failed,status="+status+",url="+url+",data="+data);
			return response;
		}
		
		response = method.getResponseBodyAsString();
		
		return response;
	}
	
	public static String httpPost(String url,Map<String,String> paramMap) throws HttpException, IOException {
		return httpPost(url, paramMap, -1);
	}
	
public static String httpPost(String url,Map<String,String> paramMap,String cookie) throws HttpException, IOException {
		
		List<NameValuePair> valueList = new ArrayList<NameValuePair>();
		Iterator<String> it = paramMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = paramMap.get(key);
			NameValuePair nv = new NameValuePair(key,value);
			valueList.add(nv);
		}
			
		NameValuePair[] valueArray = valueList.toArray(new NameValuePair[0]);
		
		String response = null;
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		PostMethod method = new PostMethod(url);
		
		method.setRequestHeader("Cookie", cookie);

		method.getParams().setContentCharset("UTF-8");
		method.setRequestBody(valueArray);
		
		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			return response;
		}
		
		response = method.getResponseBodyAsString();
		
		return response;
	}
	
	public static String httpPostData(String host, int port , String url,String data) throws HttpException, IOException {
		
		String response = null;
		HttpClient client = new HttpClient();
		HostConfiguration hostconf = new HostConfiguration();
		HttpHost httpHost = new HttpHost(url);
		hostconf.setHost(host, port);
		//hostconf.setProxy("192.168.1.9", 8888);
		client.setHostConfiguration(hostconf);
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		PostMethod method = new PostMethod(url);

		RequestEntity stringEntity = new StringRequestEntity(data,"text/plain", "UTF-8");
		long contentlen = stringEntity.getContentLength();
		method.setRequestEntity(stringEntity);
		

		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			//logger.error("post failed,status="+status+",url="+url+",data="+data);
			return response;
		}
		
		response = method.getResponseBodyAsString();
		
		return response;
	}
	
	public static String httpPost(String url,String data) throws HttpException, IOException {
		
		String response = null;
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		PostMethod method = new PostMethod(url);

		RequestEntity stringEntity = new StringRequestEntity(data,"application/json", "utf-8");
		method.setRequestEntity(stringEntity);

		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			//logger.error("post failed,status="+status+",url="+url+",data="+data);
			return response;
		}
		
		response = method.getResponseBodyAsString();
		
		return response;
	}
	
	public static String httpGet(String url) throws HttpException, IOException{
		
		String response = null;
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		GetMethod method = new GetMethod(url);

//		RequestEntity stringEntity = new StringRequestEntity(data,"application/json", "utf-8");
//		method.setRequestEntity(stringEntity);
	
		int status = client.executeMethod(method);
		if (status != HttpStatus.SC_OK) {
			//logger.error("post failed,status="+status+",url="+url+",data="+data);
			return response;
		}
		
		response = method.getResponseBodyAsString();
		
		return response;
	}
}
```





```
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import utils.LogFactory;

import com.google.gson.Gson;
import cons.GameData;
import cons.MemcacheConstants;
import cons.SystemConstants;
import cons.WebConstants;
import output.BaseJson;

public class WebUtil {

	private static final Log logger = LogFactory.getLog(WebUtil.class);

	private static Gson gson = new Gson();

	public static String generateSig(Map<String, String> paramMap) {
		String sigString = WebUtil.generateNormalizedString(paramMap,
				WebConstants.PARAM_SIG);
		String sig = DigestUtils
				.md5Hex(sigString
						+ GameData.getConstByName("SIGNATURE_KEY").getValue())
				.toLowerCase();
		return sig;
	}

	/**
	 * 根据sessionKey返回userId，sessionKey必须采用固定格式
	 * 
	 * @param sessionKey
	 * @return
	 */
	public static int getUserIdBySession(String sessionKey) {
		int userId = 0;
		if (sessionKey != null && !sessionKey.equals("")) {
			String[] array = sessionKey
					.split(MemcacheConstants.USER_SESSION_SPLIT);
			if (array.length > 1 && !array[0].equals(""))
				try {
					userId = Integer.parseInt(array[0]);
				} catch (Exception e) {
				}
		}
		return userId;
	}

	public static String getSessionByUserId(int userId) {
		String sessionKey = userId + MemcacheConstants.USER_SESSION_SPLIT
				+ UUID.randomUUID();
		return sessionKey;
	}

	public static int getUserIdFromMap(Map<String, String> paramMap) {
		int userId = 0;
		String sessionKey = paramMap.get(WebConstants.PARAM_SESSION_KEY);
		if (sessionKey != null) {
			userId = WebUtil.getUserIdBySession(sessionKey);
		}
		return userId;
	}

	public static String getInfoLog(BaseJson json, HttpServletRequest request) {

		StringBuffer logBuffer = new StringBuffer();
		if (request != null) {
			logBuffer.append("request=" + request.getRequestURI() + " ");
			String ipAddr = getIpAddr(request);
			if (ipAddr != null) {
				logBuffer.append("ip=" + ipAddr + " ");
			}
			Map<String, String> paramMap = getRequestParamMap(request);
			int userId = getUserIdFromMap(paramMap);
			if (userId != -1) {
				logBuffer.append("userId=" + userId + " ");
			}
			if (json != null) {
				logBuffer.append("msgType=" + json.getMessageCode() + " ");
				logBuffer.append("errorCode=" + json.getErrorCode() + " ");
			}

			Set<String> params = paramMap.keySet();
			List<String> sortedParams = new ArrayList<String>(params);
			Collections.sort(sortedParams);
			for (String paramKey : sortedParams) {
				logBuffer.append(paramKey).append('=')
						.append(paramMap.get(paramKey)).append(" ");
			}
		} else
			logBuffer.append("Error with get log!");
		logBuffer.append('\n');
		return logBuffer.toString();
	}

	/**
	 * 
	 * @param paramMap
	 * @param valueName
	 * @return 解析失敗返回0
	 */
	public static int getIntValueFromRequestMap(Map<String, String> paramMap,
			String valueName) {
		String valueString = paramMap.get(valueName);
		int value = NumUtil.getInt(valueString);
		return value;
	}

	public static int getIntValueFromRequestMap(Map<String, String> paramMap,
			String valueName, int value) {
		String valueString = paramMap.get(valueName);
		value = NumUtil.getInt(valueString, value);
		return value;
	}

	public static long getLongValueFromRequestMap(Map<String, String> paramMap,
			String valueName) {
		String valueString = paramMap.get(valueName);
		long value = NumUtil.getLong(valueString);
		return value;
	}

	public static double getDoubleValueFromRequestMap(
			Map<String, String> paramMap, String valueName) {
		String valueString = paramMap.get(valueName);
		double value = NumUtil.getDouble(valueString);
		return value;
	}

	/**
	 * 对http请求参数作字典排序，拼接字符串,不包括签名
	 */
	public static final String generateNormalizedString(
			Map<String, String> paramMap, String sigParamKey) {
		Set<String> params = paramMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String paramKey : sortedParams) {
			if (paramKey.equals(sigParamKey)) {
				continue;
			}
			sb.append(paramKey).append('=').append(paramMap.get(paramKey));
			// the first 50 chars
			// sb.append(paramKey).append('=').append(StringUtils.substring(paramMap.get(paramKey),
			// 0, 50));
		}
		return sb.toString();
	}

	public static String getLog(BaseJson json, Map<String, String> paramMap) {
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(json.errorStr()).append('\n');
		Set<String> params = paramMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		for (String paramKey : sortedParams) {
			logBuffer.append(paramKey).append('=')
					.append(paramMap.get(paramKey)).append("	");
		}
		logBuffer.append('\n');
		return logBuffer.toString();
	}

	/**
	 * push request params key-value to map, the values are all url decoded
	 */
	public static final Map<String, String> getRequestParamMap(
			HttpServletRequest request) {
		Map<String, String> requestParamsMap = new HashMap<String, String>();
		try {
			Enumeration<String> e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String param = e.nextElement();
				String value = request.getParameter(param);
				// logger.debug(String.format(
				// "getRequestParamMap(HttpServletRequest) - [%s=>%s]",
				// param, value));

				if (value != null) {
					requestParamsMap.put(param, value);
				}
			}
			/* 导致签名计算错误 */
			// requestParamsMap.put(CommandCons.PARAM_SERVER_REMOTE_IP_ADDRESS,getIpAddr(request));

		} catch (Exception e) {
			logger.error("Mapping request parameter exception "
					+ e.getMessage());
		}
		return requestParamsMap;
	}

	/**
	 * push request params key-value to map, the values are all url decoded
	 */
	public static final String getRequestStr(HttpServletRequest request) {
		String requestStr = "";
		try {
			Map<String, String> strMap = new HashMap<String, String>();
			Enumeration<String> e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String param = e.nextElement();
				String value = request.getParameter(param);
				if (value != null) {
					strMap.put(param, value);
				}
			}
			strMap.put(WebConstants.WEB_LOG_REQUEST, request.getPathInfo());
			strMap.put(WebConstants.WEB_LOG_IP, getIpAddr(request));
			requestStr = gson.toJson(strMap);
		} catch (Exception e) {
			logger.error("getRequestStr error",e);
		}
		return requestStr;
	}

	/**
	 * 获取IP,若获取失败返回null
	 * 
	 * @param request
	 * @return
	 */
	public static final String getIpAddr(HttpServletRequest request) {
		// Enumeration<String> xxx = request.getHeaderNames();
		// while (xxx.hasMoreElements()) {
		// String name = xxx.nextElement();
		// System.out.println(name + ":	" + request.getHeader(name));
		// }
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
		}
		return ip;
	}

	/**
	 * 根据map中的字符串返回字符串list
	 * 
	 * @param paramMap
	 * @param paramIds
	 * @return
	 */
	public static List<String> getStrList(Map<String, String> paramMap,
			String paramIds) {
		List<String> strList = new ArrayList<String>();
		try {
			String ids = paramMap.get(paramIds);
			String idArray[] = ids.split(SystemConstants.SPLIT_AND);
			for (String id : idArray)
				strList.add(id);
		} catch (Exception e) {
		}
		return strList;
	}

	/**
	 * 根据map中的字符串返回Integer list
	 * 
	 * @param paramMap
	 * @param paramIds
	 * @return
	 */
	public static List<Integer> getIntegerList(Map<String, String> paramMap,
			String paramIds) {
		List<Integer> intList = new ArrayList<Integer>();
		try {
			String ids = paramMap.get(paramIds);
			String idArray[] = ids.split(SystemConstants.SPLIT_AND);
			for (String id : idArray) {
				int value = NumUtil.getInt(id);
				intList.add(value);
			}
		} catch (Exception e) {
		}
		return intList;
	}

}

```