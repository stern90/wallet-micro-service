package wallet.server.common;

import wallet.meta.Account;

/**
 * store thread variables
 */
public class SecurityContextHolder {

	// login info
	private static ThreadLocal<Account> contextHolder = new ThreadLocal<Account>() {
		protected Account initialValue() {
			return new Account(null, null);
		};
	};

	public static void reset() {
		contextHolder.remove();
	}

	public static void setContext(Account account) {
		contextHolder.set(account);
	}

	public static String getUsername() {
		Account account = contextHolder.get();
		if (account != null && account.getUsername() != null) {
			return account.getUsername();
		}
		return null;
	}

}
