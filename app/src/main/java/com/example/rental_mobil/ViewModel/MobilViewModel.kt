package com.example.rental_mobil.ViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rental_mobil.Model.Mobil
import com.example.rental_mobil.Repository.MobilRepository

class MobilViewModel : ViewModel() {
    private val mobilRepository = MobilRepository()

    fun insert(mobil: Mobil, uri: Uri?): LiveData<Boolean> {
        return mobilRepository.insert(mobil, uri)
    }

    fun loadData(): LiveData<List<Mobil>> {
        return mobilRepository.loadData()
    }

    fun delete(id: String): LiveData<Boolean> {
        return mobilRepository.delete(id)
    }

    fun detail(id: String): LiveData<Mobil> {
        return mobilRepository.detail(id)
    }

    fun edit(mobil: Mobil, uri: Uri?): LiveData<Boolean> {
        return mobilRepository.edit(mobil, uri)
    }
}
