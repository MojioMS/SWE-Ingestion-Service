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
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.stream.core.Feature;

public class FeatureDao
        extends
        AbstractDao {

    public FeatureDao(DaoFactory daoFactory) {
        super(daoFactory);
    }

    public AbstractFeatureEntity getOrInsert(Feature f) {
        AbstractFeatureEntity feature = get(f.getId());
        if (feature == null) {
            feature = new FeatureEntity();
            feature.setIdentifier(f.getId());

            FormatEntity type = getDaoFactory().getFormatDao().getOrInsert("http://www.opengis.net/def/nil/OGC/0/unknown");
            feature.setFeatureType(type);
//            if (f.getGeometry() != null) {
//                feature.setGeometry(f.getGeometry());
//            }
            getSession().save(feature);
        }
        // don't flush here because we may be batching
        return feature;
    }
    
    public AbstractFeatureEntity get(String identifier) {
        CriteriaBuilder builder = getDaoFactory().getSession().getCriteriaBuilder();
        CriteriaQuery<AbstractFeatureEntity> cq = builder.createQuery(AbstractFeatureEntity.class);
        Root<AbstractFeatureEntity> root = cq.from(AbstractFeatureEntity.class);
        cq.select(root).where(builder.equal(root.get(AbstractFeatureEntity.IDENTIFIER), identifier));
        Query<AbstractFeatureEntity> q = getSession().createQuery(cq);
        return q.uniqueResult();
    }
}
