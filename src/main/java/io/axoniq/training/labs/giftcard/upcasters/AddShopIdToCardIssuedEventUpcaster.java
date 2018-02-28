package io.axoniq.training.labs.giftcard.upcasters;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import org.dom4j.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class AddShopIdToCardIssuedEventUpcaster extends SingleEventUpcaster {

    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        SerializedType payloadType = intermediateRepresentation.getData().getType();
        return "io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent".equals(payloadType.getName())
                && payloadType.getRevision() == null;
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return intermediateRepresentation.upcastPayload(
                new SimpleSerializedType("io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent", "1"),
                Document.class,
                event -> {
                    event.getRootElement().addElement("shopId").addText("Unknown");
                    return event;
                });
    }
}
