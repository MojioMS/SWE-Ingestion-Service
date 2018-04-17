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
import java.util.AbstractMap.SimpleEntry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * Feature
 */
@Validated
public class Feature implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("properties")
    @Valid
    private List<SimpleEntry<String, String>> properties = null;

    @JsonProperty("geometry")
    private Object geometry = null;

    public Feature withId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Feature withProperties(List<SimpleEntry<String, String>> properties) {
        this.properties = properties;
        return this;
    }

    public Feature addPropertiesItem(SimpleEntry<String, String> propertiesItem) {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(propertiesItem);
        return this;
    }

    @Valid
    public List<SimpleEntry<String, String>> getProperties() {
        return properties;
    }

    public void setProperties(List<SimpleEntry<String, String>> properties) {
        this.properties = properties;
    }

    public Feature withGeometry(Object geometry) {
        this.geometry = geometry;
        return this;
    }

    public Object getGeometry() {
        return geometry;
    }

    public void setGeometry(Object geometry) {
        this.geometry = geometry;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Feature feature = (Feature) o;
        return Objects.equals(id, feature.id) &&
                Objects.equals(properties, feature.properties) &&
                Objects.equals(geometry, feature.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, properties, geometry);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Feature {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
        sb.append("    geometry: ").append(toIndentedString(geometry)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Feature clone() {
        try {
            Feature f = (Feature) super.clone();
            f.properties = new LinkedList<>();
            if (properties != null && !properties.isEmpty()) {
                properties.forEach(p -> f.properties.add(new SimpleEntry<>(p.getKey(), p.getValue())));
            }
            return f;
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
