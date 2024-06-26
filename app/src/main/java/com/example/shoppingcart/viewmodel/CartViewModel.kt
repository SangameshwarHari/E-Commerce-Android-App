package com.example.shoppingcart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingcart.data.CartProduct
import com.example.shoppingcart.firebase.FirebaseCommon
import com.example.shoppingcart.helper.getProductPrice
import com.example.shoppingcart.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SnapshotMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice((cartProduct.product.price)*cartProduct.quantity).toDouble())
        }.toFloat()

    }


    init {

        getCartProducts()
    }

    private fun getCartProducts(){
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection(("user")).document(auth.uid!!).collection("cart")
            .addSnapshotListener{value,error ->
                if(error != null || value ==null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                }else{
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.success(cartProducts)) }
                }
            }
    }


    fun changeQuantity(
        cartProduct:CartProduct,quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if(index !=null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    if(cartProduct.quantity == 1){
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){result,exception ->
            if(exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }

        }

    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result,exception ->
            if(exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }

        }


    }


}