package io.axoniq.training.labs.giftcard.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.serialization.Revision

@Revision("1")
data class CardIssuedEvent(val cardId: String, val amount: Int, val shopId: String)
data class CardRedeemedEvent(val cardId: String, val transactionId : String, val amount: Int)
data class CardReimbursedEvent(val cardId: String, val transactionId : String, val amount: Int)
