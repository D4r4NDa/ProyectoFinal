package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal_david_rodriguez_aranda.adapters.UsuariosAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivitySelectUserBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SelectUserActivity : AppCompatActivity() {

    lateinit var binding: ActivitySelectUserBinding
    lateinit var db: FirebaseDatabase
    lateinit var lista: ArrayList<Camarero>
    lateinit var adapter: UsuariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectUserBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        setContentView(binding.root)
        setRecycler()
        traerCamareros()
    }

    private fun traerCamareros() {
        db.getReference("camareros").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for(i in snapshot.children) {
                    val camarero= i.getValue(Camarero::class.java)
                    if(camarero != null) {
                        lista.add(camarero)
                    }
                }

                lista.sortBy { camarero -> camarero.password }
                adapter.lista= lista
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun setRecycler() {
        lista= arrayListOf<Camarero>()
        adapter= UsuariosAdapter(lista) { onItemClick(it) }
        val layoutManager= LinearLayoutManager(this)

        binding.rvUsers.adapter= adapter
        binding.rvUsers.layoutManager= layoutManager
    }

    private fun onItemClick(c: Camarero) {

        if(c.online==true) {
            val builder= AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("Este usuario ya está en uso.\nSi es su usuario y no lo esta usando usted, contacte al administrador.")
                .setPositiveButton("Aceptar",null)
                .create()
                .show()

            return
        }

        val i= Intent(this, LoginActivity::class.java).apply {
            putExtra("CAMARERO", c)
        }

        startActivity(i)
    }
}