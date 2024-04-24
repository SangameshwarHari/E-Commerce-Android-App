package com.example.shoppingcart.helper

fun Float?.getProductPrice(price:Float):Float{
    if(this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * remainingPricePercentage * price

    return priceAfterOffer
}