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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DataMessage
 */
@Validated
public class DataMessage implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("timeseries")
    @Valid
    private List<Timeseries<?>> timeseries = new ArrayList<>();

    public DataMessage withId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataMessage withTimeseries(List<Timeseries<?>> timeseries) {
        this.timeseries.clear();
        this.timeseries.addAll(timeseries);
        return this;
    }

    public DataMessage addTimeseriesItem(Timeseries<?> timeseriesItem) {
        timeseries.add(timeseriesItem);
        return this;
    }

    @Valid
    public List<Timeseries<?>> getTimeseries() {
        return timeseries;
    }

    public Optional<Timeseries<?>> getTimeseriesForPhenomenon(String phenomenon) {
        return timeseries.stream().filter(p -> p.getPhenomenon().equalsIgnoreCase(phenomenon)).findFirst();
    }

    public void setTimeseries(List<Timeseries<?>> timeseries) {
        withTimeseries(timeseries);
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataMessage dataMessage = (DataMessage) o;
        return Objects.equals(id, dataMessage.id) &&
                Objects.equals(timeseries, dataMessage.timeseries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeseries);
    }

    @Override
    public DataMessage clone() {
        try {
            DataMessage dm = (DataMessage) super.clone();
            dm.timeseries = new LinkedList<>();
            if (timeseries != null && !timeseries.isEmpty()) {
                timeseries.forEach(t -> dm.timeseries.add(t.clone()));
            }
            return dm;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataMessage {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    timeseries: ").append(toIndentedString(timeseries)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
