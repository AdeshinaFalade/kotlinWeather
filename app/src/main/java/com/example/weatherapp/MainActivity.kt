package com.example.weatherapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.graphics.PathParser
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


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
//
//    val resultLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                result ->
//            val data: Intent? = result.data
//            if (result.resultCode == ContactlessReaderResult.RESULT_OK) {
//                data?.let { i ->
//                    Log.e("tag", "value: ${i.getStringExtra("data")}")
//                }
//            }
//            if (result.resultCode == ContactlessReaderResult.RESULT_ERROR) {
//                data?.let { i ->
//                    Log.e("tag", "value: ${i.getStringExtra("message")}")
//                }
//            }
//        }

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

//                            ContactlessSdk.readContactlessCard(this,
//                                resultLauncher,
//                                "86CBCDE3B0A22354853E04521686863D", //pinKey
//                                100.0, //amount
//                                0.0 //cashbackAmount(optional)
//                            )
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = "Check Weather"
                        )
                    }

                    val context = LocalContext.current

                    Button(
                        enabled = true,
                        onClick = {
                            keyboard?.hide()
                            val teamsIntent = Intent(Intent.ACTION_SEND)
                            teamsIntent.setType("text/plain")
                            teamsIntent.setPackage("com.microsoft.teams")
                            teamsIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                "The text you wanted to share"
                            )
                            try {
                                context.startActivity(teamsIntent)
                            } catch (ex: ActivityNotFoundException) {
                                Toast.makeText(context,"Teams have not been installed.",Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            text = "Share"
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
    if (uiState.isLoading) {
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
            shape = AlatXplorerModal,
            modifier = Modifier
                .fillMaxHeight(0.5f),
            containerColor = Color.White /*if (isSystemInDarkTheme()) Color(0xFF1C1B1F) else Color(0xFFFFFDD0)*/,
            sheetState = bottomSheetState,
            dragHandle = {
                Image(
                    painter = painterResource(id = R.drawable.drag_handle),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 9.43.dp)
                )
            }
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
                    text = uiState.error ?: "Error",
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

object AlatXplorerModal : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val pathData =
            """M0 47.4041C0 25.3127 17.9086 7.40404 40 7.40404H127.816C133.263 7.40404 138.701 6.959 144.075 6.07337L170.897 1.65351C181.562 -0.103868 192.441 -0.120661 203.111 1.60379L231.074 6.12309C236.35 6.97568 241.685 7.40404 247.029 7.40404H335C357.091 7.40404 375 25.3127 375 47.404V415H0V47.4041Z"""
        val scaleX = size.width / 375F
        val scaleY = size.height / 415F
        return Outline.Generic(
            PathParser.createPathFromPathData(resize(pathData, scaleX, scaleY)).asComposePath()
        )
    }

    private fun resize(pathData: String, scaleX: Float, scaleY: Float): String {
        val matcher = Pattern.compile("[0-9]+[.]?([0-9]+)?")
            .matcher(pathData) // match the numbers in the path
        val stringBuffer = StringBuffer()
        var count = 0
        while (matcher.find()) {
            val number = matcher.group().toFloat()
            matcher.appendReplacement(
                stringBuffer,
                (if (count % 2 == 0) number * scaleX else number * scaleY).toString()
            ) // replace numbers with scaled numbers
            ++count
        }
        return stringBuffer.toString() // return the scaled path
    }
}

class Parallelogram(private val angle: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                val radian = (90 - angle) * Math.PI / 180
                val xOnOpposite = (size.height * tan(radian)).toFloat()
                moveTo(0f, size.height)
                lineTo(x = xOnOpposite, y = 0f)
                lineTo(x = size.width, y = 0f)
                lineTo(x = size.width - xOnOpposite, y = size.height)
                lineTo(x = xOnOpposite, y = size.height)
            }
        )
    }
}

class Polygon(val sides: Int, val rotation: Float = 0f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = if (size.width > size.height) size.width / 2f else size.height / 2f
                val angle = 2.0 * Math.PI / sides
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = rotation * (Math.PI / 180)
                moveTo(
                    cx + (radius * cos(0.0 + r).toFloat()),
                    cy + (radius * sin(0.0 + r).toFloat())
                )
                for (i in 1 until sides) {
                    lineTo(
                        cx + (radius * cos(angle * i + r).toFloat()),
                        cy + (radius * sin(angle * i + r).toFloat())
                    )
                }
                close()
            })
    }
}



