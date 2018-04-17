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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.elements.SmlComponent;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sensorML.v20.SmlDataInterface;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.stream.generate.InsertSensorGenerator;
import org.n52.stream.seadatacloud.restcontroller.model.AppOption;
import org.n52.stream.seadatacloud.restcontroller.model.Source;
import org.n52.stream.seadatacloud.restcontroller.model.Stream;
import org.n52.stream.seadatacloud.restcontroller.model.StreamStatus;
import org.n52.stream.seadatacloud.restcontroller.model.Streams;
import org.n52.stream.seadatacloud.restcontroller.service.CloudService;
import org.n52.stream.seadatacloud.restcontroller.util.DataRecordDefinitions;
import org.n52.stream.seadatacloud.restcontroller.util.StreamNameURLs;
import org.n52.stream.util.DecoderHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@RestController
@Component
@ImportResource("classpath*:/svalbard-*.xml")
@RequestMapping("/api/streams")
public class StreamController {

    private static final Logger LOG = LoggerFactory.getLogger(StreamController.class);

    public final String APPLICATION_JSON = "application/json";
    public final String APPLICATION_XML = "application/xml";
    private final HttpHeaders CONTENT_TYPE_APPLICATION_JSON = new HttpHeaders();
    private final HttpHeaders CONTENT_TYPE_APPLICATION_XML = new HttpHeaders();
    private final HttpHeaders HEADER_ACCEPT_ALL = new HttpHeaders();
    private final HttpHeaders HEADER_ACCEPT_XML = new HttpHeaders();

    @Autowired
    CloudService service;

    @Autowired
    AppController appController;

    @Autowired
    private DecoderRepository decoderRepository;

    @Autowired
    private DataRecordDefinitions dataRecordDefinitions;

    @Autowired
    private StreamNameURLs streamNameURLs;

