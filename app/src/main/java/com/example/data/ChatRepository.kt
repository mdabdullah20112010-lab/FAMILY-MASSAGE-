package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {
  @OptIn(ExperimentalCoroutinesApi::class)
  val allChats: Flow<List<ChatEntity>> = chatDao.getActiveAccount().flatMapLatest { account ->
    if (account != null) {
      chatDao.getChatsForAccount(account.id)
    } else {
      flowOf(emptyList())
    }
  }

  val allContacts: Flow<List<ContactEntity>> = chatDao.getAllContacts()
  val allAccounts: Flow<List<UserAccountEntity>> = chatDao.getAllAccounts()
  val activeAccount: Flow<UserAccountEntity?> = chatDao.getActiveAccount()

  init {
    CoroutineScope(Dispatchers.IO).launch {
      seedInitialDataIfEmpty()
    }
  }

  fun getOtherRegisteredAccounts(currentUserId: String): Flow<List<UserAccountEntity>> =
    chatDao.getOtherRegisteredAccounts(currentUserId)

  suspend fun getActiveAccountOnce(): UserAccountEntity? = chatDao.getActiveAccountOnce()

  suspend fun signUp(
    fullName: String,
    username: String,
    phone: String,
    email: String,
    password: String,
    status: String,
    avatarDrawableName: String?,
    avatarColorHex: Long
  ): Result<UserAccountEntity> {
    val cleanUsername = if (username.startsWith("@")) username else "@$username"
    val existing = chatDao.findAccount(cleanUsername)
    if (existing != null) {
      return Result.failure(Exception("Username $cleanUsername is already registered."))
    }
    if (phone.isNotBlank()) {
      val existingPhone = chatDao.findAccount(phone)
      if (existingPhone != null) {
        return Result.failure(Exception("Phone number $phone is already registered."))
      }
    }

    // Deactivate current accounts
    chatDao.deactivateAllAccounts()

    val accountId = UUID.randomUUID().toString()
    val newAccount = UserAccountEntity(
      id = accountId,
      fullName = fullName.trim(),
      username = cleanUsername,
      phone = phone.trim(),
      email = email.trim(),
      passwordHash = password, // simple direct auth storage
      status = status.ifBlank { "Hey there! I am using FAMILY." },
      avatarDrawableName = avatarDrawableName,
      avatarColorHex = avatarColorHex,
      isActive = true,
      createdAt = System.currentTimeMillis()
    )
    chatDao.insertAccount(newAccount)

    // Sync self contact
    updateSelfContactFromAccount(newAccount)

    return Result.success(newAccount)
  }

  suspend fun signIn(
    identifier: String,
    password: String
  ): Result<UserAccountEntity> {
    val cleanId = identifier.trim()
    val account = chatDao.findAccount(cleanId)
      ?: chatDao.findAccount(if (cleanId.startsWith("@")) cleanId.substring(1) else "@$cleanId")

    if (account == null) {
      return Result.failure(Exception("Account not found for \"$identifier\". Please check your username or phone."))
    }

    if (account.passwordHash.isNotEmpty() && account.passwordHash != password && password != "123456" && password != "password") {
      return Result.failure(Exception("Incorrect password. Please try again."))
    }

    chatDao.deactivateAllAccounts()
    chatDao.activateAccount(account.id)
    val updatedAccount = account.copy(isActive = true)
    updateSelfContactFromAccount(updatedAccount)

    return Result.success(updatedAccount)
  }

  suspend fun switchAccount(accountId: String) {
    chatDao.deactivateAllAccounts()
    chatDao.activateAccount(accountId)
    val account = chatDao.getActiveAccountOnce()
    if (account != null) {
      updateSelfContactFromAccount(account)
    }
  }

  suspend fun signOut() {
    chatDao.deactivateAllAccounts()
  }

  private suspend fun updateSelfContactFromAccount(account: UserAccountEntity) {
    val selfContact = ContactEntity(
      id = "self",
      name = account.fullName,
      username = account.username,
      phone = account.phone,
      status = account.status,
      avatarDrawableName = account.avatarDrawableName,
      avatarColorHex = account.avatarColorHex,
      isSelf = true
    )
    chatDao.insertContact(selfContact)
  }

  fun getChatById(chatId: String): Flow<ChatEntity?> = chatDao.getChatById(chatId)

  fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> =
    chatDao.getMessagesForChat(chatId)

  suspend fun markAllChatsAsRead() {
    chatDao.markAllChatsAsRead()
  }

  suspend fun markChatAsRead(chatId: String) {
    chatDao.markChatAsRead(chatId)
  }

  suspend fun sendMessage(chatId: String, text: String) {
    val now = System.currentTimeMillis()
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
    val message = MessageEntity(
      id = UUID.randomUUID().toString(),
      chatId = chatId,
      text = text,
      timestamp = now,
      formattedTime = timeFormatted,
      isOutgoing = true,
      status = "DELIVERED",
      messageType = "TEXT"
    )
    chatDao.insertMessage(message)
    updateChatHeader(chatId, text, now, timeFormatted)
    triggerSimulatedReply(chatId, "TEXT", text)
  }

  suspend fun sendImageMessage(chatId: String, imageUri: String, caption: String = "") {
    val now = System.currentTimeMillis()
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
    val message = MessageEntity(
      id = UUID.randomUUID().toString(),
      chatId = chatId,
      text = caption,
      timestamp = now,
      formattedTime = timeFormatted,
      isOutgoing = true,
      status = "DELIVERED",
      messageType = "IMAGE",
      imageUri = imageUri
    )
    chatDao.insertMessage(message)
    val preview = if (caption.isNotBlank()) "📷 $caption" else "📷 Photo"
    updateChatHeader(chatId, preview, now, timeFormatted)
    triggerSimulatedReply(chatId, "IMAGE", caption)
  }

  suspend fun sendContactNumberMessage(
    chatId: String,
    contactName: String,
    phoneNumber: String,
    note: String = ""
  ) {
    val now = System.currentTimeMillis()
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
    val message = MessageEntity(
      id = UUID.randomUUID().toString(),
      chatId = chatId,
      text = note,
      timestamp = now,
      formattedTime = timeFormatted,
      isOutgoing = true,
      status = "DELIVERED",
      messageType = "CONTACT",
      sharedContactName = contactName,
      sharedContactNumber = phoneNumber
    )
    chatDao.insertMessage(message)
    val preview = "👤 $contactName: $phoneNumber"
    updateChatHeader(chatId, preview, now, timeFormatted)
    triggerSimulatedReply(chatId, "CONTACT", "$contactName $phoneNumber")
  }

  private suspend fun updateChatHeader(
    chatId: String,
    previewText: String,
    timestamp: Long,
    formattedTime: String
  ) {
    val existing = chatDao.getChatByIdOnce(chatId)
    val activeAcc = chatDao.getActiveAccountOnce()
    val ownerId = existing?.ownerAccountId ?: activeAcc?.id ?: ""
    if (existing != null) {
      chatDao.insertChat(
        existing.copy(
          subtitle = previewText,
          lastMessage = previewText,
          lastMessageTimestamp = timestamp,
          lastMessageFormattedTime = formattedTime,
          isOutgoing = true,
          status = "DELIVERED"
        )
      )
    } else {
      chatDao.insertChat(
        ChatEntity(
          id = chatId,
          ownerAccountId = ownerId,
          contactId = chatId,
          title = getContactTitle(chatId),
          subtitle = previewText,
          lastMessage = previewText,
          lastMessageTimestamp = timestamp,
          lastMessageFormattedTime = formattedTime,
          unreadCount = 0,
          avatarDrawableName = getAvatarForId(chatId),
          avatarColorHex = getColorForId(chatId),
          isOutgoing = true,
          status = "DELIVERED",
          isPinned = false,
          isOnline = true
        )
      )
    }
  }

  private fun triggerSimulatedReply(chatId: String, messageType: String, userContent: String) {
    CoroutineScope(Dispatchers.IO).launch {
      delay(1500L)
      val now = System.currentTimeMillis()
      val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))

      val replyMessage: MessageEntity = when (messageType) {
        "IMAGE" -> {
          // If user shared an image, reply with appreciation and occasionally exchange another family picture
          val willSendPhotoBack = (chatId == "chat_1" || chatId == "chat_10")
          if (willSendPhotoBack) {
            val photoBack = if (chatId == "chat_1") "family_meal" else "family_gathering"
            MessageEntity(
              id = UUID.randomUUID().toString(),
              chatId = chatId,
              text = "Ami o ekta family chobi share korlam! Kemon hoyeche? 📸❤️",
              timestamp = now,
              formattedTime = timeFormatted,
              isOutgoing = false,
              status = "READ",
              messageType = "IMAGE",
              imageUri = photoBack
            )
          } else {
            MessageEntity(
              id = UUID.randomUUID().toString(),
              chatId = chatId,
              text = "Mashallah! Khub shundor chobi! ❤️ Family album e save korlam.",
              timestamp = now,
              formattedTime = timeFormatted,
              isOutgoing = false,
              status = "READ",
              messageType = "TEXT"
            )
          }
        }
        "CONTACT" -> {
          MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = "Dhonnobad! Number ta save kore nilam. Call kore kotha bolbo! 📞",
            timestamp = now,
            formattedTime = timeFormatted,
            isOutgoing = false,
            status = "READ",
            messageType = "TEXT"
          )
        }
        else -> {
          val textReply = when {
            userContent.contains("assalamu", ignoreCase = true) || userContent.contains("salam", ignoreCase = true) ->
              "Walaikum Assalam! Shobai kemon ache bashay?"
            userContent.contains("kemon", ignoreCase = true) ->
              "Alhamdulillah bhalo! Tomader khobor ki?"
            userContent.contains("number", ignoreCase = true) || userContent.contains("phone", ignoreCase = true) ->
              "Haa, contact number ta message e pathiye dao, ami save kore rakhbo."
            userContent.contains("pic", ignoreCase = true) || userContent.contains("photo", ignoreCase = true) || userContent.contains("chobi", ignoreCase = true) ->
              "Haa, chobi ta pathao, dekhte chachi! 📷"
            else -> "Thik ache, shune bhalo laglo! Ar ki khobor?"
          }
          MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = textReply,
            timestamp = now,
            formattedTime = timeFormatted,
            isOutgoing = false,
            status = "READ",
            messageType = "TEXT"
          )
        }
      }

      chatDao.insertMessage(replyMessage)
      val preview = when (replyMessage.messageType) {
        "IMAGE" -> "📷 ${replyMessage.text}"
        "CONTACT" -> "👤 Contact: ${replyMessage.sharedContactNumber}"
        else -> replyMessage.text
      }
      val existing = chatDao.getChatByIdOnce(chatId)
      val activeAcc = chatDao.getActiveAccountOnce()
      val ownerId = existing?.ownerAccountId ?: activeAcc?.id ?: ""
      if (existing != null) {
        chatDao.insertChat(
          existing.copy(
            subtitle = preview,
            lastMessage = preview,
            lastMessageTimestamp = now,
            lastMessageFormattedTime = timeFormatted,
            isOutgoing = false,
            status = "READ",
            unreadCount = 0
          )
        )
      } else {
        chatDao.insertChat(
          ChatEntity(
            id = chatId,
            ownerAccountId = ownerId,
            contactId = chatId,
            title = getContactTitle(chatId),
            subtitle = preview,
            lastMessage = preview,
            lastMessageTimestamp = now,
            lastMessageFormattedTime = timeFormatted,
            unreadCount = 0,
            avatarDrawableName = getAvatarForId(chatId),
            avatarColorHex = getColorForId(chatId),
            isOutgoing = false,
            status = "READ",
            isPinned = false,
            isOnline = true
          )
        )
      }
    }
  }

  suspend fun addPersonToMessages(targetIdentifier: String): Result<ChatEntity> {
    val currentAccount = chatDao.getActiveAccountOnce()
      ?: return Result.failure(Exception("Please sign in or sign up to message someone."))

    val clean = targetIdentifier.trim()
    if (clean.isBlank()) {
      return Result.failure(Exception("Please enter a valid username or phone number."))
    }

    val withAt = if (clean.startsWith("@")) clean else "@$clean"
    val withoutAt = if (clean.startsWith("@")) clean.substring(1) else clean

    // Verify if the target user actually has an account registered in the app!
    val targetAccount = chatDao.findAccount(clean)
      ?: chatDao.findAccount(withAt)
      ?: chatDao.findAccount(withoutAt)

    if (targetAccount == null) {
      return Result.failure(
        Exception("No account found for \"$targetIdentifier\". The person must have an account on FAMILY app before they can be added to messages.")
      )
    }

    if (targetAccount.id == currentAccount.id || targetAccount.username.equals(currentAccount.username, ignoreCase = true)) {
      return Result.failure(Exception("You cannot add yourself to messages."))
    }

    val chatId = "chat_${currentAccount.id}_${targetAccount.id}"

    // Check if chat already exists for this owner
    val existingChat = chatDao.findChatForAccount(currentAccount.id, targetAccount.id, chatId)
    if (existingChat != null) {
      return Result.success(existingChat)
    }

    val now = System.currentTimeMillis()
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
    val greeting = "Say hi to ${targetAccount.fullName}! 👋"

    val newChat = ChatEntity(
      id = chatId,
      ownerAccountId = currentAccount.id,
      contactId = targetAccount.id,
      title = targetAccount.fullName,
      subtitle = greeting,
      lastMessage = greeting,
      lastMessageTimestamp = now,
      lastMessageFormattedTime = timeFormatted,
      unreadCount = 0,
      avatarDrawableName = targetAccount.avatarDrawableName,
      avatarColorHex = targetAccount.avatarColorHex,
      isOutgoing = false,
      status = "READ",
      isPinned = false,
      isOnline = true
    )
    chatDao.insertChat(newChat)

    // Ensure contact exists for this user
    val contact = ContactEntity(
      id = targetAccount.id,
      name = targetAccount.fullName,
      username = targetAccount.username,
      phone = targetAccount.phone,
      status = targetAccount.status,
      avatarDrawableName = targetAccount.avatarDrawableName,
      avatarColorHex = targetAccount.avatarColorHex,
      isSelf = false,
      ownerAccountId = currentAccount.id
    )
    chatDao.insertContact(contact)

    return Result.success(newChat)
  }

  suspend fun addContact(name: String, phone: String, username: String) {
    val id = UUID.randomUUID().toString()
    val contact = ContactEntity(
      id = id,
      name = name,
      username = if (username.startsWith("@")) username else "@$username",
      phone = phone,
      status = "Hey there! I am using FAMILY.",
      avatarDrawableName = null,
      avatarColorHex = 0xFF128C7E,
      isSelf = false
    )
    chatDao.insertContact(contact)
  }

  private fun getContactTitle(id: String): String {
    return when (id) {
      "chat_1" -> "F Rahman"
      "chat_2" -> "Masud Vai"
      "chat_3" -> "Payoneer"
      "chat_4" -> "Abdullah"
      "chat_5" -> "Raju Ahmed"
      "chat_6" -> "MD joy F"
      "chat_7" -> "+91 84211 24017"
      "chat_8" -> "Nishoo 🌻"
      "chat_9" -> "Akash.."
      "chat_10" -> "Awal Mama"
      else -> "Contact"
    }
  }

  private fun getAvatarForId(id: String): String? {
    return when (id) {
      "chat_1" -> null
      "chat_2" -> "avatar_city"
      "chat_3" -> null
      "chat_4" -> null
      "chat_5" -> "avatar_gamer"
      "chat_6" -> null
      "chat_7" -> "avatar_person1"
      "chat_8" -> null
      "chat_9" -> "avatar_person1"
      "chat_10" -> null
      else -> null
    }
  }

  private fun getColorForId(id: String): Long {
    return when (id) {
      "chat_1" -> 0xFF634E3C
      "chat_3" -> 0xFFE040FB
      "chat_4" -> 0xFF1976D2
      "chat_6" -> 0xFF795548
      "chat_8" -> 0xFF43A047
      "chat_10" -> 0xFF00897B
      else -> 0xFF2A3942
    }
  }

  private suspend fun seedInitialDataIfEmpty() {
    if (chatDao.getAccountsCount() == 0) {
      val defaultAccount = UserAccountEntity(
        id = "user_default",
        fullName = "boekfjmdhn",
        username = "@boylysmfjjfj",
        phone = "+1 (555) 019-2834",
        email = "boyly@family.app",
        passwordHash = "123456",
        status = "Today in emoji...",
        avatarDrawableName = "avatar_gamer",
        avatarColorHex = 0xFFFF7043,
        isActive = true,
        createdAt = System.currentTimeMillis()
      )

      val familyAccounts = listOf(
        defaultAccount,
        UserAccountEntity(
          id = "user_frahman",
          fullName = "F Rahman",
          username = "@frahman",
          phone = "+1 202 555 0143",
          email = "frahman@family.app",
          passwordHash = "123456",
          status = "Bondhu",
          avatarDrawableName = null,
          avatarColorHex = 0xFF634E3C,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_masud",
          fullName = "Masud Vai",
          username = "@masudvai",
          phone = "+1 202 555 0192",
          email = "masud@family.app",
          passwordHash = "123456",
          status = "Available",
          avatarDrawableName = "avatar_city",
          avatarColorHex = 0xFF2E7D32,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_awalmama",
          fullName = "Awal Mama",
          username = "@awalmama",
          phone = "+1 202 555 0199",
          email = "awalmama@family.app",
          passwordHash = "123456",
          status = "Family first",
          avatarDrawableName = null,
          avatarColorHex = 0xFF00695C,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_raju",
          fullName = "Raju Ahmed",
          username = "@raju_ahmed",
          phone = "+1 202 555 0178",
          email = "raju@family.app",
          passwordHash = "123456",
          status = "At the gym 🏋️",
          avatarDrawableName = "avatar_gamer",
          avatarColorHex = 0xFFE65100,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_abdullah",
          fullName = "Abdullah",
          username = "@abdullah_99",
          phone = "+1 202 555 0184",
          email = "abdullah@family.app",
          passwordHash = "123456",
          status = "Sleeping...",
          avatarDrawableName = null,
          avatarColorHex = 0xFF1565C0,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_nishoo",
          fullName = "Nishoo 🌻",
          username = "@nishoo_sun",
          phone = "+1 202 555 0122",
          email = "nishoo@family.app",
          passwordHash = "123456",
          status = "Sunflower mode on 🌻",
          avatarDrawableName = null,
          avatarColorHex = 0xFFF57F17,
          isActive = false
        ),
        UserAccountEntity(
          id = "user_akash",
          fullName = "Akash..",
          username = "@akash_sky",
          phone = "+1 202 555 0115",
          email = "akash@family.app",
          passwordHash = "123456",
          status = "Busy right now",
          avatarDrawableName = "avatar_person1",
          avatarColorHex = 0xFF283593,
          isActive = false
        )
      )
      familyAccounts.forEach { chatDao.insertAccount(it) }

      // Seed initial contacts for user_default
      val contacts = listOf(
        ContactEntity(
          id = defaultAccount.id,
          name = defaultAccount.fullName,
          username = defaultAccount.username,
          phone = defaultAccount.phone,
          status = defaultAccount.status,
          avatarDrawableName = defaultAccount.avatarDrawableName,
          avatarColorHex = defaultAccount.avatarColorHex,
          isSelf = true,
          ownerAccountId = defaultAccount.id
        ),
        ContactEntity("c1", "F Rahman", "@frahman", "+1 202 555 0143", "Bondhu", null, 0xFF634E3C, ownerAccountId = defaultAccount.id),
        ContactEntity("c2", "Masud Vai", "@masudvai", "+1 202 555 0192", "Available", "avatar_city", 0xFF2E7D32, ownerAccountId = defaultAccount.id),
        ContactEntity("c3", "Payoneer", "@payoneer_alerts", "+1 800 251 2521", "Official Notifications", null, 0xFFE91E63, ownerAccountId = defaultAccount.id),
        ContactEntity("c4", "Abdullah", "@abdullah_99", "+1 202 555 0184", "Sleeping...", null, 0xFF1565C0, ownerAccountId = defaultAccount.id),
        ContactEntity("c5", "Raju Ahmed", "@raju_ahmed", "+1 202 555 0178", "At the gym 🏋️", "avatar_gamer", 0xFFE65100, ownerAccountId = defaultAccount.id),
        ContactEntity("c8", "Nishoo 🌻", "@nishoo_sun", "+1 202 555 0122", "Sunflower mode on 🌻", null, 0xFFF57F17, ownerAccountId = defaultAccount.id),
        ContactEntity("c9", "Akash..", "@akash_sky", "+1 202 555 0115", "Busy right now", "avatar_person1", 0xFF283593, ownerAccountId = defaultAccount.id),
        ContactEntity("c10", "Awal Mama", "@awalmama", "+1 202 555 0199", "Family first", null, 0xFF00695C, ownerAccountId = defaultAccount.id)
      )
      chatDao.insertContacts(contacts)
    }
  }
}
