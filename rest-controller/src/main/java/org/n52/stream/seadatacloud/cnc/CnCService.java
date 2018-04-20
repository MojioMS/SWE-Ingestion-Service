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
package org.n52.stream.seadatacloud.cnc;

import javax.annotation.PostConstruct;
import org.n52.stream.seadatacloud.cnc.exception.AppRegisterException;
import org.n52.stream.seadatacloud.cnc.remote.RemoteConfiguration;
import org.n52.stream.seadatacloud.cnc.util.DataRecordDefinitions;
import org.n52.stream.seadatacloud.cnc.util.StreamNameURLs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@SpringBootApplication
@ComponentScan("org.n52.stream.seadatacloud.cnc")
@ComponentScan("org.n52.stream.core")
@ComponentScan("org.n52.stream.util")
@Import(RemoteConfiguration.class)
@EnableConfigurationProperties(CnCServiceConfiguration.class)
public class CnCService {

    @Autowired
    public DataRecordDefinitions dataRecordDefinitions;

    @Autowired
    private CnCServiceConfiguration properties;

    @Autowired
    public StreamNameURLs streamNameURLs;

    public static void main(String[] args) {
        new SpringApplicationBuilder(CnCService.class)
                .properties("server.port,server.servlet.contextPath")
                .run(args);
    }

    @PostConstruct
    private void init() throws AppRegisterException {
        dataRecordDefinitions = new DataRecordDefinitions();
        dataRecordDefinitions.add("https://52north.org/swe-ingestion/mqtt/3.1", "mqtt-source-rabbit");
    }

}