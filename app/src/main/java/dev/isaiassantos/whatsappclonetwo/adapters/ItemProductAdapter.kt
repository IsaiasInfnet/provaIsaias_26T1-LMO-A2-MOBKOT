package dev.isaiassantos.whatsappclonetwo.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.activities.ItemDetailActivity
import dev.isaiassantos.whatsappclonetwo.models.ItemProduct

class ItemProductAdapter(
    private val items: MutableList<ItemProduct>,
    private val context: Context
) : RecyclerView.Adapter<ItemProductAdapter.ItemViewHolder>() {

    private val REQUEST_CODE_EDIT_ITEM = 2

    inner class ItemViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val descrItemTitle: TextView = itemView.findViewById(R.id.descrItemTitle)
        private val qtdItemTitle: TextView = itemView.findViewById(R.id.qtdItemTitle)
        private val precoItemTitle: TextView = itemView.findViewById(R.id.precoItemTitle)
        private val homeItemEdit: ImageButton = itemView.findViewById(R.id.homeItemEdit)
        private val homeItemDelete: ImageButton = itemView.findViewById(R.id.homeItemDelete)
        private val layoutFirst: android.view.View = itemView.findViewById(R.id.layoutFirst)

        fun bind(item: ItemProduct, position: Int) {
            descrItemTitle.text = item.descricao
            qtdItemTitle.text = "Qtd: ${item.quantidade}"
            precoItemTitle.text = "R$ ${String.format("%.2f", item.precoV)}" // Mostrando preço de venda na lista

            if (position % 2 == 0) {
                layoutFirst.setBackgroundColor(Color.WHITE)
            } else {
                layoutFirst.setBackgroundColor(Color.parseColor("#A4CCED"))
            }

            layoutFirst.setOnClickListener {
                abrirTelaEdicao(item, adapterPosition)
            }

            homeItemEdit.setOnClickListener {
                abrirTelaEdicao(item, adapterPosition)
            }

            homeItemDelete.setOnClickListener {
                items.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        private fun abrirTelaEdicao(item: ItemProduct, position: Int) {
            val intent = Intent(context, ItemDetailActivity::class.java)
            intent.putExtra("itemId", item.id)
            intent.putExtra("descr", item.descricao)
            intent.putExtra("quant", item.quantidade)
            intent.putExtra("preco", item.preco)
            intent.putExtra("precoV", item.precoV)
            intent.putExtra("image", item.image)
            intent.putExtra("isNewItem", false)
            intent.putExtra("position", position)

            (context as Activity).startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun atualizarItem(position: Int, item: ItemProduct) {
        if (position >= 0 && position < items.size) {
            items[position] = item
            notifyItemChanged(position)
        }
    }
}
