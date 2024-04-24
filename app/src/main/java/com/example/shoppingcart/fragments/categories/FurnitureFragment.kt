package com.example.shoppingcart.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.shoppingcart.data.Category
import com.example.shoppingcart.util.Resource
import com.example.shoppingcart.viewmodel.CategoryViewModel
import com.example.shoppingcart.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FurnitureFragment:Basecategory() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Furniture)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showofferLoading()

                    }
                    is Resource.success ->{
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()

                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG)
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenResumed {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showBestProductsLoading()

                    }
                    is Resource.success ->{
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG)
                        hideBestProductsLoading()

                    }
                    else -> Unit
                }
            }
        }
    }
}
