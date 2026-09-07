package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
  @Query("SELECT * FROM chats ORDER BY lastMessageTimestamp DESC")
  fun getAllChats(): Flow<List<ChatEntity>>

  @Query("SELECT * FROM chats WHERE ownerAccountId = :ownerId ORDER BY lastMessageTimestamp DESC")
  fun getChatsForAccount(ownerId: String): Flow<List<ChatEntity>>

  @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
  fun getChatById(chatId: String): Flow<ChatEntity?>

  @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
  suspend fun getChatByIdOnce(chatId: String): ChatEntity?

  @Query("SELECT * FROM chats WHERE ownerAccountId = :ownerId AND (contactId = :contactId OR id = :chatId) LIMIT 1")
  suspend fun findChatForAccount(ownerId: String, contactId: String, chatId: String): ChatEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChat(chat: ChatEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChats(chats: List<ChatEntity>)

  @Update
  suspend fun updateChat(chat: ChatEntity)

  @Query("UPDATE chats SET unreadCount = 0")
  suspend fun markAllChatsAsRead()

  @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
  suspend fun markChatAsRead(chatId: String)

  // Messages
  @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
  fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<MessageEntity>)

  // Contacts
  @Query("SELECT * FROM contacts ORDER BY isSelf DESC, name ASC")
  fun getAllContacts(): Flow<List<ContactEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContact(contact: ContactEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContacts(contacts: List<ContactEntity>)

  @Query("SELECT COUNT(*) FROM contacts")
  suspend fun getContactsCount(): Int

  @Query("SELECT COUNT(*) FROM chats")
  suspend fun getChatsCount(): Int

  // User Accounts (Authentication & Switching)
  @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
  fun getAllAccounts(): Flow<List<UserAccountEntity>>

  @Query("SELECT * FROM user_accounts WHERE isActive = 1 LIMIT 1")
  fun getActiveAccount(): Flow<UserAccountEntity?>

  @Query("SELECT * FROM user_accounts WHERE isActive = 1 LIMIT 1")
  suspend fun getActiveAccountOnce(): UserAccountEntity?

  @Query("SELECT * FROM user_accounts WHERE LOWER(username) = LOWER(:identifier) OR phone = :identifier OR LOWER(email) = LOWER(:identifier) LIMIT 1")
  suspend fun findAccount(identifier: String): UserAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccount(account: UserAccountEntity)

  @Query("UPDATE user_accounts SET isActive = 0")
  suspend fun deactivateAllAccounts()

  @Query("UPDATE user_accounts SET isActive = 1 WHERE id = :id")
  suspend fun activateAccount(id: String)

  @Query("DELETE FROM user_accounts WHERE id = :id")
  suspend fun deleteAccount(id: String)

  @Query("SELECT * FROM user_accounts WHERE id != :currentUserId ORDER BY fullName ASC")
  fun getOtherRegisteredAccounts(currentUserId: String): Flow<List<UserAccountEntity>>

  @Query("SELECT * FROM user_accounts WHERE id != :currentUserId ORDER BY fullName ASC")
  suspend fun getOtherRegisteredAccountsOnce(currentUserId: String): List<UserAccountEntity>

  @Query("SELECT COUNT(*) FROM user_accounts")
  suspend fun getAccountsCount(): Int
}
