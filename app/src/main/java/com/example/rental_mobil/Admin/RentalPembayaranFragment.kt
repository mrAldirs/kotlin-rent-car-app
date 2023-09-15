package com.example.rental_mobil.Admin

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.rental_mobil.R
import com.example.rental_mobil.databinding.FragmentRentalAdminPembayaranBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap

class RentalPembayaranFragment : DialogFragment() {
    private lateinit var b: FragmentRentalAdminPembayaranBinding
    lateinit var v: View
    lateinit var parent: RentalDetailActivity

    val RC_OK = 100
    lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentRentalAdminPembayaranBinding.inflate(layoutInflater)
        v = b.root
        parent = activity as RentalDetailActivity

        b.btnChoose.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.setType("image/*")
            startActivityForResult(intent, RC_OK)
        }

        uri = Uri.EMPTY

        b.btnKirim.setOnClickListener {
            if (uri != Uri.EMPTY) {
                val fileName = "IMG" + SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date()) + "new"
                val fileRef = FirebaseStorage.getInstance().reference.child(fileName + ".jpg")
                val uploadTask = fileRef.putFile(uri)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileRef.downloadUrl
                }.addOnCompleteListener {
                    val hm = HashMap<String, Any>()
                    hm.set("status_pembayaran", "Sudah Bayar")
                    hm.put("bukti_pembayaran", it.result.toString())
                    hm.set("updated", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

                    FirebaseFirestore.getInstance().collection("rental")
                        .document(arguments?.get("id").toString())
                        .update(hm)
                        .addOnSuccessListener {
                            Toast.makeText(v.context, "Berhasil mengonfirmasi pembayaran rental mobil ini!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(v.context, RentalActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            requireActivity().recreate()
                            parent.overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
                        }
                }
            } else {
                AlertDialog.Builder(v.context)
                    .setTitle("Informasi!")
                    .setIcon(R.drawable.warning)
                    .setMessage("Anda harus memasukkan bukti terlebih dahulu?")
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        null
                    })
                    .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()
            }
        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK) && (requestCode == RC_OK)) {
            if (data != null){
                uri = data.data!!
                Picasso.get().load(uri.toString()).into(b.insFoto)
            }
        }
    }
}