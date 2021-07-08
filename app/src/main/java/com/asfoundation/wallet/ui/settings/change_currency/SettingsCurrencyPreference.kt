package com.asfoundation.wallet.ui.settings.change_currency

//import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.asf.wallet.R

class SettingsCurrencyPreference(context: Context?, attrs: AttributeSet?) :
    Preference(context, attrs) {
  private var selectedCurrency: FiatCurrency? = null
  private var currency: TextView? = null
  private var flag: ImageView? = null
  var preferenceClickListener: View.OnClickListener? = null

  init {
    this.layoutResource = R.layout.preferences_with_active_currency_layout
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    currency = holder.findViewById(R.id.settings_currency_text) as TextView
    flag = holder.findViewById(R.id.settings_flag_ic) as ImageView
    setCurrencyTextView()
    setFlagImageView()
  }

  fun setCurrency(selectedCurrency: FiatCurrency) {
    Log.d("APPC-2472", "SettingsCurrencyPreference: setCurrency: $selectedCurrency")
    this.selectedCurrency = selectedCurrency
  }

  private fun setCurrencyTextView() {
    currency?.text = selectedCurrency?.currency
    Log.d("APPC-2472",
        "SettingsCurrencyPreference: setCurrencyTextView: ${selectedCurrency?.currency}")
  }

  private fun setFlagImageView() {
//    GlideToVectorYou
//        .init()
//        .with(context)
//        .load(Uri.parse(selectedCurrency.flag), flag)

//    flag?.let {
//      GlideApp.with(context)
//          .`as`(PictureDrawable::class.java)
//          .transition(withCrossFade())
//          .listener(SvgSoftwareLayerSetter())
//          .load(Uri.parse(selectedCurrency?.flag))
//          .into(it)
//    }
    //TODO
  }
}