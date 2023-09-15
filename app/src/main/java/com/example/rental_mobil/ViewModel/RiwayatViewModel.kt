package com.example.rental_mobil.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rental_mobil.Model.Riwayat
import com.example.rental_mobil.Repository.RiwayatRepository

class RiwayatViewModel : ViewModel() {
    private val riwayatRepository = RiwayatRepository()

    fun loadData(): LiveData<List<Riwayat>>  {
        return riwayatRepository.loadData()
    }

    fun loadRiwayat(idP: String): LiveData<List<Riwayat>>  {
        return riwayatRepository.loadRiwayat(idP)
    }

    fun detail(id: String): LiveData<Riwayat> {
        return riwayatRepository.detail(id)
    }

    fun delete(id: String, idM: String, idS: String): LiveData<Boolean> {
        return riwayatRepository.delete(id, idM, idS)
    }
}
