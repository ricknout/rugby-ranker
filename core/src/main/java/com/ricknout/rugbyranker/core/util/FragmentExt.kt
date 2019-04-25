package com.ricknout.rugbyranker.core.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

inline fun Fragment.doIfResumed(crossinline action: () -> Unit) {
    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) action()
}
