package com.example.proyectofinal_david_rodriguez_aranda

import android.R
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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

/**
 * Esta activity gestiona la toma de comandas de las mesas. Desde aquí se toman y se envían al programa "central"
 * en el que se gestionan.
 */
class PedidosMesaActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityPedidosMesaBinding
    lateinit var db: FirebaseDatabase
    lateinit var listaBebidasPedidas: MutableList<BebidasPedidas>
    lateinit var listaComidasPedidas: MutableList<ComidasPedidas>
    lateinit var listaBebidas: MutableList<Bebida>
    lateinit var listaComidas: MutableList<Comida>
    lateinit var adapterComidas: ComidasAdapter
    lateinit var adapterBebidas: BebidasAdapter
    lateinit var mesa: Mesa
//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPedidosMesaBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        listaBebidas= mutableListOf<Bebida>()
        listaComidas= mutableListOf<Comida>()
        setContentView(binding.root)
        supportActionBar?.hide()
        recogerDatos()
        setRecyclers()
        traerProductos()
        setListerners()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de leer la base de datos para cargar los productos disponibles, tanto comida como bebida,
     * para poder ser pedidos.
     */
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método simplemente se encarga de recoger la mesa seleccionada enviada en un intent desde
     * la activity [MenuPrincipalActivity]
     */
    private fun recogerDatos() {
        val extras= intent.extras
        mesa= extras?.getSerializable("MESA") as Mesa
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de preparar los RecyclerView asignandoles el adapter y el tipo de layout con el que se estructurarán
     * los elementos
     */
    private fun setRecyclers() {
        try {
            listaBebidasPedidas= mutableListOf<BebidasPedidas>()
            adapterBebidas= BebidasAdapter(listaBebidasPedidas) { onQuitarBebida(it) }
            binding.rvBebidaPedida.layoutManager= LinearLayoutManager(this)
            binding.rvBebidaPedida.adapter= adapterBebidas

            listaComidasPedidas= mutableListOf<ComidasPedidas>()
            adapterComidas= ComidasAdapter(listaComidasPedidas) { onQuitarComida(it) }
            binding.rvComidaPedida.layoutManager= LinearLayoutManager(this)
            binding.rvComidaPedida.adapter= adapterComidas
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListerners() {
        binding.ivAddBebida.setOnClickListener {
            addBebida()
        }

        binding.ivAddComida.setOnClickListener {
            addComida()
        }

        binding.btConfirmarPedido.setOnClickListener {
            confirmarPedido()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método muestra un dialogo emergente que permite seleccionar una comida y la cantidad de ella que se desea,
     * cuando se completa la selección se añade a una lista local donde queda a la espera de ser enviado junto con el resto
     * del pedido a la base de datos.
     */
    private fun addComida() {
        try {
            val dialogBuilder = AlertDialog.Builder(this)
            val bindingDialog = SelectorPedidoLayoutBinding.inflate(layoutInflater)
            dialogBuilder.setView(bindingDialog.root)

            var unidades= 1

            bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())

            bindingDialog.ivMas.setOnClickListener {
                unidades++
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            bindingDialog.ivMenos.setOnClickListener {
                if(unidades>1) {
                    unidades--
                    bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
                }
            }

            var listaNombres= mutableListOf<String>()
            for(c in listaComidas) {
                listaNombres.add(c.nombre.toString())
            }

            val arrayAdapter = CustomSpinnerAdapter(this, R.layout.simple_spinner_item, listaNombres)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingDialog.spProducto.adapter= arrayAdapter


            dialogBuilder.setPositiveButton("Aceptar") { _, _ ->
                var encontrado= false
                var comida: Comida?= null

                try {
                    for(c in listaComidas) {
                        if(c.nombre?.equals(bindingDialog.spProducto.selectedItem) == true) {
                            comida= c
                        }
                    }

                    for(c in listaComidasPedidas) {
                        if(bindingDialog.spProducto.selectedItem.equals(c.comida?.nombre)) {
                            c.cantidad= c.cantidad?.plus(bindingDialog.tvCantidadSeleccionada.text.toString().toInt())
                            encontrado= true
                        }
                    }

                    if(!encontrado) {
                        listaComidasPedidas.add(ComidasPedidas(comida, bindingDialog.tvCantidadSeleccionada.text.toString().toInt()))
                    }

                    adapterComidas.notifyDataSetChanged()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val dialog= dialogBuilder.create()
            dialog.window?.setBackgroundDrawableResource(com.example.proyectofinal_david_rodriguez_aranda.R.drawable.dialog_round_corners)
            dialog.setOnShowListener {
                val typeface= Typeface.createFromAsset(assets, "fonts/caveat.ttf")
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                positiveButton?.let {
                    it.setTypeface(typeface)
                    it.setTextColor(Color.BLACK)
                }
            }
            dialog.show()
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método muestra un dialogo emergente que permite seleccionar una bebida y la cantidad de ella que se desea,
     * cuando se completa la selección se añade a una lista local donde queda a la espera de ser enviado junto con el resto
     * del pedido a la base de datos.
     */
    private fun addBebida() {
        try {
            val dialogBuilder = AlertDialog.Builder(this)
            val bindingDialog = SelectorPedidoLayoutBinding.inflate(layoutInflater)
            dialogBuilder.setView(bindingDialog.root)

            var unidades= 1

            bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())

            bindingDialog.ivMas.setOnClickListener {
                unidades++
                bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
            }

            bindingDialog.ivMenos.setOnClickListener {
                if(unidades>1) {
                    unidades--
                    bindingDialog.tvCantidadSeleccionada.setText(unidades.toString())
                }
            }

            var listaNombres= mutableListOf<String>()
            for(b in listaBebidas) {
                listaNombres.add(b.nombre.toString())
            }

            val arrayAdapter = CustomSpinnerAdapter(this, R.layout.simple_spinner_item, listaNombres)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bindingDialog.spProducto.adapter= arrayAdapter

            dialogBuilder.setPositiveButton("Aceptar") { _, _ ->
                var encontrado= false
                var bebida: Bebida?= null

                try {
                    for(b in listaBebidas) {
                        if(b.nombre?.equals(bindingDialog.spProducto.selectedItem) == true) {
                            bebida= b
                        }
                    }

                    for(b in listaBebidasPedidas) {
                        if(bindingDialog.spProducto.selectedItem.equals(b.bebida?.nombre)) {
                            b.cantidad= b.cantidad?.plus(bindingDialog.tvCantidadSeleccionada.text.toString().toInt())
                            encontrado= true
                        }
                    }

                    if(!encontrado) {
                        listaBebidasPedidas.add(BebidasPedidas(bebida, bindingDialog.tvCantidadSeleccionada.text.toString().toInt()))
                    }

                    adapterBebidas.notifyDataSetChanged()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val dialog= dialogBuilder.create()
            dialog.window?.setBackgroundDrawableResource(com.example.proyectofinal_david_rodriguez_aranda.R.drawable.dialog_round_corners)
            dialog.setOnShowListener {
                val typeface= Typeface.createFromAsset(assets, "fonts/caveat.ttf")
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                positiveButton?.let {
                    it.setTypeface(typeface)
                    it.setTextColor(Color.BLACK)
                }
            }
            dialog.show()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método permite eliminar una bebida de la lista local en caso de que se añada por error.
     */
    private fun onQuitarBebida(b: Bebida) {
        for(bp in listaBebidasPedidas) {
            if(bp.bebida?.nombre.equals(b.nombre)) {
                listaBebidasPedidas.remove(bp)
                adapterBebidas.notifyDataSetChanged()
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método permite eliminar una comida de la lista local en caso de que se añada por error.
     */
    private fun onQuitarComida(c: Comida) {
        for(cp in listaComidasPedidas) {
            if(cp.comida?.nombre.equals(c.nombre)) {
                listaComidasPedidas.remove(cp)
                adapterComidas.notifyDataSetChanged()
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método toma la lista local con todos los pedidos y los envía a la base de datos.
     */
    private fun confirmarPedido() {
        var encontrado= false

        try {
            val builder = AlertDialog.Builder(this)
                .setMessage("¿Quieres envíar este pedido?")
                .setPositiveButton("Aceptar") { _, _ ->
                    try {
                        if (mesa.cPedidas.isNullOrEmpty()) {
                            mesa.cPedidas = listaComidasPedidas
                        } else {
                            for (cp in listaComidasPedidas) {
                                encontrado = false

                                for (c in mesa.cPedidas!!) {
                                    if (cp.comida?.nombre.equals(c.comida?.nombre)) {
                                        c.cantidad = c.cantidad?.plus(cp.cantidad!!)
                                        encontrado = true
                                    }
                                }

                                if (!encontrado) {
                                    mesa.cPedidas!!.add(cp)
                                }
                            }
                        }

                        if (mesa.bPedidas.isNullOrEmpty()) {
                            mesa.bPedidas = listaBebidasPedidas
                        } else {
                            for (bp in listaBebidasPedidas) {
                                encontrado = false

                                for (b in mesa.bPedidas!!) {
                                    if (bp.bebida?.nombre.equals(b.bebida?.nombre)) {
                                        b.cantidad = b.cantidad?.plus(bp.cantidad!!)
                                        encontrado = true
                                    }
                                }

                                if (!encontrado) {
                                    mesa.bPedidas!!.add(bp)
                                }
                            }
                        }

                        db.getReference("mesas").child(mesa.numMesa.toString()).setValue(mesa)
                            .addOnFailureListener {
                                val error = AlertDialog.Builder(this)
                                    .setTitle("ERROR")
                                    .setMessage("Ha habido un error. Intentalo de nuevo y si el error persiste contacta un administrador")
                                    .setPositiveButton("Aceptar", null)
                                    .show()
                            }.addOnSuccessListener {
                                listaBebidasPedidas.clear()
                                listaComidasPedidas.clear()
                                adapterBebidas.notifyDataSetChanged()
                                adapterComidas.notifyDataSetChanged()
                                finish()
                            }
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancelar", null)

            val dialog= builder.create()
            dialog.window?.setBackgroundDrawableResource(com.example.proyectofinal_david_rodriguez_aranda.R.drawable.dialog_round_corners)


            dialog.setOnShowListener {
                val typeface= Typeface.createFromAsset(assets, "fonts/caveat.ttf")
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                val message = dialog.findViewById<TextView>(android.R.id.message)
                val title = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)

                positiveButton?.let {
                    it.setTypeface(typeface)
                    it.setTextColor(Color.BLACK)
                }
                negativeButton?.let {
                    it.setTypeface(typeface)
                    it.setTextColor(Color.BLACK)
                }
                message?.let {
                    it.setTypeface(typeface)
                    it.setTextColor(Color.BLACK)
                    it.setTextSize(25F)
                }
                title?.let {
                    it.setTypeface(typeface)
                    it.setTextSize(45F)
                    it.setTextColor(Color.BLACK)
                }
            }
            dialog.show()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}