package io.axoniq.training.labs.giftcard.saga;

import io.axoniq.training.labs.giftcard.coreapi.CardRedeemedEvent;
import io.axoniq.training.labs.giftcard.coreapi.RedeemCardCommand;
import io.axoniq.training.labs.order.coreapi.ConfirmGiftCardPaymentCommand;
import io.axoniq.training.labs.order.coreapi.OrderPlacedEvent;
import io.axoniq.training.labs.order.coreapi.RejectGiftCardPaymentCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.CompletionException;

import static org.axonframework.eventhandling.saga.SagaLifecycle.associateWith;
import static org.axonframework.eventhandling.saga.SagaLifecycle.end;

@Profile("saga")
@Saga
public class GiftCardPaymentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;

    @SagaEventHandler(associationProperty = "orderId")
    @StartSaga
    public void handle(OrderPlacedEvent event) {
        this.orderId = event.getOrderId();
        associateWith("cardId", event.getCardId());
        try {
            commandGateway.send(new RedeemCardCommand(event.getCardId(), orderId, event.getGiftCardAmount())).join();
        } catch (CompletionException e) {
            commandGateway.send(new RejectGiftCardPaymentCommand(orderId, event.getCardId()));
        }
    }

    @SagaEventHandler(associationProperty = "cardId")
    public void handle(CardRedeemedEvent event) {
        if (orderId.equals(event.getTransactionId())) {
            commandGateway.send(new ConfirmGiftCardPaymentCommand(orderId, event.getCardId(), event.getAmount()));
            end();
        }
    }
}
