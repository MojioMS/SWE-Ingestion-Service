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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.data.Data;

public class OfferingDao
        extends
        AbstractDao {

    public OfferingDao(DaoFactory daoFactory) {
        super(daoFactory);
    }

    
    public OfferingEntity get(String identifier) {
        CriteriaBuilder builder = getDaoFactory().getSession().getCriteriaBuilder();
        CriteriaQuery<OfferingEntity> cq = builder.createQuery(OfferingEntity.class);
        Root<OfferingEntity> root = cq.from(OfferingEntity.class);
        cq.select(root).where(builder.equal(root.get(OfferingEntity.IDENTIFIER), identifier));
        Query<OfferingEntity> q = getSession().createQuery(cq);
        return q.uniqueResult();
    }
    
    public OfferingEntity updateMetadata(OfferingEntity offering, Data<?> first, Data<?> last, Object geometry) {
        if (offering.getSamplingTimeStart() == null || (offering.getSamplingTimeStart() != null
                && first.getSamplingTimeStart() != null
                && offering.getSamplingTimeStart().after(first.getSamplingTimeStart()))) {
            offering.setSamplingTimeStart(first.getSamplingTimeStart());
        }
        if (offering.getSamplingTimeEnd() == null || (offering.getSamplingTimeEnd() != null
                && last.getSamplingTimeEnd() != null
                && offering.getSamplingTimeEnd().before(last.getSamplingTimeEnd()))) {
            offering.setSamplingTimeEnd(last.getSamplingTimeEnd());
        }
        if (offering.getResultTimeStart() == null || (offering.getResultTimeStart() != null
                && first.getResultTime() != null
                && offering.getResultTimeStart().after(first.getResultTime()))) {
            offering.setResultTimeStart(first.getResultTime());
        }
        if (offering.getResultTimeEnd() == null || (offering.getResultTimeEnd() != null
                && last.getResultTime() != null
                && offering.getResultTimeEnd().before(last.getResultTime()))) {
            offering.setResultTimeEnd(last.getResultTime());
        }
//        if (offering.getValidTimeStart() == null || (offering.getValidTimeStart() != null
//                && observation.getValidTimeStart() != null
//                && offering.getValidTimeStart().after(observation.getValidTimeStart()))) {
//            offering.setValidTimeStart(observation.getValidTimeStart());
//        }
//        if (offering.getValidTimeEnd() == null || (offering.getValidTimeEnd() != null
//                && observation.getValidTimeEnd() != null
//                && offering.getValidTimeEnd().before(observation.getValidTimeEnd()))) {
//            offering.setValidTimeEnd(observation.getValidTimeEnd());
//        }
//        if (observation.isSetGeometryEntity()) {
//            if (offering.isSetGeometry()) {
//                offering.getGeometryEntity().getGeometry().union(observation.getGeometryEntity().getGeometry());
//            } else {
//                offering.setGeometryEntity(observation.getGeometryEntity());
//            }
//        } else if (observation.getDataset().isSetFeature() && observation.getDataset().getFeature().isSetGeometry()) {
//            if (offering.isSetGeometry()) {
//                offering.getGeometryEntity().getGeometry()
//                        .union(observation.getDataset().getFeature().getGeometryEntity().getGeometry());
//            } else {
//                offering.setGeometryEntity(observation.getDataset().getFeature().getGeometryEntity());
//            }
//        }
        getSession().saveOrUpdate(offering);
        return offering;
    }
}
