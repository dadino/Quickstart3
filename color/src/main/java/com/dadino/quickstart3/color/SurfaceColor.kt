package com.dadino.quickstart3.color

import android.os.Parcelable
import com.dadino.quickstart3.color.SurfaceColor.BACKGROUND
import com.dadino.quickstart3.color.SurfaceColor.ERROR
import com.dadino.quickstart3.color.SurfaceColor.PRIMARY
import com.dadino.quickstart3.color.SurfaceColor.PRIMARY_SURFACE
import com.dadino.quickstart3.color.SurfaceColor.SECONDARY
import com.dadino.quickstart3.color.SurfaceColor.SURFACE
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing different surface colors in a UI theme.
 *  Each subclass corresponds to a specific surface role, like primary, secondary, or background.
 *  The `muted` property allows for variations of the color with reduced emphasis.
 *
 *  Subclasses include:
 *   - [PRIMARY]: Represents the primary color of the theme.
 *   - [SECONDARY]: Represents a secondary color that complements the primary color.
 *   - [SURFACE]: Represents the default surface color used for UI elements.
 *   - [BACKGROUND]: Represents the background color of the application.
 *   - [ERROR]: Represents a color used to indicate errors or warnings.
 *   - [PRIMARY_SURFACE]: Represents a surface color derived from the primary color, often used for elements requiring greater emphasis.
 */
@Parcelize
sealed class SurfaceColor(open val muted: Boolean = false) : Parcelable {
  data class PRIMARY(override val muted: Boolean = false) : SurfaceColor(muted)
  data class SECONDARY(override val muted: Boolean = false) : SurfaceColor(muted)
  data class SURFACE(override val muted: Boolean = false) : SurfaceColor(muted)
  data class BACKGROUND(override val muted: Boolean = false) : SurfaceColor(muted)
  data class ERROR(override val muted: Boolean = false) : SurfaceColor(muted)
  data class PRIMARY_SURFACE(override val muted: Boolean = false) : SurfaceColor(muted)
}
