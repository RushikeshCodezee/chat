package com.example.chat.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.UserAdapter
import com.example.chat.databinding.ActivityUserListBinding
import com.example.chat.modelClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var arr: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference

        binding.pb.visibility = View.VISIBLE

        arr = ArrayList()
        adapter = UserAdapter(this@UserListActivity, arr)
        binding.rvUserList.layoutManager = LinearLayoutManager(this)
        binding.rvUserList.adapter = adapter

        // fetch data from realtime database
        reference.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.pb.visibility = View.GONE
                arr.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (auth.currentUser!!.uid != currentUser!!.uid) {
                        arr.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        // search filter
        binding.btnSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })

        // alertDialog for conform logout or not
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.are_you_sure_want_to_logout))
            builder.setTitle(getString(R.string.logout))
            builder.setCancelable(false)
            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->

                // for user signOut
                auth.signOut()

                // shared preferences (for check user login or not)
                val sharedPref = getSharedPreferences("user_login", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()
                startActivity(Intent(this@UserListActivity, LoginActivity::class.java))
                finish()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    fun filter(text: String) {
        val filteredlist = ArrayList<User>()
        for (item in arr) {
            if (item.name!!.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Log.e("TAG", "no data")
        } else {
            adapter.filterList(filteredlist)
        }
    }

}