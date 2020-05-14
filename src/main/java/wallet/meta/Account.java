package wallet.meta;

public class Account {
	private String username;
	private String password;
	private double amount;
	private String type;
	private String token;

	public Account() {
	}

	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public static boolean checkType(String type) {
		return "admin".equals(type) || "user".equals(type);
	}

	@Override
	public String toString() {
		return username + "," + password + "," + amount + "," + type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
