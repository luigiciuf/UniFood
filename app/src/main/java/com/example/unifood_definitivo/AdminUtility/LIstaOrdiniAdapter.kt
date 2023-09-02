package com.example.unifood_definitivo.AdminUtility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifood_definitivo.Model.User
import com.example.unifood_definitivo.R
import com.google.firebase.database.FirebaseDatabase

class LIstaOrdiniAdapter(private val userList: MutableList<User>) : RecyclerView.Adapter<LIstaOrdiniAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gestione_utenti_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        // Popola le viste nel layout XML con i dati dell'utente
        holder.userNameTextView.text = "Nome: ${user.name}"
        holder.userSurnameTextView.text = "Cognome: ${user.surname}"
        holder.userMailTextView.text = "Email: ${user.email}"
        holder.idUtenteTextView.text = "User Id: ${user.id}"
        holder.cancellautente.setOnClickListener {
            deleteUser(position)
            //notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userName)
        val userSurnameTextView: TextView = itemView.findViewById(R.id.userSurname)
        val userMailTextView: TextView = itemView.findViewById(R.id.userMail)
        val idUtenteTextView: TextView = itemView.findViewById(R.id.idUtente)
        val cancellautente: TextView=itemView.findViewById(R.id.cancellautente)
    }
    private var isDeleting = false // Variabile di blocco

    fun deleteUser(position: Int) {
        if (isDeleting) {
            return // Se la cancellazione è in corso, esci senza fare nulla
        }

        val user = userList[position]
        val userId = user.id
        isDeleting = true // Imposta il flag di cancellazione su true

        // Ottieni un riferimento al nodo dell'utente nel database
        val databaseReference = FirebaseDatabase.getInstance().getReference("Utenti").child(userId)

        // Rimuovi l'utente dal database
        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Rimuovi l'utente dalla tua lista dopo che è stato rimosso dal database
                userList.remove(user)
                //notifyDataSetChanged()

                // Ripristina il flag di cancellazione a false dopo la rimozione
                isDeleting = false
            } else {
                // Gestisci l'errore se la rimozione non ha avuto successo
                // task.exception contiene l'errore specifico

                // Assicurati di ripristinare il flag anche in caso di errore
                isDeleting = false
            }
        }
    }

}