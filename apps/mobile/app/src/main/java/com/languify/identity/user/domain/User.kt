package com.languify.identity.user.domain

data class User(
  val id: String,
  val email: String,
  val firstName: String?,
  val lastName: String?,
  val image: String?,
  val createdAt: String,
)
