package io.axoniq.training.labs.order.coreapi

data class OrderPlacedEvent(val orderId: String, val cardId: String, val giftCardAmount: Int)
data class ConfirmGiftCardPaymentCommand(val orderId: String, val cardId: String, val giftCardAmount: Int)
data class RejectGiftCardPaymentCommand(val orderId: String, val cardId: String)
