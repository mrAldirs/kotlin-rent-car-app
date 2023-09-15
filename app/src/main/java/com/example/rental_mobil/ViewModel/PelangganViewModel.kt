package com.example.rental_mobil.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.Repository.PelangganRepository

class PelangganViewModel : ViewModel() {
    private val pelangganRepository = PelangganRepository()

    fun detail(id: String): LiveData<Pelanggan> {
        return pelangganRepository.detail(id)
    }
}