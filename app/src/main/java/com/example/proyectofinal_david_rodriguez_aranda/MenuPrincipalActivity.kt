package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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

/**
 * Este es la activity principal de la aplicación, desde aquí se accede al resto de funciones.
 * Aquí se muestra un RecyclerView que contiene las mesas disponibles en el establecimiento.
 * Al pulsar en una de las mesas se mostrará la activity [EstadoMesaActivity].
 *
 * Además esta activity cuenta con un menú de navegación lateral el cual permite acceder a tres opciones:
 * - Ayuda
 * - Perfil
 * - Cerrar sesión
 */
class MenuPrincipalActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityMenuPrincipalBinding
    lateinit var db: FirebaseDatabase
    lateinit var lista: ArrayList<Mesa>
    lateinit var adapter: MesasAdapter
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var camarero: Camarero
    lateinit var navView: NavigationView

//****************************************************METODOS******************************************************************************************************************************************
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de cargar los datos de perfil del camarero en el menu lateral de navegación
     */
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListeners() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> {
                    viewProfile()
                    true
                }
                R.id.nav_settings -> {
                    gotoHelp()
                    true
                }
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> true
            }
        }


    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se llama cuando se pulsa en la opción del menú de ayuda.
     * Al pulsar se abre un manual de uso de la aplicación en el navegador web predeterminado del teléfono
     */
    private fun gotoHelp() {
        val dialogBuilder = AlertDialog.Builder(this)
        val webView = WebView(this)

// Configure the WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true// Enable JavaScript if needed
        webView.loadUrl("file:///android_asset/help.html") // Load the HTML file from the assets folder

// Set the WebView as the view for the dialog
        dialogBuilder.setView(webView)

// Add any additional dialog configuration
        dialogBuilder.setTitle("HTML Content")
        dialogBuilder.setPositiveButton("Close") { _, _ ->
            // Handle close button action if needed
        }

// Create and show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se llama cuando se pulsa en la opción del menú de perfil.
     * Al pulsar se pasa a la activity [UserConfigActivity] que permite ver los datos del perfil del camarero,
     * así como modificar la imagen de perfil
     */
    private fun viewProfile() {
        startActivity(Intent(this, UserConfigActivity::class.java).apply {
            putExtra("CAMARERO", camarero)
        })
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método es llamado cunado se pulsa la opción del menú de cerrar sesión.
     * Primero se pregunta al camarero si realmente quiere cerrar sesión en caso de que lo pulse por error; si acepta, se pasa a la
     * activity [MainActivity] y se cierra esta, además se cambia el estado del camarero a desconectado
     */
    private fun logout() {
        val builder= AlertDialog.Builder(this)
            .setTitle("CERRAR SESIÓN")
            .setMessage("¿Quieres cerrar sesión?")
            .setPositiveButton("Si") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                camarero.online= false
                db.getReference("camareros").child(camarero.password.toString()).setValue(camarero)
                finish()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de leer la base de datos y cargar en el Recycler la lista de mesas que hay en el establecimiento
     */
    private fun traerMesas() {
        try {
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
                    System.out.print("ERROR AL LEER LAS MESAS")
                }

            })
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de preparar el RecyclerView asignandole el adapter y el tipo de layout con el que se estructurarán los elementos
     */
    private fun setRecycler() {
        lista= arrayListOf<Mesa>()
        adapter= MesasAdapter(lista) { onItemClick(it) }
        val layoutManager= LinearLayoutManager(this)

        binding.rvMesas.adapter= adapter
        binding.rvMesas.layoutManager= layoutManager
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método será pasado al RecyclerView como una función lambda.
     * Se encarga de detectar cuando se hace click en uno de los elementos del Recycler, en este caso una mesa.
     * Si la mesa está libre se permite al camarero asignarsela y atenderla, si está siendo ya atendida muestra que camarero lo está
     * haciendo y si está siendo preparada lo indica.
     *
     * El estado de la mesa se detalla en la activity [EstadoMesaActivity], que se lanza a través de un intent en el que además
     * se enviará el camarero activo y la mesa seleccionada.
     *
     * @param m
     */
    private fun onItemClick(m: Mesa) {

        if(m.camarero?.equals(camarero) == true) {
            val i= Intent(this, PedidosMesaActivity::class.java).apply {
                putExtra("MESA", m)
                putExtra("CAMARERO", camarero)
            }

            startActivity(i)
        }else {
            val i= Intent(this, EstadoMesaActivity::class.java).apply {
                putExtra("MESA", m)
                putExtra("CAMARERO", camarero)
            }

            startActivity(i)
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Esta sobreescritura del metodo [onBackPressed] anula su función normal de volver a la activity anterior y en su lugar
     * se le asigna la funcionalidad del método [logout]
     */
    override fun onBackPressed() {
        logout()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}