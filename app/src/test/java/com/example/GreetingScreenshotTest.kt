package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.ChatEntity
import com.example.ui.screens.CompactChatRow
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        CompactChatRow(
          chat = ChatEntity(
            id = "chat_test",
            contactId = "c1",
            title = "F Rahman",
            subtitle = "Bondhu",
            lastMessage = "Bondhu",
            lastMessageTimestamp = System.currentTimeMillis(),
            lastMessageFormattedTime = "Yesterday",
            unreadCount = 2,
            avatarDrawableName = null,
            avatarColorHex = 0xFF634E3C,
            isOutgoing = false,
            status = "READ"
          ),
          onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

