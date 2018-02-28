package io.axoniq.training.labs.giftcard.command;

import io.axoniq.training.labs.giftcard.coreapi.CardReimbursedEvent;
import io.axoniq.training.labs.giftcard.coreapi.ReimburseCardCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.EntityId;
import org.axonframework.eventsourcing.EventSourcingHandler;

import java.util.Objects;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

public class GiftCardTransaction {

    @EntityId
    private String transactionId;

    private int transactionValue;
    private boolean reimbursed = false;

    public GiftCardTransaction(String transactionId, int transactionValue) {
        this.transactionId = transactionId;
        this.transactionValue = transactionValue;
    }

    @CommandHandler
    public void handle(ReimburseCardCommand cmd) {
        if (reimbursed) throw new IllegalStateException("Transaction already reimbursed");
        apply(new CardReimbursedEvent(cmd.getCardId(), transactionId, transactionValue));
    }

    public String getTransactionId() {
        return transactionId;
    }

    @EventSourcingHandler
    public void on(CardReimbursedEvent event) {
        if (transactionId.equals(event.getTransactionId())) {
            reimbursed = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftCardTransaction that = (GiftCardTransaction) o;
        return transactionValue == that.transactionValue &&
                reimbursed == that.reimbursed &&
                Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(transactionId, transactionValue, reimbursed);
    }
}
