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
package org.n52.stream.seadatacloud.processors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.n52.shetland.ogc.sensorML.elements.SmlComponent;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.SmlDataInterface;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.stream.core.Configuration;
import org.n52.stream.core.DataMessage;
import org.n52.stream.core.Feature;
import org.n52.stream.core.Measurement;
import org.n52.stream.core.Timeseries;
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
@SpringBootApplication(scanBasePackages={"org.n52.stream.util"})
@EnableBinding(Processor.class)
@EnableConfigurationProperties(Configuration.class)
public class CsvProcessor {

    private static final String DEFINITION_PHENOMENON_TIME = "http://www.opengis.net/def/property/OGC/0/PhenomenonTime";

    private static final String DEFINITION_RESULT_TIME = "http://www.opengis.net/def/property/OGC/0/ResultTime";

    private int msgCount = 0;

    @Autowired
    private Configuration properties;

    @Autowired
    @Named("sensorml")
    private AggregateProcess processDescription;

    private SweTextEncoding textEncoding;

    private SweDataRecord tokenAssigments;

    private DataMessage templateDataMessage;

    private Feature feature;

    private static final Logger LOG = LoggerFactory.getLogger(CsvProcessor.class);

    public static void main(String[] args) {
        SpringApplication.run(CsvProcessor.class, args);
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
        AbstractProcessV20 process = checkAndExtractProcess();
        extractFeature(process);
        SmlDataInterface dataInterface = checkAndExtractDataInterface(process);
        checkAndStoreEncoding(dataInterface);
        storeTokenAssignments(dataInterface);
        fillTemplateDataMessage();
    }

    private void fillTemplateDataMessage() throws IllegalArgumentException {
        templateDataMessage = new DataMessage();
        //SweAbstractDataComponent tokenSpecification = tokenAssigments.getFields().get(i).getElement();
        boolean phenTimeSet = false;
        for (SweField field : tokenAssigments.getFields()) {
            SweAbstractDataComponent sweElement = field.getElement();
            if (!sweElement.isSetDefinition() || sweElement.getDefinition().equalsIgnoreCase(DEFINITION_RESULT_TIME)) {
                continue;
            } else if (sweElement.getDefinition().equalsIgnoreCase(DEFINITION_PHENOMENON_TIME)) {
                phenTimeSet = true;
            } else {
                String phenomenon = sweElement.getDefinition();
                Timeseries<?> ts = null;
                String unit = null;
                SweDataComponentType sweType = sweElement.getDataComponentType();
                switch(sweType) {
                    case Boolean:
                        ts = new Timeseries<Boolean>();
                        break;
                    case Category:
                        unit = ((SweCategory) sweElement).getUom();
                    case Text:
                        ts = new Timeseries<String>();
                        break;
                    case Quantity:
                        ts = new Timeseries<BigDecimal>();
                        unit = ((SweQuantity) sweElement).getUom();
                        break;
                    case Count:
                        ts = new Timeseries<Integer>();
                        break;
                    default:
                        throw logErrorAndCreateException(
                                String.format("Not supported swe:field element type found '%s'!",
                                sweType));
                }
                ts.withPhenomenon(phenomenon)
                    .withSensor(properties.getSensor())
                    .withFeature(feature);
                if (unit != null) {
                    ts.setUnit(unit);
                }
                templateDataMessage.addTimeseriesItem(ts);
            }
        }
        if (!phenTimeSet) {
            throw logErrorAndCreateException("PhenomenonTime not configured in Datastream definition");
        }
    }

    private void storeTokenAssignments(SmlDataInterface dataInterface) throws IllegalArgumentException {
        SweAbstractDataComponent elementType = dataInterface.getData().getElementType();
        if (!(elementType instanceof SweDataRecord)) {
            throw logErrorAndCreateException(
                    String.format("Datastream not encoded with DataRecord as ElementType! Found type '%s'.",
                    elementType.getClass().getName()));
        }
        tokenAssigments = (SweDataRecord) elementType;
    }

    private void checkAndStoreEncoding(SmlDataInterface dataInterface) throws IllegalArgumentException {
        SweAbstractEncoding encoding = dataInterface.getData().getEncoding();
        if (!(encoding instanceof SweTextEncoding)) {
            throw logErrorAndCreateException(
                    String.format("Datastream not encoded with text encoding! Found type: '%s'.",
                    encoding.getClass().getName()));
        }
        textEncoding = (SweTextEncoding) encoding;
    }

