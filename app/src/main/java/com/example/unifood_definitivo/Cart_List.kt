package com.example.unifood_definitivo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifood_definitivo.Adapter.CartAdapter
import com.example.unifood_definitivo.Adapter.OrariAdapter
import com.example.unifood_definitivo.Model.*


import com.google.firebase.database.*

class Cart_List : AppCompatActivity() {
 private val userCarts = HashMap<String, ArrayList<CartProduct>>()
 private val savedCartItems = ArrayList<CartProduct>()
 private lateinit var cartRecyclerView: RecyclerView
 private lateinit var cartListAdapter: CartAdapter
 private val cartItems = ArrayList<CartProduct>()
 private lateinit var database: DatabaseReference
 private val orariList = ArrayList<Orari>()
 private lateinit var orariAdapter: OrariAdapter

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_cart_list)
  database = FirebaseDatabase.getInstance().reference.child("Orari")
  val orariRecyclerView = findViewById<RecyclerView>(R.id.recyclerview2)
  orariAdapter = OrariAdapter(emptyList())
  orariRecyclerView.adapter = orariAdapter
  orariRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
  cartRecyclerView = findViewById(R.id.recyclerview)
  cartListAdapter = CartAdapter(cartItems)
  cartRecyclerView.adapter = cartListAdapter
  cartRecyclerView.layoutManager = LinearLayoutManager(this)
  val intentCartItems = intent.getSerializableExtra("cartItems") as ArrayList<CartProduct>?
  if (intentCartItems != null) {
   cartItems.addAll(intentCartItems)
   cartListAdapter.notifyDataSetChanged()
  } else {
   // Verifica se ci sono dati nell'Intent e aggiungi il prodotto al carrello


   val product = intent.getSerializableExtra("product") as Prodotti?
   val quantity = intent.getIntExtra("quantity", 0)
   val imgUri = intent.getStringExtra("imgUri")
   println("#######################Dati Ricevuti nell'Intent:")
   println("Product: $product")
   println("Quantity: $quantity")
   println("ImgUri: $imgUri")
   val userId = intent.getStringExtra("userId") ?: ""
   val userCart = userCarts.getOrPut(userId) { ArrayList() }
   if (product != null) {
    val cartItem = CartProduct(product, quantity, imgUri, product.prezzo?.times(quantity))
    cartItem.total = product.prezzo?.times(quantity)
    cartItems.add(cartItem)
    cartListAdapter.notifyDataSetChanged() // Aggiorna la RecyclerView
   }
  }

  calculateAndDisplayTotal()
  fetchOrariData()
 }
 private fun calculateAndDisplayTotal() {
  val subtotal = calculateSubtotal(cartItems)
  val commission = 2.0
  val total = subtotal + commission

  val subtotalView = findViewById<TextView>(R.id.totalFeeTxt)
  val commissionView = findViewById<TextView>(R.id.taxTxt)
  val totalView = findViewById<TextView>(R.id.totalTxt)

  subtotalView.text = "$$subtotal"
  commissionView.text = " $$commission"
  totalView.text = "$$total"
 }

 private fun calculateSubtotal(userCart: ArrayList<CartProduct>): Double {
  var subtotal = 0.0
  for (cartItem in userCart) {
   subtotal += cartItem.total ?: 0.0
  }
  return subtotal
 }

 private fun fetchOrariData() {
  val orariList = ArrayList<Orari>() // Create a list to store fetched data
  database.addValueEventListener(object : ValueEventListener {
   override fun onDataChange(snapshot: DataSnapshot) {
    orariList.clear()
    for (orariSnapshot in snapshot.children) {
     val disponibilita = orariSnapshot.child("disponibilita").getValue(Int::class.java)
     val fascia_oraria = orariSnapshot.child("fascia_oraria").getValue(String::class.java)
     if (disponibilita != null && fascia_oraria != null) {
      println("Disponibilita: $disponibilita, Fascia oraria: $fascia_oraria")
      val orari = Orari(disponibilita, fascia_oraria)
      orariList.add(orari)
     }
    }
    orariAdapter.updateData(orariList) // Update the adapter with fetched data
   }

   override fun onCancelled(error: DatabaseError) {
    // Handle onCancelled if needed
   }
  })
 }



  object CartManager {
   private val userCarts = HashMap<String, ArrayList<CartProduct>>()

   fun addToCart(userId: String, product: Prodotti, quantity: Int, imgUri: String?) {
    val userCart = userCarts.getOrPut(userId) { ArrayList() }
    val cartItem = CartProduct(product, quantity, imgUri, product.prezzo?.times(quantity))
    cartItem.total = product.prezzo?.times(quantity)
    userCart.add(cartItem)
   }

   fun getCartItems(userId: String): List<CartProduct> {
    return userCarts[userId] ?: emptyList()
   }
  }
 }



