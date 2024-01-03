package com.example.chat.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.R
import com.example.chat.databinding.ActivitySignupBinding
import com.example.chat.modelClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.txtJumpLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            binding.pb.visibility = View.VISIBLE
            signUpUser()
        }

    }

    // user register in firebase
    private fun signUpUser() {
        val name = binding.edtName.editText!!.text.toString()
        val email = binding.edtEmail.editText!!.text.toString()
        val password = binding.edtPassword.editText!!.text.toString()
        if (name.isBlank()) {
            binding.pb.visibility = View.GONE
            Toast.makeText(this, getString(R.string.name_can_t_be_blank), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (email.isBlank()) {
            binding.pb.visibility = View.GONE
            Toast.makeText(this, getString(R.string.email_can_t_be_blank), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (password.isBlank()) {
            binding.pb.visibility = View.GONE
            Toast.makeText(this, getString(R.string.password_can_t_be_blank), Toast.LENGTH_SHORT)
                .show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                binding.pb.visibility = View.GONE
                adduserToDatabase(name, email, auth.currentUser!!.uid)
                Toast.makeText(this, getString(R.string.successfully_singed_up), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }
        }.addOnFailureListener(this) {
            binding.pb.visibility = View.GONE
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    // add user in database with name (for real-time database)
    private fun adduserToDatabase(name: String, email: String, uid: String) {
        reference = FirebaseDatabase.getInstance().reference
        reference.child("user").child(uid).setValue(User(name, email, uid))
    }
}