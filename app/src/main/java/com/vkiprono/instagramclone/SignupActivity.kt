package com.vkiprono.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth?=null
    var firebaseDatabase: FirebaseDatabase?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
    }


    fun signUp(view: View) {
        val username = etSignupName.text.toString()
        val email = etSignupEmail.text.toString()
        val password = etSignupPass.text.toString()

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Account successfully created!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, SigninActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }


    }

    fun signin(view: View) {
        val intent = Intent(applicationContext, SigninActivity::class.java)
        startActivity(intent)
    }
}
