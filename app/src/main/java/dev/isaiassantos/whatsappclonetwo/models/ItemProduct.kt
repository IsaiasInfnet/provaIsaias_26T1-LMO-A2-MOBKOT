package dev.isaiassantos.whatsappclonetwo.models

data class ItemProduct(
    val id: String = "",
    val descricao: String = "",
    val quantidade: Int = 0,
    val preco: Double = 0.0,
    val precoV: Double = 0.0,
    val image: String = ""
)