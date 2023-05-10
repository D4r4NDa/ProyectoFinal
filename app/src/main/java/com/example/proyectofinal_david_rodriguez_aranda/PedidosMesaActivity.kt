package com.example.proyectofinal_david_rodriguez_aranda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal_david_rodriguez_aranda.adapters.BebidasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.adapters.ComidasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityPedidosMesaBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas
import com.example.proyectofinal_david_rodriguez_aranda.models.ComidasPedidas
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.google.firebase.database.FirebaseDatabase

class PedidosMesaActivity : AppCompatActivity() {

    lateinit var binding: ActivityPedidosMesaBinding
    lateinit var db: FirebaseDatabase
    lateinit var listaBebidas: ArrayList<BebidasPedidas>
    lateinit var listaComidas: ArrayList<ComidasPedidas>
    lateinit var adapterComidas: ComidasAdapter
    lateinit var adapterBebidas: BebidasAdapter
    lateinit var mesa: Mesa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this,"HE ENTRADO EN PEDIDOS MESA", Toast.LENGTH_SHORT).show()

        binding= ActivityPedidosMesaBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        setContentView(R.layout.activity_pedidos_mesa)
        listaBebidas= arrayListOf<BebidasPedidas>()
        adapterBebidas= BebidasAdapter(listaBebidas)
        listaComidas= arrayListOf<ComidasPedidas>()
        adapterComidas= ComidasAdapter(listaComidas)
        recogerDatos()
        setRecyclers()
        setListerners()
    }

    private fun setRecyclers() {

        Toast.makeText(this,"HE ENTRADO EN SET RECYCLER", Toast.LENGTH_SHORT).show()

        val layoutManagerBebidas= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val layoutManagerComidas= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        adapterBebidas= BebidasAdapter(listaBebidas)
        binding.rvBebidaPedida.adapter= adapterBebidas
        binding.rvBebidaPedida.layoutManager= layoutManagerBebidas
        adapterBebidas.notifyDataSetChanged()

        adapterComidas= ComidasAdapter(listaComidas)
        binding.rvComidaPedida.adapter= adapterComidas
        binding.rvComidaPedida.layoutManager= layoutManagerComidas
        adapterComidas.notifyDataSetChanged()
    }

    private fun recogerDatos() {

        Toast.makeText(this,"HE ENTRADO EN RECOGER DATOS", Toast.LENGTH_SHORT).show()

        var extras= intent.extras

        mesa= extras?.getSerializable("MESA") as Mesa
        if(mesa == null) {
            System.out.print("Mesa NULL")
        }
        listaBebidas= mesa.bPedidas!!

        listaComidas= mesa.cPedidas!!

    }

    private fun setListerners() {
        binding.ivAddBebida.setOnClickListener {
            addBebida()
        }

        binding.ivAddComida.setOnClickListener {
            addComida()
        }
    }

    private fun addComida() {
        System.out.print("ADD COMIDA")
    }

    private fun addBebida() {
        System.out.print("ADD BEBIDA")
    }
}