package wallet.client.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * HttpClient
 */
public class HttpUtil {
	static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	// default
	private static final int socketTimeout_default = 5000;
	private static final int connectTimeout_default = 5000;
	private static final int connectionRequestTimeout_default = 5000;
	private static final int maxConnTotal = 20; // maximal 1000
	private static final int maxConnPerRoute = 10;// the actual size of a single connection pool
	private static CloseableHttpClient client; // support http and https

	static {
		client = init();
	}

	public static JSONObject doPostWithJsonRet(String url, Map<String, Object> requestParam) {
		Map<String, String> paramMap = new HashMap<>();
		for (String key : requestParam.keySet()) {
			paramMap.put(key, requestParam.get(key) == null ? null : requestParam.get(key).toString());
		}
		return doPostWithJsonRet(url, paramMap, "UTF-8", -1, -1, -1);
	}

	public static JSONObject doPostWithJsonRet(String url, Map<String, String> requestParam, String charset,
			int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
		String ret = doPost(url, requestParam, charset, connectionRequestTimeout, connectTimeout, socketTimeout);
		System.out.println(ret);
		if (ret != null) {
			return JSON.parseObject(ret);
		}
		return null;
	}

	public static String doPost(String url, Map<String, String> requestParam, String charset,
			int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
		HttpPost post = new HttpPost(url);
		if (connectionRequestTimeout > 0 && connectTimeout > 0 && socketTimeout > 0) {
			RequestConfig requestConfigTmp = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
			post.setConfig(requestConfigTmp);
		}
		if (charset == null) {
			charset = "utf-8";
		}

		List<NameValuePair> params = new ArrayList<>();
		for (String key : requestParam.keySet()) {
			params.add(new BasicNameValuePair(key, requestParam.get(key)));
		}

		String nisResponse = null;
		CloseableHttpResponse httpResponse = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(params, charset));
			httpResponse = client.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				nisResponse = EntityUtils.toString(entity);
				if (!StringUtils.isEmpty(nisResponse)) {
					return nisResponse;
				}
			} else {
				logger.error("invoke api error status:[url={}][requestParam={}][statusCode={}]", url, requestParam,
						httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			logger.error("invoke api exception:[url={}][requestParam={}][exception={}]", url, requestParam, e);
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static JSONObject doGetWithJsonRet(String url, Map<String, String> params) {
		String ret = doGet(url, params, null, null);
		System.out.println(ret);
		if (ret != null) {
			return JSON.parseObject(ret);
		}
		return null;
	}

	public static String doGet(String url, Map<String, String> params, Map<String, String> headers, String charset) {
		if (url == null) {
			return null;
		}
		if (charset == null) {
			charset = "utf-8";
		}
		String nisResponse = null;

		if (params != null) {
			List<BasicNameValuePair> nameValuePairList = new ArrayList<>();
			for (String key : params.keySet()) {
				BasicNameValuePair pair = new BasicNameValuePair(key, params.get(key));
				nameValuePairList.add(pair);
			}
			String str = null;
			try {
				str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, charset));
			} catch (IOException e) {
				logger.error("get params error:[url={}][params={}]", url, params.toString(), e);
				return null;
			}
			if (url.contains("?")) {
				url = url + "&" + str;
			} else {
				url = url + "?" + str;
			}
		}

		HttpGet get = new HttpGet(url);

		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
		}

		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(get);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				nisResponse = EntityUtils.toString(entity, charset);
			} else {
				logger.error("invoke api error status:[url={}][statusCode={}]", url,
						httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			logger.error("invoke api exception:[url={}][exception={}]", url, e);
			logger.error(e.getMessage(), e);
			return null;
		}
		return nisResponse;
	}

	private static CloseableHttpClient init() {
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		try {
			SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
			ctx.init(null, new TrustManager[] { xtm }, null);
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx,
					NoopHostnameVerifier.INSTANCE);

			// create Registry
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

			// create Config
			RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
					.setExpectContinueEnabled(Boolean.TRUE)
					.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
					.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
					.setConnectTimeout(connectTimeout_default)
					.setConnectionRequestTimeout(connectionRequestTimeout_default)
					.setSocketTimeout(socketTimeout_default).build();

			// create ConnectionManager && add Connection configuration information
			PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			poolConnManager.setMaxTotal(maxConnTotal);
			poolConnManager.setDefaultMaxPerRoute(maxConnPerRoute);

			return HttpClients.custom().setConnectionManager(poolConnManager).setDefaultRequestConfig(requestConfig)
					.build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(doGet("http://www.google.se", null, null, null));
	}
}
