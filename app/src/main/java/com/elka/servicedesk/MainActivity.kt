package com.elka.servicedesk

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elka.servicedesk.other.Constants
import com.elka.servicedesk.view.dialog.InformDialog
import com.elka.servicedesk.view.dialog.LoadingDialog

class MainActivity : AppCompatActivity() {
  val informDialog by lazy { InformDialog(this) }
  val loadingDialog by lazy { LoadingDialog(this) }

  val sharedPreferences: SharedPreferences by lazy {
    getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(R.style.Theme_ServiceDesk)
    setContentView(R.layout.activity_main)
  }
}