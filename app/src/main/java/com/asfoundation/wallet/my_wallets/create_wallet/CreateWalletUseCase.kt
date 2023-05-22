package com.asfoundation.wallet.my_wallets.create_wallet

import com.appcoins.wallet.gamification.Gamification
import com.wallet.appcoins.feature.support.data.SupportInteractor
import com.appcoins.wallet.feature.walletInfo.data.WalletCreatorInteract
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateWalletUseCase @Inject constructor(
    private val walletCreatorInteract: com.appcoins.wallet.feature.walletInfo.data.WalletCreatorInteract,
    private val getCurrentPromoCodeUseCase: com.appcoins.wallet.feature.promocode.data.use_cases.GetCurrentPromoCodeUseCase,
    private val gamification: Gamification,
    private val supportInteractor: com.wallet.appcoins.feature.support.data.SupportInteractor
) {
  operator fun invoke(name: String?): Completable = walletCreatorInteract.create(name)
    .subscribeOn(Schedulers.io())
    .flatMapCompletable { wallet ->
      getCurrentPromoCodeUseCase()
        .flatMapCompletable { promoCode ->
          walletCreatorInteract.setDefaultWallet(wallet.address).toSingleDefault(promoCode)
            .doOnSuccess {
              gamification.getUserLevel(wallet.address, promoCode.code)
                .map { level ->
                  supportInteractor.registerUser(level, wallet.address)
                }
            }.subscribe()
          Completable.complete()
        }
    }
}