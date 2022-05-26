package com.asfoundation.wallet.ui.iab

import cm.aptoide.analytics.AnalyticsManager
import com.asfoundation.wallet.analytics.AnalyticsSetup
import com.asfoundation.wallet.analytics.TaskTimer
import com.asfoundation.wallet.billing.analytics.BillingAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentMethodsAnalytics @Inject constructor(
  private val analyticsManager: AnalyticsManager,
  private val billingAnalytics: BillingAnalytics,
  private val analyticsSetup: AnalyticsSetup,
  private val taskTimer: TaskTimer
) {

  companion object {
    private const val WALLET = "WALLET"

    const val WALLET_PAYMENT_LOADING_TOTAL = "wallet_payment_loading_total"
    const val WALLET_PAYMENT_LOADING_STEP = "wallet_payment_loading_step"
    const val WALLET_PAYMENT_PROCESSING_TOTAL = "wallet_payment_processing_total"

    const val INTEGRATION_SDK = "sdk"
    const val INTEGRATION_OSP = "osp"

    const val PAYMENT_METHOD_SELECTION = "selection"
    const val PAYMENT_METHOD_CC = "credit_card"
    const val PAYMENT_METHOD_PP = "paypal"
    const val PAYMENT_METHOD_APPC = "appc_c"
    const val PAYMENT_METHOD_LOCAL = "local"
    const val PAYMENT_METHOD_ASK_FRIEND = "ask_friend"

    const val LOADING_STEP_WALLET_INFO = "get_wallet_info"
    const val LOADING_STEP_CONVERT_TO_FIAT = "convert_to_local_fiat"
    const val LOADING_STEP_GET_PAYMENT_METHODS = "get_payment_methods"
    const val LOADING_STEP_GET_EARNING_BONUS = "get_earning_bonus"
    const val LOADING_STEP_GET_PROCESSING_DATA = "processing_data"

    private const val STEP_ID = "step_id"
    private const val INTEGRATION = "integration"
    private const val PAYMENT_METHOD = "payment_method"
    private const val PRESELECTED = "preselected"
    private const val DURATION = "duration"
    private const val SUCCESSFUL = "successful"
  }

  var startedIntegration: String? = null

  fun setGamificationLevel(cachedGamificationLevel: Int) {
    analyticsSetup.setGamificationLevel(cachedGamificationLevel)
  }

  fun sendPurchaseDetailsEvent(appPackage: String, skuId: String?, amount: String, type: String?) {
    billingAnalytics.sendPurchaseDetailsEvent(appPackage, skuId, amount, type)
  }

  fun sendPaymentMethodEvent(
    appPackage: String,
    skuId: String?,
    amount: String,
    paymentId: String,
    type: String?,
    action: String,
    isPreselected: Boolean = false
  ) {
    if (isPreselected) {
      billingAnalytics.sendPreSelectedPaymentMethodEvent(
        appPackage,
        skuId,
        amount,
        paymentId,
        type,
        action
      )
    } else {
      billingAnalytics.sendPaymentMethodEvent(appPackage, skuId, amount, paymentId, type, action)
    }
  }

  fun startTimingForOspTotalEvent() {
    startedIntegration = INTEGRATION_OSP
    taskTimer.start(WALLET_PAYMENT_LOADING_TOTAL)
  }

  fun startTimingForSdkTotalEvent() {
    startedIntegration = INTEGRATION_SDK
    taskTimer.start(WALLET_PAYMENT_LOADING_TOTAL)
  }

  fun startTimingForStepEvent(stepId: String) = taskTimer.start(stepId)

  fun startTimingForPurchaseEvent() = taskTimer.start(WALLET_PAYMENT_PROCESSING_TOTAL)

  fun stopTimingForTotalEvent(paymentMethod: String) {
    val duration = taskTimer.end(WALLET_PAYMENT_LOADING_TOTAL) ?: return
    val integration = startedIntegration ?: return
    analyticsManager.logEvent(
      hashMapOf<String, Any>(
        DURATION to duration,
        PAYMENT_METHOD to paymentMethod,
        INTEGRATION to integration
      ),
      WALLET_PAYMENT_LOADING_TOTAL,
      AnalyticsManager.Action.IMPRESSION,
      WALLET
    )
  }

  fun stopTimingForStepEvent(stepId: String) {
    val duration = taskTimer.end(stepId) ?: return
    analyticsManager.logEvent(
      hashMapOf<String, Any>(DURATION to duration, STEP_ID to stepId),
      WALLET_PAYMENT_LOADING_STEP,
      AnalyticsManager.Action.IMPRESSION,
      WALLET
    )
  }

  fun stopTimingForPurchaseEvent(paymentMethod: String, success: Boolean, isPreselected: Boolean) {
    val duration = taskTimer.end(WALLET_PAYMENT_PROCESSING_TOTAL) ?: return
    val integration = startedIntegration ?: return
    analyticsManager.logEvent(
      hashMapOf<String, Any>(
        DURATION to duration,
        PAYMENT_METHOD to paymentMethod,
        INTEGRATION to integration,
        PRESELECTED to isPreselected,
        SUCCESSFUL to success
      ),
      WALLET_PAYMENT_PROCESSING_TOTAL,
      AnalyticsManager.Action.IMPRESSION,
      WALLET
    )
  }
}
