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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.XmlDecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

/**
 * Helper class to decode XML documents, e.g. the process description
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 1.0.0
 *
 */
//@ImportResource("classpath*:/svalbard-*.xml")
@Component
public class DecoderHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DecoderHelper.class);

    @Autowired
    private DecoderRepository decoderRepository;

    /**
     * init method
     */
    @PostConstruct
    protected void init() {
        decoderRepository.init();
    }

    /**
     * Decodes an XML document to an representation from arctic-sea/shetland
     *
     * @param xml
     *            XML object to decode
     * @return Decoded object
     * @throws DecodingException
     *             If an error occurs during decoding.
     */
    public Object decode(XmlObject xml)
            throws DecodingException {
        Decoder<Object, Object> decoder = decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml));
        if (decoder == null) {
            String msg = String.format("No decoder found for XML: Element: '%s' with Namespace '%s'.",
                    xml.getDomNode().getNodeName(),
                    xml.getDomNode().getNamespaceURI());
            LOG.error(msg);
            throw new IllegalArgumentException();
        }
        return decoder.decode(xml);
    }

    /**
     * Decode a XML input stream
     *
     * @param inputStream
     *            XML input stream
     * @return Decoded object
     * @throws XmlException
     *             If an error occurs when parsing XML {@link InputStream} to
     *             {@link XmlObject}
     * @throws IOException
     *             If an error occurs when processing XML {@link InputStream}
     * @throws DecodingException
     *             If an error occurs during decoding.
     */
    public Object decode(InputStream inputStream)
            throws DecodingException,
            XmlException,
            IOException {
        return decode(XmlObject.Factory.parse(inputStream));
    }

    /**
     * Decode a XML file from {@link Path}
     *
     * @param path
     *            XML file path
     * @return Decoded object
     * @throws IOException
     *             If an error occurs when processing {@link Path}
     * @throws DecodingException
     *             If an error occurs during decoding.
     * @throws XmlException
     *             If an error occurs when parsing XML file from {@link Path} to
     *             {@link XmlObject}
     */
    public Object decode(Path path)
            throws DecodingException,
            XmlException,
            IOException {
        return decode(Files.newInputStream(path));
    }

    /**
     * Decode a XML string
     *
     * @param xml
     *            XML string to decode
     * @return Decoded object
     * @throws XmlDecodingException
     *             If an error occurs when parsing string to {@link XmlObject}
     * @throws DecodingException
     *             If an error occurs during decoding.
     */
    public Object decode(String xml)
            throws XmlDecodingException,
            DecodingException {
        return decode(CodingHelper.readXML(xml));
    }

    /**
     * Set {@link DecoderRepository}
     *
     * @param decoderRepository
     */
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }
}
