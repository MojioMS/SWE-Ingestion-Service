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
package org.n52.stream.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MeasurementTest {

    protected ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    protected String write(Object object) throws Exception {
        Writer writer = new StringWriter();
        mapper.writeValue(writer, object);
        return writer.toString();
    }

    protected <T> T read(String source, Class<T> targetType) throws Exception {
        return mapper.readValue(source, targetType);
    }

    @Test
    public void shouldKeepMeasurementType() throws Exception {
        Measurement<BigDecimal> m = new Measurement<>();
        m.setValue(new BigDecimal(2));

        String json = write(m);
        Measurement<?> m2 = read(json, Measurement.class);
        assertThat(m2.getValue(), instanceOf(BigDecimal.class));
        assertThat(m.equals(m2), is(true));
    }

}
