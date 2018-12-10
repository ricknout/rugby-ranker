package com.ricknout.rugbyranker.common.util

import androidx.fragment.app.Fragment

inline fun Fragment.doIfVisibleToUser(crossinline action: () -> Unit) {
    if (!userVisibleHint) return
    action()
}
