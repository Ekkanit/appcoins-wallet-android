package com.asfoundation.wallet.main.nav_bar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import cm.aptoide.analytics.AnalyticsManager
import com.asf.wallet.R
import com.asf.wallet.databinding.NavBarFragmentBinding
import com.asfoundation.wallet.base.SingleStateFragment
import com.asfoundation.wallet.main.MainActivity
import com.asfoundation.wallet.rating.RatingAnalytics
import com.asfoundation.wallet.util.createColoredString
import com.asfoundation.wallet.util.setTextFromColored
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_vip_referral.view.*
import javax.inject.Inject

@AndroidEntryPoint
class NavBarFragment : BasePageViewFragment(),
  SingleStateFragment<NavBarState, NavBarSideEffect> {

  private lateinit var navHostFragment: NavHostFragment
  private lateinit var fullHostFragment: NavHostFragment

  private val views by viewBinding(NavBarFragmentBinding::bind)
  private val viewModel: NavBarViewModel by viewModels()

  @Inject
  lateinit var navigator: NavBarFragmentNavigator

  @Inject
  lateinit var navBarAnalytics: NavBarAnalytics

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.nav_bar_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initHostFragments()
    views.bottomNav.setupWithNavController(navHostFragment.navController)
    setupTopUpItem()
    viewModel.collectStateAndEvents(lifecycle, viewLifecycleOwner.lifecycleScope)
    setBottomNavListener()
    setVipCalloutClickListener()
  }

  private fun initHostFragments() {
    navHostFragment = childFragmentManager.findFragmentById(
      R.id.nav_host_container
    ) as NavHostFragment
    fullHostFragment = childFragmentManager.findFragmentById(
      R.id.full_host_container
    ) as NavHostFragment
  }

  override fun onStateChanged(state: NavBarState) {
    setPromotionBadge(state.showPromotionsBadge)
    setVipCallout(state.showVipCallout)
  }

  override fun onSideEffect(sideEffect: NavBarSideEffect) {
    when (sideEffect) {
      NavBarSideEffect.ShowPromotionsTooltip -> showPromotionsOverlay()
      NavBarSideEffect.ShowOnboardingIap -> showOnboardingIap()
      NavBarSideEffect.ShowOnboardingTopApp -> showOnboardingTopApp()
    }
  }

  private fun setPromotionBadge(showPromotionsBadge: Boolean) {
    if (showPromotionsBadge) {
      views.bottomNav.getOrCreateBadge(R.id.promotions_graph).apply {
        backgroundColor = ContextCompat.getColor(requireContext(), R.color.wild_watermelon)
        isVisible = true
      }
    }
  }

  private fun setVipCallout(showVipCallout: Boolean) {
    if (showVipCallout)
      showVipCallout()
    else
      hideVipCallout()
  }

  private fun showPromotionsOverlay() {
    navigator.showPromotionsOverlay(requireActivity() as MainActivity, 1)
  }

  private fun showOnboardingIap() {
    views.fullHostContainer.visibility = View.VISIBLE
    navigator.showOnboardingIapScreen(fullHostFragment.navController)
  }

  private fun showOnboardingTopApp() {
    views.fullHostContainer.visibility = View.VISIBLE
    navigator.showOnboardingTopAppScreen(fullHostFragment.navController)
  }

  @SuppressLint("SetTextI18n", "ResourceType")
  private fun showVipCallout() {
    views.vipPromotionsCallout.vipCalloutLayout.visibility = View.VISIBLE
    val htmlColoredText =
      "${
        getString(R.string.vip_program_promotions_tab_1).createColoredString(
          getString(R.color.only_for_vip_background_color)
        )
      } ${
        getString(R.string.vip_program_promotions_tab_2).createColoredString(
          getString(R.color.whiteNoAlpha)
        )
      }"
    views.vipPromotionsCallout.desciptionTv.setTextFromColored(htmlColoredText)
  }

  private fun hideVipCallout() {
    views.vipPromotionsCallout.vipCalloutLayout.visibility = View.GONE
  }

  private fun setBottomNavListener() {
    views.bottomNav.setOnItemSelectedListener { menuItem ->
      when (menuItem.itemId) {
        R.id.home_graph -> {
        }
        R.id.promotions_graph -> {
          if (views.vipPromotionsCallout.vipCalloutLayout.isVisible) {
            hideVipCallout()
            viewModel.vipPromotionsSeen()
          }
        }
        R.id.my_wallets_graph -> {
        }
        else -> {
        }
      }

      // proceed with the default navigation behaviour:
      onNavDestinationSelected(menuItem, navHostFragment.navController)
      return@setOnItemSelectedListener true
    }
  }

  private fun setVipCalloutClickListener() {
    views.vipPromotionsCallout.vipCalloutLayout.setOnClickListener {
      navBarAnalytics.sendCallOutEvent()
      val menuView = views.bottomNav.getChildAt(0) as BottomNavigationMenuView
      val promoItemView = menuView.getChildAt(1) as BottomNavigationItemView
      promoItemView.performClick()
    }
  }

  private fun setupTopUpItem() {
    val menuView = views.bottomNav.getChildAt(0) as BottomNavigationMenuView
    val topUpItemView = menuView.getChildAt(3) as BottomNavigationItemView
    val topUpCustomView = LayoutInflater.from(requireContext())
      .inflate(R.layout.bottom_nav_top_up_item, menuView, false)
    topUpCustomView.setOnClickListener {
      navigator.navigateToTopUp()
    }
    topUpItemView.addView(topUpCustomView)
  }
}

