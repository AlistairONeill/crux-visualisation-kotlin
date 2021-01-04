package main.model

import java.util.*

data class TransactionRequestData(
    val type: TransactionType,
    var validTime: Date?,
    val endValidTime: Date?,
    val colour: Int
)