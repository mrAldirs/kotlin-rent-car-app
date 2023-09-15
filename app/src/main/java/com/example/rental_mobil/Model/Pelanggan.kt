package com.example.rental_mobil.Model

import java.io.Serializable

data class Pelanggan (
    val id_pelanggan: String="",
    val alamat: String="",
    val deleted: String="",
    val email: String="",
    val foto_ktp: String="",
    val foto_profil: String="",
    val hp: String="",
    val nama: String="",
    val nik: String="",
    val password: String="",
    val ttl: String="",
    val updated: String=""
) : Serializable