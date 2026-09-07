package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SearchBarBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.launch

@Composable
fun SelectContactScreen(
  contacts: List<ContactEntity>,
  onNavigateBack: () -> Unit,
  onContactSelected: (ContactEntity) -> Unit,
  onAddNewContact: (name: String, phone: String, username: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  var isSearching by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  val filteredContacts = remember(contacts, searchQuery) {
    if (searchQuery.isBlank()) contacts
    else contacts.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
          it.username.contains(searchQuery, ignoreCase = true) ||
          it.phone.contains(searchQuery, ignoreCase = true)
    }
  }

  val otherContacts = filteredContacts.filter { !it.isSelf }
  val selfContact = filteredContacts.firstOrNull { it.isSelf }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = AmoledBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .statusBarsPadding()
    ) {
      // Top Bar matching Screenshot 4
      if (!isSearching) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.testTag("btn_select_contact_back")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
              )
            }

            Column(modifier = Modifier.padding(start = 8.dp)) {
              Text(
                text = "Select contact",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${contacts.size} contacts",
                color = TextSecondary,
                fontSize = 13.sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { isSearching = true },
              modifier = Modifier.testTag("btn_contact_search")
            ) {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
            }
            IconButton(
              onClick = {
                scope.launch { snackbarHostState.showSnackbar("Contacts options") }
              }
            ) {
              Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = TextPrimary)
            }
          }
        }
      } else {
        // Search Input Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = {
            isSearching = false
            searchQuery = ""
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Close search",
              tint = TextPrimary
            )
          }

          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search name or number...", color = TextSecondary) },
            singleLine = true,
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = SearchBarBackground,
              unfocusedContainerColor = SearchBarBackground,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              cursorColor = VibrantGreen
            ),
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .clip(CircleShape)
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("select_contacts_list")
      ) {
        // Top Action Items as in Screenshot 4:
        // 1. New group
        // 2. New contact (with QR icon)
        // 3. New community
        item {
          ActionItemRow(
            icon = Icons.Default.GroupAdd,
            label = "New group",
            onClick = {
              scope.launch { snackbarHostState.showSnackbar("Create New Group") }
            }
          )

          ActionItemRow(
            icon = Icons.Default.PersonAdd,
            label = "New contact",
            showQr = true,
            onClick = { showAddDialog = true }
          )

          ActionItemRow(
            icon = Icons.Default.Groups,
            label = "New community",
            onClick = {
              scope.launch { snackbarHostState.showSnackbar("Create New Community") }
            }
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Section Title: "Contacts on FAMILY"
          Text(
            text = "Contacts on FAMILY",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        // Self Contact Row: "@username (You) - Message yourself"
        if (selfContact != null) {
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onContactSelected(selfContact) }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("contact_row_self"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AvatarView(
                size = 48.dp,
                drawableName = selfContact.avatarDrawableName,
                initials = "You",
                backgroundColorHex = selfContact.avatarColorHex
              )

              Spacer(modifier = Modifier.width(14.dp))

              Column {
                Text(
                  text = "${selfContact.username} (You)",
                  color = TextPrimary,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Message yourself",
                  color = TextSecondary,
                  fontSize = 13.sp
                )
              }
            }
          }
        }

        // Alphabetical Contact Rows
        items(otherContacts, key = { it.id }) { contact ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onContactSelected(contact) }
              .padding(horizontal = 16.dp, vertical = 10.dp)
              .testTag("contact_row_${contact.id}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AvatarView(
              size = 48.dp,
              drawableName = contact.avatarDrawableName,
              initials = contact.name,
              backgroundColorHex = contact.avatarColorHex
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = contact.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
              )
              if (contact.status.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = contact.status,
                  color = TextSecondary,
                  fontSize = 13.sp
                )
              }
            }
          }
        }
      }
    }
  }

  // Add Contact Dialog
  if (showAddDialog) {
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = DarkSurfaceVariant,
      title = {
        Text("New Contact", color = TextPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Name") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = VibrantGreen
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newPhone,
            onValueChange = { newPhone = it },
            label = { Text("Phone Number") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = VibrantGreen
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newUsername,
            onValueChange = { newUsername = it },
            label = { Text("Username (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = VibrantGreen
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newName.isNotBlank()) {
              onAddNewContact(newName.trim(), newPhone.trim(), newUsername.trim())
              showAddDialog = false
              scope.launch { snackbarHostState.showSnackbar("Contact added: $newName") }
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = VibrantGreen,
            contentColor = Color(0xFF0B1014)
          )
        ) {
          Text("Save", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
private fun ActionItemRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  showQr: Boolean = false,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .testTag("action_${label.lowercase().replace(" ", "_")}"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(VibrantGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = Color(0xFF0B1014),
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
        text = label,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    if (showQr) {
      IconButton(onClick = onClick) {
        Icon(
          imageVector = Icons.Default.QrCode,
          contentDescription = "QR Code",
          tint = TextSecondary
        )
      }
    }
  }
}
