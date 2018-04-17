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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.stream.AbstractCodingTest;
import org.n52.stream.generate.InsertSensorGenerator;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.exception.EncodingException;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

public class EncoderHelperTest extends AbstractCodingTest {

    private EncoderHelper helper;
    private InsertSensorRequest request;

    @Before
    public void setUp() throws DecodingException, IOException, XmlException {
        DecoderHelper decoderHelper = new DecoderHelper();
        decoderHelper.setDecoderRepository(initDecoderRepository());
        Path path = Paths.get(ResourceUtils.getFile(this.getClass().getResource("/")).getPath(), "sensors", "AggregateProcess.xml");
        request = new InsertSensorGenerator().generate((PhysicalSystem) ((AggregateProcess) decoderHelper.decode(path)).getComponents().get(1).getProcess());
        helper = new EncoderHelper();
        helper.setEncoderRepository(initEncoderRepository());
    }

    @Test
    public void encode() throws EncodingException, IOException, XmlException {
        Assert.isTrue(request != null, "Request should not null");
        XmlObject encode = helper.encode(request);
        Assert.isTrue(encode != null, "Should not null");
    }

    @Test
    public void encodeToString() throws EncodingException, IOException, XmlException {
        Assert.isTrue(request != null, "Request should not null");
        String encode = helper.encodeToString(request);
        Assert.isTrue(encode != null, "Should not null");
        Assert.isTrue(!encode.isEmpty(), "Should not empty");
    }
}
