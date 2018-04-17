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
package org.n52.stream.generate;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.elements.SmlPosition;
import org.n52.shetland.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.util.JTSHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to create a {@link InsertSensorRequest} from the
 * {@link AbstractProcess}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 1.0.0
 *
 */
public class InsertSensorGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSensorGenerator.class);

    /**
     * Create {@link InsertSensorRequest} from {@link AbstractProcess}
     * 
     * @param process
     *            The {@link AbstractProcess} to create
     *            {@link InsertSensorRequest} from
     * @return The generated {@link InsertSensorRequest}.
     */
    public InsertSensorRequest generate(AbstractProcess process) {
        InsertSensorRequest request = new InsertSensorRequest(Sos2Constants.SOS, Sos2Constants.SERVICEVERSION);
        request.setProcedureDescription(new SosProcedureDescription<AbstractFeature>(process));
        request.setProcedureDescriptionFormat(process.getDefaultElementEncoding());
        request.setObservableProperty(getObservableProperties(process));
        checkForFeature(process);
        request.setMetadata(getSosInsertionMetadata(process));
        return request;
    }

    /**
     * Get the observable properties identifier from the outputs of the
     * {@link AbstractProcess}
     * 
     * @param process
     *            the {@link AbstractProcess} to process
     * @return {@link List} of observable properties identifier
     */
    private List<String> getObservableProperties(AbstractProcess process) {
        List<String> list = new LinkedList<>();
        if (process.isSetOutputs()) {
            for (SmlIo smlIo : process.getOutputs()) {
                if (smlIo.getIoValue().isSetDefinition()) {
                    list.add(smlIo.getIoValue().getDefinition());
                }
            }
        }
        return list;
    }

    /**
     * Create the {@link SosInsertionMetadata} from the {@link AbstractProcess}
     * 
     * @param process
     *            the {@link AbstractProcess} to process
     * @return Created {@link SosInsertionMetadata}
     */
    private SosInsertionMetadata getSosInsertionMetadata(AbstractProcess process) {

        SosInsertionMetadata metadata = new SosInsertionMetadata();
        metadata.setFeatureOfInterestTypes(getFeatureTypes(process));
        metadata.setObservationTypes(getObservationType(process));
        return metadata;
    }

    /**
     * Get the observation types from the swe elements defined in the output.
     * 
     * @param process
     *            the {@link AbstractProcess} to process
     * @return {@link Collection} of observation types
     */
    private Collection<String> getObservationType(AbstractProcess process) {
        List<String> list = new LinkedList<>();
        if (process.isSetOutputs()) {
            for (SmlIo smlIo : process.getOutputs()) {
                list.add(getObservationTypeByOutput(smlIo.getIoValue()));
            }
        }
        return list;
    }

    /**
     * Get observation type from {@link SweAbstractDataComponent}. Default is
     * {@link OmConstants#OBS_TYPE_UNKNOWN}
     * 
     * @param ioValue
     *            The {@link SweAbstractDataComponent} to get type from
     * @return the observation type
     */
    private String getObservationTypeByOutput(SweAbstractDataComponent ioValue) {
        SweConstants.SweDataComponentType.values();
        switch (ioValue.getDataComponentType()) {
            case Boolean:
                return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
            case Category:
                return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
            case Count:
                return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
            case DataArray:
                return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
            case DataRecord:
                return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
            case Quantity:
                return OmConstants.OBS_TYPE_MEASUREMENT;
            case Text:
                return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
            case Time:
                return OmConstants.OBS_TYPE_TEMPORAL_OBSERVATION;
            default:
                return OmConstants.OBS_TYPE_UNKNOWN;
        }
    }

    /**
     * Get the feature types from the {@link SmlFeatureOfInterest}. If features
     * are encoded, get from type or from geometry. Default value is
     * {@link SfConstants#SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT}
     * 
     * @param process
     *            the {@link AbstractProcess} to process
     * @return {@link Collection} of feature types
     */
    private Collection<String> getFeatureTypes(AbstractProcess process) {
        Set<String> set = new HashSet<>();
        if (process instanceof AbstractProcessV20 && ((AbstractProcessV20) process).isSetSmlFeatureOfInterest()) {
            for (AbstractFeature feature : ((AbstractProcessV20) process).getSmlFeatureOfInterest()
                    .getFeaturesOfInterestMap().values()) {
                if (feature != null && feature instanceof AbstractSamplingFeature) {
                    if (((AbstractSamplingFeature) feature).isSetFeatureType()) {
                        set.add(((AbstractSamplingFeature) feature).getFeatureType());
                    } else if (((AbstractSamplingFeature) feature).isSetGeometry()) {
                        set.add(getFeatureTypeByGeometry(((AbstractSamplingFeature) feature).getGeometry()));
                    }
                }
            }
        }
        if (set.isEmpty()) {
            set.add(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        }
        return set;
    }

    /**
     * Get feature type from geometry type
     * 
     * @param geometry
     *            the {@link Geometry} to get type from
     * @return the feature type
     */
    private String getFeatureTypeByGeometry(Geometry geometry) {
        switch (geometry.getGeometryType()) {
            case "Point":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT;
            case "LineString":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE;
            case "Polygon":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE;
            default:
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_FEATURE;
        }
    }

    /**
     * Check if featureOfInterest has no geometry and a SML position is defined,
     * then create feature geometry from SML position.
     * 
     * @param process
     *            {@link AbstractProcess} to check
     */
    private void checkForFeature(AbstractProcess process) {
        if (process instanceof AbstractPhysicalProcess && ((AbstractPhysicalProcess) process).getPosition() != null
                && (((AbstractPhysicalProcess) process).getPosition().isSetPosition()
                        || ((AbstractPhysicalProcess) process).getPosition().isSetVector())) {
            AbstractPhysicalProcess app = (AbstractPhysicalProcess) process;
            if (app.isSetSmlFeatureOfInterest()) {
                for (AbstractFeature feature : app.getSmlFeatureOfInterest().getFeaturesOfInterestMap().values()) {
                    if (feature != null && feature instanceof AbstractSamplingFeature
                            && !((AbstractSamplingFeature) feature).isSetGeometry()) {
                        ((AbstractSamplingFeature) feature).setGeometry(createGeometry(app.getPosition()));
                    }
                }
            }
        }
    }

    /**
     * Create a {@link Geometry} from {@link SmlPosition}
     * 
     * @param position
     *            {@link SmlPosition} to create {@link Geometry} from
     * 
     * @return {@link Geometry} or null if northing/easting can not be parsed.
     */
    private Geometry createGeometry(SmlPosition position) {
        String northing = getNorthing(position.getPosition());
        String easting = getEasting(position.getPosition());
        if (northing != null && easting != null) {
            String wkt = "POINT (" + northing + " " + easting + ")";
            int srid = 4326;
            if (position.isSetReferenceFrame()) {
                String rf = position.getReferenceFrame();
                try {
                    srid = Integer.parseInt(rf.startsWith("http") ? rf.substring(rf.lastIndexOf("/"))
                            : rf.startsWith("urn") ? rf.substring(rf.lastIndexOf(":")) : "4326");
                } catch (Exception e) {
                    LOG.warn("The referenceFrame contains an unknown format!", e);
                }

            }
            try {
                return JTSHelper.createGeometryFromWKT(wkt, srid);
            } catch (ParseException e) {
                LOG.error("Error while creating geometry", e);
            }
        }
        return null;
    }

    /**
     * Get northing coordinate
     * 
     * @param position
     *            {@link SweCoordinate}s to check for northing coordinate
     * @return the northing coordinate
     */
    private String getNorthing(List<? extends SweCoordinate<? extends Number>> position) {
        for (SweCoordinate<? extends Number> c : position) {
            if (SweCoordinateNames.isY(c.getName())) {
                return c.getValue().getValue().toString();
            }
        }
        return null;
    }

    /**
     * Get easting coordinate
     * 
     * @param position
     *            {@link SweCoordinate}s to check for easting coordinate
     * @return the easting coordinate
     */
    private String getEasting(List<? extends SweCoordinate<? extends Number>> position) {
        for (SweCoordinate<? extends Number> c : position) {
            if (SweCoordinateNames.isX(c.getName())) {
                return c.getValue().getValue().toString();
            }
        }
        return null;
    }

}
