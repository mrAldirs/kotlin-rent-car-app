package com.example.rental_mobil.Model

import java.io.Serializable

data class Mobil(
    val id_mobil: String="",
    val merk: String="",
    val nama: String="",
    val harga: String="",
    val stnk: String="",
    val tahun: String="",
    val plat: String="",
    val kategori: String="",
    val status: String="",
    val keterangan: String="",
    val deleted: String="",
    val created: String="",
    val updated: String="",
    val foto_mobil: String="",
): Serializable
