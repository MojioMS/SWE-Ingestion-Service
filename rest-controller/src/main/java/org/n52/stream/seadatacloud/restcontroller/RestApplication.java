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
package org.n52.stream.seadatacloud.restcontroller;

import javax.annotation.PostConstruct;
import org.n52.stream.seadatacloud.restcontroller.controller.AppController;
import org.n52.stream.seadatacloud.restcontroller.exception.AppRegisterException;
import org.n52.stream.seadatacloud.restcontroller.remote.RemoteConfiguration;
import org.n52.stream.seadatacloud.restcontroller.util.DataRecordDefinitions;
import org.n52.stream.seadatacloud.restcontroller.util.StreamNameURLs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@SpringBootApplication
@ComponentScan("org.n52.stream.seadatacloud.restcontroller")
@ComponentScan("org.n52.stream.seadatacloud.core")
@Import(RemoteConfiguration.class)
public class RestApplication {

    @Value("${resources.path}")
    private String path;

    @Autowired
    public DataRecordDefinitions dataRecordDefinitions;

    @Autowired
    public StreamNameURLs streamNameURLs;

    @Autowired
    AppController appController;

    public static void main(String[] args) {
        new SpringApplicationBuilder(RestApplication.class)
                .properties("server.url,server.port,resources.path")
                .run(args);
    }

    @PostConstruct
    private void init() throws AppRegisterException {
        this.dataRecordDefinitions = new DataRecordDefinitions();

        // register applications:
        if (path.contains("//")) { // windows-solution

            // -- sources --
            ResponseEntity<String> response = appController.registerApp("mqtt-source-rabbit", "source", path + "/rest-controller/src/main/resources/mqtt-source-rabbit-2.0.0.BUILD-SNAPSHOT.jar");
            if ((response.getStatusCodeValue() != 200)
                    && (response.getStatusCodeValue() != 409)) {
                throw new AppRegisterException("Could not register unregistered source 'mqtt-source-rabbit' from '" + path + "/rest-controller/src/main/resources/mqtt-source-rabbit-2.0.0.BUILD-SNAPSHOT.jar'.");
            }

            // -- processors --
            response = appController.registerApp("csv-processor", "processor", path + "/csv-processor/target/csv-processor-0.0.1-SNAPSHOT-metadata.jar");
            if ((response.getStatusCodeValue() != 200)
                    && (response.getStatusCodeValue() != 409)) {
                throw new AppRegisterException("Could not register unregistered source 'csv-processor' from '" + path + "/csv-processor/target/csv-processor-0.0.1-SNAPSHOT-metadata.jar'.");
            }

            // -- sinks --
            response = appController.registerApp("log-sink", "sink", path + "/log-sink/target/log-sink-0.0.1-SNAPSHOT.jar");
            if ((response.getStatusCodeValue() != 200)
                    && (response.getStatusCodeValue() != 409)) {
                throw new AppRegisterException("Could not register unregistered source 'log-sink' from '" + path + "/log-sink/target/log-sink-0.0.1-SNAPSHOT.jar'.");
            }

            response = appController.registerApp("db-sink", "sink", path + "/db-sink/target/db-sink-0.0.1-SNAPSHOT.jar");
            if ((response.getStatusCodeValue() != 200)
                    && (response.getStatusCodeValue() != 409)) {
                throw new AppRegisterException("Could not register unregistered source 'db-sink' from '" + path + "/db-sink/target/db-sink-0.0.1-SNAPSHOT.jar'.");
            }
        } else {
            // TODO: non Windows pathing ..
        }

        this.dataRecordDefinitions.add("https://52north.org/swe-ingestion/mqtt/3.1", "mqtt-source-rabbit");
    }

}