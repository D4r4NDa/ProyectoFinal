package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Observer(c: Context, email: String) : LifecycleObserver  {

    var db: FirebaseDatabase= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
    val context= c
    var resume= false
    val correo= email

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
       if(resume) {
           val i= Intent(context, MainActivity::class.java)
           context.startActivity(i)
           resume= false
       }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        db.getReference("camareros").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children) {
                    var c= i.getValue(Camarero::class.java)
                    if(c?.email.equals(correo)) {
                        c?.online= false
                        db.getReference("camareros").child(c?.password.toString()).setValue(c)

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        resume=true
    }
}