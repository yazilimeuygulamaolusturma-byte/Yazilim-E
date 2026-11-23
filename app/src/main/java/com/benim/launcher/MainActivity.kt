package com.benim.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class AppItem(val label: String, val pkg: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apps = loadApps()
        setContent { LauncherScreen(apps) { pkg -> openApp(pkg) } }
    }

    private fun loadApps(): List<AppItem> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .map { AppItem(it.loadLabel(packageManager).toString(), it.activityInfo.packageName) }
            .filter { it.pkg != packageName }
            .sortedBy { it.label.lowercase() }
    }

    private fun openApp(pkg: String) {
        packageManager.getLaunchIntentForPackage(pkg)?.let { startActivity(it) }
    }
}

@Composable
fun LauncherScreen(apps: List<AppItem>, onOpen: (String) -> Unit) {
    var drawer by remember { mutableStateOf(false) }
    BackHandler(enabled = drawer) { drawer = false }

    Surface(color = Color(0xFF0B0B0F), modifier = Modifier.fillMaxSize()) {
        if (drawer) {
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                items(apps) { app ->
                    Text(
                        app.label,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpen(app.pkg) }
                            .padding(vertical = 14.dp)
                    )
                }
            }
        } else {
            val now = Date()
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            val date = SimpleDateFormat("d MMMM, EEEE", Locale.getDefault()).format(now)
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.padding(top = 80.dp)) {
                    Text(time, color = Color.White, fontSize = 72.sp)
                    Text(date, color = Color(0xFFAAAAAA), fontSize = 18.sp)
                }
                TextButton(
                    onClick = { drawer = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Uygulamalar", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
