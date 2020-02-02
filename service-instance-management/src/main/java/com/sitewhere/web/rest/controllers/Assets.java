/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.AssetMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for asset operations.
 */
@Path("/api/assets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "assets")
public class Assets {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a new asset.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create a new asset")
    public Response createAsset(@RequestBody AssetCreateRequest request) throws SiteWhereException {
	return Response.ok(getAssetManagement().createAsset(request)).build();
    }

    /**
     * Get information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{assetToken}")
    @ApiOperation(value = "Get asset by token")
    public Response getAssetByToken(
	    @ApiParam(value = "Asset token", required = true) @PathParam("assetToken") String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(true);
	return Response.ok(helper.convert(existing)).build();
    }

    /**
     * Update an existing asset.
     * 
     * @param assetToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{assetToken}")
    @ApiOperation(value = "Update an existing hardware asset in category")
    public Response updateAsset(
	    @ApiParam(value = "Asset token", required = true) @PathParam("assetToken") String assetToken,
	    @RequestBody AssetCreateRequest request) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return Response.ok(getAssetManagement().updateAsset(existing.getId(), request)).build();
    }

    /**
     * Get label for asset based on a specific generator.
     * 
     * @param assetToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{assetToken}/label/{generatorId}")
    @Produces("image/png")
    @ApiOperation(value = "Get label for asset")
    public Response getAssetLabel(
	    @ApiParam(value = "Asset token", required = true) @PathParam("assetToken") String assetToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	ILabel label = getLabelGeneration().getAssetLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List assets matching criteria.
     * 
     * @param assetTypeToken
     * @param includeAssetType
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List assets matching criteria")
    public Response listAssets(
	    @ApiParam(value = "Limit by asset type", required = false) @QueryParam("assetTypeToken") String assetTypeToken,
	    @ApiParam(value = "Include asset type", required = false) @QueryParam("includeAssetType") @DefaultValue("false") boolean includeAssetType,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AssetSearchCriteria criteria = new AssetSearchCriteria(page, pageSize);
	criteria.setAssetTypeToken(assetTypeToken);

	// Perform search.
	ISearchResults<? extends IAsset> matches = getAssetManagement().listAssets(criteria);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(includeAssetType);

	List<IAsset> results = new ArrayList<IAsset>();
	for (IAsset asset : matches.getResults()) {
	    results.add(helper.convert(asset));
	}
	return Response.ok(new SearchResults<IAsset>(results, matches.getNumResults())).build();
    }

    /**
     * Delete information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{assetToken}")
    @ApiOperation(value = "Delete asset by token")
    public Response deleteAsset(
	    @ApiParam(value = "Asset token", required = true) @PathParam("assetToken") String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return Response.ok(getAssetManagement().deleteAsset(existing.getId())).build();
    }

    /**
     * Find an asset by token or throw an exception if not found.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    private IAsset assureAsset(String assetToken) throws SiteWhereException {
	IAsset asset = getAssetManagement().getAssetByToken(assetToken);
	if (asset == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetToken, ErrorLevel.ERROR);
	}
	return asset;
    }

    protected IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}