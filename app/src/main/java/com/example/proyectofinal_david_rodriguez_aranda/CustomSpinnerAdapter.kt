package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Esta clase se encarga de alterar las propiedades de un spinner que se encuentra en la activity [PedidosMesaActivity]
 * para que se muestre graficamente como se necesita.
 */
class CustomSpinnerAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    private val customFont: Typeface = Typeface.createFromAsset(context.assets, "fonts/caveat.ttf")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.typeface = customFont
        textView.setTextColor(Color.BLACK)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.typeface = customFont
        textView.setTextColor(Color.BLACK)
        view.setBackgroundColor(Color.WHITE)
        return view
    }
}