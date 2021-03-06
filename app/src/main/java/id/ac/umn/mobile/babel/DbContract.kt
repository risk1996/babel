package id.ac.umn.mobile.babel

import android.app.Application
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Company   (val type: String, val typeShort: String, val name: String, val logo: String, val site: String, val officeMain: String, val officeSecondary: String)
class Account   (val _id: Int, val status: String, val email: String, val password: String, val salt: String, val name: String, val role: String, val dob: String, val regDate: String, val lastLogin: String)
class Unit      (val _id: Int, val status: String, val measure: String, val unitName: String, val value: Double, val increment: Double, val unitThumbnail: String)
class Item      (val _id: Int, val status: String, val itemName: String, val stocks: List<Double>, val safetyStock: Double, val unitId: Int, val thumbnail: String)
class Location  (val _id: Int, val status: String, val code: String, val position: String)
class ThirdParty(val _id: Int, val status: String, val role: String, val tpName: String)
class InOut     (val _id: Int, val inOutTime: Date, val accountId: Int, val locationId: Int, val thirdPartyId: Int, val inOutDetail: List<Pair<Int, Double>>)
class FirebaseDb : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
abstract class Data{
    var company : Company = Company("", "", "", "", "","", "")
    var inOutIncomingMax: Int? = null
    var globalStockPrecision: String? = null
    var accountsAll    = ArrayList<Account>()   ; var accountsActive        = ArrayList<Account>()
    var unitsAll       = ArrayList<Unit>()      ; var unitsActive           = ArrayList<Unit>()
    var itemsAll       = ArrayList<Item>()      ; var itemsActive           = ArrayList<Item>()
    var locationsAll   = ArrayList<Location>()  ; var locationsActive       = ArrayList<Location>()
    var thirdPartiesAll= ArrayList<ThirdParty>(); var thirdPartiesActive    = ArrayList<ThirdParty>()
    var transactions   = ArrayList<InOut>()
    init {
        val db = FirebaseDatabase.getInstance().reference
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                p0!!
                company = Company(
                        p0.child("_company").child("company_type").value.toString(),
                        p0.child("_company").child("company_type_short").value.toString(),
                        p0.child("_company").child("company_name").value.toString(),
                        p0.child("_company").child("company_logo").value.toString(),
                        p0.child("_company").child("company_site").value.toString(),
                        p0.child("_company").child("company_office_main").value.toString(),
                        p0.child("_company").child("company_office_secondary").value.toString()
                )
                inOutIncomingMax = p0.child("_global_pref").child("in_out_incoming_max").value.toString().toInt()
                globalStockPrecision = p0.child("_global_pref").child("global_stock_precision").value.toString()
                accountsAll.clear()
                p0.child("accounts").children.forEach{
                    accountsAll.add(Account(
                            it.key.toInt(),
                            it.child("status").value.toString(),
                            it.child("email").value.toString(),
                            it.child("password").value.toString(),
                            it.child("salt").value.toString(),
                            it.child("name").value.toString(),
                            it.child("role").value.toString(),
                            it.child("dob").value.toString(),
                            it.child("reg_date").value.toString(),
                            it.child("last_login").value.toString()
                    ))
                }
                accountsAll.sortBy { it.name }
                accountsActive = ArrayList(accountsAll.filter { it.status == "active" })
                unitsAll.clear()
                p0.child("units").children.forEach{
                    unitsAll.add(Unit(
                            it.key.toInt(),
                            it.child("status").value.toString(),
                            it.child("measure").value.toString(),
                            it.child("unit_name").value.toString(),
                            it.child("val").value.toString().toDouble(),
                            it.child("increment").value.toString().toDouble(),
                            it.child("unit_thumbnail").value.toString()
                    ))
                }
                unitsAll.sortBy { it.measure }
                unitsActive = ArrayList(unitsAll.filter { it.status == "active" })
                itemsAll.clear()
                p0.child("items").children.forEach{
                    itemsAll.add(Item(
                            it.key.toInt(),
                            it.child("status").value.toString(),
                            it.child("item_name").value.toString(),
                            it.child("stocks").children.map { it.value.toString().toDouble() },
                            it.child("safety_stock").value.toString().toDouble(),
                            it.child("unit_id").value.toString().toInt(),
                            it.child("item_thumbnail").value.toString()
                    ))
                }
                itemsAll.sortBy { it.itemName }
                itemsActive = ArrayList(itemsAll.filter { it.status == "active" })
                locationsAll.clear()
                p0.child("locations").children.forEach {
                    locationsAll.add(Location(
                            it.key.toInt(),
                            it.child("status").value.toString(),
                            it.child("code").value.toString(),
                            it.child("position").value.toString()
                    ))
                }
                locationsAll.sortBy { it.code }
                locationsActive = ArrayList(locationsAll.filter { it.status == "active" })
                thirdPartiesAll.clear()
                p0.child("third_parties").children.forEach {
                    thirdPartiesAll.add(ThirdParty(
                            it.key.toInt(),
                            it.child("status").value.toString(),
                            it.child("role").value.toString(),
                            it.child("tp_name").value.toString()
                    ))
                }
                thirdPartiesAll.sortBy { it.tpName }
                thirdPartiesActive = ArrayList(thirdPartiesAll.filter { it.status == "active" })
                p0.child("inout").children.forEach {
                    transactions.add(InOut(
                            it.key.toInt(),
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.child("io_time").value.toString()),
                            it.child("account_id").value.toString().toInt(),
                            it.child("location_id").value.toString().toInt(),
                            it.child("third_party_id").value.toString().toInt(),
                            it.child("io_detail").children.map { Pair(it.key.toInt(), it.value.toString().toDouble()) }
                    ))
                }
                try{ onComplete() }
                catch (e : Exception){ /*e.printStackTrace()*/ }
            }
        })
    }
    abstract fun onComplete()
}