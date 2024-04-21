package com.ilham.etumarketbyseller.model.chat

data class NotificationData(
    var title:String,
    var message:String,
    val imageUrl: String? = null
)
