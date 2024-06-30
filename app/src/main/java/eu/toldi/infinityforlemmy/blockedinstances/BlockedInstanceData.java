package eu.toldi.infinityforlemmy.blockedinstances;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "blocked_instances", primaryKeys = {"id", "account_name"})
public class BlockedInstanceData {
    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "domain")
    private String domain;

    @NonNull
    @ColumnInfo(name = "account_name")
    private String accountName;

    @ColumnInfo(name = "instance_name")
    private String name;

    @ColumnInfo(name = "icon")
    private String icon;


    public BlockedInstanceData(int id, String domain, String name, String icon, String accountName) {
        this.id = id;
        this.domain = domain;
        this.icon = icon;
        this.name = name;
        this.accountName = accountName;
    }

    public int getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}
