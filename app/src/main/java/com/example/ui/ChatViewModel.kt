package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatEntity
import com.example.data.ChatRepository
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.data.UserAccountEntity
import com.example.ui.components.NavTab
import com.example.ui.screens.AuthTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
  data object Home : AppScreen()
  data class ChatDetail(val chatId: String) : AppScreen()
  data object Settings : AppScreen()
  data object SelectContact : AppScreen()
  data class Auth(val initialTab: AuthTab = AuthTab.SIGN_IN) : AppScreen()
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: ChatRepository

  init {
    val db = AppDatabase.getDatabase(application)
    repository = ChatRepository(db.chatDao())
  }

  val selectedTab = MutableStateFlow(NavTab.CHATS)
  val searchQuery = MutableStateFlow("")
  val isOverflowMenuExpanded = MutableStateFlow(false)
  val currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
  val showSwitchAccountSheet = MutableStateFlow(false)
  val showNewContactDialog = MutableStateFlow(false)

  // Currently opened chat id
  val activeChatId = MutableStateFlow<String?>(null)

  val allChats: StateFlow<List<ChatEntity>> = repository.allChats
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredChats: StateFlow<List<ChatEntity>> = combine(allChats, searchQuery) { chats, query ->
    if (query.isBlank()) chats
    else {
      chats.filter {
        it.title.contains(query, ignoreCase = true) ||
            it.lastMessage.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allContacts: StateFlow<List<ContactEntity>> = repository.allContacts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAccounts: StateFlow<List<UserAccountEntity>> = repository.allAccounts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeAccount: StateFlow<UserAccountEntity?> = repository.activeAccount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val otherRegisteredAccounts: StateFlow<List<UserAccountEntity>> = activeAccount
    .flatMapLatest { account ->
      if (account != null) repository.getOtherRegisteredAccounts(account.id)
      else repository.allAccounts
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredContacts: StateFlow<List<ContactEntity>> = combine(allContacts, searchQuery) { contacts, query ->
    if (query.isBlank()) contacts
    else {
      contacts.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.username.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeChatMessages: StateFlow<List<MessageEntity>> = activeChatId
    .flatMapLatest { id ->
      if (id != null) repository.getMessagesForChat(id)
      else flowOf(emptyList())
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeChat: StateFlow<ChatEntity?> = activeChatId
    .flatMapLatest { id ->
      if (id != null) repository.getChatById(id)
      else flowOf(null)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  fun selectTab(tab: NavTab) {
    selectedTab.value = tab
  }

  fun setSearch(query: String) {
    searchQuery.value = query
  }

  fun setOverflowExpanded(expanded: Boolean) {
    isOverflowMenuExpanded.value = expanded
  }

  fun openChat(chatId: String) {
    activeChatId.value = chatId
    currentScreen.value = AppScreen.ChatDetail(chatId)
    viewModelScope.launch {
      repository.markChatAsRead(chatId)
    }
  }

  fun openSettings() {
    currentScreen.value = AppScreen.Settings
  }

  fun openSelectContact() {
    currentScreen.value = AppScreen.SelectContact
  }

  fun openAuth(tab: AuthTab = AuthTab.SIGN_IN) {
    currentScreen.value = AppScreen.Auth(tab)
    isOverflowMenuExpanded.value = false
    showSwitchAccountSheet.value = false
  }

  fun navigateBack() {
    when (currentScreen.value) {
      is AppScreen.ChatDetail, AppScreen.Settings, AppScreen.SelectContact, is AppScreen.Auth -> {
        currentScreen.value = AppScreen.Home
        activeChatId.value = null
      }
      AppScreen.Home -> {}
    }
  }

  fun signIn(identifier: String, password: String, onResult: (String?) -> Unit) {
    viewModelScope.launch {
      val result = repository.signIn(identifier, password)
      if (result.isSuccess) {
        currentScreen.value = AppScreen.Home
        onResult(null)
      } else {
        onResult(result.exceptionOrNull()?.message ?: "Sign in failed")
      }
    }
  }

  fun signUp(
    fullName: String,
    username: String,
    phone: String,
    email: String,
    password: String,
    status: String,
    avatarDrawableName: String?,
    avatarColorHex: Long,
    onResult: (String?) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.signUp(
        fullName, username, phone, email, password, status, avatarDrawableName, avatarColorHex
      )
      if (result.isSuccess) {
        currentScreen.value = AppScreen.Home
        onResult(null)
      } else {
        onResult(result.exceptionOrNull()?.message ?: "Sign up failed")
      }
    }
  }

  fun switchAccount(accountId: String) {
    viewModelScope.launch {
      repository.switchAccount(accountId)
      showSwitchAccountSheet.value = false
      if (currentScreen.value is AppScreen.Auth) {
        currentScreen.value = AppScreen.Home
      }
    }
  }

  fun signOut() {
    viewModelScope.launch {
      repository.signOut()
      currentScreen.value = AppScreen.Auth(AuthTab.SIGN_IN)
      showSwitchAccountSheet.value = false
    }
  }

  fun markAllAsRead() {
    viewModelScope.launch {
      repository.markAllChatsAsRead()
    }
  }

  fun sendMessage(text: String) {
    val chatId = activeChatId.value ?: return
    if (text.isBlank()) return
    viewModelScope.launch {
      repository.sendMessage(chatId, text.trim())
    }
  }

  fun sendImageMessage(imageUri: String, caption: String = "") {
    val chatId = activeChatId.value ?: return
    viewModelScope.launch {
      repository.sendImageMessage(chatId, imageUri, caption.trim())
    }
  }

  fun sendContactNumberMessage(contactName: String, phoneNumber: String, note: String = "") {
    val chatId = activeChatId.value ?: return
    if (phoneNumber.isBlank()) return
    viewModelScope.launch {
      repository.sendContactNumberMessage(chatId, contactName.trim(), phoneNumber.trim(), note.trim())
    }
  }

  fun addNewContact(name: String, phone: String, username: String) {
    viewModelScope.launch {
      repository.addContact(name, phone, username)
    }
  }

  fun addPersonToMessages(targetIdentifier: String, onResult: (String?, ChatEntity?) -> Unit) {
    viewModelScope.launch {
      val result = repository.addPersonToMessages(targetIdentifier)
      if (result.isSuccess) {
        val chat = result.getOrNull()
        if (chat != null) {
          openChat(chat.id)
        }
        onResult(null, chat)
      } else {
        onResult(result.exceptionOrNull()?.message ?: "Failed to add user to messages", null)
      }
    }
  }
}
