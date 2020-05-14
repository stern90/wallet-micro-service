package wallet.client;

import wallet.client.util.ClientUtil;
import wallet.meta.Account;
import wallet.meta.UserRecord;
import wallet.util.DataUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class RecordFrame extends JFrame {

	private Account account;
	MyTableModel myModel;
	JTable table;

	public RecordFrame(Account account) {
		super("Account: " + account.getUsername());
		super.setLocationRelativeTo(null); // GUI in middle
		super.setSize(600, 400);

		this.account = account;
		myModel = new MyTableModel();
		table = new JTable(myModel);
		table.setPreferredScrollableViewportSize(new Dimension(600, 70));
		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		setVisible(true);
	}

	public void display() {
		pack();
		setVisible(true);
	}

	/**
	 * store the data to be displayed in the table in the string array and Object
	 * array
	 */
	class MyTableModel extends AbstractTableModel {
		final String[] columnNames = { "ID", "Type", "Amount", "Time" };
		Object[][] data = getRecordData();

		private Object[][] getRecordData() {
			java.util.List<UserRecord> recordList = ClientUtil.getRecord();
			Object[][] data = new Object[recordList.size()][4];
			int i = 0;
			for (UserRecord userRecord : recordList) {
				data[i++] = new Object[] { userRecord.getId(), userRecord.getType(), userRecord.getAmount() + "",
						DataUtil.genSecondTime(userRecord.getTime()) };
			}
			return data;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	public static void main(String[] args) {
		RecordFrame frame = new RecordFrame(new Account("111", "123"));
		frame.display();
	}
}
