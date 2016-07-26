/**
 * Copyright (c) 2016 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowplugin.applications.frsync.impl.clustering;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonService;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonServiceRegistration;
import org.opendaylight.mdsal.singleton.common.api.ServiceGroupIdentifier;
import org.opendaylight.openflowplugin.applications.frsync.util.ReconciliationRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ClusterSingletonService} clusterSingletonServiceRegistration per connected device.
 */
public class DeviceMastership implements ClusterSingletonService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceMastership.class);
    private final NodeId nodeId;
    private final ServiceGroupIdentifier identifier;
    private final ReconciliationRegistry reconciliationRegistry;
    private ClusterSingletonServiceRegistration clusterSingletonServiceRegistration;
    private boolean deviceMastered;

    public DeviceMastership(final NodeId nodeId, final ReconciliationRegistry reconciliationRegistry) {
        this.nodeId = nodeId;
        this.identifier = ServiceGroupIdentifier.create(nodeId.getValue());
        this.reconciliationRegistry = reconciliationRegistry;
        this.deviceMastered = false;
    }

    @Override
    public void instantiateServiceInstance() {
        LOG.debug("FRS started for: {}", nodeId.getValue());
        deviceMastered = true;
        reconciliationRegistry.register(nodeId);
    }

    @Override
    public ListenableFuture<Void> closeServiceInstance() {
        LOG.debug("FRS stopped for: {}", nodeId.getValue());
        deviceMastered = false;
        reconciliationRegistry.unregisterIfRegistered(nodeId);
        return Futures.immediateFuture(null);
    }

    @Override
    public ServiceGroupIdentifier getIdentifier() {
        return identifier;
    }

    public boolean isDeviceMastered() {
        return deviceMastered;
    }

    public void setClusterSingletonServiceRegistration(final ClusterSingletonServiceRegistration registration) {
        this.clusterSingletonServiceRegistration = registration;
    }

    public ClusterSingletonServiceRegistration getClusterSingletonServiceRegistration() {
        return clusterSingletonServiceRegistration;
    }

}
