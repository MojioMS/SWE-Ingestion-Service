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

import java.util.List;
import org.n52.stream.seadatacloud.restcontroller.model.AppOption;
import org.n52.stream.seadatacloud.restcontroller.model.Processors;
import org.n52.stream.seadatacloud.restcontroller.model.Sinks;
import org.n52.stream.seadatacloud.restcontroller.model.Source;
import org.n52.stream.seadatacloud.restcontroller.model.Sources;
import org.n52.stream.seadatacloud.restcontroller.service.CloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@RestController
@Component
@RequestMapping("/api/")
public class AppController {

    public final String APPLICATION_JSON = "application/json";
    public final String APPLICATION_XML = "application/xml";

    @Autowired
    CloudService service;

    @RequestMapping(value = "sources", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Sources getSources() {
        Sources result = service.getSources();
        return result;
    }
    
    @RequestMapping(value = "processors", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Processors getProcessors() {
        Processors result = service.getProcessors();
        return result;
    }
    
    @RequestMapping(value = "sinks", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Sinks getSinks() {
        Sinks result = service.getSinks();
        return result;
    }
    
    @RequestMapping(value = "registerApp", method = RequestMethod.GET)
    public ResponseEntity<String> registerApp(
            @RequestParam("name") String appName,
            @RequestParam("type") String appType,
            @RequestParam("uri") String appUri
    ) {
        String result = service.registerApp(appName, appType, appUri);
        return new ResponseEntity(result, HttpStatus.OK);
    }
    
    public Source getSourceByName(String sourceName) {
        Sources registeredSources = service.getSources();
        for (Source current : registeredSources.getSources()) {
            if (current.getName().equalsIgnoreCase(sourceName)) {
                return current;
            }
        }
        return null;
    }
    
    public AppOption getSourceOptionByName(Source source, String appOptionName) {
        List<AppOption> sourceOptions = source.getOptions();
        for (AppOption current : sourceOptions) {
            if (current.getName().equalsIgnoreCase(appOptionName)) {
                return current;
            }
        }
        return null;
    }
}