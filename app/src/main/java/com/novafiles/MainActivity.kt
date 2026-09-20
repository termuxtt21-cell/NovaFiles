package com.novafiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novafiles.ui.theme.NovaFilesTheme
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NovaFilesTheme {
                NovaFilesApp()
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* APP                                                                        */
/* -------------------------------------------------------------------------- */

@Composable
private fun NovaFilesApp() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "navigation"
                ) { page ->

                    when (page) {

                        0 -> HomeScreen(
                            onOpenFiles = {
                                selectedTab = 1
                            }
                        )

                        1 -> FilesScreen()

                        2 -> ToolsScreen()

                        3 -> SettingsScreen()
                    }
                }
            }

            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                }
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* HOME                                                                       */
/* -------------------------------------------------------------------------- */

@Composable
private fun HomeScreen(
    onOpenFiles: () -> Unit
) {

    var storageInfo by remember {
        mutableStateOf(
            StorageManager.getInternalStorageInfo()
        )
    }

    var accessStatus by remember {
        mutableStateOf(
            AccessManager.getStatus()
        )
    }

    LaunchedEffect(Unit) {

        storageInfo =
            StorageManager.getInternalStorageInfo()

        accessStatus =
            AccessManager.getStatus()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }

        item {
            Header(
                title = "NovaFiles",
                subtitle = "Dosyalarını kontrol altında tut."
            )
        }

        item {
            StorageCard(
                total = StorageManager.formatBytes(
                    storageInfo.totalBytes
                ),
                used = StorageManager.formatBytes(
                    storageInfo.usedBytes
                ),
                available = StorageManager.formatBytes(
                    storageInfo.availableBytes
                ),
                usage = storageInfo.usagePercent
            )
        }

        item {
            SectionTitle(
                title = "Hızlı erişim"
            )
        }

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Folder,
                    title = "Dosyalar",
                    subtitle = "Depolamayı aç",
                    onClick = onOpenFiles
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Storage,
                    title = "Depolama",
                    subtitle = "Kullanımı incele",
                    onClick = {}
                )
            }
        }

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Terminal,
                    title = "Terminal",
                    subtitle = "Komut satırı",
                    onClick = {}
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Build,
                    title = "Araçlar",
                    subtitle = "Nova araçları",
                    onClick = {}
                )
            }
        }

        item {
            AccessCard(
                status = accessStatus
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* HEADER                                                                     */
/* -------------------------------------------------------------------------- */

@Composable
private fun Header(
    title: String,
    subtitle: String
) {

    Column {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

/* -------------------------------------------------------------------------- */
/* STORAGE CARD                                                               */
/* -------------------------------------------------------------------------- */

@Composable
private fun StorageCard(
    total: String,
    used: String,
    available: String,
    usage: Float
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.25f
                                    ),
                                    MaterialTheme.colorScheme.secondary.copy(
                                        alpha = 0.20f
                                    )
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Dahili depolama",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "$available boş",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = total,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            usage.coerceIn(0f, 1f)
                        )
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Kullanılan $used",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Text(
                    text = "${(usage * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* QUICK ACTION                                                               */
/* -------------------------------------------------------------------------- */

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.10f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* ACCESS CARD                                                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun AccessCard(
    status: String
) {

    val active =
        status == "Root aktif" ||
        status == "Shizuku aktif" ||
        status == "Normal erişim"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.12f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Sistem erişimi",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = status,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* FILES                                                                      */
/* -------------------------------------------------------------------------- */

@Composable
private fun FilesScreen() {

    val context = LocalContext.current

    var currentDirectory by remember {
        mutableStateOf(
            FileManager.getInternalStorage()
        )
    }

    var files by remember {
        mutableStateOf(
            emptyList<File>()
        )
    }

    var hasAccess by remember {
        mutableStateOf(
            PermissionManager.hasStorageAccess()
        )
    }

    LaunchedEffect(currentDirectory) {

        hasAccess =
            PermissionManager.hasStorageAccess()

        files =
            if (hasAccess) {
                FileManager.listFiles(
                    currentDirectory
                )
            } else {
                emptyList()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        FileBrowserHeader(
            directory = currentDirectory,
            onBack = {

                val parent =
                    currentDirectory.parentFile

                if (
                    parent != null &&
                    parent.exists() &&
                    parent != currentDirectory
                ) {
                    currentDirectory = parent
                }
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (!hasAccess) {

            StoragePermissionCard(
                onOpenSettings = {

                    PermissionManager
                        .openStorageAccessSettings(
                            context
                        )
                }
            )

        } else {

            FileList(
                files = files,
                onOpen = { file ->

                    if (file.isDirectory) {
                        currentDirectory = file
                    }
                }
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* FILE HEADER                                                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun FileBrowserHeader(
    directory: File,
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (
            directory.parentFile != null &&
            directory.parentFile != directory
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri"
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = directory.name.ifEmpty {
                    "Dahili depolama"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = directory.absolutePath,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }

        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menü"
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* FILE LIST                                                                  */
/* -------------------------------------------------------------------------- */

@Composable
private fun FileList(
    files: List<File>,
    onOpen: (File) -> Unit
) {

    if (files.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Bu klasör boş",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        items(
            items = files,
            key = {
                it.absolutePath
            }
        ) { file ->

            RealFileRow(
                file = file,
                onClick = {
                    onOpen(file)
                }
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* FILE ROW                                                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun RealFileRow(
    file: File,
    onClick: () -> Unit
) {

    val isDirectory = file.isDirectory

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (isDirectory) {
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.10f
                            )
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = if (isDirectory) {
                        Icons.Default.Folder
                    } else {
                        Icons.Default.Description
                    },
                    contentDescription = null,
                    tint = if (isDirectory) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = if (isDirectory) {
                        "Klasör"
                    } else {
                        FileManager.formatSize(
                            file.length()
                        )
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* PERMISSION CARD                                                            */
/* -------------------------------------------------------------------------- */

@Composable
private fun StoragePermissionCard(
    onOpenSettings: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onOpenSettings()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Depolama erişimi gerekli",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Gerçek dosyaları gösterebilmek için NovaFiles'a tüm dosyalara erişim izni ver.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Ayarları aç",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* TOOLS                                                                      */
/* -------------------------------------------------------------------------- */

@Composable
private fun ToolsScreen() {

    val tools = listOf(
        ToolItem(
            "APK Analyzer",
            "APK bilgilerini incele",
            Icons.Default.Build
        ),
        ToolItem(
            "Arşiv Yöneticisi",
            "ZIP / TAR işlemleri",
            Icons.Default.Folder
        ),
        ToolItem(
            "Metin Editörü",
            "Kod ve metin düzenle",
            Icons.Default.Description
        ),
        ToolItem(
            "Hex Editör",
            "Binary dosyaları incele",
            Icons.Default.Storage
        ),
        ToolItem(
            "Terminal",
            "Komut satırı",
            Icons.Default.Terminal
        ),
        ToolItem(
            "Depolama Analizi",
            "Alan kullanımını analiz et",
            Icons.Default.Storage
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Header(
                title = "Araçlar",
                subtitle = "NovaFiles araç kutusu"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        items(tools) { tool ->
            ToolCard(tool)
        }

        item {
            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }
    }
}

private data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
private fun ToolCard(
    item: ToolItem
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.10f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = item.subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* SETTINGS                                                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun SettingsScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Header(
                title = "Ayarlar",
                subtitle = "NovaFiles yapılandırması"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        item {

            SettingCard(
                icon = Icons.Default.Security,
                title = "Shizuku",
                subtitle = ShizukuManager.getStatus()
            )
        }

        item {

            SettingCard(
                icon = Icons.Default.Storage,
                title = "Depolama erişimi",
                subtitle =
                    PermissionManager
                        .getStorageAccessStatus()
            )
        }

        item {

            SettingCard(
                icon = Icons.Default.Security,
                title = "Root",
                subtitle = RootManager.getStatus()
            )
        }

        item {

            SettingCard(
                icon = Icons.Default.Build,
                title = "Güncellemeler",
                subtitle = "Yakında kullanılabilir"
            )
        }

        item {

            SettingCard(
                icon = Icons.Default.Settings,
                title = "Arayüz",
                subtitle = "Tema ve görünüm seçenekleri"
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* SETTING CARD                                                               */
/* -------------------------------------------------------------------------- */

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* SECTION TITLE                                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
    )
}

/* -------------------------------------------------------------------------- */
/* BOTTOM NAVIGATION                                                          */
/* -------------------------------------------------------------------------- */

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    val items = listOf(
        NavigationItem(
            "Ana Sayfa",
            Icons.Default.Home
        ),
        NavigationItem(
            "Dosyalar",
            Icons.Default.Folder
        ),
        NavigationItem(
            "Araçlar",
            Icons.Default.Build
        ),
        NavigationItem(
            "Ayarlar",
            Icons.Default.Settings
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            items.forEachIndexed { index, item ->

                NavigationButton(
                    item = item,
                    selected = selectedTab == index,
                    onClick = {
                        onTabSelected(index)
                    }
                )
            }
        }
    }
}

private data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

@Composable
private fun NavigationButton(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background =
        if (selected) {
            MaterialTheme.colorScheme.primary.copy(
                alpha = 0.12f
            )
        } else {
            Color.Transparent
        }

    val tint =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onClick()
            }
            .background(background)
            .padding(
                horizontal = 15.dp,
                vertical = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = item.title,
            color = tint,
            fontSize = 10.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}
