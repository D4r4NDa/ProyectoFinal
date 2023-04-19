package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.proyectofinal_david_rodriguez_aranda.adapters.MesasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityMenuPrincipalBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class MenuPrincipalActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuPrincipalBinding
    lateinit var db: FirebaseDatabase
    lateinit var lista: ArrayList<Mesa>
    lateinit var adapter: MesasAdapter
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var camarero: Camarero
    lateinit var v: View
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        camarero= intent.extras?.getSerializable("CAMARERO") as Camarero
        val drawerLayout: DrawerLayout= findViewById(R.id.drawer_layout)
        navView= findViewById(R.id.nav_view)
        toggle= ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setRecycler()
        traerMesas()
        setListeners()
        setProfile()
    }

    private fun setProfile() {

        var storage: FirebaseStorage = FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")

        navView.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_name).setText(camarero.nombre)
        navView.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_email).setText(camarero.email)

        //Comprobar si el usuario tiene imagen o no
        val ref= storage.reference
        val imagen= ref.child("${camarero.email}/profile.png")
        imagen.metadata.addOnSuccessListener {
            //Existe la imagen y se la ponemos
            imagen.downloadUrl.addOnSuccessListener {
                val requestOptions= RequestOptions().transform(CircleCrop())
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(findViewById(R.id.iv_nav_profile))
            }
        }
            .addOnFailureListener {
                //No existe la imagen, se aplica la default
                val default= ref.child("default/profile.png")
                default.downloadUrl.addOnSuccessListener {
                    val requestOptions= RequestOptions().transform(CircleCrop())
                    Glide.with(this)
                        .load(it)
                        .centerCrop()
                        .apply(requestOptions)
                        .into(findViewById(R.id.iv_nav_profile))
                }
            }
    }

    private fun setListeners() {

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