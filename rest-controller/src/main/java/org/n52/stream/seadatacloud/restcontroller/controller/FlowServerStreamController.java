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
package org.n52.stream.seadatacloud.restcontroller.controller;

import org.n52.stream.seadatacloud.restcontroller.service.CloudService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@RestController
@Component
@RequestMapping("/api")
public class FlowServerStreamController {

    public final String BASE_URL = "http://localhost:8080";
    public final String APPLICATION_JSON = "application/json";
    public final String APPLICATION_XML = "application/xml";

    @Autowired
    CloudService service;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ResponseEntity<String> getApi() {
        String sources = "{\"name\": \"sources\", \"description\":\"List of registered sources.\", \"href\": \"" +BASE_URL+ "/api/sources\"}";
        String processors = "{\"name\": \"processors\", \"description\":\"List of registered processors.\", \"href\": \"" +BASE_URL+ "/api/processors\"}";
        String sinks = "{\"name\": \"sinks\", \"description\":\"List of registered sinks.\", \"href\": \"" +BASE_URL+ "/api/sinks\"}";
        String streams = "{\"name\": \"streams\", \"description\":\"List of registered streams.\", \"href\": \"" +BASE_URL+ "/api/streams\"}";
        String resources = "{ \"resources\" : [" + sources + "," + processors + "," + sinks + "," + streams + "] }";
        return new ResponseEntity(resources, HttpStatus.ACCEPTED);
    }

}