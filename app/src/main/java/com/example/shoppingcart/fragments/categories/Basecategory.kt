package com.example.shoppingcart.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.R
import com.example.shoppingcart.adapters.BestProductsAdapter
import com.example.shoppingcart.databinding.FragmentBaseCategoryBinding
import com.example.shoppingcart.util.ShowBottomNavigationView

open class Basecategory:Fragment(R.layout.fragment_base_category) {
  private lateinit var binding: FragmentBaseCategoryBinding
  protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
  protected val bestProductsAdapter : BestProductsAdapter by lazy{BestProductsAdapter()}

 override fun onCreateView(
  inflater: LayoutInflater,
  container: ViewGroup?,
  savedInstanceState: Bundle?
 ): View {
  binding =  FragmentBaseCategoryBinding.inflate(inflater)
  return binding.root
 }

 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  super.onViewCreated(view, savedInstanceState)

  setupOfferRv()
  setupBestProductRv()

  bestProductsAdapter.onClick = {
   val b=Bundle().apply { putParcelable("product",it)}
   findNavController().navigate(R.id.action_homefragment_to_productDetailsFragment,b)

  }

  offerAdapter.onClick = {
   val b=Bundle().apply { putParcelable("product",it)}
   findNavController().navigate(R.id.action_homefragment_to_productDetailsFragment,b)

  }

  binding.rvOfferProductss.addOnScrollListener(object :RecyclerView.OnScrollListener(){
   override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    if(!recyclerView.canScrollHorizontally(1) && dx != 0){
     onOfferPagingRequest()
    }
   }
  })
  binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrolly, _, _ ->
   if(v.getChildAt(0).bottom <= v.height + scrolly){
    onBestProductsPagingRequest()
   }
  })
 }
 fun showofferLoading(){
  binding.offerProductsProgressbar.visibility=View.VISIBLE
 }
 fun hideOfferLoading(){
  binding.offerProductsProgressbar.visibility=View.GONE
 }

 fun showBestProductsLoading(){
  binding.bestProductProgressBar.visibility=View.VISIBLE
 }

 fun hideBestProductsLoading(){
  binding.bestProductProgressBar.visibility=View.GONE
 }

 open fun onOfferPagingRequest(){

 }
 open fun onBestProductsPagingRequest(){}

 private fun setupBestProductRv() {
  binding.rvBestProducts.apply {
   layoutManager =
    GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL, false)
   adapter = bestProductsAdapter
 }}

 private fun setupOfferRv() {
  binding.rvOfferProductss.apply{
   layoutManager =
    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
   adapter = offerAdapter
  }
 }

 override fun onResume() {
  super.onResume()
  ShowBottomNavigationView()
 }
}