package io.axoniq.training.labs.giftcard.saga;

import io.axoniq.training.labs.giftcard.coreapi.CardRedeemedEvent;
import io.axoniq.training.labs.order.coreapi.ConfirmGiftCardPaymentCommand;
import io.axoniq.training.labs.order.coreapi.OrderPlacedEvent;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;

public class GiftCardPaymentSagaTest {

    private SagaTestFixture<GiftCardPaymentSaga> fixture;

    @Before
    public void setUp() {
        this.fixture = new SagaTestFixture<>(GiftCardPaymentSaga.class);
    }

    @Test
    public void shouldRedeemGiftCardWhenOrderIsPlaced() {
        fixture.givenAPublished(new OrderPlacedEvent("orderId", "cardId", 100))
               .whenPublishingA(new CardRedeemedEvent("cardId", "orderId", 100))
               .expectDispatchedCommands(new ConfirmGiftCardPaymentCommand("orderId", "cardId", 100))
               .expectActiveSagas(0);
    }
}
