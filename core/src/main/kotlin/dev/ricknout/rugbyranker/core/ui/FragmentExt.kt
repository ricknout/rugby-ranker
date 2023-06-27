package dev.ricknout.rugbyranker.core.ui

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import dev.ricknout.rugbyranker.core.R

fun Fragment.openDrawer() = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout).openDrawer(GravityCompat.START)
