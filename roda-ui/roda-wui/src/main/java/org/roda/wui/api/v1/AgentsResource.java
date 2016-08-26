/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.api.v1;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.roda.core.common.UserUtility;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.v2.agents.Agent;
import org.roda.core.data.v2.agents.Agents;
import org.roda.core.data.v2.ip.metadata.IndexedPreservationAgent;
import org.roda.core.data.v2.user.User;
import org.roda.wui.api.controllers.Browser;
import org.roda.wui.api.v1.utils.ApiResponseMessage;
import org.roda.wui.api.v1.utils.ApiUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path(AgentsResource.ENDPOINT)
@Api(value = AgentsResource.SWAGGER_ENDPOINT)
public class AgentsResource {
  public static final String ENDPOINT = "/v1/agents";
  public static final String SWAGGER_ENDPOINT = "v1 agents";

  @Context
  private HttpServletRequest request;

  @GET
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "List Agents", notes = "Gets a list of Agents.", response = IndexedPreservationAgent.class, responseContainer = "List")
  public Response listAgents(
    @ApiParam(value = "Index of the first element to return", defaultValue = "0") @QueryParam(RodaConstants.API_QUERY_KEY_START) String start,
    @ApiParam(value = "Maximum number of elements to return", defaultValue = "100") @QueryParam(RodaConstants.API_QUERY_KEY_LIMIT) String limit,
    @ApiParam(value = "Choose format in which to get the agents", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    Agents agents = (Agents) Browser.retrieveObjects(user, Agent.class, start, limit, acceptFormat);
    return Response.ok(agents, mediaType).build();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Creates a new Agent", notes = "Creates a new Agent.", response = Agent.class)
  public Response createAgent(Agent agent,
    @ApiParam(value = "Choose format in which to get the agent", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    Agent newAgent = org.roda.wui.api.controllers.Agents.createAgent(user, agent);
    return Response.ok(newAgent, mediaType).build();
  }

  @PUT
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Updates an Agent", notes = "Updates an Agent.", response = Agent.class)
  public Response updateAgent(Agent agent,
    @ApiParam(value = "Choose format in which to get the agent", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    Agent updatedAgent = org.roda.wui.api.controllers.Agents.updateAgent(user, agent);
    return Response.ok(updatedAgent, mediaType).build();
  }

  @GET
  @Path("/{" + RodaConstants.API_PATH_PARAM_AGENT_ID + "}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get Agent", notes = "Gets a particular Agent.", response = Agent.class)
  public Response getAgent(@PathParam(RodaConstants.API_PATH_PARAM_AGENT_ID) String agentId,
    @ApiParam(value = "Choose format in which to get the agent", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    Agent agent = org.roda.wui.api.controllers.Browser.retrieve(user, Agent.class, agentId);
    return Response.ok(agent, mediaType).build();
  }

  @DELETE
  @Path("/{" + RodaConstants.API_PATH_PARAM_AGENT_ID + "}")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Delete Agent", notes = "Delete a particular Agent.", response = ApiResponseMessage.class)
  public Response deleteAgent(@PathParam(RodaConstants.API_PATH_PARAM_AGENT_ID) String agentId,
    @ApiParam(value = "Choose format in which to get the result", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    org.roda.wui.api.controllers.Agents.deleteAgent(user, agentId);
    return Response.ok(new ApiResponseMessage(ApiResponseMessage.OK, "Agent deleted"), mediaType).build();
  }
}
