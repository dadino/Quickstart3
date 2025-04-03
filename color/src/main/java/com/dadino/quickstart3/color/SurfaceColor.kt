package com.dadino.quickstart3.color

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SurfaceColor(open val muted: Boolean = false) : Parcelable {
  class PRIMARY(override val muted: Boolean = false) : SurfaceColor(muted)
  class SECONDARY(override val muted: Boolean = false) : SurfaceColor(muted)
  class SURFACE(override val muted: Boolean = false) : SurfaceColor(muted)
  class BACKGROUND(override val muted: Boolean = false) : SurfaceColor(muted)
  class ERROR(override val muted: Boolean = false) : SurfaceColor(muted)
  class PRIMARY_SURFACE(override val muted: Boolean = false) : SurfaceColor(muted)
}
