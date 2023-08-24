package com.example.weatherapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.absoluteValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val weatherVM: WeatherVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme

                MyApp(weatherVM)

            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun MyApp(
    weatherVM: WeatherVM
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val keyboard = LocalSoftwareKeyboardController.current
    val uiState = weatherVM.uiState.collectAsState().value
    val scope = rememberCoroutineScope()
    val imageUrl = remember { mutableStateOf(uiState.imageUrl) }
    imageUrl.value = uiState.imageUrl
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.weather),
//            contentDescription = "",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
        Surface(
            color = if (isSystemInDarkTheme()) Color(0xFF1C1B1F) else Color(0xFFFFFDD0),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = uiState.city,
                        onValueChange = { weatherVM.updateCity(it) },
                        keyboardActions = KeyboardActions {
                            keyboard?.hide()
                            if (uiState.city.isNotEmpty())
                                weatherVM.getWeather()
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        label = {
                            Text(
                                text = "City Name",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                    )
                    Button(
                        enabled = uiState.city.isNotEmpty(),
                        onClick = {
                            keyboard?.hide()
                            weatherVM.getWeather()
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = "Check Weather"
                        )
                    }

                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(20.dp)
                ) {
                    Row {
                        AsyncImage(
                            model = imageUrl.value,
                            contentDescription = "icon",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 15.dp)
                        )

                        Text(
                            text = if (uiState.temperature.isNotEmpty()) "${uiState.temperature}°C" else "",
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.offset(y = 9.dp)
                        )
                    }
                    Text(
                        text = uiState.description.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        fontSize = 40.sp,
                        lineHeight = 50.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = if (uiState.place.isNotEmpty())
                            "${uiState.place}, ${uiState.country}"
                        else "",
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
                    PdfPicker { uri ->
                        selectedPdfUri = uri
                    }

                    // Display the selected PDF if available
                    selectedPdfUri?.let { uri ->
                        var selectedFileName by remember { mutableStateOf("") }
                        var selectedFileSize by remember { mutableStateOf("") }
                        Text("Selected PDF: $selectedFileName ($selectedFileSize)")
                        val context = LocalContext.current
                        val contentResolver = context.contentResolver
                        val cursor = contentResolver.query(uri, null, null, null, null)

                        cursor?.use {
                            if (it.moveToFirst()) {
                                val displayNameIndex =
                                    it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                                selectedFileName = it.getString(displayNameIndex)
                                val size = it.getLong(sizeIndex)
                                selectedFileSize = formatFileSize(size)
                            }
                        }

                    }


                }
            }
        }
    }
    if(uiState.isLoading) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        AlternatingSwappingCircles(
            modifier = Modifier.fillMaxSize()
        )
    }
    }


    if (uiState.error != null) {
        ModalBottomSheet(
            onDismissRequest = { weatherVM.clearError() },
            modifier = Modifier
                .fillMaxHeight(0.5f),
            containerColor = if (isSystemInDarkTheme()) Color(0xFF1C1B1F) else Color(0xFFFFFDD0),
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_error_icon
                    ),
                    contentDescription = "status_icon",
                    modifier = Modifier.size(86.dp)
                )
                Spacer(Modifier.height(9.dp))
                Text(
                    text = "Failed",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.Black,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(17.dp))
                Text(
                    text = uiState.error,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color.Black,
                        lineHeight = 23.8.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.size(47.dp))
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                weatherVM.clearError()
                            }
                        }
                    }
                ) {
                    Text("Okay")
                }
            }

        }
//        AlertDialog(
//            onDismissRequest = { }
//        ) {
//            Column(
//                modifier = Modifier
//                    .wrapContentSize()
//                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
//                    .padding(25.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//            Image(
//                painter = painterResource(
//                    id = R.drawable.ic_error_icon
//                ),
//                contentDescription = "status_icon",
//                modifier = Modifier.size(86.dp)
//            )
//            Spacer(Modifier.height(9.dp))
//            Text(
//                text = "Failed",
//                style = TextStyle(
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.W600,
//                    color = Color.Black,
//                    lineHeight = 27.sp,
//                    textAlign = TextAlign.Center
//                ),
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//            Spacer(Modifier.height(17.dp))
//            Text(
//                text = uiState.error,
//                style = TextStyle(
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.W400,
//                    color = Color.Black,
//                    lineHeight = 23.8.sp,
//                    textAlign = TextAlign.Center
//                ),
//                maxLines = 2,
//            )
//            Spacer(modifier = Modifier.size(47.dp))
//            Button(
//                // Note: If you provide logic outside of onDismissRequest to remove the sheet,
//                // you must additionally handle intended state cleanup, if any.
//                onClick = {
//                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
//                        if (!bottomSheetState.isVisible) {
//                            weatherVM.clearError()
//                        }
//                    }
//                }
//            ) {
//                Text("Okay")
//            }
//        }
//        }
    }
}

@Composable
fun PdfPicker(onPdfSelected: (Uri) -> Unit) {
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                if (isFileSizeWithinLimit(context, it, 1)) {
                    onPdfSelected(it)
                    pdfUri = uri
                } else {
                    // Show an error message or handle exceeding file size
                    Toast.makeText(context, "File size exceeds 1MB", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )


    Button(
        onClick = {
            launcher.launch("application/pdf")
        }
    ) {
        Text("Select PDF")
    }

}

private fun formatFileSize(size: Long): String {
    val decimalFormat = DecimalFormat("#.##")
    val kiloByte = 1024
    val megaByte = kiloByte * kiloByte

    return when {
        size < kiloByte -> "${decimalFormat.format(size)} B"
        size < megaByte -> "${decimalFormat.format(size.toFloat() / kiloByte)} KB"
        else -> "${decimalFormat.format(size.toFloat() / megaByte)} MB"
    }
}

fun isFileSizeWithinLimit(context: Context, uri: Uri, maxSizeInMB: Int): Boolean {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            val size = it.getLong(sizeIndex)
            val maxSizeInBytes = maxSizeInMB * 1024 * 1024 // Convert MB to bytes
            return size <= maxSizeInBytes
        }
    }
    return false
}


@Composable
fun AlternatingSwappingCircles(
    modifier: Modifier = Modifier,
    circleSize: Float = 40f,
    circleSpacing: Float = 2f,
    colors: List<Color> = listOf(Color.Cyan, Color.Red),
    animationDuration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val space = (circleSize / 2) + circleSpacing
    val firstCircleOffset by infiniteTransition.animateFloat(
        initialValue = -space,
        targetValue = space,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val secondCircleOffset by infiniteTransition.animateFloat(
        initialValue = space,
        targetValue = -space,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

//    val secondCircleRadius = if (secondCircleOffset == 0f) (circleSize * 0.4f) / 2
//    else ((secondCircleOffset / space) * (circleSize / 2)).absoluteValue

    val progress = (space - secondCircleOffset.absoluteValue) / space
    val minSize = circleSize * 0.4f / 2
    val maxSize = circleSize / 2
    val secondCircleRadius = lerp(maxSize, minSize, progress)


    Canvas(modifier = modifier) {
        val centerY = size.height / 2
        val centerX = size.width / 2

        drawCircle(
            color = colors[0],
            center = Offset(centerX + firstCircleOffset, centerY),
            radius = circleSize / 2
        )

        drawCircle(
            color = colors[1],
            center = Offset(centerX + secondCircleOffset, centerY),
            radius = secondCircleRadius
        )
    }
}





