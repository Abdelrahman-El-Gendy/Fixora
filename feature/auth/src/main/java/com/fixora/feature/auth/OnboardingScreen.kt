package com.fixora.feature.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val descriptionAr: String? = null,
    val buttonText: String,
    val showSkip: Boolean = true
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            title = "Find Trusted\nProfessionals",
            description = "Browse verified local experts rated by your neighbors",
            buttonText = "Next",
            showSkip = true
        ),
        OnboardingPage(
            title = "Book in Seconds",
            description = "Describe your problem, pick a time, and we handle the rest",
            buttonText = "Next",
            showSkip = true
        ),
        OnboardingPage(
            title = "Track & Rate with Ease",
            description = "Follow your provider in real-time. Leave a review when done.",
            descriptionAr = "تابع مقدم الخدمة مباشرة وقيم تجربتك",
            buttonText = "Get Started",
            showSkip = false
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBFDFF) // Very light blue-ish white
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom Indicator
                OnboardingIndicator(
                    currentPage = pagerState.currentPage,
                    pageCount = pages.size
                )

                if (pages[pagerState.currentPage].showSkip) {
                    TextButton(onClick = {
                        viewModel.completeOnboarding()
                        onOnboardingFinished()
                    }) {
                        Text(
                            text = "SKIP",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            // Pager for Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex], index = pageIndex)
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding()
                            onOnboardingFinished()
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .then(
                            if (pagerState.currentPage == pages.size - 1) Modifier.fillMaxWidth()
                            else Modifier.wrapContentWidth()
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D70F2) // Professional Blue
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = pages[pagerState.currentPage].buttonText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (pagerState.currentPage < pages.size - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder for illustrations/mockups based on the images
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Since I cannot provide actual image assets, I will create a stylized mockup using Compose
            OnboardingIllustrationMockup(index)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 32.sp,
                lineHeight = 40.sp,
                color = Color(0xFF0F172A)
            ),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        if (page.descriptionAr != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = page.descriptionAr,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OnboardingIndicator(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = currentPage == index
            Canvas(modifier = Modifier.size(width = if (isSelected) 24.dp else 8.dp, height = 6.dp)) {
                drawRoundRect(
                    color = if (isSelected) Color(0xFF1D70F2) else Color(0xFFE2E8F0),
                    size = Size(width = size.width, height = size.height),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
        }
    }
}

@Composable
fun OnboardingIllustrationMockup(index: Int) {
    // Stylized containers representing the UI mockups in the images
    Box(
        modifier = Modifier
            .size(300.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFFF1F5F9).copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        when (index) {
            0 -> {
                // Mockup for "Find Trusted Professionals"
                Card(
                    modifier = Modifier.size(220.dp, 120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Box(modifier = Modifier.size(80.dp, 12.dp).background(Color(0xFFCBD5E1), RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.size(60.dp, 10.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row {
                                repeat(5) {
                                    Box(modifier = Modifier.size(12.dp).background(Color(0xFFF59E0B), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Mockup for "Book in Seconds"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(32.dp).background(Color(0xFFDBEAFE), CircleShape))
                    }}
                    Box(modifier = Modifier.padding(horizontal = 12.dp).size(40.dp).background(Color(0xFF1D70F2), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(32.dp).background(Color(0xFFFEF3C7), CircleShape))
                    }}
                }
            }
            2 -> {
                // Mockup for "Track & Rate"
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                            .background(Color(0xFF1D70F2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(Modifier.size(32.dp).background(Color.White, CircleShape))
                    }
                }
            }
        }
    }
}
