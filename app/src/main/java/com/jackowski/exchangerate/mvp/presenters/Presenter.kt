package com.jackowski.exchangerate.mvp.presenters

import android.arch.lifecycle.ViewModel
import com.jackowski.exchangerate.mvp.views.View

abstract class Presenter<T : View> : ViewModel() {
    var view: T? = null

    open fun onAttach(view: T) {
        this.view = view
    }

    open fun onDetach() {
        this.view = null
    }
}