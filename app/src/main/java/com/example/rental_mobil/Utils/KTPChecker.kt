package com.example.rental_mobil.Utils

import android.annotation.SuppressLint
import android.util.Log

class KTPChecker {
    companion object{
        fun checkNik(text: List<String>): String {
            var nik = ""
            for (i in text.indices) {
                if (text[i].contains("NIK")) {
                    nik = text[i + 1]
                    nik = nik.toLowerCase()
                    nik = nik.replace(":","")
                    nik = nik.replace("b","6")
                    nik = nik.replace("s","9")
                    nik = nik.replace("o","0")
                    nik = nik.replace("i","1")
                    nik = nik.replace("l","1")
                    nik = nik.replace("]","1")
                    nik = nik.replace("e","2")
                    break
                }
            }
            return nik
        }
        @SuppressLint("SuspiciousIndentation")
        fun checkNama(text: List<String>): String {
            var nama = ""
            var tempNama:MutableList<String> = ArrayList()
            var indexFirst = 0
            var indexLast = 0
            val regex = Regex(pattern = "[A-Za-z0-9]+-[A-Za-z0-9]+-[A-Za-z0-9]+", options = setOf(RegexOption.IGNORE_CASE))
            for (i in text.indices){

//                first name
                if(regex.matches(text[i])) {
                    indexFirst = i
                        if (text[indexFirst + 1].contains("Gol", ignoreCase = false)) {
                            indexFirst += 1
                            if (text[indexFirst + 1].contains("Darah", ignoreCase = true)) {
                                indexFirst += 1
                            }
                            if (text[indexFirst + 1].contains("Nama", ignoreCase = true)) {
                                indexFirst += 1
                            }
                        }
                }else if(text[i].contains("Gol", ignoreCase = true) && indexFirst ==0){
                    indexFirst = i
                    if(text[indexFirst+1].contains("Darah", ignoreCase = true)) {
                        indexFirst += 1
                    }
                }else if(text[i].contains("Darah", ignoreCase = true) && indexFirst ==0) {
                    indexFirst = i
                    if(text[indexFirst+1].contains("Nama", ignoreCase = true)) {
                        indexFirst += 1
                    }
                }else if(text[i].contains("Nama", ignoreCase = true) && indexFirst ==0) {
                    indexFirst = i
                }
//                last name
                if(indexFirst != 0 && (text[i].contains("Gol", ignoreCase = true) || text[i].contains("NIK", ignoreCase = true))) {
                    if(indexFirst < i) {
                        indexLast = i
                        break
                    }
                }

            }
            Log.d("result-ocr-indexFirst", indexFirst.toString())
            Log.d("result-ocr-indexLast", indexLast.toString())
            indexFirst += 1
            indexLast -= 1
            for (i in indexFirst .. indexLast) {
                tempNama.add(text[i])
            }

            if (tempNama.size > 0) {
                nama = tempNama.joinToString(" ")
            }
            return nama
        }
        fun checkTanggalLahir(text: List<String>): String {
            var tanggalLahir = ""
            var tempTanggalLahir:MutableList<String> = ArrayList()
             val regex = Regex(pattern = "[A-Za-z0-9]+-[A-Za-z0-9]+-[A-Za-z0-9]+", options = setOf(RegexOption.IGNORE_CASE))
            for (i in text.indices) {
                if (regex.matches(text[i])) {
                    tempTanggalLahir.add(text[i])
                }
            }
            if (tempTanggalLahir.size > 0) {
                tanggalLahir = tempTanggalLahir.last()
            }
            tanggalLahir = tanggalLahir.toLowerCase()
            tanggalLahir = tanggalLahir.replace(":","")
            tanggalLahir = tanggalLahir.replace("b","6")
            tanggalLahir = tanggalLahir.replace("s","9")
            tanggalLahir = tanggalLahir.replace("o","0")
            tanggalLahir = tanggalLahir.replace("i","1")
            tanggalLahir = tanggalLahir.replace("l","1")
            tanggalLahir = tanggalLahir.replace("]","1")
            tanggalLahir = tanggalLahir.replace("e","2")
            return tanggalLahir
        }
        fun checkJenisKelamin(text: List<String>): String {
            var jenisKelamin = ""
            for (result in text) {
                if (result == "LAK-LAKI" ||
                    result == "LAKHAKI" ||
                    result == "TLAKHAKI" ||
                    result == "LAK-LAK" ||
                    result == "LAKI-LAK" ||
                    result == "AK-LAK" ||
                    result == "LAKFLAKI" ||
                    result == "LAKHLAK" ||
                    result == "LAKFEAKI" ||
                    result == "LAKELAKI" ||
                    result == "LAKELAK" ||
                    result == "LAKHLAKI" ||
                    result == "LAKHEAK" ||
                    result == "LAK-LAKI" ||
                    result == "LAKHEAKI" ||
                    result == "LAKIFEAK" ||
                    result == "LAKFEAKE" ||
                    result == "LAKIFEAKI" ||
                    result == "LAKFEAR" ||
                    result == "LAKFLAK" ||
                    result == "LAK-LAKE" ||
                    result == "LAK-EAK" ||
                    result == "LAKFEAK" ||
                    result == "LAK-EAKI" ||
                    result == "LAKELAKE") {
                    jenisKelamin = "Laki-Laki"
                    break
                }
            }
            if(jenisKelamin == ""){
                jenisKelamin = "Perempuan"
            }
            return jenisKelamin
        }
        fun checkAgama(text: List<String>): String {
            var agama = ""
            for (result in text) {
                if (result == "ISLAM" ||result == "SLAM" ||
                    result == "AM" ||
                    result == "SLA AM" ||
                    result == "ISLU AM" ||
                    result == "SL LAM" ||
                    result == "ISLAME" ||
                    result == "SLA M" ||
                    result == "ISL AM" ||
                    result == "ISLA AM" ||
                    result == "S AM" ||
                    result == "SLL AM" ||
                    result == "SL AM" ||
                    result == "SE AM" ||
                    result == "1SLAM" ||
                    result == "ISLAMM" ||
                    result == "SLA" ||
                    result == "LAM") {
                    agama = "Islam"
                    break
                }
            }
            return agama
        }
    }
}