    private SmlDataInterface checkAndExtractDataInterface(AbstractProcessV20 process) throws IllegalArgumentException {
        if (!process.isSetOutputs() || process.getOutputs().isEmpty()) {
            throw logErrorAndCreateException("Element <outputs><OutputList><output> of first component is NOT set!");
        }
        List<SmlIo> ioValue = process.getOutputs().stream().
                filter(p -> p.getIoName().equalsIgnoreCase("fluorometerOutput") &&
                        p.getIoValue().getClass().getName().equalsIgnoreCase(SmlDataInterface.class.getName())).
                collect(Collectors.toList());
        if (ioValue.isEmpty() || ioValue.size() > 1 || !(ioValue.get(0).getIoValue() instanceof SmlDataInterface)) {
            throw logErrorAndCreateException(
                    "Element <outputs><OutputList><output><DataInterface> of 1st component is not set correct!");
        }
        SmlDataInterface dataInterface = (SmlDataInterface) ioValue.get(0).getIoValue();
        if (dataInterface.getData().isEmpty()) {
            throw logErrorAndCreateException(
                    "Element <outputs><OutputList><output><DataInterface><Data> of 1st component is not set correct!");
        }
        return dataInterface;
    }

    private void extractFeature(AbstractProcessV20 process) throws IllegalArgumentException {
        if (!process.isSetSmlFeatureOfInterest()) {
            throw logErrorAndCreateException("Element <featureofinterest> of first component is NOT set!");
        }
        Set<String> featuresOfInterest = process.getSmlFeatureOfInterest().getFeaturesOfInterest();
        if (featuresOfInterest.isEmpty() || featuresOfInterest.size() > 1) {
            throw logErrorAndCreateException("Only ONE Element <featureofinterest> of first component supported!");
        }
        feature = new Feature().withId(featuresOfInterest.iterator().next());
    }

    private AbstractProcessV20 checkAndExtractProcess() throws IllegalArgumentException {
        if (!processDescription.isSetComponents() || processDescription.getComponents().isEmpty()) {
            throw logErrorAndCreateException("AggregateProcess does not contain any component!");
        }
        SmlComponent smlComponent = processDescription.getComponents().get(0);
        if (smlComponent.isReferencedExternally()) {
            throw logErrorAndCreateException("First component is referenced externally but should not!");
        }
        if (!(smlComponent.getProcess() instanceof AbstractProcessV20)) {
            throw logErrorAndCreateException(
                    String.format("First component is not of type '%s' but '%s'!",
                            AbstractProcessV20.class,
                            smlComponent.getProcess().getClass()));
        }
        AbstractProcessV20 process = (AbstractProcessV20) smlComponent.getProcess();
        return process;
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public DataMessage process(Message<String> mqttMessage) {
        if (mqttMessage == null) {
            throw logErrorAndCreateException("NO MQTT message received! Input is 'null'.");
        }
        String mqttMessagePayload = mqttMessage.getPayload();
        msgCount++;
        if (mqttMessagePayload == null || mqttMessagePayload.isEmpty()) {
            throw logErrorAndCreateException("Empty MQTT payload received.");
        }
        DataMessage processedDataset = processMqttPayload(mqttMessagePayload);
        LOG.info("Processed dataset #{}", msgCount);
        LOG.trace("DataMessage: \n{}", processedDataset);
        return processedDataset;
    }

    // Example payload: 2018-04-04T08:34:14.945Z;WL-ECO-FLNTU-4476;2018-04-04T08:34:14.945Z;695;44;700;56;554
    private DataMessage processMqttPayload(String mqttMessagePayload) {
        LOG.trace("MQTT-Payload received: {}", mqttMessagePayload);
        List<String> payloadBlocks = tokenize(mqttMessagePayload, textEncoding.getBlockSeparator());
        if (payloadBlocks.isEmpty()) {
            return null;
        }
        DataMessage dataMessage = templateDataMessage.clone();
        for (String block : payloadBlocks) {
            List<String> tokens = tokenize(block, textEncoding.getTokenSeparator());
            if (tokens.isEmpty()) {
                continue;
            }
            OffsetDateTime resultTime = null;
            OffsetDateTime phenTime = null;
            List<Measurement<?>> measurements = new LinkedList<>();
            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                SweAbstractDataComponent tokenSpecification = tokenAssigments.getFields().get(i).getElement();
                if (!tokenSpecification.isSetDefinition()) {
                    continue;
                }
                String definition = tokenSpecification.getDefinition();
                if (definition.equals(DEFINITION_RESULT_TIME)) {
                    resultTime = OffsetDateTime.parse(token);
                } else if (definition.equals(DEFINITION_PHENOMENON_TIME)) {
                    phenTime = OffsetDateTime.parse(token);
                } else {
                    processDataValue(dataMessage, measurements, token, tokenSpecification, definition);
                }
            }
            setTimestamps(resultTime, phenTime, measurements);
        }
        return dataMessage;
    }

