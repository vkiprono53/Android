package com.vkiprono.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.vkiprono.instagramclone.models.Post
import com.vkiprono.instagramclone.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.post_layout.view.*

class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var mAuthStateListener:FirebaseAuth.AuthStateListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        mAuthStateListener = FirebaseAuth.AuthStateListener {  }


        checkIfLoggedin()

        getPosts()
    }

    private fun getPosts() {
        Log.d("HOME ACTIVITY", "BEGINNING TO RETRIEVE POSTS")

        val firebaseRef = firebaseDatabase!!.getReference("/posts")

        val adapter = GroupAdapter<GroupieViewHolder>()

        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val posts = it.getValue(Post::class.java)
                    adapter.add(PostItem(posts!!))
                    Log.d("GETTING POSTS","data from db ${it.toString()}")
                }
                recyclerPosts.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    private fun checkIfLoggedin() {
        val uid = mAuth!!.uid
        if (uid == null) {
            val intent = Intent(applicationContext, SigninActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(applicationContext, SigninActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        if (item.itemId == R.id.addPost) {
            val intent = Intent(applicationContext, UploadActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

}

class PostItem(val posts:Post):Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Log.d("RETRIEVE FROM DB=======","POSTS FROM THE DB")

        Picasso.get().load(posts.imgUrl).into(viewHolder.itemView.imgPosts)
        viewHolder.itemView.tvPostsEmail.text = posts.userEmail
        viewHolder.itemView.tvcommentPosts.text = posts.comment
        Log.d("EMAILS", "EMAIL IS ***********---->${posts.userEmail}")
    }
    override fun getLayout(): Int {
        return R.layout.post_layout
    }
}