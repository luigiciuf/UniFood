package com.example.unifood_definitivo.Admin.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.unifood_definitivo.User.Model.OrdineS
import com.example.unifood_definitivo.R
import com.google.firebase.database.*

/**
 * Activity per la visualizzazione delle statistiche relative agli ordini,
 * inclusa la fascia oraria più affollata, il totale degli ordini, il prodotto
 * più frequentemente ordinato e la media delle spese.
 */
class Statistiche : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var fasciaOrariaTextView: TextView
    private lateinit var totaleOrdiniTextView: TextView
    private lateinit var prodottoFrequenteTextView: TextView
    private lateinit var mediaSpeseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistiche)

        // Inizializza il riferimento al nodo "OrdiniSemplificati" nel tuo database Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("OrdiniSemplificati")

        // Inizializza le TextView
        fasciaOrariaTextView = findViewById(R.id.fasciaoraria)
        totaleOrdiniTextView = findViewById(R.id.totale)
        prodottoFrequenteTextView = findViewById(R.id.cibo) // Usiamo la stessa TextView per il prodotto più frequentemente ordinato
        mediaSpeseTextView = findViewById(R.id.media)

        // Aggiungi un ValueEventListener per recuperare i dati dal database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listaOrdini = mutableListOf<OrdineS>()
                val prodottiConteggio = HashMap<String, Int>()
                var totaleSpese = 0.0

                for (snapshot in dataSnapshot.children) {
                    // Per ogni ordine nel database, deserializza i dati in un oggetto OrdineS
                    val ordine = snapshot.getValue(OrdineS::class.java)

                    // Aggiungi l'ordine alla lista
                    if (ordine != null) {
                        listaOrdini.add(ordine)

                        // Aggiorna il conteggio dei prodotti
                        for (prodotto in ordine.nomiProdotti) {
                            prodottiConteggio[prodotto] = prodottiConteggio.getOrDefault(prodotto, 0) + 1
                        }

                        // Aggiorna il totale delle spese
                        totaleSpese += ordine.prezzo
                    }
                }

                // Calcola la fascia oraria più affollata
                val fasciaOrariaPiùAffollata = calcolaFasciaOrariaPiùAffollata(listaOrdini)

                // Trova il prodotto più frequentemente ordinato
                val prodottoFrequente = prodottiConteggio.maxByOrNull { it.value }?.key ?: ""

                // Calcola la media delle spese
                val mediaSpese = totaleSpese / listaOrdini.size

                // Inserisci i valori nelle TextView
                fasciaOrariaTextView.text = "Orario più affollato: $fasciaOrariaPiùAffollata"
                totaleOrdiniTextView.text = "Totale Ordini: ${listaOrdini.size}"
                prodottoFrequenteTextView.text = "Prodotto più ordinato: $prodottoFrequente"
                mediaSpeseTextView.text = "Spese medie: ${String.format("%.2f", mediaSpese)} €"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gestisci eventuali errori qui
            }
        })
    }

    /**
     * Calcola la fascia oraria più affollata tra gli ordini forniti.
     *
     * @param listaOrdini La lista degli ordini da analizzare.
     * @return La fascia oraria più affollata.
     */
    private fun calcolaFasciaOrariaPiùAffollata(listaOrdini: List<OrdineS>): String {
        val fasciaOrariaConteggio = HashMap<String, Int>()
        for (ordine in listaOrdini) {
            val fasciaOraria = ordine.fascia_oraria
            fasciaOrariaConteggio[fasciaOraria] = fasciaOrariaConteggio.getOrDefault(fasciaOraria, 0) + 1
        }
        var fasciaOrariaPiùAffollata = ""
        var maxConteggio = 0
        for ((fasciaOraria, conteggio) in fasciaOrariaConteggio) {
            if (conteggio > maxConteggio) {
                maxConteggio = conteggio
                fasciaOrariaPiùAffollata = fasciaOraria
            }
        }
        return fasciaOrariaPiùAffollata
    }

    companion object {
        private const val TAG = "StatisticheActivity"
    }
}
