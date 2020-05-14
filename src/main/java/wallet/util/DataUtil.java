package wallet.util;

import wallet.meta.Account;
import wallet.meta.UserRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, Account> accountMap = new LinkedHashMap<>();
	private static Map<String, List<UserRecord>> recordMap = new HashMap<>();
	private static Map<String, Account> loginUserMap = new HashMap<>();

	static {
		init();
	}

	public static void init() {
		initUser();
		initRecord();
	}

	public static Account isLogin(String token) {
		Account account = loginUserMap.get(token);
		return account;
	}

	public static List<UserRecord> getRecordList(String username) {
		return Collections.unmodifiableList(recordMap.getOrDefault(username, new ArrayList<>()));
	}

	public static Account checkAccount(String userName, String password) {
		if (!accountMap.containsKey(userName)) {
			return null;
		}
		String token = UUID.randomUUID().toString();
		Account account = accountMap.get(userName);
		if (account != null && account.getPassword().equals(password)) {
			account.setToken(token);
			loginUserMap.put(token, account);
			return account;
		}
		return null;
	}

	/**
	 * register new user
	 * @param account
	 */
	public static void addAccount(Account account) {
		try {
			FileWriter fileWriter = new FileWriter("data/user.txt", true);
			fileWriter.write("\r\n" + account.toString());
			fileWriter.flush();
			accountMap.put(account.getUsername(), account);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void refreshAccountData() {
		try {
			FileWriter fileWriter = new FileWriter("data/user.txt", false);
			fileWriter.write("# userName password amount admin/user\r\n");
			for (Account tmp : accountMap.values()) {
				fileWriter.write(tmp.toString() + "\r\n");
			}
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * read existing user data from a file
	 */
	private static void initUser() {
		accountMap.clear();
		List<String> lineList = readFile("data/user.txt");
		if (lineList.size() > 0) {
			for (String line : lineList) {
				String[] strings = line.split(",");
				if (strings.length != 4) {
					continue;
				}
				Account account = new Account(strings[0], strings[1]);
				account.setAmount(Double.parseDouble(strings[2]));
				String type = strings[3].toLowerCase();
				if (Account.checkType(type)) {
					account.setType(strings[3]);
					accountMap.put(account.getUsername(), account);
				}
			}
		}
	}

	/**
	 * add new transactions: debit/credit
	 * @param userRecord
	 * @return
	 */
	public static boolean addRecord(UserRecord userRecord) {
		// check if the transaction ID is unique
		for (List<UserRecord> userRecordList : recordMap.values()) {
			for (UserRecord userRecordTmp : userRecordList) {
				if (userRecord.getId().equalsIgnoreCase(userRecordTmp.getId())) {
					return false;
				}
			}
		}

		try {
			FileWriter fileWriter = new FileWriter("data/user_record.txt", true);
			fileWriter.write("\r\n" + userRecord.toString());
			fileWriter.flush();
			List<UserRecord> list = recordMap.computeIfAbsent(userRecord.getUsername(), k -> new ArrayList<>());
			list.add(userRecord);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * read existing transactions from the file
	 */
	private static void initRecord() {
		recordMap.clear();
		List<String> lineList = readFile("data/user_record.txt");
		if (lineList.size() > 0) {
			for (String line : lineList) {
				String[] strings = line.split(",");
				if (strings.length != 5) {
					continue;
				}
				try {
					UserRecord userRecord = new UserRecord(strings[1], strings[2], Double.parseDouble(strings[3]),
							parseTime(strings[4]));
					userRecord.setId(strings[0]);
					List<UserRecord> list = recordMap.computeIfAbsent(userRecord.getUsername(), k -> new ArrayList<>());
					list.add(userRecord);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * read the file
	 * @param fileName
	 * @return
	 */
	private static List<String> readFile(String fileName) {
		List<String> lines = new ArrayList<>();
		int lineCount = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				lineCount++;
				if (lineCount == 0) {
					continue;
				}
				lines.add(line.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static String genSecondTime(Date time) {
		return sdf.format(time);
	}

	public static Date parseTime(String time) {
		try {
			return sdf.parse(time);
		} catch (Exception e) {
//            e.printStackTrace();
		}
		return new Date();
	}

}
