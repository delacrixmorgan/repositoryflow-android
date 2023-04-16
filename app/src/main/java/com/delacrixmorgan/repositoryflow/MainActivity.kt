package com.delacrixmorgan.repositoryflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delacrixmorgan.repositoryflow.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                RegistrationScreen()
            }
        }
    }
}

@Preview
@Composable
fun RegistrationScreen(
    viewModel: MainViewModel = viewModel()
) {
    Column {
        Text(
            text = "Registration",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        RegistrationFields(viewModel)

        Text(
            text = "Preview",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        PreviewCard(viewModel)

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = { viewModel.clearDogDetails() }
        ) {
            Text(
                text = "Clear Data",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFields(viewModel: MainViewModel) = with(viewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = name,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
            singleLine = true,
            label = { Text(text = "Name") },
            onValueChange = { saveDogName(it) }
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
            expanded = !expanded
        }) {
            TextField(
                value = favouriteToy,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Favourite Toy") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                favouriteToys.forEach { selectedOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectedOption) },
                        onClick = {
                            saveDogFavouriteToy(selectedOption)
                            expanded = false
                        })
                }
            }
        }
        TextField(
            value = ownerEmail,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            label = { Text(text = "Emergency Contact Email") },
            onValueChange = { saveDogOwnerEmail(it) }
        )
    }
}

@Composable
fun PreviewCard(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
        ) {
            Text(
                text = "Canine Day Care \uD83E\uDDB4",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(
                modifier = Modifier.height(14.dp)
            )
            if (viewModel.previewName.text.isNotBlank()) {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = viewModel.previewName.text,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
            Text(
                text = "Favourite Toy",
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = viewModel.previewFavouriteToy,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (viewModel.previewOwnerEmail.text.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(12.dp),
                )
                Text(
                    text = "In case of emergency, please contact",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = viewModel.previewOwnerEmail.text,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}