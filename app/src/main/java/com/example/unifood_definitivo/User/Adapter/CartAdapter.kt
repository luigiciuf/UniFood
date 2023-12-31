package com.example.unifood_definitivo.User.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifood_definitivo.User.Model.CartProduct
import com.example.unifood_definitivo.R
import com.squareup.picasso.Picasso
/**
* Adapter per la visualizzazione dei prodotti nel carrello
 */
class CartAdapter(private val cartProducts: MutableList<CartProduct>) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {
    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currentPosition: Int = 0
        var currentQuantity: Int = 0
        val titleTextView: TextView = itemView.findViewById(R.id.idUtente)
        val quantityTextView: TextView = itemView.findViewById(R.id.numberItemTxt)
        val priceTextView: TextView = itemView.findViewById(R.id.feeEachItem)
        val totalTextView: TextView = itemView.findViewById(R.id.totalEachItem)
        val picCardTextView: ImageView = itemView.findViewById(R.id.picCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_element, parent, false)
        return CartItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem = cartProducts[position]

        holder.currentPosition = position
        holder.currentQuantity = currentItem.quantity

        holder.titleTextView.text = currentItem.product.nome_prodotto
        holder.quantityTextView.text = "${currentItem.quantity}x"
        holder.priceTextView.text = currentItem.product.prezzo.toString()
        holder.totalTextView.text = currentItem.total.toString()
        Picasso.get()
            .load(currentItem.imgUri)
            .into(holder.picCardTextView)


    }

    /**
     * Questa funzione restituisce il numero di elementi nella lista di prodotti nel carrello.
     */
    override fun getItemCount(): Int {
        return cartProducts.size
    }

    /**
     * Questa funzione aggiorna la lista dei prodotti nel carrello con una nuova lista fornita come argomento.
        newCartItems: La nuova lista di prodotti nel carrello.
     */
    fun updateCartItems(newCartItems: List<CartProduct>) {
        cartProducts.clear()
        cartProducts.addAll(newCartItems)
        notifyDataSetChanged()
    }
}