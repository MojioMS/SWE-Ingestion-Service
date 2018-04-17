/*
 * Copyright (C) 2018-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.stream.seadatacloud.marine;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.stream.core.Configuration;
import org.n52.stream.core.DataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.cloud.stream.messaging.Processor;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 */
@SpringBootApplication
@EnableBinding(Processor.class)
@EnableConfigurationProperties(Configuration.class)
public class MarineProcessor {

    private int msgCount = 0;

    @Autowired
    private Configuration properties;

    @Autowired
    @Named("sensorml")
    private AggregateProcess processDescription;

    private static final Logger LOG = LoggerFactory.getLogger(MarineProcessor.class);

    public static void main(String[] args) {
        SpringApplication.run(MarineProcessor.class, args);
    }

    /**
     * Init the processor by checking the properties and finalize the custom configuration
     */
    @PostConstruct
    public void init() {
        LOG.info("init(); processor called");
        checkSetting("offering", properties.getOffering());
        checkSetting("sensor", properties.getSensor());
        checkSetting("sensorml-url", properties.getSensormlUrl());
    }

    private void checkSetting(String settingName, String setting) throws IllegalArgumentException {
        if (setting == null || setting.isEmpty()) {
            String msg = String.format("setting '%s' not set correct. Received value: '%s'.", settingName, setting);
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        LOG.trace("'{}': '{}'", settingName, setting);
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public DataMessage process(Message<String> mqttMessage) {
        if (mqttMessage == null) {
            String msg = "NO MQTT message received! Input is 'null'.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        // check property value
        // download sensorML
        // parse sensorml
        // configure csv parser
        String mqttMessagePayload = mqttMessage.getPayload();
        String mqttTopic = mqttMessage.getHeaders().get("mqtt_receivedTopic", String.class);
        msgCount++;
        if (mqttTopic == null || mqttTopic.isEmpty()) {
            String msg = "MQTT topic not specified.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (mqttMessagePayload == null || mqttMessagePayload.isEmpty()) {
            String msg = "Empty MQTT payload received.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        DataMessage processedDataset = processMarineMqttPayload(mqttTopic, mqttMessagePayload);
        LOG.info("Processed dataset #{}", msgCount);
        LOG.trace("DataMessage: \n{}", processedDataset);
        return processedDataset;
    }

    private DataMessage processMarineMqttPayload(String mqttTopic, String mqttMessagePayload) {
        LOG.trace("MQTT-Payload received: {}", mqttMessagePayload);

        String[] payloadChunks = mqttMessagePayload.split("\\|");
        if (payloadChunks.length != 3) {
            String msg = String.format(
                    "Received mqtt payload not in correct format. Expected three '|' separated chunks: '%s'",
                    mqttMessagePayload);
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // TIMESTAMP
        LOG.trace("Receiver Station Timestamp chunk: '{}'", payloadChunks[0]);
        OffsetDateTime receiverStationTimestamp = OffsetDateTime.parse(payloadChunks[0]);

        // Sensor
        String sensor = payloadChunks[1];
        LOG.trace("Sensor chunk   : '{}'", sensor);

        // Process values
        LOG.trace("Data chunk     : '{}", payloadChunks[2]);
        List<String> values = Stream.of(payloadChunks[2].split("\\s+"))
                .filter(value -> (value!=null && !value.isEmpty()))
                .collect(Collectors.toList());
        String msg = String.format("Generic Processor not yet implemented.");
        LOG.error(msg);
        throw new IllegalArgumentException(msg);
    }

}