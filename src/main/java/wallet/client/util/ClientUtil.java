package wallet.client.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import wallet.meta.Account;
import wallet.meta.UserRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wallet.ClientMain.loginAccount;

public class ClientUtil {
	private static final String HOST = "http://127.0.0.1:9000/wallet";

	public static Account login(String username, String password) {
		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		params.put("password", password);
		JSONObject jsonObject = HttpUtil.doPostWithJsonRet(HOST + "/login", params);
		if (jsonObject != null && jsonObject.getIntValue("code") == 200) {
			return jsonObject.getObject("data", Account.class);
		}
		return null;
	}

	public static List<UserRecord> getRecord() {
		Map<String, String> params = new HashMap<>();
		params.put("token", loginAccount.getToken());
		JSONObject jsonObject = HttpUtil.doGetWithJsonRet(HOST + "/getRecord", params);
		if (jsonObject != null && jsonObject.getIntValue("code") == 200) {
			return JSON.parseArray(jsonObject.getJSONObject("data").getString("records"), UserRecord.class);
		}
		return new ArrayList<>();
	}

	public static boolean debit(double amount) {
		Map<String, Object> params = new HashMap<>();
		params.put("token", loginAccount.getToken());
		params.put("amount", amount);
		JSONObject jsonObject = HttpUtil.doPostWithJsonRet(HOST + "/debit", params);
		return jsonObject != null && jsonObject.getIntValue("code") == 200;
	}

	public static boolean credit(double amount) {
		Map<String, Object> params = new HashMap<>();
		params.put("token", loginAccount.getToken());
		params.put("amount", amount);
		JSONObject jsonObject = HttpUtil.doPostWithJsonRet(HOST + "/credit", params);
		return jsonObject != null && jsonObject.getIntValue("code") == 200;
	}

}
