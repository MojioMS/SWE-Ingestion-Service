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
package org.n52.stream.generate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.stream.AbstractCodingTest;
import org.n52.stream.util.DecoderHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {InsertSensorGeneratorTest.class})
public class InsertSensorGeneratorTest extends AbstractCodingTest {

    private Object decode;
    private InsertSensorGenerator generator;

    @Before
    public void setUp() throws DecodingException, IOException, XmlException {
        DecoderRepository decoderRepository = initDecoderRepository();

        DecoderHelper helper = new DecoderHelper();
        helper.setDecoderRepository(decoderRepository);
        Path path = Paths.get(ResourceUtils.getFile(this.getClass().getResource("/")).getPath(), "sensors", "AggregateProcess.xml");
        decode = helper.decode(path);
        generator = new InsertSensorGenerator();
    }

    @Test
    public void generate()
            throws DecodingException,
            IOException,
            XmlException {
        Assert.isTrue(decode != null, "Should not be null");
        Assert.isTrue(decode instanceof AggregateProcess, "Should be instance of AggregateProcess");
        AggregateProcess aggregateProcess = (AggregateProcess) decode;
        Assert.isTrue(aggregateProcess.isSetComponents(), "Should have Components");
        Assert.isTrue(aggregateProcess.getComponents().size() == 2, "Components size should be 2");
        Assert.isTrue(aggregateProcess.getComponents().get(1).getProcess() instanceof PhysicalSystem,
                "Component 2 should be a PhysicalSystem");
        InsertSensorRequest request =
                generator.generate((PhysicalSystem) aggregateProcess.getComponents().get(1).getProcess());
        Assert.isTrue(request.isSetProcedureDescription(), "Should have ProcedureDescription");
        Assert.isTrue(request.isSetProcedureDescriptionFormat(), "Should have ProcedureDescriptionFormat");
        Assert.isTrue(request.getProcedureDescriptionFormat().equals(SensorML20Constants.NS_SML_20), "ProcedureDescriptionFormat should be http://www.opengis.net/sensorml/2.0");
        checkObservedProperties(request);
        checkMetadata(request);
    }

    @Test
    public void generateHrefFeature()
            throws DecodingException,
            IOException,
            XmlException {
        Assert.isTrue(decode != null, "Should not be null");
        Assert.isTrue(decode instanceof AggregateProcess, "Should be instance of AggregateProcess");
        AggregateProcess aggregateProcess = (AggregateProcess) decode;
        Assert.isTrue(aggregateProcess.isSetComponents(), "Should have Components");
        Assert.isTrue(aggregateProcess.getComponents().size() == 2, "Components size should be 2");
        Assert.isTrue(aggregateProcess.getComponents().get(1).getProcess() instanceof PhysicalSystem,
                "Component 2 should be a PhysicalSystem");
        AbstractSensorML process = aggregateProcess.getComponents().get(1).getProcess();
        if (process instanceof AbstractProcessV20) {
            AbstractProcessV20 p = (AbstractProcessV20) process;
            for (AbstractFeature f : p.getSmlFeatureOfInterest().getFeaturesOfInterestMap().values()) {
                if (f instanceof AbstractSamplingFeature) {
                    ((AbstractSamplingFeature) f).setGeometry(null);
                }
            }
            InsertSensorRequest request = generator.generate(p);
            Assert.isTrue(request.isSetProcedureDescription(), "Should have ProcedureDescription");
            Assert.isTrue(request.isSetProcedureDescriptionFormat(), "Should have ProcedureDescriptionFormat");
            Assert.isTrue(request.getProcedureDescriptionFormat().equals(SensorML20Constants.NS_SML_20), "ProcedureDescriptionFormat should be http://www.opengis.net/sensorml/2.0");
            checkObservedProperties(request);
            checkMetadata(request);
        }
    }

    private void checkObservedProperties(InsertSensorRequest request) {
        Assert.isTrue(request.isSetObservableProperty(), "Should have ObservableProperties");
        Assert.isTrue(request.getObservableProperty().size() == 2, "ObservableProperties size should be 2");
        Assert.isTrue(
                request.getObservableProperty()
                        .contains("http://vocab.nerc.ac.uk/collection/B39/current/fluorescence/"),
                "ObservableProperties contains http://vocab.nerc.ac.uk/collection/B39/current/fluorescence/");
        Assert.isTrue(
                request.getObservableProperty().contains("http://vocab.nerc.ac.uk/collection/P01/current/TURBXXXX/"),
                "ObservableProperties contains http://vocab.nerc.ac.uk/collection/P01/current/TURBXXXX/");
    }

    private void checkMetadata(InsertSensorRequest request) {
        Assert.isTrue(request.isSetMetadata(), "Should have SosInsertionMetadata");
        SosInsertionMetadata metadata = request.getMetadata();
        Assert.isTrue(metadata.getObservationTypes() != null, "Should have ObservationType");
        Assert.isTrue(metadata.getObservationTypes().size() == 1, "ObservationType size should be 1");
        Assert.isTrue(metadata.getObservationTypes().iterator().next().equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION), "ObservationType should be OM_CountObservation");
        Assert.isTrue(metadata.getFeatureOfInterestTypes() != null, "Should have FeatureType");
        Assert.isTrue(metadata.getFeatureOfInterestTypes().iterator().next().equals(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT), "FeatureType should be SF_SamplingPoint");
    }
}