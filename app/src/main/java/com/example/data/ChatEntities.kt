package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
  @PrimaryKey val id: String,
  val name: String,
  val username: String,
  val phone: String,
  val status: String,
  val avatarDrawableName: String? = null,
  val avatarColorHex: Long = 0xFF2A3942,
  val isSelf: Boolean = false,
  val ownerAccountId: String = ""
)

@Entity(tableName = "chats")
data class ChatEntity(
  @PrimaryKey val id: String,
  val ownerAccountId: String = "",
  val contactId: String,
  val title: String,
  val subtitle: String,
  val lastMessage: String,
  val lastMessageTimestamp: Long,
  val lastMessageFormattedTime: String,
  val unreadCount: Int = 0,
  val avatarDrawableName: String? = null,
  val avatarColorHex: Long = 0xFF2A3942,
  val isOutgoing: Boolean = false,
  val status: String = "READ", // SENT, DELIVERED, READ
  val isPinned: Boolean = false,
  val isOnline: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey val id: String,
  val chatId: String,
  val text: String,
  val timestamp: Long,
  val formattedTime: String,
  val isOutgoing: Boolean,
  val status: String = "READ", // SENT, DELIVERED, READ
  val messageType: String = "TEXT", // "TEXT", "IMAGE", "CONTACT"
  val imageUri: String? = null,
  val sharedContactName: String? = null,
  val sharedContactNumber: String? = null
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
  @PrimaryKey val id: String,
  val fullName: String,
  val username: String,
  val phone: String,
  val email: String = "",
  val passwordHash: String,
  val status: String = "Hey there! I am using FAMILY.",
  val avatarDrawableName: String? = "avatar_gamer",
  val avatarColorHex: Long = 0xFFFF7043,
  val isActive: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
