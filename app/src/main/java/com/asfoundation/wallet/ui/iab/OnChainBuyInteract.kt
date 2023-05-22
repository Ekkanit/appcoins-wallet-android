package com.asfoundation.wallet.ui.iab

import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.asfoundation.wallet.entity.TransactionBuilder
import com.wallet.appcoins.feature.support.data.SupportInteractor
import com.asfoundation.wallet.ui.iab.AsfInAppPurchaseInteractor.CurrentPaymentStep
import com.asfoundation.wallet.wallet_blocked.WalletBlockedInteract
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

class OnChainBuyInteract @Inject constructor(
    private val inAppPurchaseInteractor: InAppPurchaseInteractor,
    private val supportInteractor: com.wallet.appcoins.feature.support.data.SupportInteractor,
    private val walletService: WalletService,
    private val walletBlockedInteract: WalletBlockedInteract,
    private val walletVerificationInteractor: com.appcoins.wallet.feature.walletInfo.data.verification.WalletVerificationInteractor) {

  fun showSupport(gamificationLevel: Int): Completable {
    return supportInteractor.showSupport(gamificationLevel)
  }

  fun isWalletBlocked() = walletBlockedInteract.isWalletBlocked()

  fun isWalletVerified() =
      walletService.getAndSignCurrentWalletAddress()
          .flatMap { walletVerificationInteractor.isVerified(it.address, it.signedAddress) }
          .onErrorReturn { true }

  fun getTransactionState(uri: String?): Observable<Payment> =
      inAppPurchaseInteractor.getTransactionState(uri)

  fun send(uri: String?, transactionType: AsfInAppPurchaseInteractor.TransactionType,
           packageName: String, productName: String?, developerPayload: String?,
           isBds: Boolean, transactionBuilder: TransactionBuilder): Completable {
    return inAppPurchaseInteractor.send(uri, transactionType, packageName, productName,
        developerPayload, isBds, transactionBuilder)
  }

  fun parseTransaction(uri: String?, isBds: Boolean): Single<TransactionBuilder> =
      inAppPurchaseInteractor.parseTransaction(uri, isBds)

  fun getCurrentPaymentStep(packageName: String,
                            transactionBuilder: TransactionBuilder): Single<CurrentPaymentStep> =
      inAppPurchaseInteractor.getCurrentPaymentStep(packageName, transactionBuilder)

  fun resume(uri: String?, transactionType: AsfInAppPurchaseInteractor.TransactionType,
             packageName: String, productName: String?, developerPayload: String?,
             isBds: Boolean, type: String, transactionBuilder: TransactionBuilder): Completable {
    return inAppPurchaseInteractor.resume(uri, transactionType, packageName, productName,
        developerPayload, isBds, type, transactionBuilder)
  }

  fun getCompletedPurchase(transaction: Payment, isBds: Boolean): Single<Payment> =
      inAppPurchaseInteractor.getCompletedPurchase(transaction, isBds)

  fun remove(uri: String?): Completable = inAppPurchaseInteractor.remove(uri)

  fun getTopUpChannelSuggestionValues(price: BigDecimal): List<BigDecimal> =
      inAppPurchaseInteractor.getTopUpChannelSuggestionValues(price)

  fun convertToFiat(appcValue: Double, currency: String): Single<com.appcoins.wallet.feature.changecurrency.data.currencies.FiatValue> =
      inAppPurchaseInteractor.convertToFiat(appcValue, currency)

  fun getBillingMessagesMapper(): BillingMessagesMapper =
      inAppPurchaseInteractor.billingMessagesMapper
}