package com.vkiprono.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    var mAuth:FirebaseAuth?=null
    var firebaseDatabase:FirebaseDatabase?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance()
    }

    fun signIn(view: View) {

        val email = etSigninEmail.text.toString()
        val password = etSignInPass.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext, "All Fields are mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(applicationContext, "Successfully logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
            }
        }
            .addOnFailureListener {
                exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }

    }

    fun signup(view: View) {
        val intent = Intent(applicationContext, SignupActivity::class.java)
        startActivity(intent)
    }
}
