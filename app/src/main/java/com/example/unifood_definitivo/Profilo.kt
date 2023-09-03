package com.example.unifood_definitivo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import java.util.*

class Profilo : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmEditText: EditText
    private lateinit var balanceEditText: EditText
    private lateinit var helloText: TextView
    private var isEditMode: Boolean = false // Variabile di stato per la modalità di modifica

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilo)
        val userName = intent.getStringExtra("userName")
        val userId = intent.getStringExtra("userId")
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.reference.child("Utenti")

        val confirmButton: MaterialButton = findViewById(R.id.confirm_button)
        nameEditText = findViewById(R.id.name)
        surnameEditText = findViewById(R.id.surname)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        confirmEditText = findViewById(R.id.confirm)
        balanceEditText = findViewById(R.id.balance)


        // Trova la TextView "Modifica" nel layout
        val editTextView = findViewById<TextView>(R.id.modificaView)
        editTextView.setOnClickListener {
            isEditMode = !isEditMode
            updateEditMode()
        }
        confirmButton.setOnClickListener {
            if (!isEditMode) {
                showToast("Clicca su Modifica prima di apportare modifiche.")
                return@setOnClickListener
            }
            // Chiamata alla funzione per aggiornare le informazioni nel database
            if (userId != null) {
                updateUserInfo(userId)
            }
            // Disabilita la modalità di modifica dopo aver aggiornato
            isEditMode = false
            updateEditMode()
        }

        if (userId != null) {
            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nome = snapshot.child("name").getValue(String::class.java)
                        val cognome = snapshot.child("surname").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val password = snapshot.child("password").getValue(String::class.java)
                        val saldo = snapshot.child("initialBalance").getValue(Double::class.java)
                        nameEditText.setText(nome)
                        surnameEditText.setText(cognome)
                        emailEditText.setText(email)
                        passwordEditText.setText(password)
                        confirmEditText.setText(password)
                        val saldoFormatted = String.format(Locale.getDefault(), "%.2f", saldo)
                        balanceEditText.setText(saldoFormatted)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori
                }
            })
        }
        updateEditMode()
    }

    private fun updateEditMode() {
        nameEditText.isEnabled = isEditMode
        surnameEditText.isEnabled = isEditMode
        emailEditText.isEnabled = isEditMode
        passwordEditText.isEnabled = isEditMode
        confirmEditText.isEnabled = isEditMode
        balanceEditText.isEnabled = isEditMode
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun updateUserInfo(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.reference.child("Utenti").child(userId)

        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmEditText.text.toString()
        val balanceString = balanceEditText.text.toString()
        if (password != confirmPassword) {
            showToast("Le password inserite sono diverse.")
            return
        }

        try {
            val balance = balanceString.toDouble()

            val userUpdates = hashMapOf<String, Any>(
                "name" to name,
                "surname" to surname,
                "email" to email,
                "password" to password,
                "initialBalance" to balance
            )

            usersRef.updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Aggiornamento completato con successo
                        // Puoi mostrare un messaggio o effettuare altre azioni
                    } else {
                        // Gestisci l'errore nell'aggiornamento
                    }
                }
        } catch (e: NumberFormatException) {
            // Gestisci il caso in cui la conversione non è riuscita
            showToast("Inserire un saldo valido")
        }
    }
}