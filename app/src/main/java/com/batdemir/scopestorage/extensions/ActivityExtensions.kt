package com.batdemir.scopestorage.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle

fun Activity.move(
    to: Class<*>,
    isKeepHistory: Boolean = false,
    bundle: Bundle? = null
) {
    val intent = Intent(this, to)
    if (bundle != null) intent.putExtras(bundle)
    this.startActivity(intent)
    if (!isKeepHistory) finish()
}
