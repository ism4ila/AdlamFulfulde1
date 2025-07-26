package com.bekisma.adlamfulfulde.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew // Import the standard icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme // Assuming your theme is here

// Data classes remain the same
data class Contributor(val name: String)
data class Reference(val title: String, val url: String)

// Define some consistent spacing values
object AppSpacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    val contributors = listOf(
        Contributor(stringResource(R.string.contributor_aysha_sow)),
        Contributor(stringResource(R.string.contributor_aboubacar_ballo)),
        Contributor(stringResource(R.string.contributor_ismaila_hamadou))
    )

    val references = listOf(
        Reference(
            title = stringResource(R.string.adlam_by_microsoft),
            url = "https://unlocked.microsoft.com/adlam-can-an-alphabet-save-a-culture/"
        ),
        Reference(
            title = stringResource(R.string.android_developers_guide),
            url = "https://developer.android.com/guide"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about),
                        style = MaterialTheme.typography.titleLarge // Use titleLarge for AppBar title
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary // Ensure back icon color matches title
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = AppSpacing.medium) // Apply horizontal padding
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = AppSpacing.medium) // Apply vertical padding to content
        ) {
            item {
                // Add a small app logo or icon for visual interest
                Image(
                    painter = painterResource(id = R.drawable.iconapp), // Replace with your app logo drawable
                    contentDescription = stringResource(R.string.app_logo_description), // Add content description
                    modifier = Modifier
                        .size(96.dp) // Adjust size as needed
                        .padding(bottom = AppSpacing.medium)
                )
            }
            item {
                Text(
                    text = stringResource(R.string.app_name), // Assuming you have an app_name string resource
                    style = MaterialTheme.typography.headlineMedium, // Use a slightly larger headline
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = AppSpacing.small)
                )
            }

            item {
                Text(
                    text = stringResource(R.string.app_version), // Add an app_version string resource
                    style = MaterialTheme.typography.bodyMedium, // Use a smaller text for version
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Use a less prominent color
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = AppSpacing.large)
                )
            }


            item {
                Text(
                    text = stringResource(R.string.contributors),
                    style = MaterialTheme.typography.titleLarge.copy( // Use titleLarge for section titles
                        fontWeight = FontWeight.SemiBold // Use SemiBold for slightly less emphasis than Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth() // Make the title take full width
                        .padding(vertical = AppSpacing.small),
                    textAlign = TextAlign.Start // Align title to the start
                )
                Divider(modifier = Modifier.padding(bottom = AppSpacing.small)) // Add a divider below the title
            }
            items(contributors) { contributor ->
                Text(
                    text = contributor.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start, // Align names to the start
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth() // Make the name take full width
                        .padding(vertical = AppSpacing.extraSmall) // Smaller vertical padding for list items
                )
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.large)) // Use consistent large spacing
            }

            item {
                Text(
                    text = stringResource(R.string.references),
                    style = MaterialTheme.typography.titleLarge.copy( // Use titleLarge for section titles
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth() // Make the title take full width
                        .padding(vertical = AppSpacing.small),
                    textAlign = TextAlign.Start // Align title to the start
                )
                Divider(modifier = Modifier.padding(bottom = AppSpacing.small)) // Add a divider below the title
            }

            items(references) { reference ->
                ReferenceLink(
                    title = reference.title,
                    url = reference.url // Pass the URL down
                )
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.large))
                
                // Developer section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.developed_by),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = AppSpacing.small)
                        )
                        Text(
                            text = stringResource(R.string.developer_name),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = AppSpacing.small)
                        )
                        
                        // Contact section
                        Divider(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = AppSpacing.small)
                        )
                        
                        Text(
                            text = "Contact",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = AppSpacing.extraSmall)
                        )
                        
                        // Email contact
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:ism4company@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Adlam Fulfulde App - Contact")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Handle error silently or show toast
                                    }
                                }
                                .padding(AppSpacing.small)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_dialog_email),
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.small))
                            Text(
                                text = "ism4company@gmail.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                        
                        // App information
                        Spacer(modifier = Modifier.height(AppSpacing.medium))
                        Text(
                            text = "Version: 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "© 2024 ISM4 Company",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(AppSpacing.medium))
                
                // App description section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.medium)
                    ) {
                        Text(
                            text = "À propos de l'application",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = AppSpacing.small)
                        )
                        Text(
                            text = "Adlam Fulfulde est une application éducative conçue pour enseigner l'écriture Adlam et la langue Fulfulde. L'application offre des outils d'apprentissage interactifs incluant l'alphabet, les chiffres, la lecture guidée, l'écriture pratique et des quiz pour améliorer vos compétences linguistiques.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(AppSpacing.medium))
                
                // Banner ad at the bottom
            }
        }
    }
}

@Composable
fun ReferenceLink(title: String, url: String) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.extraSmall)
            .clickable(onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    // Handle the exception
                }
            }),
        shape = RoundedCornerShape(AppSpacing.small),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use the standard link icon from Icons.Default
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = stringResource(R.string.external_link_icon_description), // Use existing string resource
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = AppSpacing.small)
                    .size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AdlamFulfuldeTheme {
        AboutScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun ReferenceLinkPreview() {
    AdlamFulfuldeTheme {
        ReferenceLink(
            title = "Exemple de référence",
            url = "https://www.example.com"
        )
    }
}