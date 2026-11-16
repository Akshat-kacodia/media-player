package com.example.mediapalyer.playback

import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.TimeBar
import com.example.mediapalyer.CoverImage
import com.example.mediapalyer.R
import com.example.mediapalyer.Utils

@Composable
fun VideoPlayer(
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier,

){
    val playerUiModel by playerViewModel.playerUiModel.collectAsState()
    Box{
        AndroidExternalSurface(
            modifier = modifier
                .aspectRatio(playerUiModel.videoAspectRatio)
                .clickable { playerViewModel.showPlayerControls() }

        ) {//the oninit callback lambda function
            onSurface { surface, _, _ ->
                //tell exoPlayer the surface is ready , send this surface to exoPlayer
                playerViewModel.handleAction(AttachSurface(surface))
                surface.onDestroyed {
                    //tell exoPlayer the surface is destroyed
                    playerViewModel.handleAction(DetachSurface)
                }
            }
        }
        VideoOverlay(
            modifier = modifier.matchParentSize(),
            playerViewModel = playerViewModel,
            onCollapseClicked = {
                playerViewModel.exitFullScreen()
            },
            onExpandClicked = {
                playerViewModel.enterFullScreen()
            },
            onControlsClicked = {
                playerViewModel.hidePlayerControls()
            },
            onAction = {
                playerViewModel.handleAction(action = it)
            },
            onSettingsClicked = {
                playerViewModel.openTrackSelector( )
            }
        )
    }


}

@Composable
fun VideoOverlay(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    onCollapseClicked : () -> Unit,
    onExpandClicked : () -> Unit,
    onControlsClicked : () -> Unit,
    onAction: (Action) -> Unit,
    onSettingsClicked: () -> Unit
){
    val playerUiModel by playerViewModel.playerUiModel.collectAsState()
    Box(
        modifier = modifier
    ){
        playerUiModel.placeHolderImageResourceId?.let{
            CoverImage(imageRes = it, modifier = Modifier.matchParentSize())
        }
        if(playerUiModel.playerControlsVisible){
            PlaybackControls(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onControlsClicked),
                isFullScreen = playerUiModel.isFullScreen,
                onCollapseClicked = onCollapseClicked,
                onExpandClicked = onExpandClicked,
                playerUiModel = playerUiModel,
                onAction = onAction,
                onSettingsClicked = onSettingsClicked
            )
        }
        Subtitles(
            modifier = Modifier.matchParentSize(),
            cues = playerUiModel.currentSubtitles
        )

    }

}

@OptIn(UnstableApi::class)
@Composable
fun Subtitles(
    modifier: Modifier = Modifier,
    cues: List<Cue>,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SubtitleView(context)
        },
        update = { subtitleView ->
            subtitleView.setCues(cues)
        }
    )
}


@Composable
fun PlaybackControls(
    modifier: Modifier = Modifier,
    playerUiModel: PlayerUiModel,
    isFullScreen: Boolean = false,
    onCollapseClicked : () -> Unit,
    onExpandClicked : () -> Unit,
    onSettingsClicked : () -> Unit ,
    onAction : (Action) -> Unit
){



    Box(
        modifier = modifier.background(Color(0xA0000000))
    ){
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            if(playerUiModel.adUiModel?.currentAdBreak == null){
                PlaybackButton(resourceId = R.drawable.settings, description = "Open track selector" ){
                    onSettingsClicked()
                }
            }

            if (isFullScreen){
                PlaybackButton(resourceId = R.drawable.collapse, description = "Exit full screen"){
                    onCollapseClicked()
                }
            }else{
                PlaybackButton(resourceId = R.drawable.expand, description = "Enter full screen"){
                    onExpandClicked()
                }
            }
        }
        if(playerUiModel.adUiModel?.currentAdBreak == null) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (playerUiModel.playbackState.isReady()) {
                    PlaybackButton(resourceId = R.drawable.startover, description = "Start Over") {
                        onAction(Seek(0))
                    }
                    PlaybackButton(resourceId = R.drawable.rewind, description = "Rewind") {
                        onAction(Rewind(15_000))
                    }

                }
                when (playerUiModel.playbackState) {
                    PlaybackState.IDLE -> {
                        PlaybackButton(resourceId = R.drawable.play, description = "Start") {
                            onAction(Start())
                        }
                    }

                    PlaybackState.PLAYING -> {
                        PlaybackButton(resourceId = R.drawable.pause, description = "Pause") {
                            onAction(Pause)
                        }
                    }

                    PlaybackState.PAUSED -> {
                        PlaybackButton(resourceId = R.drawable.play, description = "Play") {
                            onAction(Resume)
                        }
                    }

                    PlaybackState.BUFFERING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White
                        )
                    }

                    PlaybackState.COMPLETED -> {
                        PlaybackButton(resourceId = R.drawable.replay, description = "Replay") {
                            onAction(Start(0))
                        }
                    }

                    PlaybackState.ERROR -> {
                        PlaybackButton(resourceId = R.drawable.error, description = "Error")
                        PlaybackButton(resourceId = R.drawable.replay, description = "Retry") {

                            onAction(Start(playerUiModel.timelineUiModel?.currentPositionInMs))
                        }
                    }
                }
                if (playerUiModel.playbackState.isReady()) {
                    PlaybackButton(
                        resourceId = R.drawable.fastforward,
                        description = "Fast Forward"
                    ) {
                        onAction(FastForward(15_000))
                    }
                }

            }
        }
        playerUiModel.timelineUiModel?.let{timeline ->
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                PlaybackPositon(
                    contentPositionInMs = timeline.currentPositionInMs,
                    contentDurationInMs = timeline.durationInMs,
                    adBreak = playerUiModel.adUiModel?.currentAdBreak
                )
                if(playerUiModel.adUiModel?.currentAdBreak == null) {
                    Timebar(
                        positionInMs = timeline.currentPositionInMs,
                        durationInMs = timeline.durationInMs,
                        bufferedPositionInMs = timeline.bufferedPositionInMs,
                        adGroups = playerUiModel.adUiModel?.adGroups ?: emptyList()
                    ) {
                        onAction(Seek(it.toLong()))
                    }
                }
            }
        }


    }

}

