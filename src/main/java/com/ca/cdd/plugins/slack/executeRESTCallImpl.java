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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class executeRESTCallImpl implements ExecuteRESTCallinvoker {

	private static final Logger log = LoggerFactory.getLogger(executeRESTCallImpl.class);
	
	private static final ExecuteRESTCallinvoker instance = new executeRESTCallImpl();
	
	private Client slackClient = null;
	
	/**
	 * Setting up the executeRestCallImpl method()
	 * 
	 */
	private executeRESTCallImpl() 
	{
		slackClient = ClientBuilder.newClient();
		slackClient.register(HttpAuthenticationFeature.basicBuilder().build());
		slackClient.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
		addShutdownHook();
	}
	
	/**
	 * Setting up the instance of the ExecuteRESTCallInvoker interface
	 * @return
	 */
	public static ExecuteRESTCallinvoker getInstance() 
	{
		return instance;
	}
	
	@Override
	/**
	 * This is the implemention of the method defined in the ExecuteRESTCallInvoker interface.
	 */
	public Response executeRESTCall(String url, String method, String body, String userName, String password) {

		log.debug("Executing the slack call for URL: " + url);

		Response response = null;

		
		if (body != null && !body.isEmpty())
		{
			response = slackClient.target(url)
					.request(MediaType.APPLICATION_JSON)
					.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, userName)
					.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password)
					.method(method, Entity.json(body));
		}
		else
		{
			log.debug("this is the call that will be executed, where no body is being passed");
			response =slackClient.target(url)
					.request(MediaType.APPLICATION_JSON)
					.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, userName)
					.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password)
					.method(method);
			log.debug("results of the restful call execution...");
			log.debug("the response value from the executeRest method: " + response.getStatus());
			log.debug("the headers of the response : " + response.getHeaders());
					
		}
		
		if (response != null)
		{
			return response;
		}
		else
		{
			throw new RuntimeException("Failed to execute slack restful call, No Response");
		}
	}

	private void addShutdownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
				{
					public void run()
					{
						slackClient.close();
					}
			
				});
	}
}
