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
///*
// * Copyright (C) 2018-2018 52°North Initiative for Geospatial Open Source
// * Software GmbH
// *
// * This program is free software; you can redistribute it and/or modify it
// * under the terms of the GNU General Public License version 2 as published
// * by the Free Software Foundation.
// *
// * If the program is linked with libraries which are licensed under one of
// * the following licenses, the combination of the program with the linked
// * library is not considered a "derivative work" of the program:
// *
// *     - Apache License, version 2.0
// *     - Apache Software License, version 1.0
// *     - GNU Lesser General Public License, version 3
// *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
// *     - Common Development and Distribution License (CDDL), version 1.0
// *
// * Therefore the distribution of the program linked with libraries licensed
// * under the aforementioned licenses, is permitted by the copyright holders
// * if the distribution is compliant with both the GNU General Public
// * License version 2 and the aforementioned licenses.
// *
// * This program is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// * Public License for more details.
// */
//package org.n52.stream.seadatacloud.restcontroller.decoder;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.junit.Test;
//
//import org.locationtech.jts.util.Assert;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//
//import org.n52.stream.seadatacloud.restcontroller.model.AppOption;
//import org.n52.stream.seadatacloud.restcontroller.model.Processor;
//import org.n52.stream.seadatacloud.restcontroller.model.Processors;
//import org.n52.stream.seadatacloud.restcontroller.model.Sink;
//import org.n52.stream.seadatacloud.restcontroller.model.Sinks;
//import org.n52.stream.seadatacloud.restcontroller.model.Source;
//import org.n52.stream.seadatacloud.restcontroller.model.Sources;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.util.ResourceUtils;
//
///**
// *
// * @author Maurin Radtke <m.radtke@52north.org>
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {AppAndStreamDecoderTest.class})
//public class AppAndStreamDecoderTest {
//    
//    Path testFilesPath;
//    
//    @Before
//    public void setUp() throws FileNotFoundException {
//        testFilesPath = Paths.get(ResourceUtils.getFile(this.getClass().getResource("/")).getParent(), "classes", "test");
//    }
//    
//    @Test
//    public void sourcesDecoderTest() throws FileNotFoundException, IOException {
//        Path path = Paths.get(testFilesPath.toString(), "getSourcesResponse.json");
//        File jsonResponse = path.toFile();
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule sm = new SimpleModule();
//        sm.addDeserializer(Sources.class, new SourcesDecoder());
//        objectMapper.registerModule(sm);
//        
//        Sources sources = objectMapper.readValue(jsonResponse, Sources.class);
//        Assert.isTrue(sources.getSources().size() == 1);
//        Source source = sources.getSources().get(0);
//        Assert.isTrue(source.getName().equals("mqtt-source-rabbit"));
//        List<AppOption> appOptions = source.getOptions();
//        Assert.isTrue(!appOptions.isEmpty());
//        Assert.isTrue(appOptions.get(0).getName().equals("qos"));
//    }
//    
//    @Test
//    public void processorsDecoderTest() throws FileNotFoundException, IOException {
//        Path path = Paths.get(testFilesPath.toString(), "getProcessorsResponse.json");
//        File jsonResponse = path.toFile();
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule sm = new SimpleModule();
//        sm.addDeserializer(Processors.class, new ProcessorsDecoder());
//        objectMapper.registerModule(sm);
//        
//        Processors processors = objectMapper.readValue(jsonResponse, Processors.class);
//        Assert.isTrue(processors.getProcessors().size() == 1);
//        Processor processor = processors.getProcessors().get(0);
//        Assert.isTrue(processor.getName().equals("csv-processor"));
//        List<AppOption> appOptions = processor.getOptions();
//        Assert.isTrue(appOptions.isEmpty());
//    }
//    
//    @Test
//    public void sinksDecoderTest() throws FileNotFoundException, IOException {
//        Path path = Paths.get(testFilesPath.toString(), "getSinksResponse.json");
//        File jsonResponse = path.toFile();
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule sm = new SimpleModule();
//        sm.addDeserializer(Sinks.class, new SinksDecoder());
//        objectMapper.registerModule(sm);
//        
//        Sinks sinks = objectMapper.readValue(jsonResponse, Sinks.class);
//        Assert.isTrue(sinks.getSinks().size() == 2);
//        Sink sink = sinks.getSinks().get(0);
//        Assert.isTrue(sink.getName().equals("db-sink"));
//        List<AppOption> appOptions = sink.getOptions();
//        Assert.isTrue(!appOptions.isEmpty());
//        Sink logSink = sinks.getSinks().get(1);
//        Assert.isTrue(logSink.getName().equals("log-sink"));
//        List<AppOption> logSinkAppOptions = logSink.getOptions();
//        Assert.isTrue(!logSinkAppOptions.isEmpty());
//    }
//    
//}