package com.example.shoppingcart.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shoppingcart.R
import com.example.shoppingcart.adapters.HomeViewpagerAdapter
import com.example.shoppingcart.databinding.FragmentHomeBinding
import com.example.shoppingcart.fragments.categories.AccessoryFragment
import com.example.shoppingcart.fragments.categories.ChairFragment
import com.example.shoppingcart.fragments.categories.CupboardFragment
import com.example.shoppingcart.fragments.categories.FurnitureFragment
import com.example.shoppingcart.fragments.categories.MainCategories
import com.example.shoppingcart.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator


class homefragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategories(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewPagerHome.isUserInputEnabled = false
        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter=viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){tab,position ->
            when(position){
                0 -> tab.text = "Main"
                1 ->tab.text = "Chair"
                2 ->tab.text = "Cupboard"
                3 ->tab.text = "Table"
                4 ->tab.text = "Accessory"
                5 ->tab.text = "Furniture"
            }
        }.attach()
    }
}