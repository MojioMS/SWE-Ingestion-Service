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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * <b>Timeseries&lt;T&gt;</b><br>
 * <br>
 * is a collection of {@link Measurement}s which share the following properties:
 * <ul>
 * <li>Unit</li>
 * <li>Phenomenon</li>
 * <li>Sensor</li>
 * <li>Feature</li>
 * </ul>
 * as explicit ones and the type as implicit via the generic parameter T.
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 */
@Validated
public class Timeseries<T> implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("unit")
    private String unit = null;

    @JsonProperty("phenomenon")
    private String phenomenon = null;

    @JsonProperty("sensor")
    private String sensor = null;

    @JsonProperty("feature")
    private Feature feature = null;

    @JsonProperty("measurements")
    @Valid
    private List<Measurement<T>> measurements = null;

    public Timeseries<T> withId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timeseries<T> withUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public boolean hasUnit() {
        return unit != null && !unit.isEmpty();
    }

    public Timeseries<T> withPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
        return this;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public Timeseries<T> withSensor(String sensor) {
        this.sensor = sensor;
        return this;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public Timeseries<T> withFeature(Feature feature) {
        this.feature = feature;
        return this;
    }

    @Valid
    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Timeseries<T> withMeasurements(List<Measurement<T>> measurements) {
        this.measurements = measurements;
        return this;
    }

    public Timeseries<T> addMeasurementsItem(Measurement<T> measurementsItem) {
        if (measurements == null) {
            measurements = new LinkedList<>();
        }
        measurements.add(measurementsItem);
        return this;
    }

    @Valid
    public List<Measurement<T>> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement<T>> measurements) {
        this.measurements = measurements;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Timeseries<?> timeseries = (Timeseries<?>) o;
        return Objects.equals(id, timeseries.id) &&
                Objects.equals(unit, timeseries.unit) &&
                Objects.equals(phenomenon, timeseries.phenomenon) &&
                Objects.equals(sensor, timeseries.sensor) &&
                Objects.equals(feature, timeseries.feature) &&
                Objects.equals(measurements, timeseries.measurements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unit, phenomenon, sensor, feature, measurements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Timeseries {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
        sb.append("    phenomenon: ").append(toIndentedString(phenomenon)).append("\n");
        sb.append("    sensor: ").append(toIndentedString(sensor)).append("\n");
        sb.append("    feature: ").append(toIndentedString(feature)).append("\n");
        sb.append("    measurements: ").append(toIndentedString(measurements)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Timeseries<T> clone() {
        try {
            @SuppressWarnings("unchecked")
            Timeseries<T> ts = (Timeseries<T>) super.clone();
            if (feature != null) {
                ts.feature = this.feature.clone();
            }
            ts.measurements = new LinkedList<>();
            if (this.measurements != null && !this.measurements.isEmpty()) {
                this.measurements.forEach(m -> ts.measurements.add(m.clone()));
            }
            return ts;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
