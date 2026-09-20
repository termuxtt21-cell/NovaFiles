package com.novafiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NovaFilesApp()
        }
    }
}

private val BackgroundTop = Color(0xFF080B12)
private val BackgroundBottom = Color(0xFF030509)
private val Surface = Color(0xFF111722)
private val SurfaceLight = Color(0xFF182130)
private val Primary = Color(0xFF4CC9FF)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextSecondary = Color(0xFF8C98AA)

@Composable
fun NovaFilesApp() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            )
    ) {

        when (selectedTab) {
            0 -> HomeScreen()
            1 -> FilesScreen()
            2 -> ToolsScreen()
            3 -> SettingsScreen()
        }

        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 12.dp
                )
        )
    }
}

@Composable
private fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 42.dp,
                bottom = 110.dp
            )
    ) {

        Text(
            text = "NOVAFILES",
            color = TextPrimary,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Powerful file management",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        StoragePanel()

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Quick Access",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            QuickAction(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Folder,
                title = "Files"
            )

            QuickAction(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Terminal,
                title = "Terminal"
            )

            QuickAction(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Security,
                title = "Access"
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "System Access",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        AccessPanel()
    }
}

@Composable
private fun StoragePanel() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF172333),
                        Color(0xFF0D131D)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.07f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(22.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column {
                    Text(
                        text = "Internal Storage",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Storage information",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.07f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Primary,
                                    Color(0xFF7B61FF)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Storage",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "Calculating...",
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier,
    icon: ImageVector,
    title: String
) {

    Box(
        modifier = modifier
            .height(105.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Surface)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(22.dp)
            )
            .clickable { }
            .padding(15.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(25.dp)
            )

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AccessPanel() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(23.dp))
            .background(Surface)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(23.dp)
            )
            .padding(18.dp)
    ) {

        Column {

            AccessRow(
                icon = Icons.Default.Storage,
                title = "Storage",
                status = "Not checked",
                active = false
            )

            Spacer(modifier = Modifier.height(15.dp))

            AccessRow(
                icon = Icons.Default.Security,
                title = "Shizuku",
                status = "Not connected",
                active = false
            )

            Spacer(modifier = Modifier.height(15.dp))

            AccessRow(
                icon = Icons.Default.Security,
                title = "Root",
                status = "Not detected",
                active = false
            )
        }
    }
}

@Composable
private fun AccessRow(
    icon: ImageVector,
    title: String,
    status: String,
    active: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) Primary else TextSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp
            )

            Text(
                text = status,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (active)
                        Primary
                    else
                        Color(0xFF555E6D)
                )
        )
    }
}

@Composable
private fun FilesScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 42.dp,
                bottom = 110.dp
            )
    ) {

        Text(
            text = "Files",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "Internal Storage",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        EmptyState(
            icon = Icons.Default.Folder,
            title = "File browser",
            subtitle = "Native file operations will appear here."
        )
    }
}

@Composable
private fun ToolsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 42.dp,
                bottom = 110.dp
            )
    ) {

        Text(
            text = "Tools",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        ToolItem(
            icon = Icons.Default.Terminal,
            title = "Terminal",
            subtitle = "Shell and privileged terminal"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolItem(
            icon = Icons.Default.Build,
            title = "APK Manager",
            subtitle = "Inspect and manage APK files"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolItem(
            icon = Icons.Default.Storage,
            title = "Storage Analyzer",
            subtitle = "Analyze storage usage"
        )
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Surface)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.06f),
                RoundedCornerShape(22.dp)
            )
            .clickable { }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SettingsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 42.dp,
                bottom = 110.dp
            )
    ) {

        Text(
            text = "Settings",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(22.dp))

        ToolItem(
            icon = Icons.Default.Settings,
            title = "Application",
            subtitle = "NovaFiles preferences"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolItem(
            icon = Icons.Default.Security,
            title = "Access",
            subtitle = "Storage, Shizuku and Root"
        )
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val items = listOf(
        Triple(Icons.Default.Home, "Home", 0),
        Triple(Icons.Default.Folder, "Files", 1),
        Triple(Icons.Default.Build, "Tools", 2),
        Triple(Icons.Default.Settings, "Settings", 3)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Color(0xFF111722).copy(alpha = 0.96f)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(26.dp)
            )
            .padding(7.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        items.forEach { item ->

            val selected = selectedTab == item.third

            NavButton(
                icon = item.first,
                title = item.second,
                selected = selected,
                onClick = {
                    onTabSelected(item.third)
                }
            )
        }
    }
}

@Composable
private fun NavButton(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.94f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navigationScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(19.dp))
            .background(
                if (selected)
                    Primary.copy(alpha = 0.13f)
                else
                    Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 15.dp,
                vertical = 9.dp
            )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) Primary else TextSecondary,
                modifier = Modifier.size(21.dp)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = title,
                color = if (selected) Primary else TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
