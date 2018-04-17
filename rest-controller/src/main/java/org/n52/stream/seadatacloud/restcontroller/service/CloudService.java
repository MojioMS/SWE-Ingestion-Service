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
package org.n52.stream.seadatacloud.restcontroller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.n52.stream.seadatacloud.restcontroller.model.Processors;
import org.n52.stream.seadatacloud.restcontroller.model.Sinks;
import org.n52.stream.seadatacloud.restcontroller.model.Sources;
import org.n52.stream.seadatacloud.restcontroller.model.Stream;
import org.n52.stream.seadatacloud.restcontroller.model.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@Component
public class CloudService {

    private static final Logger LOG = LoggerFactory.getLogger(CloudService.class);

    @Autowired
    private ObjectMapper objectMapper;

    public static final String BASE_URL_FLOW_SERVER = "http://localhost:9393";

    public Sources getSources() {
        Sources sources = new Sources();
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/apps?type=source");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();

            sources = objectMapper.readValue(response, Sources.class);

        } catch (Exception e) {
            System.out.println(e);
            LOG.error(e.getMessage());
        }
        return sources;
    }

    public Processors getProcessors() {
        Processors processors = null;
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/apps?type=processor");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();

            processors = objectMapper.readValue(response, Processors.class);

        } catch (Exception e) {
            System.out.println(e);
            LOG.error(e.getMessage());
        }
        return processors;
    }

    public Sinks getSinks() {
        Sinks sinks = null;
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/apps?type=sink");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();

            sinks = objectMapper.readValue(response, Sinks.class);

        } catch (Exception e) {
            System.out.println(e);
            LOG.error(e.getMessage());
        }
        return sinks;
    }

    public String registerApp(String appName, String appType, String appUri) {
        String response = "";
        try {

            URL url = new URL(BASE_URL_FLOW_SERVER + "/apps/" + appType + "/" + appName);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(("uri=" + appUri).getBytes());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            response = res.toString() + "success.";

        } catch (IOException e) {
            response = e.getMessage();
            LOG.error(e.getMessage());
        } catch (Exception e) {
            response = e.getMessage();
            LOG.error(e.getMessage());
        }
        return response;
    }

    public Future<Stream> createStream(String streamName, String streamDefinition, boolean deploy) throws InterruptedException {
        CompletableFuture<Stream> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Stream stream = null;
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/definitions");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream;
            try {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.getOutputStream().write(("name=" + streamName + "&definition=" + streamDefinition + "&deploy=" + deploy).getBytes());
                int responseCode = conn.getResponseCode();
                if (responseCode >= 300) {
                    inputStream = conn.getErrorStream();
                    Scanner scanner = new Scanner(inputStream);
                    scanner.useDelimiter("\\Z");
                    String response = scanner.next();
                    LOG.error(response);
                }
                inputStream = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);
                String line;
                StringBuffer res = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    res.append(line);
                    res.append("\n");
                }
                in.close();
                conn.disconnect();
                String response = res.toString();
                stream = objectMapper.readValue(response, Stream.class);

            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }

            completableFuture.complete(stream);
            return stream;
        });

        return completableFuture;
    }

    ;

    public Stream undeployStream(String streamName) {
        Stream stream = null;
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/deployments/" + streamName);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();
            stream = objectMapper.readValue(response, Stream.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return stream;
    }

    public Stream deployStream(String streamName) {
        Stream stream = null;
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/deployments/" + streamName);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write("".getBytes());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();
            stream = objectMapper.readValue(response, Stream.class);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return stream;
    }

    public String deleteStream(String streamName) {
        String response = "";
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/definitions/" + streamName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            response = res.toString();
        } catch (Exception e) {
            response = e.getMessage();
        }
        return response;
    }

    public Streams getStreams() {
        Streams streams = new Streams();
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/definitions?size=100");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();

            streams = objectMapper.readValue(response, Streams.class);

        } catch (Exception e) {
            System.out.println(e);
        }
        return streams;
    }

    public Stream getStream(String streamId) {
        Stream stream = null;
        try {
            URL url = new URL(BASE_URL_FLOW_SERVER + "/streams/definitions/" + streamId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer res = new StringBuffer();
            while ((line = in.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            in.close();
            conn.disconnect();
            String response = res.toString();

            stream = objectMapper.readValue(response, Stream.class);

        } catch (Exception e) {
            return null;
        }
        return stream;
    }

}