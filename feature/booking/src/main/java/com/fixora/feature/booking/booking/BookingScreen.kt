package com.fixora.feature.booking.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fixora.core.common.result.Result
import com.fixora.core.designsystem.components.FixoraButton
import com.fixora.core.designsystem.components.FixoraTextField
import com.fixora.core.designsystem.components.GlassCard
import com.fixora.core.designsystem.components.RatingBar
import com.fixora.core.model.ServiceProvider
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel
) {
    val providerState by viewModel.providerState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()

    var issueDescription by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    // Handle booking state changes
    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingUiState.Success -> {
                viewModel.resetState()
                onBookingSuccess()
            }
            is BookingUiState.Error -> {
                showErrorDialog = (bookingState as BookingUiState.Error).message
            }
            else -> {}
        }
    }

    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = null
                viewModel.resetState()
            },
            title = { Text("Booking Error") },
            text = { Text(showErrorDialog ?: "Something went wrong") },
            confirmButton = {
                TextButton(onClick = { 
                    showErrorDialog = null
                    viewModel.resetState()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Book Service") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when (providerState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is Result.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load provider details",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is Result.Success -> {
                val provider = (providerState as Result.Success<ServiceProvider?>).data
                if (provider == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Provider not found")
                    }
                } else {
                    BookingContent(
                        provider = provider,
                        selectedDate = selectedDate,
                        selectedTimeSlot = selectedTimeSlot,
                        issueDescription = issueDescription,
                        onIssueDescriptionChange = { issueDescription = it },
                        onDateSelect = { viewModel.selectDate(it) },
                        onTimeSlotSelect = { viewModel.selectTimeSlot(it) },
                        onConfirmBooking = {
                            viewModel.submitBooking(
                                issueDescription = issueDescription
                            )
                        },
                        isLoading = bookingState is BookingUiState.Loading,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BookingContent(
    provider: ServiceProvider,
    selectedDate: String,
    selectedTimeSlot: String,
    issueDescription: String,
    onIssueDescriptionChange: (String) -> Unit,
    onDateSelect: (String) -> Unit,
    onTimeSlotSelect: (String) -> Unit,
    onConfirmBooking: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val dates = remember {
        (0..6).map { daysToAdd ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val dayNumber = SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time)
            val fullDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            Triple(dayName, dayNumber, fullDateStr)
        }
    }

    val availableSlots = provider.availableSlots.ifEmpty {
        listOf("09:00 AM", "11:00 AM", "01:00 PM", "03:00 PM", "05:00 PM")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Provider Brief Summary
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = provider.avatarUrl,
                    contentDescription = provider.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = provider.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingBar(rating = provider.rating, starSize = 12.dp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${provider.pricePerHour}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/hr",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Date Picker Section
        Column {
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dates) { (dayName, dayNumber, dateStr) ->
                    val isSelected = selectedDate == dateStr
                    Box(
                        modifier = Modifier
                            .size(width = 64.dp, height = 74.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { onDateSelect(dateStr) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayNumber,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Time Slot Picker Section
        Column {
            Text(
                text = "Select Time Slot",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                availableSlots.forEach { slot ->
                    val isSelected = selectedTimeSlot == slot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minWidth(80)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onTimeSlotSelect(slot) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Issue Description
        Column {
            Text(
                text = "Describe Your Problem",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FixoraTextField(
                value = issueDescription,
                onValueChange = onIssueDescriptionChange,
                label = "E.g., leaky kitchen pipe, kitchen faucet replacement",
                singleLine = false,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        // Pricing Breakdown Card
        Column {
            Text(
                text = "Price Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val baseRate = provider.pricePerHour
            val serviceFee = 15.00
            val totalCost = baseRate + serviceFee

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Initial Work Hour Rate",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", baseRate)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fixora Booking Fee",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", serviceFee)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Estimate",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", totalCost)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Spacer to push up
        Spacer(modifier = Modifier.height(24.dp))

        // Confirm Button
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            FixoraButton(
                text = "Confirm Booking",
                onClick = onConfirmBooking,
                enabled = selectedDate.isNotBlank() && selectedTimeSlot.isNotBlank() && issueDescription.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Custom Modifier extension helper to support min width on box inside flowrow
private fun Modifier.minWidth(minWidth: Int) = this.then(
    Modifier.widthIn(min = minWidth.dp)
)
