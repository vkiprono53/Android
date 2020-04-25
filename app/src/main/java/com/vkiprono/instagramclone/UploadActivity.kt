package com.vkiprono.instagramclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vkiprono.instagramclone.models.Post
import kotlinx.android.synthetic.main.activity_upload.*
import java.util.*

class UploadActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var firebaseStorage: FirebaseStorage? = null
    var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun selectImg(view: View) {
        Log.d("SELECT IMAGE", "BEGINNING OF SELECT IMAGE*******")

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("INSIDE RESULTS", "BEGINNING ON ACTIVITY RESULT")

        if (requestCode == 2) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                try {
                    imgUri = data.data
                    Log.d("INSIDE RESULTS", "IMAGE URI IS======>$imgUri")
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgUri)
                    imgUpload.setImageBitmap(bitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun upload(view: View) {
        val uuid = UUID.randomUUID().toString()
        val storageRef = firebaseStorage!!.getReference("/images/$uuid")

        storageRef.putFile(imgUri!!).addOnSuccessListener { taskSnapshot ->
            Log.d("UPLOADING TO DB", "Successfully uploaded the image to the db")
            storageRef.downloadUrl.addOnSuccessListener {
                Log.d("Image URI", "Image Uri=====>$it")

                saveToDatabase(it.toString())

                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun saveToDatabase(imgUrl: String) {
        val comment = etUploadComment.text.toString()
        val userEmail = mAuth!!.currentUser!!.email.toString()

        val post = Post(comment, userEmail, imgUrl)

        val uuid = UUID.randomUUID().toString()

        val databaseRef = firebaseDatabase!!.getReference("/posts/$uuid")

        databaseRef.setValue(post).addOnSuccessListener {
            Log.d("SAVING TO DB", "Successfully saved to the database")

        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }
}
