package com.asfoundation.wallet.onboarding.use_cases

import com.asfoundation.wallet.repository.PreferencesRepositoryType
import javax.inject.Inject

class SetOnboardingFromIapStateUseCase @Inject constructor(
  private val preferencesRepositoryType: PreferencesRepositoryType
) {

  operator fun invoke(state: Boolean) {
    preferencesRepositoryType.setOnboardingFromIapState(state)
  }
}