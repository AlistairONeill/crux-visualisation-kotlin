package main.model

import java.util.*

data class TransactionData(
    val type: TransactionType,
    val transactionTime: Date,
    val validTime: Date?,
    val endValidTime: Date?,
    val colour: Int?)
