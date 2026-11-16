package com.example.mediapalyer.home

import com.example.mediapalyer.R

data class HomeUiModel (
    val coverImageResourceId : Int= R.drawable.img,
    val streamUrl : String = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd",
    val attribution : String ="(CC) Blender Foundation | mango.blender.org",
    val description : String = "The film's premise is about a group of warriors and scientists, who\n" +
            "gathered at the \"Oude Kerk\" in Amsterdam to stage a crucial event\n" +
            "from the past, in a desperate attempt to rescue the world from\n" +
            "destructive robots.",
    val adTagUrl: String? = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator="
)