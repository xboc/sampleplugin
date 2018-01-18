package com.ca.cdd.plugins.sample;

import com.ca.rp.plugins.dto.model.*;

import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by admin on 24/02/2016.
 * A sample application source that can be used as a template for Continous Delivery Edition plugin application sources
 */
@Path("application-sources/application-source")
public class SampleApplicationSource {


    @POST
    @Path("/application-source")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ExternalApplicationsResponse execute(ExternalApplicationSourceInput externalApplicationSourceInput) throws Exception {
        ExternalApplicationsResponse externalApplicationsResponse;

        externalApplicationsResponse = getApplicationsAndEnvironments(externalApplicationSourceInput);

        return externalApplicationsResponse;
    }


    private ExternalApplicationsResponse getApplicationsAndEnvironments(ExternalApplicationSourceInput externalApplicationSourceInput) throws Exception {
        ExternalApplicationsResponse externalApplicationsResponse;
        List < ExternalApplication > externalApplications;

        Map < String, String > endPointProperties = externalApplicationSourceInput.getEndPointProperties();

        externalApplications = getListOfExternalApplications(endPointProperties);

        for (int i = 0; i < externalApplications.size(); i++) {
            ExternalApplication externalApplication;
            List < ExternalEnvironment > externalEnvironments;

            externalApplication = externalApplications.get(i);

            externalEnvironments = getListOfEnvironmentByApplication(externalApplication, endPointProperties);

            externalApplication.setEnvironments(externalEnvironments);
        }

        externalApplicationsResponse = new ExternalApplicationsResponse();
        externalApplicationsResponse.setApplications(externalApplications);

        return externalApplicationsResponse;
    }

    private List < ExternalApplication > getListOfExternalApplications(Map < String, String > endPointProperties) {
        List < ExternalApplication > externalApplications = null;

        return externalApplications;
    }

    private List < ExternalEnvironment > getListOfEnvironmentByApplication(ExternalApplication externalApplication, Map < String, String > endPointProperties) {
        List < ExternalEnvironment > externalEnvironments = null;

        return externalEnvironments;
    }
}