/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceType;

@Entity
@Table(name = "device_type", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_DEVICE_TYPE_BY_TOKEN, query = "SELECT t FROM RdbDeviceType t WHERE t.token = :token")
public class RdbDeviceType extends RdbBrandedEntity implements IDeviceType {

    /** Serial version UID */
    private static final long serialVersionUID = 9005645694403373144L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Name */
    @Column(name = "name")
    private String name;

    /** Decription */
    @Column(name = "description")
    private String description;

    /** Device container policy */
    @Column(name = "container_policy")
    @Enumerated(EnumType.STRING)
    private DeviceContainerPolicy containerPolicy = DeviceContainerPolicy.Standalone;

    @Column(name = "device_element_schema_id", insertable = false, updatable = false)
    private UUID deviceElementSchemaId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_element_schema_id")
    private RdbDeviceElementSchema deviceElementSchema;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_type_metadata", joinColumns = @JoinColumn(name = "device_type_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @JsonIgnore
    @OneToMany(mappedBy = "deviceType", fetch = FetchType.LAZY)
    private List<RdbDeviceCommand> commands;

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.IAccessible#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.common.IAccessible#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getContainerPolicy()
     */
    @Override
    public DeviceContainerPolicy getContainerPolicy() {
	return containerPolicy;
    }

    public void setContainerPolicy(DeviceContainerPolicy containerPolicy) {
	this.containerPolicy = containerPolicy;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceType#getDeviceElementSchemaId()
     */
    @Override
    public UUID getDeviceElementSchemaId() {
	return deviceElementSchemaId;
    }

    public void setDeviceElementSchemaId(UUID deviceElementSchemaId) {
	this.deviceElementSchemaId = deviceElementSchemaId;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public List<RdbDeviceCommand> getCommands() {
	return commands;
    }

    public void setCommands(List<RdbDeviceCommand> commands) {
	this.commands = commands;
    }

    public static void copy(IDeviceType source, RdbDeviceType target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getDescription() != null) {
	    target.setDescription(source.getDescription());
	}
	if (source.getContainerPolicy() != null) {
	    target.setContainerPolicy(source.getContainerPolicy());
	}
	if (source.getDeviceElementSchemaId() != null) {
	    target.setDeviceElementSchemaId(source.getDeviceElementSchemaId());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
