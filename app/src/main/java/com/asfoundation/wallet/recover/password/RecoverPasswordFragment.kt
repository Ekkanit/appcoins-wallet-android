package com.asfoundation.wallet.recover.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asf.wallet.R
import com.asf.wallet.databinding.RecoverPasswordFragmentBinding
import com.asfoundation.wallet.base.Async
import com.asfoundation.wallet.base.SingleStateFragment
import com.asfoundation.wallet.onboarding.OnboardingFragment
import com.asfoundation.wallet.recover.entry.RecoverEntryFragment
import com.asfoundation.wallet.recover.result.FailedPasswordRecover
import com.asfoundation.wallet.recover.result.RecoverPasswordResult
import com.asfoundation.wallet.recover.result.SuccessfulPasswordRecover
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecoverPasswordFragment : BasePageViewFragment(),
  SingleStateFragment<RecoverPasswordState, RecoverPasswordSideEffect> {

  @Inject
  lateinit var navigator: RecoverPasswordNavigator

  private val viewModel: RecoverPasswordViewModel by viewModels()
  private val views by viewBinding(RecoverPasswordFragmentBinding::bind)

  override fun onResume() {
    super.onResume()
    handleFragmentResult()
  }

  override fun onCreateView(
    inflater: LayoutInflater, @Nullable container: ViewGroup?,
    @Nullable savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.recover_password_fragment, container, false)
  }

  override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (!requireActivity().intent.getBooleanExtra(RecoverEntryFragment.ONBOARDING_LAYOUT, false)) {
      views.recoverWalletBackButton.visibility = View.GONE
    }
    views.recoverWalletBackButton.setOnClickListener {
      navigator.navigateBack()
    }
    views.recoverWalletPasswordButton.setOnClickListener {
      viewModel.handleRecoverPasswordClick(views.recoverPasswordInfo.recoverPasswordInput.getText())
    }
    viewModel.collectStateAndEvents(lifecycle, viewLifecycleOwner.lifecycleScope)
  }

  override fun onStateChanged(state: RecoverPasswordState) {
    handleRecoverPasswordState(state.recoverResultAsync)
  }

  override fun onSideEffect(sideEffect: RecoverPasswordSideEffect) = Unit

  private fun handleRecoverPasswordState(asyncRecoverResult: Async<RecoverPasswordResult>) {
    when (asyncRecoverResult) {
      is Async.Uninitialized,
      is Async.Loading -> {
        showWalletContent()
      }
      is Async.Fail -> {
        handleErrorState(FailedPasswordRecover.GenericError(asyncRecoverResult.error.throwable))
      }
      is Async.Success -> {
        handleSuccessState(asyncRecoverResult())
      }
    }
  }

  private fun showWalletContent() {
    views.recoverPasswordInfo.recoverWalletBalance.text =
      requireArguments().getString(WALLET_BALANCE_KEY)
    views.recoverPasswordInfo.recoverWalletAddress.text =
      requireArguments().getString(WALLET_ADDRESS_KEY)
  }

  private fun handleSuccessState(recoverResult: RecoverPasswordResult) {
    when (recoverResult) {
      is SuccessfulPasswordRecover -> {
        navigator.navigateToCreateWalletDialog()
      }
      else -> handleErrorState(recoverResult)
    }
  }

  private fun handleErrorState(recoverResult: RecoverPasswordResult) {
    when (recoverResult) {
      is FailedPasswordRecover.InvalidPassword -> {
        views.recoverPasswordInfo.recoverPasswordInput.setError(getString(R.string.import_wallet_wrong_password_body))
      }
      else -> return
    }
  }

  private fun handleFragmentResult() {
    parentFragmentManager.setFragmentResultListener(
      OnboardingFragment.ONBOARDING_FINISHED_KEY,
      this
    ) { _, _ ->
      navigator.navigateToMainActivity(fromSupportNotification = false)
    }
  }

  companion object {
    const val KEYSTORE_KEY = "keystore"
    const val WALLET_BALANCE_KEY = "wallet_balance"
    const val WALLET_ADDRESS_KEY = "wallet_address"
  }
}