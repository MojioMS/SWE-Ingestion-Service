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
package org.n52.stream.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sensorML.v20.SmlDataInterface;
import org.n52.stream.AbstractCodingTest;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DecoderHelperTest.class})
public class DecoderHelperTest extends AbstractCodingTest {

    private DecoderHelper helper;
    private Path path;
    
    @Before
    public void setUp() throws FileNotFoundException {
        DecoderRepository decoderRepository = initDecoderRepository();
        
        helper = new DecoderHelper();
        helper.setDecoderRepository(decoderRepository);
        path = Paths.get(ResourceUtils.getFile(this.getClass().getResource("/")).getPath(), "sensors", "AggregateProcess.xml");
    }
    
    @Test
    public void loadFromPath() throws DecodingException, IOException, XmlException {
        Object decode = helper.decode(path);
        Assert.isTrue(decode != null, "Should not null");
        Assert.isTrue(decode instanceof AggregateProcess, "Should be instance of AggregateProcess");
        AggregateProcess aggregateProcess = (AggregateProcess) decode;
        Assert.isTrue(aggregateProcess.isSetComponents(), "Should have Components");
        Assert.isTrue(aggregateProcess.getComponents().size() == 2, "Components size should be 2");
        Assert.isTrue(aggregateProcess.getComponents().get(0).getProcess() instanceof PhysicalSystem, "");
        PhysicalSystem physicalSystem = (PhysicalSystem) aggregateProcess.getComponents().get(0).getProcess();
        Assert.isTrue(physicalSystem.getOutputs().size() == 1, "");
        Assert.isTrue(physicalSystem.getOutputs().get(0).getIoValue() instanceof SmlDataInterface, "");
        Assert.isTrue(aggregateProcess.isSetConnections(), "Should have Connections");
        Assert.isTrue(aggregateProcess.getComponents().size() == 2, "Connections size should be 2");
    }
}
