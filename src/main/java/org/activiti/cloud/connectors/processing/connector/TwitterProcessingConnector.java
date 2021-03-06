package org.activiti.cloud.connectors.processing.connector;

import java.util.HashMap;
import java.util.Map;

import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.api.process.model.IntegrationResult;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;
import org.activiti.cloud.connectors.starter.model.IntegrationResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static net.logstash.logback.marker.Markers.append;

@Component
@EnableBinding(ProcessingConnectorChannels.class)
public class TwitterProcessingConnector {

    private final Logger logger = LoggerFactory.getLogger(TwitterProcessingConnector.class);
    private final IntegrationResultSender integrationResultSender;
    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private ConnectorProperties connectorProperties;

    public TwitterProcessingConnector(IntegrationResultSender integrationResultSender) {

        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = ProcessingConnectorChannels.TWITTER_PROCESSING_CONSUMER)
    public void processEnglish(IntegrationRequest event) throws InterruptedException {

        String tweet = String.valueOf(event.getIntegrationContext().getInBoundVariables().get("text"));
        logger.info(append("service-name",
                appName),
                ">>> Doing cleaning/processing of posted content sized " + (tweet == null ? "null" : tweet.length()));

        //@TODO: perform processing here

        Map<String, Object> results = new HashMap<>();
        results.put("text",
                tweet);
        Message<IntegrationResult> message = IntegrationResultBuilder.resultFor(event, connectorProperties)
                .withOutboundVariables(results)
                .buildMessage();
        integrationResultSender.send(message);
    }
}
