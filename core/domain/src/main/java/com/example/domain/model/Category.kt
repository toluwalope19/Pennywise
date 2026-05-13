package com.example.domain.model


data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val isDefault: Boolean = false
)