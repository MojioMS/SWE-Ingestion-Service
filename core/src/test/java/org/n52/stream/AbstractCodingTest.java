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
package org.n52.stream;

import java.util.Arrays;

import org.apache.xmlbeans.XmlOptions;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.GmlDecoderv321;
import org.n52.svalbard.decode.SamplingDecoderv20;
import org.n52.svalbard.decode.SensorMLDecoderV20;
import org.n52.svalbard.decode.SweCommonDecoderV20;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.GmlEncoderv321;
import org.n52.svalbard.encode.InsertSensorRequestEncoder;
import org.n52.svalbard.encode.SamplingEncoderv20;
import org.n52.svalbard.encode.SchemaRepository;
import org.n52.svalbard.encode.SensorMLEncoderv20;
import org.n52.svalbard.encode.SosInsertionMetadataTypeEncoder;
import org.n52.svalbard.encode.SweCommonEncoderv20;

public abstract class AbstractCodingTest {

    public DecoderRepository initDecoderRepository() {
        DecoderRepository decoderRepository = new DecoderRepository();

        SensorMLDecoderV20 sensorMLDecoderV20 = new SensorMLDecoderV20();
        sensorMLDecoderV20.setDecoderRepository(decoderRepository);
        sensorMLDecoderV20.setXmlOptions(XmlOptions::new);

        SweCommonDecoderV20 sweCommonDecoderV20 = new SweCommonDecoderV20();
        sweCommonDecoderV20.setDecoderRepository(decoderRepository);
        sweCommonDecoderV20.setXmlOptions(XmlOptions::new);

        GmlDecoderv321 gmlDecoderv321 = new GmlDecoderv321();
        gmlDecoderv321.setDecoderRepository(decoderRepository);
        gmlDecoderv321.setXmlOptions(XmlOptions::new);

        SamplingDecoderv20 samplingDecoderv20 = new SamplingDecoderv20();
        samplingDecoderv20.setDecoderRepository(decoderRepository);
        samplingDecoderv20.setXmlOptions(XmlOptions::new);

        decoderRepository.setDecoders(Arrays.asList(sensorMLDecoderV20,
                                                    sweCommonDecoderV20,
                                                    gmlDecoderv321,
                                                    samplingDecoderv20));
        decoderRepository.init();
        return decoderRepository;
    }

    public EncoderRepository initEncoderRepository() {
        EncoderRepository encoderRepository = new EncoderRepository();

        InsertSensorRequestEncoder insertSensorRequestEncoder = new InsertSensorRequestEncoder();
        insertSensorRequestEncoder.setEncoderRepository(encoderRepository);
        insertSensorRequestEncoder.setXmlOptions(XmlOptions::new);

        SosInsertionMetadataTypeEncoder sosInsertionMetadataTypeEncoder = new SosInsertionMetadataTypeEncoder();
        sosInsertionMetadataTypeEncoder.setEncoderRepository(encoderRepository);
        sosInsertionMetadataTypeEncoder.setXmlOptions(XmlOptions::new);

        SensorMLEncoderv20 sensorMLEncoderV20 = new SensorMLEncoderv20();
        sensorMLEncoderV20.setEncoderRepository(encoderRepository);
        sensorMLEncoderV20.setXmlOptions(XmlOptions::new);

        SweCommonEncoderv20 sweCommonEncoderV20 = new SweCommonEncoderv20();
        sweCommonEncoderV20.setEncoderRepository(encoderRepository);
        sweCommonEncoderV20.setXmlOptions(XmlOptions::new);

        GmlEncoderv321 gmlEncoderv321 = new GmlEncoderv321();
        gmlEncoderv321.setEncoderRepository(encoderRepository);
        gmlEncoderv321.setXmlOptions(XmlOptions::new);

        SamplingEncoderv20 samplingEncoderv20 = new SamplingEncoderv20();
        samplingEncoderv20.setEncoderRepository(encoderRepository);
        samplingEncoderv20.setXmlOptions(XmlOptions::new);

        encoderRepository.setEncoders(Arrays.asList(insertSensorRequestEncoder,
                                                    sosInsertionMetadataTypeEncoder,
                                                    sensorMLEncoderV20,
                                                    sweCommonEncoderV20,
                                                    gmlEncoderv321,
                                                    samplingEncoderv20));
        encoderRepository.init();
        SchemaRepository schemaRepository = new SchemaRepository();
        schemaRepository.setEncoderRepository(encoderRepository);
        schemaRepository.init();
        insertSensorRequestEncoder.setSchemaRepository(schemaRepository);
        return encoderRepository;
    }
}