private enum class TrackState{
    VIDEO , AUDIO , SUBTITLE , LIST
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelector(
    trackSelectionUiModel: TrackSelectionUiModel,
    onVideoTrackSelected: (VideoTrack) -> Unit,
    onAudioTrackSelected: (AudioTrack) -> Unit,
    onSubtitleTrackSelected: (SubtitleTrack) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentState by remember { mutableStateOf(TrackState.LIST) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        when (currentState) {
            TrackState.LIST -> {
                Column {
                    Text(
                        text = "Video Tracks",
                        modifier = Modifier
                            .clickable {
                                currentState = TrackState.VIDEO
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Text(
                        text = "Audio Tracks",
                        modifier = Modifier
                            .clickable {
                                currentState = TrackState.AUDIO
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Text(
                        text = "Subtitle Tracks",
                        modifier = Modifier
                            .clickable {
                                currentState = TrackState.SUBTITLE
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            TrackState.VIDEO -> {
                Column {
                    trackSelectionUiModel.videoTracks.forEach { videoTrack ->
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onVideoTrackSelected(videoTrack)
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = videoTrack.displayName,
                            color = if (videoTrack === trackSelectionUiModel.selectedVideoTrack) {
                                Color.Yellow
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            }
            TrackState.AUDIO -> {
                Column {
                    trackSelectionUiModel.audioTracks.forEach { audioTrack ->
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onAudioTrackSelected(audioTrack)
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = audioTrack.displayName,
                            color = if (audioTrack === trackSelectionUiModel.selectedAudioTrack) {
                                Color.Yellow
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            }
            TrackState.SUBTITLE -> {
                Column {
                    trackSelectionUiModel.subtitleTracks.forEach { subtitleTrack ->
                        Text(
                            modifier = Modifier
                                .clickable {
                                    onSubtitleTrackSelected(subtitleTrack)
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = subtitleTrack.displayName,
                            color = if (subtitleTrack === trackSelectionUiModel.selectedSubtitleTrack) {
                                Color.Yellow
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybackPositon(
    contentPositionInMs : Long,
    contentDurationInMs : Long,
    adBreak: AdBreak?
){
    if(adBreak == null){
        val positionString = Utils.formatMsToString(contentPositionInMs)
        val durationString = Utils.formatMsToString(contentDurationInMs)
        Text(
            text = "$positionString / $durationString",
            fontSize = 10.sp
        )
    }else{
        val positionString = Utils.formatMsToString(adBreak.playbackPositionInMs)
        val durationString = Utils.formatMsToString(adBreak.durationInMs)
        Text(
            text = " Ad ${adBreak.adIndexInGroup} of ${adBreak.totalAdsInGroup} ($positionString / $durationString)",
            fontSize = 10.sp
        )

    }

}

@OptIn(UnstableApi::class)
@Composable
fun Timebar(
    positionInMs : Long ,
    durationInMs : Long,
    bufferedPositionInMs : Long,
    adGroups : List<AdGroup>,
    onSeek : (Float) -> Unit,

){
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { context ->
            DefaultTimeBar(context).apply {
                // setScrubberColor() is used to change the color of the scrubber (the draggable handle) in an ExoPlayer PlayerView's seek bar.The scrubber is the small circular handle that you drag to seek forward or backward in a video.
                setScrubberColor(0xFFFF0000.toInt())
                setPlayedColor(0xCCFF0000.toInt())
                setBufferedColor(0x77FF0000)
                setAdMarkerColor(0xFFFFFF00.toInt())
                setPlayedAdMarkerColor(0xFF888888.toInt())
            }
        },
        update = {timebar ->
            with(timebar){
                addListener(object : TimeBar.OnScrubListener{
                    override fun onScrubStart(timeBar: TimeBar, position: Long) {}
                    override fun onScrubMove(timeBar: TimeBar, position: Long) {}
                    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                        onSeek(position.toFloat())
                    }

                })
                setPosition(positionInMs)
                setDuration(durationInMs)
                setBufferedPosition(bufferedPositionInMs)
                setAdGroupTimesMs(
                    adGroups.map { it.positionInMs }.toLongArray(),
                    adGroups.map { it.hasPlayed }.toBooleanArray(),
                    adGroups.size
                )
            }
        }
    )
}

@Composable
fun PlaybackButton(
    @DrawableRes resourceId: Int,
    description: String,
    onClick : () -> Unit = {}
){
    Image(
        modifier = Modifier
            .size(32.dp)
            .clickable(onClick = onClick),
        painter = painterResource(id = resourceId),
        contentDescription =description
    )
}


