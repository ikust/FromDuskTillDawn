package com.raywenderlich.android.rwandroidtutorial

import android.content.Context
import android.support.annotation.StringRes


fun formatTimeString(context: Context, @StringRes format: Int, value: String?): String? = context.getString(
    format, value ?: context.getString(R.string.no_data)
)