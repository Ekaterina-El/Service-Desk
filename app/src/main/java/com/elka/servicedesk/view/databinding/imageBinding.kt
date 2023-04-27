package com.elka.servicedesk.view.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.elka.servicedesk.other.ImageLoader

@BindingAdapter("app:imageUrl")
fun setImageByUrl(image: ImageView, url: String?) {
  ImageLoader.getInstance(image.context).loadTo(url ?: "", image)
}