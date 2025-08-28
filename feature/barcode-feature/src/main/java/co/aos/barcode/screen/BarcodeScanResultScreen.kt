package co.aos.barcode.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 바코드 스캔 결과 화면
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScanResultScreen(
    barcode: String,
    onBack: () -> Unit
) {
    BackHandler {
        onBack.invoke()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = "바코드 스캔 결과", style = MaterialTheme.typography.titleMedium)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "Barcode Result : $barcode")
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = {
                        onBack.invoke()
                    }
                ) {
                    Text(text = "Back", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}