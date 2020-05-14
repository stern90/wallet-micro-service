package wallet.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wallet.meta.Account;
import wallet.meta.UserRecord;
import wallet.server.common.SecurityContextHolder;
import wallet.util.DataUtil;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

import static wallet.server.WebResponse.LOGIN_FAIL;
import static wallet.server.WebResponse.SERVER_ERROE;

@RestController
@RequestMapping(value = "/wallet")
public class ApiController {

	@RequestMapping(value = "/login", method = RequestMethod.POST) // mapping URL
	@ResponseBody //convert java objects to data in json format
	public WebResponse login(String username, String password) {
		Account account = DataUtil.checkAccount(username, password);
		if (account != null) {
			return WebResponse.success(JSON.parseObject(JSON.toJSONString(account)));
		}
		return WebResponse.error(LOGIN_FAIL);
	}

	@RequestMapping(value = "/getRecord", method = RequestMethod.GET)
	@ResponseBody
	public WebResponse getRecord() {
		String username = SecurityContextHolder.getUsername();
		List<UserRecord> records = DataUtil.getRecordList(username);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("records", records);
		return WebResponse.success(jsonObject);
	}

	@RequestMapping(value = "/debit", method = RequestMethod.POST)
	@ResponseBody
	public WebResponse getRecord(double amount) {
		String username = SecurityContextHolder.getUsername();
		UserRecord userRecord = new UserRecord(username, "Debit", amount);
		userRecord.setId(UserRecord.genId());
		if (!DataUtil.addRecord(userRecord)) {
			return WebResponse.error(SERVER_ERROE);
		}
		return WebResponse.success();
	}

	@RequestMapping(value = "/credit", method = RequestMethod.POST)
	@ResponseBody
	public WebResponse loan(double amount) {
		String username = SecurityContextHolder.getUsername();
		UserRecord userRecord = new UserRecord(username, "Credit", amount);
		userRecord.setId(UserRecord.genId());
		if (!DataUtil.addRecord(userRecord)) {
			return WebResponse.error(SERVER_ERROE);
		}
		return WebResponse.success();
	}

}
