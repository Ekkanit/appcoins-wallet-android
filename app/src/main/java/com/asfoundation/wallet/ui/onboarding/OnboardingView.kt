package com.asfoundation.wallet.ui.onboarding

import io.reactivex.Observable

interface OnboardingView {

  fun setupUi()

  fun showLoading()

  fun finishOnboarding()

  fun getOkClick(): Observable<Any>

  fun getSkipClick(): Observable<Any>

  fun getCheckboxClick(): Observable<Any>

}