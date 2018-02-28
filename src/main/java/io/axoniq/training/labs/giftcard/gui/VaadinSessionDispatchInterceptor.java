package io.axoniq.training.labs.giftcard.gui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedSession;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.MessageDispatchInterceptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class VaadinSessionDispatchInterceptor implements MessageDispatchInterceptor<Message<?>> {

    @Override
    public BiFunction<Integer, Message<?>, Message<?>> handle(List<Message<?>> messages) {
        return (integer, message) ->
                Optional.ofNullable(VaadinRequest.getCurrent())
                        .map(r -> r.getWrappedSession(false))
                        .map(WrappedSession::getId)
                        .map(sessionId -> message.andMetaData(Collections.singletonMap("vaadin-session-id", sessionId)))
                        .orElse((Message) message);
    }
}
