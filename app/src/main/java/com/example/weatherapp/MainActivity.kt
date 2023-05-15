package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
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
           ){
//        Image(
//            painter = painterResource(id = R.drawable.weather),
//            contentDescription = "",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
        Surface(
            color = if(isSystemInDarkTheme()) Color(0xFF1C1B1F) else  Color(0xFFFFFDD0),
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
            CircularProgressIndicator(modifier = Modifier.size(60.dp))
        }
    }
    if(uiState.error != null){
        ModalBottomSheet(
            onDismissRequest = {weatherVM.clearError()},
            modifier = Modifier
                .fillMaxHeight(0.5f),
            containerColor = if(isSystemInDarkTheme()) Color(0xFF1C1B1F) else  Color(0xFFFFFDD0),
            sheetState = bottomSheetState
        ){
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

