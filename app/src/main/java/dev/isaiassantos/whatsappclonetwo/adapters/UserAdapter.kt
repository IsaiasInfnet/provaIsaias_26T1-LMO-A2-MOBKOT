package dev.isaiassantos.whatsappclonetwo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.isaiassantos.whatsappclonetwo.R
import dev.isaiassantos.whatsappclonetwo.models.UserModel

class UserAdapter(
    private val users: List<UserModel>,
    private val context: Context
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textUserName)
        val textEmail: TextView = itemView.findViewById(R.id.textUserEmail)
        val textPhone: TextView = itemView.findViewById(R.id.textUserPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.textName.text = user.nome
        holder.textEmail.text = user.email
        holder.textPhone.text = user.telefone
    }

    override fun getItemCount(): Int = users.size
}