package io.axoniq.training.labs.giftcard.command;

import io.axoniq.training.labs.giftcard.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateMember;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Profile("command")
@Aggregate
public class GiftCard {

    @AggregateIdentifier
    private String id;

    @AggregateMember
    private List<GiftCardTransaction> transactions = new ArrayList<>();

    private int remainingValue;

    public GiftCard() {
    }

    @CommandHandler
    public GiftCard(IssueCardCommand cmd) {
        if (cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        apply(new CardIssuedEvent(cmd.getCardId(), cmd.getAmount(), cmd.getShopId()));
    }

    @CommandHandler
    public void handle(RedeemCardCommand cmd) {
        if (cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        if (cmd.getAmount() > remainingValue) throw new IllegalStateException("amount > remaining value");
        if (transactions.stream().map(GiftCardTransaction::getTransactionId).anyMatch(cmd.getTransactionId()::equals))
            throw new IllegalStateException("TransactionId must be unique");
        apply(new CardRedeemedEvent(id, cmd.getTransactionId(), cmd.getAmount()));
    }

    @EventSourcingHandler
    public void on(CardIssuedEvent evt) {
        id = evt.getCardId();
        remainingValue = evt.getAmount();
    }

    @EventSourcingHandler
    public void on(CardRedeemedEvent evt) {
        remainingValue -= evt.getAmount();
        transactions.add(new GiftCardTransaction(evt.getTransactionId(), evt.getAmount()));
    }

}
