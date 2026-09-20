package com.novafiles

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

                        else -> SettingsScreen()
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
            SectionTitle("Hızlı erişim")
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
            AccessCard(accessStatus)
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
        modifier = modifier.clickable {
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
/* FILES SCREEN                                                               */
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

    var searchMode by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var showHidden by remember {
        mutableStateOf(false)
    }

    var selectedFiles by remember {
        mutableStateOf(
            emptySet<String>()
        )
    }

    var dialog by remember {
        mutableStateOf<FileDialogState?>(null)
    }

    fun refresh() {

        hasAccess =
            PermissionManager.hasStorageAccess()

        files =
            if (hasAccess) {
                FileManager.listFiles(
                    currentDirectory,
                    showHidden
                )
            } else {
                emptyList()
            }
    }

    LaunchedEffect(
        currentDirectory,
        showHidden
    ) {
        refresh()
    }

    val displayedFiles =
        if (searchText.isBlank()) {
            files
        } else {
            files.filter {
                it.name.contains(
                    searchText,
                    ignoreCase = true
                )
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (selectedFiles.isNotEmpty()) {

            SelectionHeader(
                count = selectedFiles.size,
                onClose = {
                    selectedFiles = emptySet()
                },
                onDelete = {

                    selectedFiles.forEach { path ->
                        FileManager.delete(
                            File(path)
                        )
                    }

                    selectedFiles = emptySet()
                    refresh()
                }
            )

        } else if (searchMode) {

            SearchHeader(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                onClose = {
                    searchMode = false
                    searchText = ""
                }
            )

        } else {

            FileBrowserHeader(
                directory = currentDirectory,
                showHidden = showHidden,
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
                },
                onSearch = {
                    searchMode = true
                },
                onToggleHidden = {
                    showHidden = !showHidden
                },
                onNew = {
                    dialog =
                        FileDialogState.Create
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
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
                files = displayedFiles,
                selectedFiles = selectedFiles,
                onOpen = { file ->

                    if (file.isDirectory) {
                        currentDirectory = file
                        searchText = ""
                        selectedFiles = emptySet()
                    }

                },
                onSelect = { file ->

                    val path =
                        file.absolutePath

                    selectedFiles =
                        if (selectedFiles.contains(path)) {
                            selectedFiles - path
                        } else {
                            selectedFiles + path
                        }
                },
                onLongPress = { file ->

                    selectedFiles =
                        selectedFiles + file.absolutePath
                },
                onMenu = { file ->
                    dialog =
                        FileDialogState.Menu(file)
                }
            )
        }
    }

    dialog?.let { state ->

        when (state) {

            is FileDialogState.Create -> {

                CreateDialog(
                    onDismiss = {
                        dialog = null
                    },
                    onCreateFolder = {
                        FileManager.createDirectory(
                            currentDirectory,
                            it
                        )
                        dialog = null
                        refresh()
                    },
                    onCreateFile = {
                        FileManager.createFile(
                            currentDirectory,
                            it
                        )
                        dialog = null
                        refresh()
                    }
                )
            }

            is FileDialogState.Menu -> {

                FileMenuDialog(
                    file = state.file,
                    onDismiss = {
                        dialog = null
                    },
                    onRename = {
                        dialog =
                            FileDialogState.Rename(
                                state.file
                            )
                    },
                    onDelete = {

                        FileManager.delete(
                            state.file
                        )

                        dialog = null
                        refresh()
                    },
                    onProperties = {
                        dialog =
                            FileDialogState.Properties(
                                state.file
                            )
                    },
                    onCopy = {
                        dialog =
                            FileDialogState.Copy(
                                state.file
                            )
                    },
                    onMove = {
                        dialog =
                            FileDialogState.Move(
                                state.file
                            )
                    }
                )
            }

            is FileDialogState.Rename -> {

                RenameDialog(
                    file = state.file,
                    onDismiss = {
                        dialog = null
                    },
                    onRename = { name ->

                        FileManager.rename(
                            state.file,
                            name
                        )

                        dialog = null
                        refresh()
                    }
                )
            }

            is FileDialogState.Properties -> {

                PropertiesDialog(
                    file = state.file,
                    onDismiss = {
                        dialog = null
                    }
                )
            }

            is FileDialogState.Copy -> {

                CopyMoveDialog(
                    file = state.file,
                    title = "Kopyala",
                    onDismiss = {
                        dialog = null
                    },
                    onConfirm = {

                        copyRecursively(
                            state.file,
                            currentDirectory
                        )

                        dialog = null
                        refresh()
                    }
                )
            }

            is FileDialogState.Move -> {

                CopyMoveDialog(
                    file = state.file,
                    title = "Taşı",
                    onDismiss = {
                        dialog = null
                    },
                    onConfirm = {

                        moveRecursively(
                            state.file,
                            currentDirectory
                        )

                        dialog = null
                        refresh()
                    }
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* FILE DIALOG STATE                                                          */
/* -------------------------------------------------------------------------- */

private sealed class FileDialogState {

    data object Create :
        FileDialogState()

    data class Menu(
        val file: File
    ) : FileDialogState()

    data class Rename(
        val file: File
    ) : FileDialogState()

    data class Properties(
        val file: File
    ) : FileDialogState()

    data class Copy(
        val file: File
    ) : FileDialogState()

    data class Move(
        val file: File
    ) : FileDialogState()
}

/* -------------------------------------------------------------------------- */
/* FILE HEADER                                                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun FileBrowserHeader(
    directory: File,
    showHidden: Boolean,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onToggleHidden: () -> Unit,
    onNew: () -> Unit
) {

    Column {

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
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = directory.absolutePath,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onSearch
            ) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ara"
                )
            }

            IconButton(
                onClick = onToggleHidden
            ) {

                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Gizli dosyalar"
                )
            }

            IconButton(
                onClick = onNew
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni"
                )
            }
        }

        if (showHidden) {

            Text(
                text = "Gizli dosyalar gösteriliyor",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                modifier = Modifier.padding(
                    start = 8.dp
                )
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/* SEARCH HEADER                                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun SearchHeader(
    value: String,
    onValueChange: (String) -> Unit,
    onClose: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onClose
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Kapat"
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = {
                Text("Dosya ara...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        )
    }
}

/* -------------------------------------------------------------------------- */
/* SELECTION HEADER                                                           */
/* -------------------------------------------------------------------------- */

@Composable
private fun SelectionHeader(
    count: Int,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onClose
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Seçimi kapat"
            )
        }

        Text(
            text = "$count seçildi",
            modifier = Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onDelete
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Sil",
                tint = MaterialTheme.colorScheme.error
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
    selectedFiles: Set<String>,
    onOpen: (File) -> Unit,
    onSelect: (File) -> Unit,
    onLongPress: (File) -> Unit,
    onMenu: (File) -> Unit
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
                selected =
                    selectedFiles.contains(
                        file.absolutePath
                    ),
                onClick = {
                    if (selectedFiles.isNotEmpty()) {
                        onSelect(file)
                    } else {
                        onOpen(file)
                    }
                },
                onLongPress = {
                    onLongPress(file)
                },
                onMenu = {
                    onMenu(file)
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
    selected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onMenu: () -> Unit
) {

    val isDirectory =
        file.isDirectory

    val icon =
        when {
            isDirectory ->
                Icons.Default.Folder

            file.extension.equals(
                "apk",
                ignoreCase = true
            ) ->
                Icons.Default.Archive

            file.extension.lowercase() in
                setOf(
                    "jpg",
                    "jpeg",
                    "png",
                    "webp",
                    "gif"
                ) ->
                Icons.Default.Image

            file.extension.lowercase() in
                setOf(
                    "mp4",
                    "mkv",
                    "avi",
                    "mov",
                    "webm"
                ) ->
                Icons.Default.VideoFile

            file.extension.lowercase() in
                setOf(
                    "zip",
                    "rar",
                    "7z",
                    "tar",
                    "gz"
                ) ->
                Icons.Default.Archive

            else ->
                Icons.Default.Description
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.12f
                    )
                } else {
                    MaterialTheme.colorScheme.surface
                }
        )
    ) {

        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(43.dp)
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
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                        if (isDirectory) {
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

                Text(
                    text =
                        if (isDirectory) {
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

            if (selected) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp)
                )

            } else {

                IconButton(
                    onClick = onMenu
                ) {

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menü",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/* CREATE DIALOG                                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun CreateDialog(
    onDismiss: () -> Unit,
    onCreateFolder: (String) -> Unit,
    onCreateFile: (String) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var folder by remember {
        mutableStateOf(true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (folder) {
                    "Yeni klasör"
                } else {
                    "Yeni dosya"
                }
            )
        },
        text = {

            Column {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (folder) {

                        Button(
                            onClick = {}
                        ) {
                            Text("Klasör")
                        }

                        OutlinedButton(
                            onClick = {
                                folder = false
                            }
                        ) {
                            Text("Dosya")
                        }

                    } else {

                        OutlinedButton(
                            onClick = {
                                folder = true
                            }
                        ) {
                            Text("Klasör")
                        }

                        Button(
                            onClick = {}
                        ) {
                            Text("Dosya")
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text(
                            if (folder) {
                                "Klasör adı"
                            } else {
                                "Dosya adı"
                            }
                        )
                    }
                )
            }
        },
        confirmButton = {

            TextButton(
                onClick = {

                    if (name.isNotBlank()) {

                        if (folder) {
                            onCreateFolder(name)
                        } else {
                            onCreateFile(name)
                        }
                    }
                }
            ) {
                Text("Oluştur")
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}

/* -------------------------------------------------------------------------- */
/* FILE MENU                                                                  */
/* -------------------------------------------------------------------------- */

@Composable
private fun FileMenuDialog(
    file: File,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onProperties: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = file.name,
                maxLines = 1
            )
        },
        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                MenuAction(
                    Icons.Default.Edit,
                    "Yeniden adlandır",
                    onRename
                )

                MenuAction(
                    Icons.Default.ContentCopy,
                    "Kopyala",
                    onCopy
                )

                MenuAction(
                    Icons.Default.DriveFileMove,
                    "Taşı",
                    onMove
                )

                MenuAction(
                    Icons.Default.Info,
                    "Özellikler",
                    onProperties
                )

                MenuAction(
                    Icons.Default.Delete,
                    "Sil",
                    onDelete,
                    MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Kapat")
            }
        }
    )
}

@Composable
private fun MenuAction(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = title,
            color = tint,
            fontSize = 14.sp
        )
    }
}

/* -------------------------------------------------------------------------- */
/* RENAME DIALOG                                                              */
/* -------------------------------------------------------------------------- */

@Composable
private fun RenameDialog(
    file: File,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {

    var name by remember {
        mutableStateOf(file.name)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yeniden adlandır")
        },
        text = {

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {

            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onRename(name)
                    }
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}

/* -------------------------------------------------------------------------- */
/* PROPERTIES                                                                 */
/* -------------------------------------------------------------------------- */

@Composable
private fun PropertiesDialog(
    file: File,
    onDismiss: () -> Unit
) {

    val size =
        if (file.isDirectory) {
            StorageAnalyzer.calculateSize(file)
        } else {
            file.length()
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Özellikler")
        },
        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                PropertyRow(
                    "Ad",
                    file.name
                )

                PropertyRow(
                    "Tür",
                    if (file.isDirectory) {
                        "Klasör"
                    } else {
                        file.extension
                            .uppercase()
                            .ifBlank {
                                "Dosya"
                            }
                    }
                )

                PropertyRow(
                    "Boyut",
                    FileManager.formatSize(size)
                )

                PropertyRow(
                    "Yol",
                    file.absolutePath
                )

                PropertyRow(
                    "Gizli",
                    if (file.isHidden) {
                        "Evet"
                    } else {
                        "Hayır"
                    }
                )
            }
        },
        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Tamam")
            }
        }
    )
}

