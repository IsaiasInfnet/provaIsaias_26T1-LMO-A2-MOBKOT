package dev.isaiassantos.whatsappclonetwo.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration

class ForgotPasswordActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private lateinit var emailForgotPassText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.forgotpass_layout)

        setupInsets()
        initViews()
        checkUserStatus()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        emailForgotPassText = findViewById(R.id.emailforgotPass)
    }

    private fun checkUserStatus() {
        val currentUser = firebaseAuth.currentUser
        
        if (currentUser != null && currentUser.email != null) {
            // Usuário logado: exibe o email
            emailForgotPassText.text = currentUser.email
        } else {
            // Usuário não logado: exibe mensagem de alerta
            emailForgotPassText.text = "execute login antes"
        }
    }

    fun sendPasswordReset(view: View) {
        val email = emailForgotPassText.text.toString()
        
        if (email == "execute login antes" || email.isEmpty()) {
            Toast.makeText(this, "Não é possível redefinir sem um email válido", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Link de redefinição enviado para $email", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun backToLogin(view: View) {
        finish()
    }
}