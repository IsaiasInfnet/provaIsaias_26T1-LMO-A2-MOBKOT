package dev.isaiassantos.whatsappclonetwo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.adapters.ItemProductAdapter
import dev.isaiassantos.whatsappclonetwo.models.ItemProduct
import dev.isaiassantos.whatsappclonetwo.services.FirebaseConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ItemProductAdapter
    private lateinit var itemList: MutableList<ItemProduct>
    private lateinit var progressBar: ProgressBar
    private val REQUEST_CODE_ADD_ITEM = 1
    private val REQUEST_CODE_EDIT_ITEM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)
        
        setupInsets()
        setupViews()
        loadProductsFromFirestore()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        val homeItemList: RecyclerView = findViewById(R.id.homeItemList)
        homeItemList.layoutManager = LinearLayoutManager(this)
        
        itemList = mutableListOf()
        adapter = ItemProductAdapter(itemList, this)
        homeItemList.adapter = adapter

        // Adicionar um ProgressBar no home_layout.xml se necessário, 
        // ou usar um Toast de "Carregando"
        // progressBar = findViewById(R.id.homeProgressBar) 

        findViewById<Button>(R.id.homeReturnButton).setOnClickListener {
            finish()
        }

        findViewById<FloatingActionButton>(R.id.homeAddItemButton).setOnClickListener {
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("isNewItem", true)
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM)
        }
    }

    private fun loadProductsFromFirestore() {
        // Exibir carregamento se tiver ProgressBar
        // progressBar.visibility = View.VISIBLE

        val firestore = FirebaseConfiguration.getFirebaseFirestore()
        
        firestore.collection("produtos")
            .get()
            .addOnSuccessListener { result ->
                itemList.clear()
                for (document in result) {
                    val item = ItemProduct(
                        id = document.id,
                        descricao = document.getString("descr") ?: "",
                        quantidade = document.getLong("quant")?.toInt() ?: 0,
                        preco = document.getDouble("preco") ?: 0.0,
                        precoV = document.getDouble("precoV") ?: 0.0,
                        image = document.getString("image") ?: ""
                    )
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
                // progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // progressBar.visibility = View.GONE
                Toast.makeText(this, "Erro ao carregar produtos: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            // Após salvar no detalhe, recarregamos do Firestore para garantir sincronia real
            loadProductsFromFirestore()
        }
    }
}