package com.elka.servicedesk.other

import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

object CropOptions {
  private fun getDefaultCropOptions() = CropImageOptions(
    guidelines = CropImageView.Guidelines.ON
  )

  private fun getCropOptions(byY: Int, byX: Int): CropImageOptions {
    val options = getDefaultCropOptions()
    options.fixAspectRatio = true
    options.aspectRatioX = byX
    options.aspectRatioY = byY
    return options
  }

  private fun getCropOptions() = getDefaultCropOptions()

  val freeCropImageOptions: CropImageOptions get() = getCropOptions(9, 16)
}