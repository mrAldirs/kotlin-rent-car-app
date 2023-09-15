package com.example.rental_mobil.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rental_mobil.Model.Pelanggan
import com.google.firebase.firestore.FirebaseFirestore

class PelangganRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun detail(id: String): LiveData<Pelanggan> {
        val resultLiveData = MutableLiveData<Pelanggan>()

        firestore.collection("pelanggan")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val data = Pelanggan(
                        doc.get("id_pelanggan").toString(),
                        doc.get("alamat").toString(),
                        doc.get("deleted").toString(),
                        doc.get("email").toString(),
                        doc.get("foto_ktp").toString(),
                        doc.get("foto_profil").toString(),
                        doc.get("hp").toString(),
                        doc.get("nama").toString(),
                        doc.get("nik").toString(),
                        doc.get("password").toString(),
                        doc.get("ttl").toString(),
                        doc.get("updated").toString()
                    )
                    resultLiveData.value = data
                }
            }
            .addOnFailureListener { exception ->

            }

        return resultLiveData
    }
}