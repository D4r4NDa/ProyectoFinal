package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal_david_rodriguez_aranda.adapters.MesasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityMenuPrincipalBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuPrincipalActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuPrincipalBinding
    lateinit var db: FirebaseDatabase
    lateinit var lista: ArrayList<Mesa>
    lateinit var adapter: MesasAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuPrincipalBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        setContentView(binding.root)
        setRecycler()
        traerMesas()
    }

    private fun traerMesas() {
        db.getReference("mesas").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                for(i in snapshot.children) {
                    val mesa= i.getValue(Mesa::class.java)
                    if(mesa != null) {
                        lista.add(mesa)
                    }
                }

                lista.sortBy { mesa -> mesa.numMesa }
                adapter.lista= lista
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun setRecycler() {
        lista= arrayListOf<Mesa>()
        adapter= MesasAdapter(lista) { onItemClick(it) }
        val layoutManager= LinearLayoutManager(this)

        binding.rvMesas.adapter= adapter
        binding.rvMesas.layoutManager= layoutManager
    }

    private fun onItemClick(m: Mesa) {
        val i= Intent(this, EstadoMesaActivity::class.java).apply {
            putExtra("MESA", m)
        }

        startActivity(i)
    }
}