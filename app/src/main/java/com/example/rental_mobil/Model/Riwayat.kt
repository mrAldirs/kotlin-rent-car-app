package com.example.rental_mobil.Model

import java.io.Serializable

data class Riwayat (
    val biaya_sopir : String="",
    val bukti_pembayaran : String="",
    val created : String="",
    val deleted : String="",
    val hari_rental : String="",
    val id_mobil : String="",
    val id_pelanggan : String="",
    val id_rental : String="",
    val id_sopir : String="",
    val keterangan : String="",
    val mulai_rental : String="",
    val selesai_rental : String="",
    val status_pembayaran : String="",
    val status_rental : String="",
    val tgl_rental : String="",
    val total : String="",
    val updated : String="",
    val denda : String="",
    val waktu_denda : String="",
    var nama_mobil : String=""
): Serializable