package id.ac.umn.mobile.babel

import android.app.Application
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Company(val status: String, val statusShort: String, val name: String, val logo: String, val site: String, val officeMain: String, val officeSecondary: String)
class Account(val _id: Int, val email: String, val password: String, val salt: String, val name: String, val role: String, val dob: String, val reg_date: String)
class Unit(val _id: Int, val measure: String, val unit_name: String, val value: Double, val increment: Double, val unit_thumbnail: String)
class Item(val _id: Int, val itemName: String, val stocks: List<Double>, val safetyStock: Double, val unit_id: Int, val thumbnail: String)
class Location(val _id: Int, val code: String, val position: String)
class ThirdParty(val _id: Int, val tp_type: String, val tp_name: String, val tp_status: String)
//database menggunakan FirebaseDb
class FirebaseDb : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
abstract class Data{
    var company : Company = Company("", "", "", "", "","", "")
    var accounts = ArrayList<Account>()
    var units = ArrayList<Unit>()
    var items = ArrayList<Item>()
    var locations = ArrayList<Location>()
    var thirdParties = ArrayList<ThirdParty>()
    init {
        val db = FirebaseDatabase.getInstance().reference
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                p0!!
                company = Company(
                        p0.child("_company").child("company_status").value.toString(),
                        p0.child("_company").child("company_status_short").value.toString(),
                        p0.child("_company").child("company_name").value.toString(),
                        p0.child("_company").child("company_logo").value.toString(),
                        p0.child("_company").child("company_site").value.toString(),
                        p0.child("_company").child("company_office_main").value.toString(),
                        p0.child("_company").child("company_office_secondary").value.toString()
                )
                accounts.clear()
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
                            it.child("stocks").children.map { it.value.toString().toDouble() },
                            it.child("safety_stock").value.toString().toDouble(),
                            it.child("unit_id").value.toString().toInt(),
                            it.child("item_thumbnail").value.toString()
                    ))
                }
                locations.clear()
                p0.child("locations").children.forEach {
                    locations.add(Location(
                            it.key.toInt(),
                            it.child("code").value.toString(),
                            it.child("position").value.toString()
                    ))
                }
                thirdParties.clear()
                p0.child("third_parties").children.forEach {
                    thirdParties.add(ThirdParty(
                            it.key.toInt(),
                            it.child("tp_type").value.toString(),
                            it.child("tp_name").value.toString(),
                            it.child("tp_status").value.toString()
                    ))
                }
                try{
                    onComplete()
                }
                catch (e : NoSuchElementException){}
            }
        })
    }
    abstract fun onComplete()
}