package com.example.chat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.R
import com.example.chat.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.txtJumpSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            binding.pb.visibility = View.VISIBLE
            login()
        }

    }

    // login (verify user)
    private fun login() {
        val email = binding.edtEmail.editText!!.text.toString()
        val password = binding.edtPassword.editText!!.text.toString()

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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                binding.pb.visibility = View.GONE

                // shared preferences (for check user login or not)
                val sharedPref = getSharedPreferences("user_login", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                Toast.makeText(this, getString(R.string.successfully_loggedin), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, UserListActivity::class.java))
                finish()
            }
        }.addOnFailureListener(this) {
            binding.pb.visibility = View.GONE
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}