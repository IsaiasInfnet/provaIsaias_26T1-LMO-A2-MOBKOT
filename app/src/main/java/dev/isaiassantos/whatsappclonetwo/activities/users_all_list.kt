package dev.isaiassantos.whatsappclonetwo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.adapters.UserAdapter
import dev.isaiassantos.whatsappclonetwo.models.UserModel
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration

class UsersAllListActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private lateinit var userList: MutableList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.layout_users)

        setupInsets()
        setupViews()
        loadUsersFromFirestore()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        val usersItemList: RecyclerView = findViewById(R.id.usersItemList)
        usersItemList.layoutManager = LinearLayoutManager(this)

        userList = mutableListOf()
        adapter = UserAdapter(userList, this)
        usersItemList.adapter = adapter

        findViewById<Button>(R.id.homeReturnButton).setOnClickListener {
            finish()
        }
    }

    private fun loadUsersFromFirestore() {
        val firestore = FirebaseConfiguration.getFirebaseFirestore()

        // Ajustado para a coleção "usuarios" (conforme config_activity.kt)
        firestore.collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    val user = UserModel(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        email = document.getString("email") ?: "",
                        pass = document.getString("pass") ?: "",
                        telefone = document.getString("telefone") ?: ""
                    )
                    userList.add(user)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar usuários: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}