package io.axoniq.training.labs.giftcard.upcasters;

import org.axonframework.eventsourcing.eventstore.GenericDomainEventEntry;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class AddShopIdToCardIssuedEventUpcasterTest {

    private static final String eventV0 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent><cardId>cardId</cardId><amount>100</amount></io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent>";
    private static final String eventV1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent><cardId>cardId</cardId><amount>100</amount><shopId>Unknown</shopId></io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent>";
    private static final String payloadType = "io.axoniq.training.labs.giftcard.coreapi.CardIssuedEvent";

    private AddShopIdToCardIssuedEventUpcaster testSubject;
    private Serializer serializer;

    @Before
    public void setUp() {
        testSubject = new AddShopIdToCardIssuedEventUpcaster();
        serializer = new XStreamSerializer();
    }

    @Test
    public void shouldAddUnknownShopIdToOldRevisions() {
        InitialEventRepresentation initialEventRepresentation = new InitialEventRepresentation(new GenericDomainEventEntry<>("type", "aggregateIdentifier", 0, "eventId", Instant.now(), payloadType, null, eventV0.getBytes(), MetaData.emptyInstance()), serializer);
        List<IntermediateEventRepresentation> result = testSubject.upcast(Stream.of(initialEventRepresentation)).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(eventV1, result.get(0).getData(String.class).getData());
        assertEquals("1", result.get(0).getData(String.class).getType().getRevision());
        assertEquals(payloadType, result.get(0).getData(String.class).getType().getName());
    }
}
