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
package org.n52.stream.seadatacloud.logsink;

import org.n52.stream.core.DataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@SpringBootApplication
@EnableBinding(Sink.class)
public class LogSinkApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LogSinkApplication.class);

    @StreamListener(Sink.INPUT)
    public void input(Message<DataMessage> dataMessage){
        LOG.info("Received processor output:\n{}", dataMessage.getPayload());
    }

    public static void main(String[] args){
        SpringApplication.run(LogSinkApplication.class, args);
    }

}