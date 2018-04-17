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
package org.n52.stream.seadatacloud.dbsink.dao;

import org.hibernate.Session;

public class DaoFactory {

    private Session session;

    public DaoFactory(Session session) {
        this.session =  session;
    }
    
    public Session getSession() {
        return session;
    }

    public DatasetDao getDatasetDao() {
        return new DatasetDao(this);
    }

    public ObservationDao getObservationDao() {
        return new ObservationDao(this);
    }

    public ProcedureDao getProcedureDAO() {
        return new ProcedureDao(this);
    }
    
    public PhenomenonDao getPhenomenonDao() {
        return new PhenomenonDao(this);
    }
    
    public CategoryDao getCategoryDao() {
        return new CategoryDao(this);
    }
    
    public OfferingDao getOfferingDao() {
        return new OfferingDao(this);
    }
    
    public FeatureDao getFeatureDao() {
        return new FeatureDao(this);
    }

    public FormatDao getFormatDao() {
        return new FormatDao(this);
    }

    public UnitDao getUnitDao() {
        return new UnitDao(this);
    }
}
