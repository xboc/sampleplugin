package com.ca.cdd.plugins.sample;


import com.ca.rp.plugins.dto.model.*;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;


import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;














/**
 * Created by admin on 24/02/2016.
 * A sample task that can be used as a template for Continous Delivery Edition plugin tasks
 */
@Path("tasks/task1")
public class SampleTask {


    private static final String PARAMETER1 = "parameter1";
    private static final String PARAMETER2 = "parameter2";
    private static final String PARAMETER3 = "parameter3";
    private static final String FILTER = "filter";


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ExternalTaskResult execute(@QueryParam(TaskConstants.ACTION_PARAM) String action, ExternalTaskInputs taskInputs) {
        ExternalTaskResult externalTaskResult;


        if (TaskConstants.EXECUTE_ACTION.equalsIgnoreCase(action)) {
            externalTaskResult = executeTask(taskInputs);
        } else if (TaskConstants.STOP_ACTION.equalsIgnoreCase(action)) {
            externalTaskResult = stopTask(taskInputs);
        } else {
            throw new IllegalArgumentException("Unexpected invalid action '" + action + "' while trying to execute task.");
        }


        return externalTaskResult;
    }


    @POST
    @Path("/parameter1/values")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DynamicValuesOutput getDynamicValuesForParameter1(DynamicValuesInput dynamicValuesInput, @DefaultValue("") @QueryParam(FILTER) String filter) throws Exception {


        DynamicValuesOutput dynamicValuesOutput = null;


        dynamicValuesOutput = getDynamicValues(dynamicValuesInput, filter);


        return dynamicValuesOutput;
    }


    private ExternalTaskResult executeTask(ExternalTaskInputs taskInputs) {
        ExternalTaskResult externalTaskResult;


        try {
            Map < String, String > taskProperties = taskInputs.getTaskProperties();
            Map < String, String > endpointProperties = taskInputs.getEndPointProperties();


            String username;
            String password;
            String url;


            if (endpointProperties != null && !endpointProperties.isEmpty()) {
                username = endpointProperties.get("username");
                password = endpointProperties.get("password");
                url = endpointProperties.get("URL");
            } else {
                externalTaskResult = ExternalTaskResult.createResponseForFailure("Missing endpoint", "Unexpected invalid empty endpoint while trying to execute task");
                return externalTaskResult;
            }


            // These are the task inputs
            String parameter1 = taskProperties.get(PARAMETER1);
            String parameter2 = taskProperties.get(PARAMETER2);
            String parameter3 = taskProperties.get(PARAMETER3);


            Client httpClient = ClientBuilder.newClient();
            if (username != null && !username.isEmpty()) {
                HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
                httpClient.register(feature);
            }


            // http header support
            Invocation.Builder request = httpClient.target(url).request();
            try {
                addHeaders(request);
            } catch (Exception e) {
                externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to execute",
                        "Could not parse headers " + e.getLocalizedMessage());
                return externalTaskResult;
            }




            Response response;
            String method = "GET";
            String body = null;
            response = request.method(method, Entity.json(body));


            if (response != null) {
                int responseCode = response.getStatus();
                String responseBody = response.readEntity(String.class);
                return ExternalTaskResult.createResponseForFinished("Rest operation ended successfully", responseBody);
            } else {
                return ExternalTaskResult.createResponseForFailure("Failed to execute",
                        "No response");
            }
        } catch (Exception e) {
            externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to execute",
                    e.getLocalizedMessage());
        }


        return externalTaskResult;
    }


    private ExternalTaskResult stopTask(ExternalTaskInputs taskInputs) {
        ExternalTaskResult externalTaskResult;


        externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to stop task", "Cannot stop");


        return externalTaskResult;
    }


    private void addHeaders(Invocation.Builder request) {
        request.header("Content-Type", "application/json");


        return;
    }


    private DynamicValuesOutput getDynamicValues(DynamicValuesInput dynamicValuesInput, String filter) throws Exception {
        return null;
    }


}