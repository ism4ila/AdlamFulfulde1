package com.bekisma.adlamfulfulde.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            WritingTopAppBar(navController)
        }
    ) { innerPadding ->
        WritingScreenContent(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritingTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.learn_to_write),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun WritingScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AlphabetCard()
            Spacer(modifier = Modifier.height(24.dp))
            WritingActions(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}

@Composable
private fun AlphabetCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.the_alphabet),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.trace_and_learn_all_adlam_letters),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.adlam_alphabet_description).trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun WritingActions(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WritingActionButton(
                modifier = Modifier.weight(1f),
                navController = navController,
                route = "writingPractice/${WritingType.UPPERCASE.name}",
                text = stringResource(R.string.upper_case),
                icon = Icons.Default.Create
            )
            WritingActionButton(
                modifier = Modifier.weight(1f),
                navController = navController,
                route = "writingPractice/${WritingType.LOWERCASE.name}",
                text = stringResource(R.string.lower_case),
                icon = Icons.Default.Edit
            )
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WritingActionButton(
                modifier = Modifier.weight(1f),
                navController = navController,
                route = "writingPractice/${WritingType.NUMBERS.name}",
                text = stringResource(R.string.numbers),
                icon = Icons.Outlined.Numbers
            )
            WritingActionButton(
                modifier = Modifier.weight(1f),
                navController = navController,
                route = "culturalWords",
                text = "Mots culturels",
                icon = Icons.Default.Book
            )
        }
        // Row 3 - Full Width
        WritingActionButton(
            modifier = Modifier.fillMaxWidth(),
            navController = navController,
            route = "comparisonMode/${WritingType.UPPERCASE.name}",
            text = "Mode comparaison",
            icon = Icons.Default.Compare
        )
    }
}

@Composable
private fun WritingActionButton(
    modifier: Modifier = Modifier,
    navController: NavController,
    route: String,
    text: String,
    icon: ImageVector
) {
    OutlinedButton(
        onClick = { navController.navigate(route) },
        modifier = modifier.height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "WritingScreen Dark")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "WritingScreen Light")
@Composable
fun WritingScreenPreview() {
    val navController = rememberNavController()
    // Theme { // Décommentez et utilisez votre thème si nécessaire pour la preview
    WritingScreen(navController)
    // }
}

