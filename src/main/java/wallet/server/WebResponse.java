package wallet.server;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class WebResponse {
	public static int LOGIN_FAIL = 401;
	public static int SERVER_ERROE = 500;
	private int code;
	private Map<String, Object> data;

	public static WebResponse success(Map<String, Object> data) {
		WebResponse response = new WebResponse();
		response.code = 200;
		response.data = data;
		return response;
	}

	public static WebResponse success() {
		WebResponse response = new WebResponse();
		response.code = 200;
		return response;
	}

	public static WebResponse error(int code) {
		WebResponse response = new WebResponse();
		response.code = code;
		return response;
	}

	public static void writeReturn(ServletResponse response, WebResponse webResponse) {
		try {
			response.reset();
			response.setContentType("application/json;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(JSON.toJSONString(webResponse));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
