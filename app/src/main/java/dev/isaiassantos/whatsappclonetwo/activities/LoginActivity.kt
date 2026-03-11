package dev.isaiassantos.whatsappclonetwo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.data.local.database.AppDatabase
import dev.isaiassantos.whatsappclonetwo.data.repository.AuthRepository
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration
import dev.isaiassantos.whatsappclonetwo.ui.state.AuthState
import dev.isaiassantos.whatsappclonetwo.ui.viewmodel.LoginViewModel
import dev.isaiassantos.whatsappclonetwo.ui.viewmodel.LoginViewModelFactory
import dev.isaiassantos.whatsappclonetwo.utils.Validations
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var checkNight: CheckBox
 //   private lateinit var checkLight: CheckBox
    private lateinit var checkDefault: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar o tema salvo antes de criar a view
        applySavedTheme()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_layout)
        setupInsets()
        setupToolbar()
        setupViews()
        setupViewModel()
        observeState()
        setupPasswordValidation()
        setupThemeListeners()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val mode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun setupViewModel() {
        val repository = AuthRepository(FirebaseConfiguration.getFirebaseAuth())
        val database = AppDatabase.getDatabase(applicationContext)
        val factory = LoginViewModelFactory(repository, database)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro de autenticação")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isUserLogged()) {
            // goToMainActivity()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.authState.collectLatest { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Opcional: Mostrar ProgressBar
                    }
                    is AuthState.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.message_login_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        goToMainActivity()
                    }
                    is AuthState.Error -> {
                        showError(state.message)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
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
        checkNight = findViewById(R.id.checkNight)
  //      checkLight = findViewById(R.id.checkLight)
        checkDefault = findViewById(R.id.checkDefault)

        val currentMode = AppCompatDelegate.getDefaultNightMode()
        checkNight.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES
       // checkLight.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_NO
        checkDefault.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM || currentMode == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
    }

    private fun setupThemeListeners() {
        checkNight.setOnClickListener {
            updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
   //     checkLight.setOnClickListener {
   //         updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
   //     }
        checkDefault.setOnClickListener {
            updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun updateTheme(mode: Int) {
        // Salvar preferência
        val prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("theme_mode", mode).apply()

        // Atualizar CheckBoxes (comportamento de RadioButton)
        checkNight.isChecked = mode == AppCompatDelegate.MODE_NIGHT_YES
     //   checkLight.isChecked = mode == AppCompatDelegate.MODE_NIGHT_NO
        checkDefault.isChecked = mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        // Aplicar tema (isso recriará a activity)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun setupPasswordValidation() {
        passwordInput.doAfterTextChanged { editable ->
            val password = editable?.toString().orEmpty()
            passwordLayout.error = when {
                password.isNotEmpty() && !isStrongPassword(password) ->
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
            R.id.action_list -> {
                if (viewModel.isUserLogged()) {
                    goToMainActivity()
                } else {
                    Toast.makeText(this, "Faça login primeiro", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_users -> {
                startActivity(Intent(this, UsersAllListActivity::class.java))
                true
            }
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
            .setPositiveButton(R.string.sim) { _, _ ->
                FirebaseConfiguration.getFirebaseAuth().signOut()
                finishAffinity()
            }
            .setNegativeButton(R.string.nao) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun isStrongPassword(password: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")
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

        viewModel.login(email, password)
    }

    fun fun_forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    fun goToRegisterPage(view: View) {
        startActivity(Intent(this, ConfigActivity::class.java))
    }
}