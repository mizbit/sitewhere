/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.configuration;

import com.sitewhere.datastore.DatastoreDefinition;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps device state YAML configuration to objects.
 */
public class DeviceStateTenantConfiguration implements ITenantEngineConfiguration {

    /** Datastore definition */
    private DatastoreDefinition datastore;

    public DatastoreDefinition getDatastore() {
	return datastore;
    }

    public void setDatastore(DatastoreDefinition datastore) {
	this.datastore = datastore;
    }
}