package io.axoniq.training.labs.giftcard.command;

import io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent;
import io.axoniq.training.labs.giftcard.coreapi.CardRedeemedEvent;
import io.axoniq.training.labs.giftcard.coreapi.IssueCardCommand;
import io.axoniq.training.labs.giftcard.coreapi.RedeemCardCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GiftCardTest {

    private static final String CARD_ID = "id";
    private static final String SHOP_ID = "shopid";
    private static final String TX_ID = "txid";
    private FixtureConfiguration<GiftCard> fixture;

    @Before
    public void setup() {
        fixture = new AggregateTestFixture<>(GiftCard.class);
    }

    @Test
    public void shouldIssueGiftCard() {
        fixture.givenNoPriorActivity()
               .when(new IssueCardCommand(CARD_ID, 100, SHOP_ID))
               .expectEvents(new CardIssuedEvent(CARD_ID, 100, SHOP_ID));
    }

    @Test
    public void shouldRedeemGiftCard() {
        fixture.given(new CardIssuedEvent(CARD_ID, 100, SHOP_ID))
               .when(new RedeemCardCommand(CARD_ID, TX_ID, 20))
               .expectEvents(new CardRedeemedEvent(CARD_ID, TX_ID, 20));
    }

    @Test
    public void shouldNotRedeemWithNegativeAmount() {
        fixture.given(new CardIssuedEvent(CARD_ID, 100, SHOP_ID))
               .when(new RedeemCardCommand(CARD_ID, TX_ID, -10))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void shouldNotRedeemWhenThereIsNotEnoughMoney() {
        fixture.given(new CardIssuedEvent(CARD_ID, 100, SHOP_ID))
               .when(new RedeemCardCommand(CARD_ID, TX_ID, 110))
               .expectException(IllegalStateException.class);
    }

}
