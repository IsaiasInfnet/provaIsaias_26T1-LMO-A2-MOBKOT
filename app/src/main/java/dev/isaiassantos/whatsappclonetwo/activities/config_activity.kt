package dev.isaiassantos.whatsappclonetwo.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration

class ConfigActivity : AppCompatActivity() {

    private lateinit var cadEmailInput: TextInputEditText
    private lateinit var cadNameInput: TextInputEditText
    private lateinit var cadPhoneInput: TextInputEditText
    private lateinit var cadPasswordInput: TextInputEditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.config_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cadEmailInput = findViewById(R.id.editTextEmail)
        cadPasswordInput = findViewById(R.id.editTextPassword)
        cadNameInput = findViewById(R.id.editTextUsername)
        cadPhoneInput = findViewById(R.id.editTextPhone)
    }

    fun newuser(view: View) {
        val email = cadEmailInput.text.toString().trim()
        val nome = cadNameInput.text.toString().trim()
        val pass = cadPasswordInput.text.toString().trim()
        val telefone = cadPhoneInput.text.toString().trim()

        if (email.isEmpty() || nome.isEmpty() || pass.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseConfiguration.getFirebaseFirestore()
        
        val userData = hashMapOf(
            "email" to email,
            "nome" to nome,
            "pass" to pass,
            "telefone" to telefone
        )

        firestore.collection("usuarios")
            .add(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuário gravado", Toast.LENGTH_SHORT).show()
                finish() // Opcional: fechar a tela após salvar
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao gravar usuário: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun cancelNew(view: View) {
        finish() // Volta para a tela anterior
    }
}