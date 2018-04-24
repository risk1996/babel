package id.ac.umn.mobile.babel

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class Item(val itemName: String, val stock: Double, val safetyStock: Double, val unit_id: Int, val location: String, var thumbnail: String)
class DbContract(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
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
        var COL_VALUE = "val"
        var COL_INCREMENT = "increment"
        var COL_UNIT_THUMBNAIL = "unit_thumbnail"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s VARCHAR(100) NOT NULL", COL_MEASURE),
                String.format("%s VARCHAR(100) NOT NULL", COL_UNIT_NAME),
                String.format("%s DOUBLE NOT NULL", COL_VALUE),
                String.format("%s DOUBLE NOT NULL", COL_INCREMENT),
                String.format("%s VARCHAR(100)", COL_UNIT_THUMBNAIL)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class Items : BaseColumns{
        var TABLE_NAME = "items"
        var COL_ITEM_NAME = "item_name"
        var COL_STOCK = "stock"
        var COL_SAFETY_STOCK = "safety_stock"
        var COL_UNIT_ID = "unit_id"
        var COL_LOCATION = "location"
        var COL_ITEM_THUMBNAIL = "item_thumbnail"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s VARCHAR(255) NOT NULL UNIQUE", COL_ITEM_NAME),
                String.format("%s DOUBLE NOT NULL", COL_STOCK),
                String.format("%s DOUBLE", COL_SAFETY_STOCK),
                String.format("%s INTEGER NOT NULL FOREIGN KEY REFERENCES units(_id)", COL_UNIT_ID),
                String.format("%s VARCHAR(100) NOT NULL", COL_LOCATION),
                String.format("%s VARCHAR(100)", COL_ITEM_THUMBNAIL)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class ItemHistories : BaseColumns{
        var TABLE_NAME = "item_histories"
        val COL_ACCOUNT_ID = "account_id"
        var COL_OPERATION = "operation"
        var COL_REASON = "reason"
        var COL_OCCURRENCE = "occurrence"
        var SYNTAX_CREATE = String.format(
                "CREATE TABLE %s(%s, %s, %s, %s);",
                TABLE_NAME,
                String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                String.format("%s INTEGER NOT NULL FOREIGN KEY REFERENCES accounts(_id)", COL_ACCOUNT_ID),
                String.format("%s VARCHAR(100) NOT NULL", COL_OPERATION),
                String.format("%s TEXT", COL_REASON),
                String.format("%s DATETIME NOT NULL", COL_OCCURRENCE)
        )
        var SYNTAX_DROP = String.format(
                "DROP TABLE IF EXISTS %s;",
                TABLE_NAME
        )
    }
    class ItemHistoryDetails : BaseColumns{
            var TABLE_NAME = "item_history_details"
            var COL_ITEM_HISTORY_ID = "item_history_id"
            var COL_ITEM_ID = "item_id"
            var COL_STOCK_CHANGE = "stock_change"
            var SYNTAX_CREATE = String.format(
                    "CREATE TABLE %s(%s, %s, %s, %s);",
                    TABLE_NAME,
                    String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", BaseColumns._ID),
                    String.format("%s INTEGER NOT NULL FOREIGN KEY REFERENCES item_histories(_id)", COL_ITEM_HISTORY_ID),
                    String.format("%s INTEGER NOT NULL FOREIGN KEY REFERENCES items(_id)", COL_ITEM_ID),
                    String.format("%s DOUBLE NOT NULL", COL_STOCK_CHANGE)
            )
            var SYNTAX_DROP = String.format(
                    "DROP TABLE IF EXISTS %s;",
                    TABLE_NAME
            )
        }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!
        p0.execSQL(ItemHistoryDetails().SYNTAX_DROP)
        p0.execSQL(ItemHistories().SYNTAX_DROP)
        p0.execSQL(Items().SYNTAX_DROP)
        p0.execSQL(Units().SYNTAX_DROP)
        p0.execSQL(Accounts().SYNTAX_DROP)
        p0.execSQL(Accounts().SYNTAX_CREATE)
        p0.execSQL(Units().SYNTAX_CREATE)
        p0.execSQL(Items().SYNTAX_CREATE)
        p0.execSQL(ItemHistories().SYNTAX_CREATE)
        p0.execSQL(ItemHistoryDetails().SYNTAX_CREATE)
    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) = onCreate(p0)
}