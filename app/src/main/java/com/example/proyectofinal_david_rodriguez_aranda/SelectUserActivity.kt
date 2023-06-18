package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal_david_rodriguez_aranda.adapters.UsuariosAdapter
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivitySelectUserBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.ComidasPedidas
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Esta activity consiste en un RecyclerView que carga los usuarios disponibles para iniciar sesión.
 * Si un usuario está ya conectado no se permite seleccionarlo, saltando un mensaje de error en caso de que se haga.
 */
class SelectUserActivity : AppCompatActivity() {

//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivitySelectUserBinding
    lateinit var db: FirebaseDatabase
    lateinit var lista: ArrayList<Camarero>
    lateinit var adapter: UsuariosAdapter

//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectUserBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        setContentView(binding.root)
        supportActionBar?.hide()
        setRecycler()
        traerCamareros()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de leer la base de datos y cargar en el Recycler la lista de camareros que están registrados
     */
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de preparar el RecyclerView asignandole el adapter y el tipo de layout con el que se estructurarán los elementos
     */
    private fun setRecycler() {
        lista= arrayListOf<Camarero>()
        adapter= UsuariosAdapter(lista) { onItemClick(it) }
        val layoutManager= LinearLayoutManager(this)

        binding.rvUsers.adapter= adapter
        binding.rvUsers.layoutManager= layoutManager
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método será pasado al RecyclerView como una función lambda.
     * Se encarga de detectar cuando se hace click en uno de los elementos del Recycler, en este caso un camarero.
     * Si el camarero está ya conectado se muestra un dialogo con el mensaje de alerta indicandolo, en caso contrario se crea un intent
     * que iniciará la activity [LoginActivity] para introducir la contraseña;
     * dentro de este intent se enviará el camarero seleccionado para hacer uso de sus datos más adelante.
     *
     * @param c
     */
    private fun onItemClick(c: Camarero) {
        val typeface= Typeface.createFromAsset(assets, "fonts/caveat.ttf")

        if(c.online==true) {
            val builder= AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("Este usuario ya está en uso.\nSi es su usuario y no lo esta usando usted, contacte al administrador.")
                .setPositiveButton("Aceptar",null)

            val dialog= builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val message = dialog.findViewById<TextView>(android.R.id.message)
                val title = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)

                positiveButton?.let {
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

            return
        }

        val i= Intent(this, LoginActivity::class.java).apply {
            putExtra("CAMARERO", c)
        }

        startActivity(i)
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}