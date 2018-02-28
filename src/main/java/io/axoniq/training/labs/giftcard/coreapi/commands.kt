package io.axoniq.training.labs.giftcard.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class IssueCardCommand(@TargetAggregateIdentifier val cardId: String, val amount: Int, val shopId: String)
data class RedeemCardCommand(@TargetAggregateIdentifier val cardId: String, val transactionId : String, val amount: Int)
data class ReimburseCardCommand(@TargetAggregateIdentifier val cardId: String, val transactionId : String)
