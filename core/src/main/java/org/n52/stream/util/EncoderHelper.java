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

import javax.annotation.PostConstruct;

import org.apache.xmlbeans.XmlObject;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.service.OwsOperationKey;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.svalbard.encode.AbstractXmlEncoder;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.OperationRequestEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

/**
 * Helper class to encode internal representations to XML documents, e.g. the
 * InsertSensor request
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 1.0.0
 *
 */
@ImportResource("classpath*:svalbard-*.xml")
@Component
public class EncoderHelper {

    @Autowired
    private EncoderRepository encoderRepository;

    @PostConstruct
    protected void init() {
        encoderRepository.init();
    }

    /**
     * Encode {@link InsertSensorRequest} to {@link XmlObject}
     *
     * @param request
     *            The {@link InsertSensorRequest} to encode
     * @return the encoded {@link XmlObject}
     * @throws EncodingException
     *             If an error occurs during encoding.
     */
    public XmlObject encode(InsertSensorRequest request)
            throws EncodingException {
        Encoder<XmlObject, Object> encoder = getEncoder(request);
        if (encoder != null) {
            return encoder.encode(request);
        }
        return null;
    }

    /**
     * Encode {@link InsertSensorRequest} to XML string
     *
     * @param request
     *            The {@link InsertSensorRequest} to encode
     * @return the encoded XML string
     * @throws EncodingException
     *             If an error occurs during encoding.
     */
    public String encodeToString(InsertSensorRequest request)
            throws EncodingException {
        XmlObject xml = encode(request);
        if (xml != null) {
            return xml.xmlText(((AbstractXmlEncoder<?, ?>) getEncoder(request)).getXmlOptions());
        }
        return null;
    }

    protected void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    private Encoder<XmlObject, Object> getEncoder(InsertSensorRequest request) {
        OwsOperationKey key =
                new OwsOperationKey(request.getService(), request.getVersion(), request.getOperationName());
        return encoderRepository.getEncoder(new OperationRequestEncoderKey(key, MediaTypes.APPLICATION_XML));
    }
}
