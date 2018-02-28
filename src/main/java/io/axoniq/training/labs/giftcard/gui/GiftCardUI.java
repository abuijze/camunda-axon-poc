package io.axoniq.training.labs.giftcard.gui;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import io.axoniq.training.labs.giftcard.coreapi.IssueCardCommand;
import io.axoniq.training.labs.giftcard.coreapi.RedeemCardCommand;
import io.axoniq.training.labs.giftcard.coreapi.ReimburseCardCommand;
import io.axoniq.training.labs.giftcard.query.CardSummary;
import io.axoniq.training.labs.order.coreapi.OrderPlacedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

@SpringUI
@Push
public class GiftCardUI extends UI {

    private final CommandGateway commandGateway;

    private final CardSummaryDataProvider cardSummaryDataProvider;
    private final EventBus eventBus;
    private final TransactionTemplate transactionTemplate;

    public GiftCardUI(CommandGateway commandGateway, CardSummaryDataProvider cardSummaryDataProvider,
                      PlatformTransactionManager transactionManager, EventBus eventBus) {
        this.commandGateway = commandGateway;
        this.cardSummaryDataProvider = cardSummaryDataProvider;
        this.eventBus = eventBus;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        HorizontalLayout commandBar = new HorizontalLayout(issuePanel(), bulkIssuePanel(), redeemPanel(), reimbursePanel(), salesPanel());
        commandBar.setSizeFull();

        VerticalLayout layout = new VerticalLayout(commandBar, summaryGrid());
        layout.setSizeFull();

        getUI().setErrorHandler(event -> {
            Throwable cause = event.getThrowable();
            while (cause.getCause() != null) cause = cause.getCause();
            Notification.show("Error", cause.getMessage(), Notification.Type.ERROR_MESSAGE);
        });
        setContent(layout);
    }

    private Panel issuePanel() {
        TextField id = new TextField("Card id");
        TextField amount = new TextField("Amount");
        TextField shopId = new TextField("Shop id");
        Button submit = new Button("Submit", evt -> {
            commandGateway.sendAndWait(new IssueCardCommand(id.getValue(), Integer.parseInt(amount.getValue()), shopId.getValue()));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE);
            cardSummaryDataProvider.refreshAll();
        });

        FormLayout form = new FormLayout(id, amount, shopId, submit);
        form.setMargin(true);
        return new Panel("Issue single card", form);
    }

    private Panel bulkIssuePanel() {
        TextField number = new TextField("Number");
        TextField amount = new TextField("Amount");
        TextField shopId = new TextField("Shop id");
        Button submit = new Button("Submit", evt -> {
            CompletableFuture<?> future = CompletableFuture.completedFuture(null);
            for (int i = 0; i < Integer.parseInt(number.getValue()); i++) {
                String id = UUID.randomUUID().toString().substring(0, 11).toUpperCase();
                future = CompletableFuture.allOf(
                        future,
                        commandGateway.send(new IssueCardCommand(id, Integer.parseInt(amount.getValue()), shopId.getValue()))
                );
            }
            future.join();
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE);
            cardSummaryDataProvider.refreshAll();
        });

        FormLayout form = new FormLayout(number, amount, shopId, submit);
        form.setMargin(true);

        Panel panel = new Panel("Bulk issue cards");
        panel.setContent(form);
        return panel;
    }

    private Panel redeemPanel() {
        TextField id = new TextField("Card ID");
        TextField txId = new TextField("TransactionID");
        TextField amount = new TextField("Amount");
        Button submit = new Button("Submit", evt -> {
            int parsedAmount = Integer.parseInt(amount.getValue());
            commandGateway.sendAndWait(new RedeemCardCommand(id.getValue(), txId.getValue(), parsedAmount));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE);
            cardSummaryDataProvider.refreshAll();
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, txId, amount, submit);
        form.setMargin(true);

        return new Panel("Redeem card", form);
    }

    private Panel reimbursePanel() {
        TextField id = new TextField("Card ID");
        TextField txId = new TextField("TransactionID");
        Button submit = new Button("Submit", evt -> {
            commandGateway.sendAndWait(new ReimburseCardCommand(id.getValue(), txId.getValue()));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE);
            cardSummaryDataProvider.refreshAll();
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, txId, submit);
        form.setMargin(true);

        return new Panel("Reimburse card", form);
    }

    private Panel salesPanel() {
        TextField id = new TextField("Card ID");
        TextField amount = new TextField("Gift Card Amount");
        Button submit = new Button("Submit", evt -> {
            int parsedAmount = Integer.parseInt(amount.getValue());
            transactionTemplate.execute(transactionStatus -> {
                eventBus.publish(asEventMessage(new OrderPlacedEvent(UUID.randomUUID().toString(), id.getValue(), parsedAmount)));
                return null;
            });
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE);
            cardSummaryDataProvider.refreshAll();
        });

        FormLayout form = new FormLayout(id, amount, submit);
        form.setMargin(true);

        return new Panel("Simulate order", form);
    }

    private VerticalLayout summaryGrid() {
        Grid<CardSummary> grid = new Grid<>();
        grid.addColumn(CardSummary::getCardId).setCaption("Card ID");
        grid.addColumn(CardSummary::getInitialValue).setCaption("Initial value");
        grid.addColumn(CardSummary::getIssuedAt).setCaption("Issued at");
        grid.addColumn(CardSummary::getIssuedBy).setCaption("Issued by");
        grid.addColumn(CardSummary::getRemainingValue).setCaption("Remaining value");
        grid.addColumn(CardSummary::getNumberOfTransactions).setCaption("# Transactions");
        grid.setSizeFull();
        grid.setDataProvider(cardSummaryDataProvider);
        return new VerticalLayout(grid,
                                  new Button("Refresh", e -> cardSummaryDataProvider.refreshAll()));
    }

}
