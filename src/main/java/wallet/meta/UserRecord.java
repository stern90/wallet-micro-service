package wallet.meta;

import wallet.util.DataUtil;

import java.util.Date;
import java.util.UUID;

public class UserRecord {

    private String id; // transaction id
    private String username;
    private String type; // transaction type
    private double amount;
    private Date time;

    public UserRecord() {
    }

    public UserRecord(String username, String type, double amount) {
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.time = new Date();
    }

    public UserRecord(String username, String type, double amount, Date time) {
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.time = time;
    }

    public static String genId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return id + "," + username + "," + type + "," + amount + "," + DataUtil.genSecondTime(time);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
