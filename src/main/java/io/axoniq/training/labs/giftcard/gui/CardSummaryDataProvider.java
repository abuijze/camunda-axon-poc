package io.axoniq.training.labs.giftcard.gui;

import io.axoniq.training.labs.giftcard.coreapi.CountCardSummariesQuery;
import io.axoniq.training.labs.giftcard.coreapi.CountCardSummariesResponse;
import io.axoniq.training.labs.giftcard.coreapi.FindCardSummariesQuery;
import io.axoniq.training.labs.giftcard.coreapi.FindCardSummariesResponse;
import io.axoniq.training.labs.giftcard.query.CardSummary;
import com.vaadin.data.provider.CallbackDataProvider;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

@Component
public class CardSummaryDataProvider extends CallbackDataProvider<CardSummary, Void> {

    public CardSummaryDataProvider(QueryGateway queryGateway) {
        super(
                q -> {
                    FindCardSummariesQuery query = new FindCardSummariesQuery(q.getOffset(), q.getLimit());
                    FindCardSummariesResponse response = queryGateway.send(query, FindCardSummariesResponse.class).join();
                    return response.getData().stream();
                },
                q -> {
                    CountCardSummariesQuery query = new CountCardSummariesQuery();
                    CountCardSummariesResponse response = queryGateway.send(query, CountCardSummariesResponse.class).join();
                    return response.getCount();
                }
        );
    }

}
