package com.example.proyectofinal_david_rodriguez_aranda

import android.R
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal_david_rodriguez_aranda.adapters.BebidasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.adapters.ComidasAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityPedidosMesaBinding
import com.example.proyectofinal_david_rodriguez_aranda.databinding.SelectorPedidoLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PedidosMesaActivity : AppCompatActivity() {

    lateinit var binding: ActivityPedidosMesaBinding
    lateinit var db: FirebaseDatabase
    lateinit var listaBebidasPedidas: MutableList<BebidasPedidas>
    lateinit var listaComidasPedidas: MutableList<ComidasPedidas>
    lateinit var listaBebidas: MutableList<Bebida>
    lateinit var listaComidas: MutableList<Comida>
    lateinit var adapterComidas: ComidasAdapter
    lateinit var adapterBebidas: BebidasAdapter
    lateinit var mesa: Mesa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPedidosMesaBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        listaBebidas= mutableListOf<Bebida>()
        listaComidas= mutableListOf<Comida>()
        setContentView(binding.root)
        setRecyclers()
        recogerDatos()
        traerProductos()
        setListerners()
    }

    private fun traerProductos() {
        db.getReference("bebidas").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaBebidas.clear()
                for(i in snapshot.children) {
                    val bebida= i.getValue(Bebida::class.java)
                    if(bebida != null) {
                        listaBebidas.add(bebida)
                    }
                }

                listaBebidas.sortBy { bebida -> bebida.nombre }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        db.getReference("comidas").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaComidas.clear()
                for(i in snapshot.children) {
                    val comida= i.getValue(Comida::class.java)
                    if(comida != null) {
                        listaComidas.add(comida)
                    }
                }

                listaComidas.sortBy { comida -> comida.nombre }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setRecyclers() {
        listaBebidasPedidas= mutableListOf<BebidasPedidas>()
        adapterBebidas= BebidasAdapter(listaBebidasPedidas)
        binding.rvBebidaPedida.layoutManager= LinearLayoutManager(this)
        binding.rvBebidaPedida.adapter= adapterBebidas

        listaComidasPedidas= mutableListOf<ComidasPedidas>()
        adapterComidas= ComidasAdapter(listaComidasPedidas)
        binding.rvComidaPedida.layoutManager= LinearLayoutManager(this)
        binding.rvComidaPedida.adapter= adapterComidas
    }

    private fun recogerDatos() {
        var extras= intent.extras

        mesa= extras?.getSerializable("MESA") as Mesa

        listaBebidasPedidas= mesa.bPedidas!!
        adapterBebidas.lista= listaBebidasPedidas
        adapterBebidas.notifyDataSetChanged()

        listaComidasPedidas= mesa.cPedidas!!
        adapterComidas.lista= listaComidasPedidas
        adapterComidas.notifyDataSetChanged()
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
        try {
            val dialogBuilder = AlertDialog.Builder(this)
            val bindingDialog = SelectorPedidoLayoutBinding.inflate(layoutInflater)
            dialogBuilder.setView(bindingDialog.root)

            var unidades= 0

            bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())

            bindingDialog.ivMas.setOnClickListener {
                unidades++
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            bindingDialog.ivMenos.setOnClickListener {
                unidades--
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            var listaNombres= mutableListOf<String>()
            for(c in listaComidas) {
                listaNombres.add(c.nombre.toString())
            }

            val arrayAdapter = CustomSpinnerAdapter(this, R.layout.simple_spinner_item, listaNombres)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingDialog.spProducto.adapter= arrayAdapter

            dialogBuilder.show()
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addBebida() {
        try {
            val dialogBuilder = AlertDialog.Builder(this)
            val bindingDialog = SelectorPedidoLayoutBinding.inflate(layoutInflater)
            dialogBuilder.setView(bindingDialog.root)

            var unidades= 0

            bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())

            bindingDialog.ivMas.setOnClickListener {
                unidades++
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            bindingDialog.ivMenos.setOnClickListener {
                unidades--
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            var listaNombres= mutableListOf<String>()
            for(b in listaBebidas) {
                listaNombres.add(b.nombre.toString())
            }

            val arrayAdapter = CustomSpinnerAdapter(this, R.layout.simple_spinner_item, listaNombres)
            val font = Typeface.createFromAsset(assets, "fonts/caveat.ttf")
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingDialog.spProducto.adapter= arrayAdapter

            dialogBuilder.show()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}