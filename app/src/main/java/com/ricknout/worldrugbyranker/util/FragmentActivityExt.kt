package com.ricknout.worldrugbyranker.util

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

fun FragmentActivity.replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        tag: String,
        @AnimatorRes @AnimRes enterAnimation: Int = 0,
        @AnimatorRes @AnimRes exitAnimation: Int = 0,
        addToBackStack: Boolean = true,
        fragmentManager: FragmentManager = supportFragmentManager
) {
    fragmentManager.beginTransaction().apply {
        setCustomAnimations(enterAnimation, 0, 0, exitAnimation)
        replace(containerViewId, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
        commit()
    }
}
