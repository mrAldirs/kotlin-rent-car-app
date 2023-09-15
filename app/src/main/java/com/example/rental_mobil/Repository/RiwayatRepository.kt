package com.example.rental_mobil.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rental_mobil.Model.Riwayat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class RiwayatRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun loadData(): LiveData<List<Riwayat>> {
        val resultLiveData = MutableLiveData<List<Riwayat>>()

        firestore.collection("rental")
            .whereEqualTo("deleted", "")
            .get()
            .addOnSuccessListener { result ->
                val dataList = mutableListOf<Riwayat>()
                for (doc in result) {
                    val data = Riwayat(
                        "", "", "", "", "",
                        doc.get("id_mobil").toString(),
                        doc.get("id_pelanggan").toString(),
                        doc.get("id_rental").toString(),
                        doc.get("id_sopir").toString(), "", "", "", "",
                        doc.get("status_rental").toString(),
                        doc.get("tgl_rental").toString(),
                        doc.get("total").toString(), "", "", ""
                    )
                    dataList.add(data)
                }

                val idMobilList = dataList.map { it.id_mobil }
                if (idMobilList.isNotEmpty()) {
                    val mobilRef = firestore.collection("mobil")
                        .whereIn("id_mobil", idMobilList)
                    mobilRef.get()
                        .addOnSuccessListener { result ->
                            for (doc in result) {
                                val idMobil = doc.get("id_mobil").toString()
                                dataList.forEach { rental ->
                                    if (rental.id_mobil == idMobil) {
                                        rental.nama_mobil = doc.get("nama").toString()
                                    }
                                }
                            }
                            resultLiveData.value = dataList
                        }
                } else {
                    resultLiveData.value = dataList
                }
            }
            .addOnFailureListener { exception ->

            }
        return resultLiveData
    }

    fun loadRiwayat(idP: String): LiveData<List<Riwayat>> {
        val resultLiveData = MutableLiveData<List<Riwayat>>()

        firestore.collection("rental")
            .whereEqualTo("id_pelanggan", idP)
            .whereEqualTo("deleted", "")
            .get()
            .addOnSuccessListener { result ->
                val dataList = mutableListOf<Riwayat>()
                for (doc in result) {
                    val data = Riwayat(
                        "", "", "", "", "",
                        doc.get("id_mobil").toString(),
                        doc.get("id_pelanggan").toString(),
                        doc.get("id_rental").toString(),
                        doc.get("id_sopir").toString(), "", "", "", "",
                        doc.get("status_rental").toString(),
                        doc.get("tgl_rental").toString(),
                        doc.get("total").toString(), "", "", ""
                    )
                    dataList.add(data)
                }

                val idMobilList = dataList.map { it.id_mobil }
                if (idMobilList.isNotEmpty()) {
                    val mobilRef = firestore.collection("mobil")
                        .whereIn("id_mobil", idMobilList)
                    mobilRef.get()
                        .addOnSuccessListener { result ->
                            for (doc in result) {
                                val idMobil = doc.get("id_mobil").toString()
                                dataList.forEach { rental ->
                                    if (rental.id_mobil == idMobil) {
                                        rental.nama_mobil = doc.get("nama").toString()
                                    }
                                }
                            }
                            resultLiveData.value = dataList
                        }
                } else {
                    resultLiveData.value = dataList
                }
            }
            .addOnFailureListener { exception ->

            }
        return resultLiveData
    }

    fun detail(id: String): LiveData<Riwayat> {
        val resultLiveData = MutableLiveData<Riwayat>()

        firestore.collection("rental")
            .document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val data = Riwayat(
                        doc.get("biaya_sopir").toString(),
                        doc.get("bukti_pembayaran").toString(),
                        doc.get("created").toString(),
                        doc.get("deleted").toString(),
                        doc.get("hari_rental").toString(),
                        doc.get("id_mobil").toString(),
                        doc.get("id_pelanggan").toString(),
                        doc.get("id_rental").toString(),
                        doc.get("id_sopir").toString(),
                        doc.get("keterangan").toString(),
                        doc.get("mulai_rental").toString(),
                        doc.get("selesai_rental").toString(),
                        doc.get("status_pembayaran").toString(),
                        doc.get("status_rental").toString(),
                        doc.get("tgl_rental").toString(),
                        doc.get("total").toString(),
                        doc.get("updated").toString(),
                        doc.get("waktu_denda").toString(), ""
                        )
                    resultLiveData.value = data
                } else {

                }
            }
            .addOnFailureListener { exception ->

            }

        return resultLiveData
    }

    fun delete(id: String, idM: String, idS: String): LiveData<Boolean> {
        val resultLiveData = MutableLiveData<Boolean>()

        firestore.collection("rental")
            .document(id)
            .delete()
            .addOnSuccessListener {
                val hm = HashMap<String, Any>()
                hm.set("status", "tersedia")

                firestore.collection("mobil")
                    .document(idM)
                    .update(hm)
                    .addOnSuccessListener {

                        if (idS.isNotEmpty()) {
                            firestore.collection("sopir")
                                .document(idS)
                                .update(hm)
                                .addOnSuccessListener {
                                    resultLiveData.value = true
                                }
                        } else {
                            resultLiveData.value = true
                        }
                    }
            }
            .addOnFailureListener {
                resultLiveData.value = false
            }

        return resultLiveData
    }
}