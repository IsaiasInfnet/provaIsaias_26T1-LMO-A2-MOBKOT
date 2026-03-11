package dev.isaiassantos.whatsappclonetwo.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration
import dev.isaiassantos.whatsappclonetwo.utils.Validations
import dev.isaiassantos.whatsappclonetwo.data.local.database.AppDatabase
import dev.isaiassantos.whatsappclonetwo.data.local.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login_Activity : AppCompatActivity() {

    // Se não for usar agora, pode remover
    private val firebaseAuth by lazy { FirebaseConfiguration.getFirebaseAuth() }

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_layout)

        setupInsets()
        setupToolbar()
        setupViews()
        setupPasswordValidation()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.loginToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.login)
    }

    private fun setupViews() {
        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        passwordLayout = findViewById(R.id.textLayoutPassword)
    }

    private fun setupPasswordValidation() {
        passwordInput.doAfterTextChanged { editable ->
            val password = editable?.toString().orEmpty()

            passwordLayout.error = when {
                password.isEmpty() -> null
                !isStrongPassword(password) ->
                    "Mínimo 8 caracteres, maiúscula, minúscula, número e símbolo."
                else -> null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                showExitDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.sair)
            .setMessage(R.string.confirmar_saida)
            .setPositiveButton(R.string.sim) { _, _ -> finishAffinity() }
            .setNegativeButton(R.string.nao) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun isStrongPassword(password: String): Boolean {
        val pattern = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}$"
        )
        return pattern.matches(password)
    }

    fun signIn(view: View) {

        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString().orEmpty()

        if (!Validations.validateUserInputs(emailInput, passwordInput)) return

        if (!isStrongPassword(password)) {
            passwordLayout.error = "Senha fraca"
            return
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {

            // Executa Room em thread IO (boa prática)
            withContext(Dispatchers.IO) {
                usuarioDao.inserir(
                    Usuario(
                        nome = "Usuário Login",
                        email = email
                    )
                )
            }

            Toast.makeText(
                this@Login_Activity,
                R.string.message_login_success,
                Toast.LENGTH_LONG
            ).show()

            startActivity(Intent(this@Login_Activity, MainActivity::class.java))
          //  finish()
        }
    }

    fun fun_forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    fun goToRegisterPage(view: View) {
        startActivity(Intent(this, ConfigActivity::class.java))
    }
}