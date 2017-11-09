package net.skaverat.haxler.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude
class RenderData() {

    var projectName: String = ""
    var frame: String = ""
    var resolutionX: String = ""
    var resolutionY: String = ""
    var useParts: Boolean = false
    var partsNum: Int = 0
    var partsMinX: Float = 0.0f
    var partsMaxX: Float = 0.0f
    var partsMinY: Float = 0.0f
    var partsMaxY: Float = 0.0f
    var samples: String = ""
    var use_stereo: Boolean = false
}