package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ChatViewModel
import com.example.ui.components.AccountSwitcherSheet
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SelectContactScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = AmoledBackground
        ) {
          FamilyApp()
        }
      }
    }
  }
}

@Composable
fun FamilyApp(viewModel: ChatViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val isOverflowExpanded by viewModel.isOverflowMenuExpanded.collectAsStateWithLifecycle()
  val chats by viewModel.filteredChats.collectAsStateWithLifecycle()
  val contacts by viewModel.filteredContacts.collectAsStateWithLifecycle()
  val activeChat by viewModel.activeChat.collectAsStateWithLifecycle()
  val activeMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
  val showAccountSheet by viewModel.showSwitchAccountSheet.collectAsStateWithLifecycle()
  val allAccounts by viewModel.allAccounts.collectAsStateWithLifecycle()
  val activeAccount by viewModel.activeAccount.collectAsStateWithLifecycle()

  // Handle system back navigation
  if (currentScreen !is AppScreen.Home) {
    BackHandler {
      viewModel.navigateBack()
    }
  }

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "ScreenTransition"
  ) { targetScreen ->
    when (targetScreen) {
      is AppScreen.Auth -> {
        AuthScreen(
          currentAccounts = allAccounts,
          initialTab = targetScreen.initialTab,
          onSignIn = viewModel::signIn,
          onSignUp = viewModel::signUp,
          onSwitchToAccount = viewModel::switchAccount,
          onCancelOrBack = if (allAccounts.any { it.isActive }) {
            viewModel::navigateBack
          } else null
        )
      }

      is AppScreen.Home -> {
        HomeScreen(
          chats = chats,
          selectedTab = selectedTab,
          searchQuery = searchQuery,
          isOverflowExpanded = isOverflowExpanded,
          onTabSelected = viewModel::selectTab,
          onSearchChanged = viewModel::setSearch,
          onToggleOverflow = viewModel::setOverflowExpanded,
          onChatClicked = viewModel::openChat,
          onOpenSettings = viewModel::openSettings,
          onOpenSelectContact = viewModel::openSelectContact,
          onReadAll = viewModel::markAllAsRead,
          onSwitchAccount = { viewModel.showSwitchAccountSheet.value = true }
        )
      }

      is AppScreen.ChatDetail -> {
        ChatDetailScreen(
          chat = activeChat,
          messages = activeMessages,
          contacts = contacts,
          onNavigateBack = viewModel::navigateBack,
          onSendMessage = viewModel::sendMessage,
          onSendImageMessage = viewModel::sendImageMessage,
          onSendContactNumber = viewModel::sendContactNumberMessage
        )
      }

      is AppScreen.Settings -> {
        SettingsScreen(
          onNavigateBack = viewModel::navigateBack,
          activeAccount = activeAccount,
          onOpenAccountSwitcher = { viewModel.showSwitchAccountSheet.value = true },
          onOpenAuth = viewModel::openAuth,
          onSignOut = viewModel::signOut
        )
      }

      is AppScreen.SelectContact -> {
        SelectContactScreen(
          contacts = contacts,
          onNavigateBack = viewModel::navigateBack,
          onContactSelected = { contact ->
            // If there's an existing chat, open it; otherwise create/open
            val existingChat = chats.find { it.contactId == contact.id || it.title == contact.name }
            if (existingChat != null) {
              viewModel.openChat(existingChat.id)
            } else {
              viewModel.openChat(contact.id)
            }
          },
          onAddNewContact = viewModel::addNewContact
        )
      }
    }
  }

  if (showAccountSheet) {
    AccountSwitcherSheet(
      accounts = allAccounts,
      onDismiss = { viewModel.showSwitchAccountSheet.value = false },
      onAccountSelected = viewModel::switchAccount,
      onAddAccount = { viewModel.openAuth(com.example.ui.screens.AuthTab.SIGN_UP) },
      onSignOut = viewModel::signOut
    )
  }
}
