package dev.isaiassantos.whatsappclonetwo.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var descrInput: TextInputEditText
    private lateinit var qtdInput: TextInputEditText
    private lateinit var precoInput: TextInputEditText
    private lateinit var precoVInput: TextInputEditText
    private lateinit var productImageView: ImageView
    private lateinit var btnLoadImage: Button
    
    private var itemId: String = ""
    private var isNewItem: Boolean = false
    private var position: Int = -1
    private var selectedDrawableResId: Int = R.drawable.sementes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.item_detail_layout)

        setupInsets()
        initViews()
        loadIntentData()
        setupButtons()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        descrInput = findViewById(R.id.itemDescrInput)
        qtdInput = findViewById(R.id.itemQtdInput)
        precoInput = findViewById(R.id.itemPrecoInput)
        precoVInput = findViewById(R.id.itemPrecoVInput)
        productImageView = findViewById(R.id.productImageView)
        btnLoadImage = findViewById(R.id.btnLoadImage)
    }

    private fun loadIntentData() {
        itemId = intent.getStringExtra("itemId") ?: ""
        val descricao = intent.getStringExtra("descr") ?: ""
        val quantidade = intent.getIntExtra("quant", 0)
        val preco = intent.getDoubleExtra("preco", 0.0)
        val precoV = intent.getDoubleExtra("precoV", 0.0)
        isNewItem = intent.getBooleanExtra("isNewItem", false)
        position = intent.getIntExtra("position", -1)
        val imageResName = intent.getStringExtra("image")

        if (!isNewItem) {
            descrInput.setText(descricao)
            qtdInput.setText(quantidade.toString())
            precoInput.setText(String.format("%.2f", preco))
            precoVInput.setText(String.format("%.2f", precoV))
            
            if (!imageResName.isNullOrEmpty()) {
                selectedDrawableResId = resources.getIdentifier(imageResName, "drawable", packageName)
                if (selectedDrawableResId == 0) selectedDrawableResId = R.drawable.sementes
            }
            productImageView.setImageResource(selectedDrawableResId)
        }
    }

    private fun setupButtons() {
        btnLoadImage.setOnClickListener {
            showDrawablePicker()
        }

        findViewById<Button>(R.id.itemDetailBackButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.itemDetailSaveButton).setOnClickListener {
            saveProduct()
        }
    }

    private fun showDrawablePicker() {
        val drawables = listOf(
            Pair("Sementes", R.drawable.sementes),
            Pair("Cesta Compras", R.drawable.cesta_compras),
            Pair("Logo Compras", R.drawable.logocompras),
            Pair("Logo Compras 3", R.drawable.logocompras3)
        )

        val items = drawables.map { it.first }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecione uma Imagem do Produto")
            .setItems(items) { _, which ->
                selectedDrawableResId = drawables[which].second
                productImageView.setImageResource(selectedDrawableResId)
            }
            .show()
    }

    private fun saveProduct() {
        val descr = descrInput.text.toString().trim()
        val qtd = qtdInput.text.toString().trim()
        val precoText = precoInput.text.toString().trim()
        val precoVText = precoVInput.text.toString().trim()

        if (descr.isEmpty() || qtd.isEmpty() || precoText.isEmpty() || precoVText.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val preco = precoText.replace(",", ".").toDouble()
        val precoV = precoVText.replace(",", ".").toDouble()
        val quant = qtd.toInt()

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage("aguarde...")
        val progressDialog = builder.create()
        progressDialog.show()

        val firestore = FirebaseConfiguration.getFirebaseFirestore()
        
        val productData = hashMapOf(
            "descr" to descr,
            "quant" to quant,
            "preco" to preco,
            "precoV" to precoV,
            "image" to resources.getResourceEntryName(selectedDrawableResId),
            "status" to true
        )

        val task = if (isNewItem) {
            firestore.collection("produtos").add(productData)
        } else {
            firestore.collection("produtos").document(itemId).set(productData)
        }

        task.addOnSuccessListener { documentReference ->
            val finalId = if (isNewItem) (documentReference as com.google.firebase.firestore.DocumentReference).id else itemId
            
            progressDialog.setMessage("Registro Atualizado")
            
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isFinishing) {
                    progressDialog.dismiss()
                    
                    val resultIntent = Intent()
                    resultIntent.putExtra("itemId", finalId)
                    resultIntent.putExtra("descr", descr)
                    resultIntent.putExtra("quant", quant)
                    resultIntent.putExtra("preco", preco)
                    resultIntent.putExtra("precoV", precoV)
                    resultIntent.putExtra("image", resources.getResourceEntryName(selectedDrawableResId))
                    resultIntent.putExtra("isNewItem", isNewItem)
                    resultIntent.putExtra("position", position)

                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }, 300)
        }.addOnFailureListener { e ->
            if (!isFinishing) progressDialog.dismiss()
            Toast.makeText(this, "Erro ao salvar no Firestore: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}