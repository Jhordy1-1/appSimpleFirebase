package com.example.simpleapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa Firebase Database
        database = FirebaseDatabase.getInstance().reference

        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)
        messagesTextView = findViewById(R.id.messagesTextView)

        // Configura el botón para enviar mensajes
        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessageToFirebase(message)
                inputMessage.text.clear()  // Limpia el campo de entrada
            }
        }

        // Escucha los cambios en la base de datos para mostrar los mensajes
        displayMessagesFromFirebase()
    }

    private fun sendMessageToFirebase(message: String) {
        // Guarda el mensaje en Firebase con una clave única
        val messageId = database.child("messages").push().key
        if (messageId != null) {
            database.child("messages").child(messageId).setValue(message)
        }
    }

    private fun displayMessagesFromFirebase() {
        // Escucha los cambios en la base de datos para mostrar los mensajes en tiempo real
        database.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = StringBuilder()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(String::class.java)
                    if (message != null) {
                        messages.append(message).append("\n")
                    }
                }
                messagesTextView.text = messages.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                messagesTextView.text = "Error al cargar mensajes"
            }
        })
    }
}