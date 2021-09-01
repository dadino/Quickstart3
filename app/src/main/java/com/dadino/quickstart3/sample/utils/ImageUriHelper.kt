package com.dadino.quickstart3.sample.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.dadino.quickstart3.sample.BuildConfig
import java.io.File

object ImageUriHelper {

	fun createImageUri(context: Context): Uri {
		val file = File.createTempFile("tmp_${System.currentTimeMillis()}", ".jpg", context.cacheDir).apply {
			createNewFile()
			deleteOnExit()
		}
		return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
	}
}