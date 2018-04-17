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
package org.n52.stream.seadatacloud.dbsink;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.stream.core.Configuration;
import org.n52.stream.core.DataMessage;
import org.n52.stream.core.Measurement;
import org.n52.stream.core.Timeseries;
import org.n52.stream.seadatacloud.dbsink.dao.DaoFactory;
import org.n52.stream.seadatacloud.dbsink.dao.DatasetDao;
import org.n52.stream.seadatacloud.dbsink.dao.ObservationDao;
import org.n52.stream.seadatacloud.dbsink.dao.OfferingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication(scanBasePackages={"org.n52.stream.util"})
@EnableTransactionManagement
@Transactional
@EnableBinding(Sink.class)
@EnableConfigurationProperties(Configuration.class)
public class DatabaseSinkApplication {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSinkApplication.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private Configuration properties;

    @Autowired(required=false)
    @Named("sensorml")
    private AggregateProcess processDescription;
    
    public static void main(String[] args) {
        SpringApplication.run(DatabaseSinkApplication.class, args);
    }

    /**
     * Init the processor by checking the properties and finalize the custom configuration
     */
    @PostConstruct
    public void init() {
        LOG.info("init(); processor called");
        checkSetting("offering", properties.getOffering());
        checkSetting("sensor", properties.getSensor());
    }

    @Transactional(rollbackFor=Exception.class)
    @StreamListener(Sink.INPUT)
    public synchronized void input(DataMessage message) {
        Session session = null;
        try {
            session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
            DaoFactory daoFactory = new DaoFactory(session);
            session.beginTransaction();
            DatasetDao datasetDao = daoFactory.getDatasetDao();
            OfferingDao offeringDao = daoFactory.getOfferingDao();
            for (Timeseries<?> series : message.getTimeseries()) {
                if (series.getMeasurements() != null && !series.getMeasurements().isEmpty()) {
                    ProcedureEntity procedure = daoFactory.getProcedureDAO().get(series.getSensor());
                    if (procedure != null && properties.getSensor().equals(series.getSensor())) {
                        DatasetEntity datasetEntity = datasetDao.getOrInsert(series, getOutputs(), properties.getOffering());
                        if (datasetEntity != null) {
                            ObservationDao observationDao = daoFactory.getObservationDao();
                            Data<?> first = null;
                            Data<?> last = null;
                            for (Measurement<?> m : series.getMeasurements()) {
                                Data<?> data = observationDao.persist(m, datasetEntity, getOutputs());
                                first = updateFirst(first, data);
                                last = updateLast(last, data);
                            }
                            // update dataset and offering with times and geometry
                            datasetDao.updateMetadata(datasetEntity, first, last);
                            offeringDao.updateMetadata(datasetEntity.getOffering(), first, last, null);
                        }
                    }
                }
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error("error", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.clear();
                session.close();
            }
        }
        LOG.info("Received processor output:\n{}", message);
    }

    private Data<?> updateFirst(Data<?> first, Data<?> data) {
        return  first == null || first.getSamplingTimeStart().after(data.getSamplingTimeStart()) ? data : first;
    }

    private Data<?> updateLast(Data<?> last, Data<?> data) {
        return last == null ||last.getSamplingTimeEnd().before(data.getSamplingTimeEnd()) ? data : last;
    }

    private List<SmlIo> getOutputs() {
        if (processDescription != null) {
            if (processDescription.isSetOutputs()) {
                return processDescription.getOutputs();
            } else  if (processDescription.isSetComponents() 
                    && processDescription.getComponents().get(processDescription.getComponents().size()-1).isSetProcess()
                    && processDescription.getComponents().get(processDescription.getComponents().size()-1).getProcess() instanceof AbstractProcess
                    && ((AbstractProcess) processDescription.getComponents().get(processDescription.getComponents().size()-1).getProcess()).isSetOutputs()) {
                    return ((AbstractProcess) processDescription.getComponents().get(processDescription.getComponents().size()-1).getProcess()).getOutputs();
            }
        }
        return Collections.emptyList();
    }
    
    private IllegalArgumentException logErrorAndCreateException(String msg) throws IllegalArgumentException {
        LOG.error(msg);
        return new IllegalArgumentException(msg);
    }

    private void checkSetting(String settingName, String setting) throws IllegalArgumentException {
        if (setting == null || setting.isEmpty()) {
            throw logErrorAndCreateException(String.format("setting '%s' not set correct. Received value: '%s'.",
                    settingName,
                    setting));
        }
        LOG.trace("'{}': '{}'", settingName, setting);
    }

}
