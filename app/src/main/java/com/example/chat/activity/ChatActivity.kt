package com.example.chat.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.R
import com.example.chat.adapter.MessageAdapter
import com.example.chat.databinding.ActivityChatBinding
import com.example.chat.modelClass.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var arr: ArrayList<Message>
    private lateinit var reference: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.txtName.text = name

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        arr = ArrayList()
        adapter = MessageAdapter(this@ChatActivity, arr)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        reference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    arr.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        arr.add(message!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCreate --> get chat : $error")
                }

            })

        binding.btnSend.setOnClickListener {
            val msg = binding.edtMsg.text.toString()
            val msgObject = Message(msg, senderUid)

            if (binding.edtMsg.text.isNotEmpty()) {
                reference.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(msgObject)
                    .addOnSuccessListener {
                        reference.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(msgObject)
                    }
                binding.edtMsg.setText("")
            } else {
                Log.e("TAG", "onCreate: " + getString(R.string.blank_text_not_valid))
            }
        }
    }

}