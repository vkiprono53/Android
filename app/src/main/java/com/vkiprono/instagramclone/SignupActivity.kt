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
import kotlinx.android.synthetic.main.activity_signup.*
import java.lang.Exception
import java.util.*

class SignupActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var imgUri: Uri? = null
    private var firebaseStorage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

    }

    fun signUp(view: View) {
        val username = etSignupName.text.toString()
        val email = etSignupEmail.text.toString()
        val password = etSignupPass.text.toString()

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Account successfully created!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(applicationContext, SigninActivity::class.java)
                    startActivity(intent)

                    //UPLOADING IMAGE && ADDING DATA TO THE DATABASE:

                    uploadImage()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun signin(view: View) {
        val intent = Intent(applicationContext, SigninActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun selectImg(view: View) {
        Log.d("INSIDE SELECT IMAGE", "BEGINNING TO SELECT IMAGE")

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
        if (requestCode == 2) {
            if (data != null && resultCode == Activity.RESULT_OK) {

                try {
                    //   imgSignup.setImageURI(data.data)
                    imgUri = data.data

                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgUri)
                    imgSignup.setImageBitmap(bitmap)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImage() {
        Log.d("UPLOAD IMAGE", "BEGINNING TO UPLOAD IMAGE======>")
        val uuid = UUID.randomUUID().toString()

        if (imgUri == null) return

        val ref = firebaseStorage!!.getReference("/images/$uuid")

        ref.putFile(imgUri!!).addOnSuccessListener { task ->
            Toast.makeText(applicationContext, "Image successfully uploaded", Toast.LENGTH_SHORT)
                .show()

            ref.downloadUrl.addOnSuccessListener {
                Log.d("UPLOAD IMAGE", "SUCCESSFULLY UPLOADED IMAGE WITH URI OF====>${it.toString()}")

                saveDataToFirebaseDatabase(it.toString())

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveDataToFirebaseDatabase(profileImg: String) {

        val username = etSignupName.text.toString()
        val email = etSignupEmail.text.toString()

        val userId = mAuth!!.currentUser!!.uid
        val fireBaseRef = firebaseDatabase!!.getReference("/users/$userId")

        Log.d("USER ID", "ID:=======>$userId")

        val user = User(username, email, profileImg)


        fireBaseRef.setValue(user).addOnSuccessListener {
            Log.d("SAVING TO DB", "SUCCESSFULLY UPLOADED DATA TO THE DATABASE")
        }

    }


}
