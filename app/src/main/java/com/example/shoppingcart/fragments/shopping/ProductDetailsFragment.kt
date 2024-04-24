package com.example.shoppingcart.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingcart.R
import com.example.shoppingcart.activities.ShoppingActivity
import com.example.shoppingcart.adapters.ColorsAdapter
import com.example.shoppingcart.adapters.SizesAdapter
import com.example.shoppingcart.adapters.ViewPager2Images
import com.example.shoppingcart.data.CartProduct
import com.example.shoppingcart.databinding.FragmentProductDetailsBinding
import com.example.shoppingcart.util.Resource
import com.example.shoppingcart.util.hideBottomNavigationView
import com.example.shoppingcart.viewmodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor:Int? = null
    private var selectedSize:String? = null
    private val viewModel by viewModels<DetailsViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      hideBottomNavigationView()
        binding= FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.imageclose.setOnClickListener {
            findNavController().navigateUp()
        }
        sizesAdapter.OnItemClick = {
            selectedSize = it
        }
        colorsAdapter.OnItemClick = {
            selectedColor = it
        }

        binding.buttonaddtocart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor,selectedSize))
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonaddtocart.startAnimation()
                    }
                    is Resource.success ->{
                        binding.buttonaddtocart.revertAnimation()
                        binding.buttonaddtocart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error ->{
                        binding.buttonaddtocart.stopAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProductname.text = product.name
            tvProductprice.text = "$ ${product.price}"
            tvProductdescriptionn.text = product.description


            if(product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE

            if(product.sizes.isNullOrEmpty())
                tvProductSizes.visibility = View.INVISIBLE

        }
        viewPagerAdapter.differ.submitList((product.images))
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }


    }

    private fun setupViewpager() {
        binding.apply {
            viewpagerProductImage.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColorss.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizess.apply {
            adapter = sizesAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}