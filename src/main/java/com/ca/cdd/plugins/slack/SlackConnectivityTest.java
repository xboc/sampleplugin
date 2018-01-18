/**
 * Slack Send Message CDE Task 
 * 
 * Description:
 * This CDE task will send a message to a hard-coded Slack messaging system based on the username/password provided.
 *  
 * <p>Date: October 18, 2016</p>

 * History: October 18, 2016
 * 			Initial release.
 * 
 * @author Walter Guerrero
 * Copyright © 2016 CA, Inc. All rights reserved.  
 * All marks used herein may belong to their respective companies. This 
 * document does not contain any warranties and is provided for informational 
 * purposes only. Any functionality descriptions may be unique to the customers 
 * depicted herein and actual product performance may vary.
 */

package com.ca.cdd.plugins.slack;

import com.ca.rp.plugins.dto.model.ConnectivityInput;
import com.ca.rp.plugins.dto.model.ConnectivityResult;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

/**
 * The default path for this class is the "connectivity-test", which will be used to 
 * build the full URL path to the connect method.
 * 
 * @author guewa01
 *
 */
@Path("connectivity-test")
public class SlackConnectivityTest  implements SlackConnect {

	private static final Logger log = LoggerFactory.getLogger(SlackConnectivityTest.class);

	/**
	 * This is the implementation of the public method that is part of the 
	 * SlackConnect interface. 
	 * 
	 * This method has the final path of "connectivity-test/connect".
	 * 
	 */
	@Path("/connect")
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    public ConnectivityResult connect(ConnectivityInput connectivityInput) {

    	Map < String, String > endpointProperties = connectivityInput.getEndPointProperties();
        
        // extracting the necessary values from the endpoint properties
        String url = endpointProperties.get("URL");
        String userName = endpointProperties.get("Username");
        String password = endpointProperties.get("Password");
		
        // now we need to make sure that the URL string contains a "/" at the end of it.
        if (!url.endsWith("/"))
        {
        	log.debug("adding the / to the end of the string as needed for the url...");
        	url = url.concat("/");
        	log.debug("new contents of url string: " + url);
        }
        	
		ConnectivityResult result = new ConnectivityResult(true, null);
		Response response = null;
        
        // publishing these values in the log file
        log.debug("value for URL: " + url);
        
        try 
        {
        	response = executeRESTCallImpl.getInstance().executeRESTCall(url, "POST", null, userName, password);
        	
        	// debug info needed...
        	log.debug("Here is the response status code: " + response.getStatus());
        	log.debug("Here are the response contents for the connectivity test: " + response.getStatusInfo());
        	
        	if (response.getStatus() != Response.Status.OK.getStatusCode())
        	{
        		result.setSuccess(false);
        		result.setErrorMessage(response.getStatusInfo().toString());
        	}
        }
        catch (Exception e)
        {
        	result.setSuccess(false);
        	result.setErrorMessage(e.getMessage());
        	
        }
        finally 
        {
        	if (response != null) {
        		response.close();
        	}
        		
        }
        return result;
    }
	

}
