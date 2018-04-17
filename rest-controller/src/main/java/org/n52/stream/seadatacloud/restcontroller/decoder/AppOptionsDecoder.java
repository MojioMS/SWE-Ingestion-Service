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
package org.n52.stream.seadatacloud.restcontroller.decoder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import org.n52.stream.seadatacloud.restcontroller.model.AppOption;
import org.n52.stream.seadatacloud.restcontroller.model.AppOptions;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
public class AppOptionsDecoder extends BaseDeserializer<AppOptions> {

    @Override
    public AppOptions deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        AppOptions results = new AppOptions();

        JsonNode node = jp.readValueAsTree();

        ArrayNode options = (ArrayNode) node.get("options");
        
        ArrayList<AppOption> resultOptions = new ArrayList();
        options.forEach((op) -> {
            AppOption ao = new AppOption();
            ao.setName(op.get("name").asText());
            ao.setDescription(op.get("description").asText());
            ao.setDefaultValue(op.get("defaultValue").asText());
            ao.setType(op.get("type").asText());
            resultOptions.add(ao);
        });
        results.setAppOptions(resultOptions);

        return results;
    }

}