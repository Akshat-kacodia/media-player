package com.example.mediapalyer

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediapalyer.home.HomeViewModel
import com.example.mediapalyer.ui.theme.MediapalyerTheme

class HomeActivity : ComponentActivity() {
    private val homeViewModel : HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeUiModel by homeViewModel.homeUiModel.collectAsState()
            MediapalyerTheme {
                Surface {
                    Column(
                        modifier = Modifier.fillMaxSize().systemBarsPadding(),
                        verticalArrangement =  Arrangement.Top
                    ){
                        Box {
                            CoverImage(
                                modifier = Modifier.fillMaxWidth(),
                                imageRes = homeUiModel.coverImageResourceId
                            )
                            Image(
                                modifier = Modifier.size(40.dp).align(Alignment.Center)
                                    .clickable {
                                        startActivity(
                                            PlaybackActivity.buildIntent(
                                                context = this@HomeActivity,
                                                streamUrl = homeUiModel.streamUrl,
                                                adTagUrl = homeUiModel.adTagUrl
                                            )
                                        )
                                    },
                                contentDescription = "Start playback",
                                painter = painterResource(R.drawable.play)
                            )
                        }
                        Attributions(
                            attribution = homeUiModel.attribution
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Description(
                            description = homeUiModel.description
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun Attributions(
    attribution : String
){
    Text(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        text =attribution,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp
    )
}

@Composable
fun Description(
    description: String,
){
    Text(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        text = description,
        fontSize = 18.sp
    )
}
@Composable
fun CoverImage(
    modifier: Modifier = Modifier,
    imageRes : Int

){
    Image(
        modifier = modifier,
         painter = painterResource(id = imageRes),
        contentDescription = "Tears of Steel",
        contentScale = ContentScale.Crop
    )
}