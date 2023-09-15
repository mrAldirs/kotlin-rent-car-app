package com.example.rental_mobil.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rental_mobil.Model.Sopir
import com.example.rental_mobil.Repository.SopirRepository

class SopirViewModel : ViewModel() {
    private val sopirRepository = SopirRepository()

    fun detail(id: String): LiveData<Sopir> {
        return sopirRepository.detail(id)
    }
}