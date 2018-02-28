package io.axoniq.training.labs.giftcard.saga;

import org.axonframework.common.property.Property;
import org.axonframework.common.property.PropertyAccessStrategy;
import org.axonframework.eventhandling.EventHandlerInvoker;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.Segment;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Execution;

import java.util.List;

public class CamundaSagaManager implements EventHandlerInvoker {

    private final ProcessEngine processEngine;
    private final String processDefinitionKey;

    public CamundaSagaManager(ProcessEngine processEngine, String processDefinitionKey) {
        this.processEngine = processEngine;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public boolean canHandle(EventMessage<?> eventMessage, Segment segment) {
        return true;
    }

    @Override
    public void handle(EventMessage<?> message, Segment segment) throws Exception {
        String eventName = message.getPayloadType().getSimpleName();
        String associationProperty = "orderId";
        List<Execution> executions = processEngine.getRuntimeService().createExecutionQuery()
                                                  .processDefinitionKey(processDefinitionKey)
                                                  .messageEventSubscriptionName(eventName)
                                                  .processVariableValueEquals(associationProperty, getPropertyValue(associationProperty, message))
                                                  .list();
        executions.forEach(e -> processEngine.getRuntimeService().createMessageCorrelation(eventName)
                                             .processInstanceId(e.getProcessInstanceId())
                                             // TODO: Find out how to assign message data
                                             .setVariable("message-payload", message.getPayload())
                                             .setVariables(message.getMetaData())
                                             .correlate());
    }

    private Object getPropertyValue(String associationProperty, EventMessage<?> message) {
        Property<Object> property = PropertyAccessStrategy.getProperty(message.getPayloadType(), associationProperty);
        return property.getValue(message.getPayload());
    }
}
