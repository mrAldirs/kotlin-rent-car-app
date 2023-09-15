package com.example.rental_mobil.ViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rental_mobil.Model.Pelanggan
import com.example.rental_mobil.Repository.ProfilRepository

class ProfilViewModel : ViewModel() {
    private val profilRepository = ProfilRepository()

    fun profil(email: String): LiveData<Pelanggan> {
        return profilRepository.profil(email)
    }

    fun edit(pelanggan: Pelanggan, uri: Uri?) : LiveData<Boolean> {
        return profilRepository.edit(pelanggan, uri)
    }
}