    /*
     * We can suppress these warnings because we know the type of the values from the specification. If something
     * fails, the input MUST be invalid and an exception is okay!
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void processDataValue(DataMessage dataMessage, List<Measurement<?>> measurements, String token,
            SweAbstractDataComponent tokenSpecification, String definition)
            throws IllegalArgumentException, NumberFormatException {
        Optional<Timeseries<?>> ts = dataMessage.getTimeseriesForPhenomenon(definition);
        if (!ts.isPresent()) {
            logErrorAndCreateException(String.format("Could not get timeseries for phenomenon '%s'!",
                    definition));
        }
        Timeseries<?> timeseries = ts.get();
        SweDataComponentType sweType = tokenSpecification.getDataComponentType();
        Measurement<?> measurementsItem = createMeasurement(token, sweType);
        timeseries.addMeasurementsItem((Measurement) measurementsItem);
        measurements.add(measurementsItem);
    }

    private Measurement<?> createMeasurement(String token, SweDataComponentType sweType)
            throws NumberFormatException, IllegalArgumentException {
        Measurement<?> measurementsItem = null;
        switch(sweType) {
            case Boolean:
                Measurement<Boolean> booleanMeasurement = new Measurement<>();
                booleanMeasurement.setValue(Boolean.valueOf(token));
                measurementsItem = booleanMeasurement;
                break;
            case Category:
            case Text:
                Measurement<String> stringMeasurement = new Measurement<>();
                stringMeasurement.setValue(token);
                measurementsItem = stringMeasurement;
                break;
            case Quantity:
                Measurement<BigDecimal> bigDecimalMeasurement = new Measurement<>();
                bigDecimalMeasurement.setValue(new BigDecimal(token));
                measurementsItem = bigDecimalMeasurement;
                break;
            case Count:
                Measurement<Integer> integerMeasurement = new Measurement<>();
                integerMeasurement.setValue(Integer.parseInt(token));
                measurementsItem = integerMeasurement;
                break;
            default:
                throw logErrorAndCreateException(
                        String.format("Not supported swe:field element type found '%s'!",
                        sweType));
        }
        return measurementsItem;
    }

    private void setTimestamps(OffsetDateTime resultTime, OffsetDateTime phenTime, List<Measurement<?>> measurements) {
        for (Measurement<?> measurement : measurements) {
            if (phenTime != null) {
                measurement.setPhenomenonTime(phenTime);
            }
            if (resultTime != null) {
                measurement.setResultTime(resultTime);
            }
        }
    }

    private List<String> tokenize(String mqttMessagePayload, String delimeter) {
        List<String> tokens = new LinkedList<>();
        StringTokenizer tokenizer = new StringTokenizer(mqttMessagePayload, delimeter);
        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        if (tokens.isEmpty()) {
            return Collections.emptyList();
        }
        return tokens;
    }

    private IllegalArgumentException logErrorAndCreateException(String msg) throws IllegalArgumentException {
        LOG.error(msg);
        return new IllegalArgumentException(msg);
    }

    private void checkSetting(String settingName, String setting) throws IllegalArgumentException {
        if (setting == null || setting.isEmpty()) {
            throw logErrorAndCreateException(String.format("setting '%s' not set correct. Received value: '%s'.",
                    settingName,
                    setting));
        }
        LOG.trace("'{}': '{}'", settingName, setting);
    }

}
