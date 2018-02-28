package io.axoniq.training.labs.order.coreapi;

import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderCommandHandler.class);

    @CommandHandler
    public void handle(ConfirmGiftCardPaymentCommand command) {
        logger.info("Payment confirmed for {} from cardId {} for order {}", command.getGiftCardAmount(),
                    command.getCardId(),
                    command.getOrderId());
    }

    @CommandHandler
    public void handle(RejectGiftCardPaymentCommand command) {
        logger.info("Payment rejected from cardId {} for order {}", command.getCardId(),
                    command.getOrderId());
    }
}
