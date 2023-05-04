package com.appcoins.wallet.feature.changecurrency.data.use_cases

import com.appcoins.wallet.core.arch.data.DataResult
import com.appcoins.wallet.feature.changecurrency.data.ChangeFiatCurrency
import com.appcoins.wallet.feature.changecurrency.data.FiatCurrenciesRepository
import com.appcoins.wallet.feature.changecurrency.data.currencies.LocalCurrencyConversionService
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.map
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class GetChangeFiatCurrencyModelUseCase @Inject constructor(
  private val fiatCurrenciesRepository: FiatCurrenciesRepository,
  private val conversionService: LocalCurrencyConversionService
) {

  suspend operator fun invoke(): DataResult<ChangeFiatCurrency> {
    return runBlocking {
      binding {
        val selectedCurrency = async { fiatCurrenciesRepository.getSelectedCurrency().bind() }
        val fiatCurrencyList = async { fiatCurrenciesRepository.getCurrenciesList().bind() }
        async {
          val selected = selectedCurrency.await()
          val list = fiatCurrencyList.await()
          if (selected.isEmpty()) {
            val localCurrency = conversionService.localCurrency.await()
            ChangeFiatCurrency(list, localCurrency.currency)
          } else {
            ChangeFiatCurrency(list, selected)
          }
        }
      }.map { it.await() }
    }
  }
}