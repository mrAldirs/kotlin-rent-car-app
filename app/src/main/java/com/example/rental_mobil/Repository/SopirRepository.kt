package com.example.rental_mobil.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rental_mobil.Model.Sopir
import com.google.firebase.firestore.FirebaseFirestore

class SopirRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun detail(id: String): LiveData<Sopir> {
        val resultLiveData = MutableLiveData<Sopir>()

        firestore.collection("sopir")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val data = Sopir(
                        doc.get("alamat").toString(),
                        doc.get("created").toString(),
                        doc.get("deleted").toString(),
                        doc.get("email").toString(),
                        doc.get("foto_ktp").toString(),
                        doc.get("foto_profil").toString(),
                        doc.get("foto_sim").toString(),
                        doc.get("hp").toString(),
                        doc.get("id_sopir").toString(),
                        doc.get("nama").toString(),
                        doc.get("nik").toString(),
                        doc.get("sim").toString(),
                        doc.get("status").toString(),
                        doc.get("tanggal_lahir").toString(),
                        doc.get("tempat_lahir").toString(),
                        doc.get("updated").toString()
                    )
                    resultLiveData.value = data
                } else {

                }
            }
            .addOnFailureListener { exception ->

            }

        return resultLiveData
    }
}