    @PostConstruct
    public void init() {
        this.dataRecordDefinitions.add("https://52north.org/swe-ingestion/mqtt/3.1", "mqtt-source-rabbit");
        CONTENT_TYPE_APPLICATION_JSON.setContentType(MediaType.APPLICATION_JSON);
        CONTENT_TYPE_APPLICATION_XML.setContentType(MediaType.APPLICATION_XML);
        List<MediaType> defaultMediaType = new ArrayList();
        defaultMediaType.add(MediaType.TEXT_HTML);
        defaultMediaType.add(MediaType.TEXT_PLAIN);
        defaultMediaType.add(MediaType.APPLICATION_JSON);
        defaultMediaType.remove(MediaType.APPLICATION_XML);
        HEADER_ACCEPT_ALL.setAccept(defaultMediaType);
        List<MediaType> xmlMediaType = new ArrayList();
        xmlMediaType.add(MediaType.APPLICATION_XML);
        HEADER_ACCEPT_XML.setAccept(xmlMediaType);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = APPLICATION_XML, produces = APPLICATION_JSON)
    public ResponseEntity<Stream> uploadConfig(
            @RequestBody byte[] requestBody) {
        String streamName = UUID.randomUUID().toString();
        try {
            String streamXML = new String(requestBody);

            DecoderHelper helper = new DecoderHelper();
            decoderRepository.init();
            helper.setDecoderRepository(decoderRepository);

            Object decode = helper.decode(streamXML);

            if (decode instanceof AggregateProcess) {
                ArrayList<SmlComponent> al = (ArrayList<SmlComponent>) ((AggregateProcess) decode).getComponents();
                SmlComponent comp = al.get(0);
                // TODO: instanceof Abfragen:
                AbstractProcess asml = (AbstractProcess) comp.getProcess();
                ArrayList<SmlIo> smlOutputs = (ArrayList<SmlIo>) asml.getOutputs();
                SmlIo smlIo = smlOutputs.get(0);
                SmlDataInterface smlDataInterface = (SmlDataInterface) smlIo.getIoValue();
                SweDataRecord sdr = smlDataInterface.getInterfaceParameters();

                String sdrDefinition = sdr.getDefinition();
                String sourceName = "";
                Source source;

                if (dataRecordDefinitions.hasDataRecordDefinition(sdrDefinition)) {
                    source = appController.getSourceByName(dataRecordDefinitions.getSourceType(sdrDefinition));
                } else {
                    return new ResponseEntity("{\"error\":\"No supported Source found for DataRecord definition '" + sdrDefinition + "'\"}", HttpStatus.NOT_FOUND);
                }
                if (source == null) {
                    return new ResponseEntity("{ \"error\": \"DataRecord definition '" + sdrDefinition + "' is supposed to be supported by Source '" + sourceName + "', but Source '" + sourceName + "' not found.\"}", HttpStatus.NOT_FOUND);
                }
                sourceName = source.getName();

                LinkedList<SweField> sweFields = (LinkedList<SweField>) sdr.getFields();

                String streamSourceDefinition = "";
                String streamDefinition = "";

                for (SweField current : sweFields) {
                    SweText sweText = (SweText) current.getElement();
                    String optionUrl = sweText.getDefinition();
                    String appOptionName;
                    if (optionUrl.indexOf('#') > -1) {
                        appOptionName = optionUrl.substring(optionUrl.lastIndexOf('#') + 1);
                    } else {
                        return new ResponseEntity("{ \"error\": \"swe:Text definition '" + optionUrl + "' requires a hashtag ( # ) option.\"}", HttpStatus.BAD_REQUEST);
                    }
                    AppOption ao = appController.getSourceOptionByName(source, appOptionName);
                    if (ao == null) {
                        return new ResponseEntity("{ \"error\": \"Option '" + appOptionName + "' is not supported by source '" + sourceName + "'.\"}", HttpStatus.BAD_REQUEST);
                    }
                    streamSourceDefinition += " --" + ao.getName() + "=" + sweText.getValue();
                };
                if (streamSourceDefinition.length() > 0) {
                    streamDefinition = sourceName + streamSourceDefinition + " ";
                } else {
                    streamDefinition = sourceName + " ";
                }

                // TODO: parse processor...
                streamDefinition += "| csv-processor ";
                // TODO: parse sink...
                streamDefinition += "| db-sink";

                Stream createdStream = null;
                streamName = "s" + streamName;
                Future<Stream> futureStream = service.createStream(streamName, streamDefinition, false);

                createdStream = futureStream.get(15, TimeUnit.SECONDS);

                if (createdStream != null) {
                    this.streamNameURLs.add(streamName, streamXML);
                    // InserObservation:
                    InsertSensorGenerator generator = new InsertSensorGenerator();
                    AggregateProcess aggregateProcess = (AggregateProcess) decode;
                    InsertSensorRequest request = generator.generate((PhysicalSystem) aggregateProcess.getComponents().get(1).getProcess());
                    
                    return new ResponseEntity(createdStream, CONTENT_TYPE_APPLICATION_JSON, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity(null, CONTENT_TYPE_APPLICATION_JSON, HttpStatus.CONFLICT);
                }
            } else {
                return new ResponseEntity(null, CONTENT_TYPE_APPLICATION_JSON, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), CONTENT_TYPE_APPLICATION_JSON, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ResponseEntity<Streams> getStreams() {
        Streams result = service.getStreams();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{streamId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ResponseEntity<Stream> getStream(
            @PathVariable String streamId) {
        Stream result = service.getStream(streamId);
        if (result == null) {
            return new ResponseEntity("{ \"error\": \"stream with name '" + streamId + "' not found.\"}", CONTENT_TYPE_APPLICATION_JSON, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result, CONTENT_TYPE_APPLICATION_JSON, HttpStatus.OK);
    }

    @RequestMapping(value = "/{streamId}", method = RequestMethod.GET, produces = APPLICATION_XML)
    public ResponseEntity<Stream> getStreamSensorMLURL(
            @PathVariable String streamId) {
        Stream result = service.getStream(streamId);
        if (result == null) {
            return new ResponseEntity("{ \"error\": \"stream with name '" + streamId + "' not found.\"}", CONTENT_TYPE_APPLICATION_JSON, HttpStatus.NOT_FOUND);
        }
        if (this.streamNameURLs.hasStreamNameUrl(streamId)) {
            String SensormlURL = this.streamNameURLs.getSensormlURL(streamId);
            if (SensormlURL != null) {
                return new ResponseEntity(SensormlURL, CONTENT_TYPE_APPLICATION_XML, HttpStatus.OK);
            } else {
                return new ResponseEntity("{\"error\": \"no sensorML process decription found for stream '" + streamId + "'.\"}", CONTENT_TYPE_APPLICATION_JSON, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity("{\"error\": \"no sensorML process decription found for stream '" + streamId + "'.\"}", CONTENT_TYPE_APPLICATION_JSON, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE deletes a stream.
     *
     * @param streamId - name of the stream
     * @return succes or error message
     */
    @RequestMapping(value = "/{streamId}", produces = APPLICATION_JSON, method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteStream(
            @PathVariable String streamId) {
        String result = service.deleteStream(streamId);
        return new ResponseEntity(result, CONTENT_TYPE_APPLICATION_JSON, HttpStatus.OK);
    }

    @RequestMapping(value = "/{streamId}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON, method = RequestMethod.PUT)
    public ResponseEntity<Stream> putStream(
            @PathVariable String streamId,
            @RequestBody StreamStatus requestStatus) {
        Stream stream = service.getStream(streamId);
        if (stream == null) {
            return new ResponseEntity("{\"error\":\"Stream '" + streamId + "' not found.\"}", HttpStatus.NOT_FOUND);
        } else {
            String status = stream.getStatus();
            if (status.equals("deploying")) {
                return new ResponseEntity("{\"Accepted\": \"The Stream '" + streamId + "' is currently 'deploying' and thus, the resource' status will not be changed.\"}", HttpStatus.ACCEPTED);
            }
            if (requestStatus.getStatus() == null) {
                return new ResponseEntity("{\"error\":\"Request is missing required field 'status'.\"}", HttpStatus.BAD_REQUEST);
            }
            switch (requestStatus.getStatus()) {
                case "deployed":
                    Stream deployedStream = service.deployStream(streamId);
                    return new ResponseEntity(deployedStream, HttpStatus.OK);
                case "undeployed":
                    Stream undeployedStream = service.undeployStream(streamId);
                    return new ResponseEntity(undeployedStream, HttpStatus.OK);
                default:
                    return new ResponseEntity("{\"error\":\"The requested status '" + requestStatus.getStatus() + "' is not supported. Supported status are: 'deployed' and 'undeployed'.\"}", HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    
}
