package id.ac.umn.mobile.babel

import android.provider.BaseColumns

class DbContract{
    class Accounts : BaseColumns{
        var TABLE_NAME = "accounts"
        var COL_EMAIL = "email"
        var COL_PASSWORD = "password"
        var COL_SALT = "salt"
        var COL_NAME = "name"
        var COL_ROLE = "role"
        var COL_DATE_OF_BIRTH = "dob"
        var COL_REGISTRATION_DATE = "reg_date"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s, %s, %s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s VARCHAR(255) NOT NULL UNIQUE", COL_EMAIL),
                String.format("%s CHAR(64) NOT NULL", COL_PASSWORD),
                String.format("%s CHAR(5) NOT NULL", COL_SALT),
                String.format("%s VARCHAR(255) NOT NULL", COL_NAME),
                String.format("%s VARCHAR(100) NOT NULL", COL_ROLE),
                String.format("%s DATE NOT NULL", COL_DATE_OF_BIRTH),
                String.format("%s DATETIME NOT NULL", COL_REGISTRATION_DATE)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class Units : BaseColumns{
        var TABLE_NAME = "units"
        var COL_MEASURE = "measure"
        var COL_UNIT_NAME = "unit_name"
        var COL_VALUE = "value"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s VARCHAR(100) NOT NULL", COL_MEASURE),
                String.format("%s VARCHAR(100) NOT NULL UNIQUE", COL_UNIT_NAME),
                String.format("%s DOUBLE NOT NULL", COL_VALUE)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class Items : BaseColumns{
        var TABLE_NAME = "items"
        var COL_ITEM_NAME = "item_name"
        var COL_QUANTITY = "quantity"
        var COL_UNIT_ID = "unit_id"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s VARCHAR(255) NOT NULL", COL_ITEM_NAME),
                String.format("%s DOUBLE NOT NULL", COL_QUANTITY),
                String.format("%s INTEGER NOT NULL FOREIGN KEY REFERENCES units(_ID)", COL_UNIT_ID)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class ItemHistories : BaseColumns{
        var TABLE_NAME = "item_histories"
        val COL_ACCOUNT_ID = "account_id"
        var COL_ACTION = "action"
        var COL_OCCURRENCE = "occurrence"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s INTEGER NOT NULL", COL_ACCOUNT_ID),
                String.format("%s VARCHAR(100) NOT NULL", COL_ACTION),
                String.format("%s DATETIME NOT NULL", COL_OCCURRENCE)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class ItemHistoryDetails : BaseColumns{

    }
}