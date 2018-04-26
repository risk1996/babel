package id.ac.umn.mobile.babel

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.Semaphore

class Company(val status: String, val statusShort: String, val name: String, val logo: String, val site: String, val officeMain: String, val officeSecondary: String)
class Account(val _id: Int, val email: String, val password: String, val salt: String, val name: String, val role: String, val dob: String, val reg_date: String)
class Unit(val _id: Int, val measure: String, val unit_name: String, val value: Double, val increment: Double, val unit_thumbnail: String)
class Item(val _id: Int, val itemName: String, val stock: Double, val safetyStock: Double, val unit_id: Int, val location: String, var thumbnail: String)
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
class FirebaseDb : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
abstract class Data{
    var company = ArrayList<Company>()
    var accounts = ArrayList<Account>()
    var units = ArrayList<Unit>()
    var items = ArrayList<Item>()
    var dbEventListener = object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(p0: DataSnapshot?) {
            p0!!
            company.add(Company(
                    p0.child("_company").child("company_status").value.toString(),
                    p0.child("_company").child("company_status_short").value.toString(),
                    p0.child("_company").child("company_name").value.toString(),
                    p0.child("_company").child("company_logo").value.toString(),
                    p0.child("_company").child("company_site").value.toString(),
                    p0.child("_company").child("company_office_main").value.toString(),
                    p0.child("_company").child("company_office_secondary").value.toString()
            ))
            p0.child("accounts").children.forEach{
                accounts.add(Account(
                        it.key.toInt(),
                        it.child("email").value.toString(),
                        it.child("password").value.toString(),
                        it.child("salt").value.toString(),
                        it.child("name").value.toString(),
                        it.child("role").value.toString(),
                        it.child("dob").value.toString(),
                        it.child("reg_date").value.toString()
                ))
            }
            units.clear()
            p0.child("units").children.forEach{
                units.add(Unit(
                        it.key.toInt(),
                        it.child("measure").value.toString(),
                        it.child("unit_name").value.toString(),
                        it.child("val").value.toString().toDouble(),
                        it.child("increment").value.toString().toDouble(),
                        it.child("unit_thumbnail").value.toString()
                ))
            }
            items.clear()
            p0.child("items").children.forEach{
                items.add(Item(
                        it.key.toInt(),
                        it.child("item_name").value.toString(),
                        it.child("stock").value.toString().toDouble(),
                        it.child("safety_stock").value.toString().toDouble(),
                        it.child("unit_id").value.toString().toInt(),
                        it.child("location").value.toString(),
                        it.child("item_thumbnail").value.toString()
                ))
            }
            onComplete()
        }
    }
    init {
        val db = FirebaseDatabase.getInstance().reference
        db.addValueEventListener(dbEventListener)
    }
    abstract fun onComplete()
}