package io.axoniq.training.labs.giftcard.coreapi

import io.axoniq.training.labs.giftcard.query.CardSummary

data class FindCardSummariesQuery(val offset: Int, val limit: Int)
data class FindCardSummariesResponse(val data: List<CardSummary>)

class CountCardSummariesQuery {
    override fun toString(): String = "CountCardSummariesQuery"
}

data class CountCardSummariesResponse(val count: Int)
