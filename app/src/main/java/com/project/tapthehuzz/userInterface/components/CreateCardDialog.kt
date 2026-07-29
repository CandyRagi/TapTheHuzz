package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.data.model.User
import com.project.tapthehuzz.userInterface.theme.AccentRed
import com.project.tapthehuzz.userInterface.theme.GlassBorder
import com.project.tapthehuzz.userInterface.theme.GlassSurface
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCardDialog(
    user: User,
    card: Card? = null,
    onDismiss: () -> Unit,
    onSave: (Card) -> Unit
) {
    var cardName by remember { mutableStateOf(card?.name ?: "") }
    var cardLink by remember { mutableStateOf(card?.link ?: "") }
    var cardCategory by remember { mutableStateOf(if (card != null && card.category !in listOf("Social", "Game", "GitHub", "Business")) "Custom" else card?.category ?: "") }
    var customCategory by remember { mutableStateOf(if (card != null && card.category !in listOf("Social", "Game", "GitHub", "Business")) card.category else "") }
    var selectedDesign by remember { mutableStateOf(card?.designId ?: "") }
    var imageUrl by remember { mutableStateOf(card?.imageUrl ?: user.pfp) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val quickLinks = remember(user) {
        listOfNotNull(
            "Instagram".takeIf { user.instagramLink.isNotEmpty() }?.let { it to user.instagramLink },
            "Snapchat".takeIf { user.snapchatLink.isNotEmpty() }?.let { it to user.snapchatLink },
            "TikTok".takeIf { user.tiktokLink.isNotEmpty() }?.let { it to user.tiktokLink },
            "YouTube".takeIf { user.youtubeLink.isNotEmpty() }?.let { it to user.youtubeLink },
            "Facebook".takeIf { user.facebookLink.isNotEmpty() }?.let { it to user.facebookLink },
            "Valorant".takeIf { user.valorantLink.isNotEmpty() }?.let { it to user.valorantLink },
            "Discord".takeIf { user.discordLink.isNotEmpty() }?.let { it to user.discordLink },
            "WhatsApp".takeIf { user.whatsappLink.isNotEmpty() }?.let { it to user.whatsappLink },
            "Phone".takeIf { user.phoneNumber.isNotEmpty() }?.let { it to user.phoneNumber }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GlassSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(GlassBorder)
            )

            Text(
                text = if (card == null) "Create Card" else "Edit Card",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture Preview
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .border(1.dp, GlassBorder, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                         Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }

                    // Sync PFP Button
                    IconButton(
                        onClick = { imageUrl = user.pfp },
                        modifier = Modifier
                            .size(26.dp)
                            .background(AccentRed, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, // Using Person icon as "Sync/Use Profile"
                            contentDescription = "Sync PFP",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Card Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = cardLink,
                    onValueChange = { cardLink = it },
                    label = { Text("Link") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Dropdown
                var expanded by remember { mutableStateOf(false) }
                val categories = listOf("Social", "Game", "GitHub", "Business", "Custom")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = cardCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    cardCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (cardCategory == "Custom") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Enter Custom Category") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Quick Link Options
                if (quickLinks.isNotEmpty()) {
                    Text(
                        text = "Quick Fill",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 16.dp, bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickLinks) { (label, link) ->
                            SuggestionChip(
                                onClick = { cardLink = link },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Background Design",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                val designs = listOf(
                    "design_one" to com.project.tapthehuzz.R.drawable.card_design_one,
                    "design_two" to com.project.tapthehuzz.R.drawable.card_design_two,
                    "design_three" to com.project.tapthehuzz.R.drawable.card_design_three,
                    "design_four" to com.project.tapthehuzz.R.drawable.card_design_four
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Increased height to accommodate 2:1 aspect ratio cards
                        .padding(vertical = 8.dp)
                ) {
                    gridItems(designs) { (designId, drawableId) ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(2f) // 2:1 Aspect Ratio
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (selectedDesign == designId) 2.dp else 1.dp,
                                    color = if (selectedDesign == designId) AccentRed else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDesign = designId }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = drawableId),
                                contentDescription = designId,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val cardId = card?.id ?: UUID.randomUUID().toString().substring(0, 8)
                            val cardNumber = card?.cardNumber ?: List(12) { (0..9).random() }.joinToString("")
                            val newCard = Card(
                                id = cardId,
                                userId = user.uid,
                                name = cardName,
                                link = cardLink,
                                backgroundColor = Color.White.toArgb().toLong(),
                                imageUrl = imageUrl,
                                cardNumber = cardNumber,
                                category = if (cardCategory == "Custom") customCategory.ifEmpty { "Custom" } else cardCategory.ifEmpty { "Uncategorized" },
                                designId = selectedDesign
                            )
                            onSave(newCard)
                        },
                        enabled = cardName.isNotEmpty() && cardLink.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (card == null) "Create" else "Save")
                    }
                }
            }
        }
    }
}
