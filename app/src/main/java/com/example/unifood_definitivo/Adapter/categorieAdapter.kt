package com.example.unifood_definitivo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.unifood_definitivo.R
import com.example.unifood_definitivo.Model.Categorie

class CategorieAdapter(private val categorie: List<Categorie>) : RecyclerView.Adapter<CategorieAdapter.ViewHolder>() {

    private var itemClickListener: ((String) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val catName: TextView = view.findViewById(R.id.categoryName)
        val productIcon: ImageView = view.findViewById(R.id.categoryPic)
        val itemBackground: ConstraintLayout = view.findViewById(R.id.constraint_background)

        init {
            itemView.setOnClickListener {
                val clickedCategory = categorie[adapterPosition]
                itemClickListener?.invoke(clickedCategory.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lista_categorie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = categorie[position]
        holder.catName.text = product.name
        holder.productIcon.setImageResource(product.imageResId)
        holder.itemBackground.setBackgroundResource(product.backgroundResId)
    }

    override fun getItemCount() = categorie.size

    fun setOnItemClickListener(listener: (String) -> Unit) {
        itemClickListener = listener
    }
}
