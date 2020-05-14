package wallet;

import org.apache.commons.lang.StringUtils;
import wallet.client.RecordFrame;
import wallet.client.util.ClientUtil;
import wallet.meta.Account;
import wallet.util.DataUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ClientMain extends JFrame implements ActionListener {
	public static final int WIDTH = 500;
	public static final int HEIGHT = 400;
	public static Account loginAccount;
	private JPanel loginJPanel, userJPanel;
	private JTextField userNameField, passwordField;
	private JLabel amountLabel;
	private JButton loginBtn, showRecordBtn, debitBtn, creditBtn;

	public static void main(String[] args) {
		new ClientMain();
	}

	public ClientMain() {
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);// GUI in middle
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		loginJPanel = initLoginJPanel();
		this.add(loginJPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	public JPanel initLoginJPanel() {
		JPanel loginJPanel = new JPanel();
		loginJPanel.setLayout(null);

		JLabel jLabel = new JLabel("Username:");
		jLabel.setFont(new Font("Serif", 0, 20));
		jLabel.setBounds(100, 100, 100, 20);
		loginJPanel.add(jLabel);

		jLabel = new JLabel("Password:");
		jLabel.setFont(new Font("Serif", 0, 20));
		jLabel.setBounds(100, 140, 100, 20);
		loginJPanel.add(jLabel);

		userNameField = new JTextField();
		userNameField.setBounds(190, 100, 160, 25);
		loginJPanel.add(userNameField);

		passwordField = new JTextField();
		passwordField.setBounds(190, 140, 160, 25);
		loginJPanel.add(passwordField);

		loginBtn = new JButton("Login");
		loginBtn.setFont(new Font("Serif", 1, 20));
		loginBtn.setBounds(190, 180, 160, 30);
		loginBtn.addActionListener(this);
		loginJPanel.add(loginBtn);

		loginJPanel.setVisible(true);
		return loginJPanel;
	}

	private void turnToUserJPanel() {
		if (userJPanel == null) {
			userJPanel = initUserJPanel();
		}
//        this.removeAll();
		loginJPanel.setVisible(false);

		setAmountValue(loginAccount.getAmount());
		this.add(userJPanel, BorderLayout.CENTER);
		userJPanel.setVisible(true);
	}

	private JPanel initUserJPanel() {
		JPanel userJPanel = new JPanel();
		userJPanel.setLayout(null);

		JLabel jLabel = new JLabel("Balance:");
		jLabel.setFont(new Font("Serif", 1, 25));
		jLabel.setBounds(200, 260, 100, 50);
		userJPanel.add(jLabel);

		amountLabel = new JLabel();
		setAmountValue(loginAccount.getAmount());
		amountLabel.setFont(new Font("Serif", Font.PLAIN, 26));
		amountLabel.setBounds(300, 260, 200, 50);
		userJPanel.add(amountLabel);

		showRecordBtn = new JButton("Record");
		showRecordBtn.setFont(new Font("Serif", 0, 20));
		showRecordBtn.setBounds(60, 50, 120, 70);
		showRecordBtn.addActionListener(this);
		userJPanel.add(showRecordBtn);

		debitBtn = new JButton("Debit");
		debitBtn.setFont(new Font("Serif", 0, 20));
		debitBtn.setBounds(60, 140, 120, 70);
		debitBtn.addActionListener(this);
		userJPanel.add(debitBtn);

		creditBtn = new JButton("Credit");
		creditBtn.setFont(new Font("Serif", 0, 20));
		creditBtn.setBounds(60, 230, 120, 70);
		creditBtn.addActionListener(this);
		userJPanel.add(creditBtn);

		return userJPanel;
	}

	private void setAmountValue(double amountValue) {
		amountLabel.setText(amountValue + " Kr");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(loginBtn)) {
			String username = userNameField.getText();
			String password = passwordField.getText();
			if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				JOptionPane.showMessageDialog(null, "Input username or password", "Notice",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			Map<String, String> params = new HashMap<>();
			params.put("username", username);
			params.put("password", password);
			Account account = ClientUtil.login(username, password);
			if (account != null) {
				loginAccount = account;
				userNameField.setText("");
				passwordField.setText("");
				turnToUserJPanel();
			} else {
				JOptionPane.showMessageDialog(null, "Login failure!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource().equals(showRecordBtn)) {
			new RecordFrame(loginAccount);
		} else if (e.getSource().equals(debitBtn)) {
			Object ret = JOptionPane.showInputDialog(null, "Debit Amount：\n", "Debit", JOptionPane.PLAIN_MESSAGE, null,
					null, "0");
			if (ret == null || StringUtils.isBlank(ret.toString())) {
				return;
			}

			double amount = Double.parseDouble(ret.toString());
			if (amount <= 0) {
				JOptionPane.showMessageDialog(null, "Amount input error!!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (loginAccount.getAmount() < amount) {
				JOptionPane.showMessageDialog(null, "Do not have enough money!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!ClientUtil.debit(amount)) {
				JOptionPane.showMessageDialog(null, "Debit failure!!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			loginAccount.setAmount(loginAccount.getAmount() - amount);
			setAmountValue(loginAccount.getAmount());
			DataUtil.refreshAccountData();
		} else if (e.getSource().equals(creditBtn)) {
			Object ret = JOptionPane.showInputDialog(null, "Credit Amount：\n", "Credit", JOptionPane.PLAIN_MESSAGE, null,
					null, "0");
			double amount = Double.parseDouble(ret.toString());
			if (amount <= 0) {
				JOptionPane.showMessageDialog(null, "Amount input error!!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!ClientUtil.credit(amount)) {
				JOptionPane.showMessageDialog(null, "Credit failure!!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			loginAccount.setAmount(loginAccount.getAmount() + amount);
			setAmountValue(loginAccount.getAmount());
			DataUtil.refreshAccountData();
		}
	}
}