@Composable
private fun PropertyRow(
    title: String,
    value: String
) {

    Column {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/* -------------------------------------------------------------------------- */
/* COPY / MOVE                                                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun CopyMoveDialog(
    file: File,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {

            Text(
                text = "${file.name}\n\nMevcut klasöre ${title.lowercase()}?"
            )
        },
        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {
                Text(title)
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}

/* -------------------------------------------------------------------------- */
/* COPY HELPERS                                                               */
/* -------------------------------------------------------------------------- */

private fun copyRecursively(
    source: File,
    destinationDirectory: File
): Boolean {

    if (!source.exists()) {
        return false
    }

    return try {

        val destination =
            File(
                destinationDirectory,
                source.name
            )

        if (source.isDirectory) {

            destination.mkdirs()

            source.listFiles()
                ?.forEach { child ->

                    copyRecursively(
                        child,
                        destination
                    )
                }

        } else {

            source.inputStream().use { input ->

                destination.outputStream().use { output ->

                    input.copyTo(output)
                }
            }
        }

        true

    } catch (_: Exception) {

        false
    }
}

private fun moveRecursively(
    source: File,
    destinationDirectory: File
): Boolean {

    if (!source.exists()) {
        return false
    }

    return try {

        val destination =
            File(
                destinationDirectory,
                source.name
            )

        if (source.renameTo(destination)) {
            true
        } else {

            val copied =
                copyRecursively(
                    source,
                    destinationDirectory
                )

            if (copied) {
                FileManager.delete(source)
            }

            copied
        }

    } catch (_: Exception) {

        false
    }
}

/* -------------------------------------------------------------------------- */
/* STORAGE PERMISSION                                                         */
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
            Icons.Default.Archive
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

    val context = LocalContext.current

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
