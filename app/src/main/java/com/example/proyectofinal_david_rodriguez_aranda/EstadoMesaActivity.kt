package com.example.proyectofinal_david_rodriguez_aranda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityEstadoMesaBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.google.firebase.database.FirebaseDatabase

/**
 * Esta activity muestra los detalles de una mesa seleccionada en la activity [MenuPrincipalActivity] según su estado.
 * Si la mesa está libre, permitirá asignarla al camarero que está usando la aplicación;
 * si la mesa está siendo atendida por otro camarero, infromará de quien la está atendiendo;
 * y si la mesa está en preparación, lo indicará.
 */
class EstadoMesaActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityEstadoMesaBinding
    lateinit var db: FirebaseDatabase
    var mesa: Mesa?= null
    var camarero: Camarero?= null
//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEstadoMesaBinding.inflate(layoutInflater)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        setContentView(binding.root)
        recogerDatos()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de recoger los datos enviados por el intent de la activity [MenuPrincipalActivity];
     * en este caso son el camarero que está usando la aplicación y la mesa seleccionada,
     * los cuales se cargan en las variables [camarero] y [mesa] respectivamente.
     * Despues se llama a los métodos [pintar] y [setListeners]
     */
    private fun recogerDatos() {
        val extra= intent.extras
        mesa= extra?.getSerializable("MESA") as Mesa?
        camarero= extra?.getSerializable("CAMARERO") as Camarero?

        pintar()
        setListeners()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListeners() {
        binding.btAtenderMesa.setOnClickListener {
            asignarMesa()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método cuando es llamado asigna la mesa seleccionada en la activity [MenuPrincipalActivity] al camarero
     * que está usando la aplicación y, en caso de que haya un error, lo notifica con un dialogo
     */
    private fun asignarMesa() {
        mesa?.camarero = camarero
        mesa?.estado= 2
        db.getReference("mesas").child(mesa?.numMesa.toString()).setValue(mesa).addOnSuccessListener {
            finish()

        }.addOnFailureListener {
            val builder= AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("Ha ocurrido un error en la asignación. Si el error persiste contacte al administrador")
                .setPositiveButton("Aceptar",null)
                .create()
                .show()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de cambiar el texto del estado de la mesa según cual sea
     */
    private fun pintar() {
        if(mesa?.estado==0) {
            binding.btAtenderMesa.isVisible= true
            binding.tvMensajeEstadoMesa.text= "MESA LIBRE"

        }else if(mesa?.estado==1) {
            binding.btAtenderMesa.isVisible= false
            binding.tvMensajeEstadoMesa.text= "LA MESA ESTA SIENDO\nLIMPIADA"

        }else if(mesa?.estado==2) {
            binding.btAtenderMesa.isVisible= false
            binding.tvMensajeEstadoMesa.text= "LA MESA ESTA SIENDO ATENDIDA POR\n${mesa?.camarero?.nombre}"

        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}