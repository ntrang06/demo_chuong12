package com.example.demo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private val Context.settings by preferencesDataStore(name = "settings")
private val DARK_MODE = booleanPreferencesKey("dark_mode")
private const val DRAFT_FILE = "ghichu.txt"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteApp()
        }
    }
}

@Composable
private fun NoteApp() {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()

    // 1. Đọc chế độ tối. Nếu chưa từng lưu thì mặc định là false.
    val darkModeFlow = remember(context) {
        context.settings.data.map { settings ->
            settings[DARK_MODE] ?: false
        }
    }
    val darkMode by darkModeFlow.collectAsState(initial = false)

    var note by rememberSaveable { mutableStateOf("") }
    var draftLoaded by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }

    // 2. Khi mở app, đọc bản nháp từ thư mục riêng.
    LaunchedEffect(Unit) {
        if (!draftLoaded) {
            try {
                note = withContext(Dispatchers.IO) {
                    val file = File(context.filesDir, DRAFT_FILE)
                    if (file.exists()) file.readText() else ""
                }
            } catch (e: IOException) {
                message = "Không đọc được bản nháp."
            }
            draftLoaded = true
        }
    }

    // 3. SAF: Android cho người dùng chọn tên tệp và nơi lưu bản sao.
    val exportFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            val content = note
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val stream = context.contentResolver.openOutputStream(uri)
                            ?: throw IOException("Không mở được tệp")
                        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
                            writer.write(content)
                        }
                    }
                    message = "Đã xuất bản sao đến nơi bạn chọn."
                } catch (e: IOException) {
                    message = "Xuất tệp chưa thành công."
                }
            }
        }
    }

    // 4. Giao diện dùng nền sáng hoặc tối theo giá trị từ DataStore.
    MaterialTheme(
        colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ghi chú buổi học",
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Chế độ tối")
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                try {
                                    context.settings.edit { settings ->
                                        settings[DARK_MODE] = enabled
                                    }
                                } catch (e: IOException) {
                                    message = "Chưa lưu được chế độ tối."
                                }
                            }
                        }
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        message = ""
                    },
                    enabled = draftLoaded,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nội dung ghi chú") },
                    placeholder = { Text("Nhập điều bạn muốn ghi nhớ…") },
                    minLines = 4
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = draftLoaded,
                    onClick = {
                        val content = note
                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    File(context.filesDir, DRAFT_FILE)
                                        .writeText(content)
                                }
                                message = "Đã lưu bản nháp trong ứng dụng."
                            } catch (e: IOException) {
                                message = "Lưu bản nháp chưa thành công."
                            }
                        }
                    }
                ) {
                    Text("Lưu bản nháp")
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = draftLoaded && note.isNotBlank(),
                    onClick = { exportFile.launch(DRAFT_FILE) }
                ) {
                    Text("Xuất bản sao")
                }

                if (message.isNotEmpty()) {
                    Text(message, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}