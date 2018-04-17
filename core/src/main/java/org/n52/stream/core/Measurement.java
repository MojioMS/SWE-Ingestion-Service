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
import java.time.OffsetDateTime;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.springframework.validation.annotation.Validated;

/**
 * Measurement
 */
@Validated
public class Measurement<T> implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("resultTime")
    private OffsetDateTime resultTime = null;

    @JsonProperty("phenomenonTime")
    private OffsetDateTime phenomenonTime = null;

    @JsonProperty("value")
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="type", defaultImpl = Object.class)
    private T value = null;

    public Measurement<T> withPhenomenonTime(OffsetDateTime phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    public OffsetDateTime getPhenomenonTime() {
        return phenomenonTime;
    }

    public void setPhenomenonTime(OffsetDateTime phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Measurement<T> withResultTime(OffsetDateTime resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    public OffsetDateTime getResultTime() {
        return resultTime;
    }

    public void setResultTime(OffsetDateTime resultTime) {
        this.resultTime = resultTime;
    }

    public Measurement<T> withValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Measurement<?> measurement = (Measurement<?>) o;
        return Objects.equals(resultTime, measurement.resultTime) &&
                Objects.equals(phenomenonTime, measurement.phenomenonTime) &&
                Objects.equals(value, measurement.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultTime, phenomenonTime, value);
    }

    @Override
    public Measurement<T> clone() {
        try {
            @SuppressWarnings("unchecked")
            Measurement<T> m = (Measurement<T>) super.clone();
            return m;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Measurement {\n");
        sb.append("    resultTime    : ").append(toIndentedString(resultTime)).append("\n");
        sb.append("    phenomenonTime: ").append(toIndentedString(phenomenonTime)).append("\n");
        sb.append("    value         : ").append(toIndentedString(value)).append("\n");
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
