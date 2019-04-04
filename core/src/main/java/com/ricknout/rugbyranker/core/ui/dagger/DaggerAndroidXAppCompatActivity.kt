package com.ricknout.rugbyranker.core.ui.dagger

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * AndroidX version of DaggerAppCompatActivity to support contentLayoutId.
 */
abstract class DaggerAndroidXAppCompatActivity @JvmOverloads constructor(@LayoutRes contentLayoutId: Int = 0) : AppCompatActivity(contentLayoutId), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector
}
