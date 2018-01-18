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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.rp.plugins.dto.model.ExternalTaskInputs;
import com.ca.rp.plugins.dto.model.ExternalTaskResult;
import com.ca.rp.plugins.dto.model.TaskConstants;

/**
 * This class contains wrapping for the Slack message task.
 * Controller wrapping for the RaExternalTask
 * The task is exposed as "messaging/sendMessage" path.
 */
@Path("messaging")
public class slackSendMessage {

 private static final Logger log = LoggerFactory.getLogger(slackSendMessage.class);

 /** 
  * This is the method that will be executed based on the "/messaging/sendMessage" that has been
  * exposed. This method consumes and produces JSON and it is based off the POST method.
  * @param action
  * @param taskInputs
  * @return
  */
 @Path("/sendMessage")
 @POST
 @Consumes(MediaType.APPLICATION_JSON)
 @Produces(MediaType.APPLICATION_JSON)
 public ExternalTaskResult execute(@QueryParam(TaskConstants.ACTION_PARAM) String action, ExternalTaskInputs taskInputs) 
 {
  ExternalTaskResult externalTaskResult;

  if (TaskConstants.EXECUTE_ACTION.equalsIgnoreCase(action)) 
  {
   externalTaskResult = executeTask(taskInputs);
  }
  else if (TaskConstants.STOP_ACTION.equalsIgnoreCase(action)) 
  {
   externalTaskResult = stopTask(taskInputs);
  }
  else 
  {
   throw new IllegalArgumentException("Unexpected invalid action '" + action + "' while trying to execute task.");
  }

  return externalTaskResult;
 }

 /**
  * The method will take the task inputs and build the necessary JSON payload, which in this case is the sending
  * of the Slack message to the slack application that we have setup.
  * @param taskInputs
  * @return
  */
 private ExternalTaskResult executeTask(ExternalTaskInputs taskInputs) 
 {
  ExternalTaskResult externalTaskResult;
  Map<String, String> message = new HashMap<String, String>();

  try 
  {
   Map < String, String > taskProperties = taskInputs.getTaskProperties();
   Map < String, String > endpointProperties = taskInputs.getEndPointProperties();

   String url;
   String userName;
   String password;
   String method = "POST";

   if (endpointProperties != null && !endpointProperties.isEmpty()) 
   {
	   url = endpointProperties.get("URL");
	   userName = endpointProperties.get("Username");
	   password = endpointProperties.get("Password");
   } 
   else 
   {
    externalTaskResult = ExternalTaskResult.createResponseForFailure("Missing endpoint", "Unexpected invalid empty endpoint while trying to execute task");
    return externalTaskResult;
   }

   // now we need to make sure that the URL string contains a "/" at the end of it.
   if (!url.endsWith("/"))
   {
   	log.debug("adding the / to the end of the string as needed for the url...");
   	url = url.concat("/");
   	log.debug("new contents of url string: " + url);
   }

   // publishing these values in the log file
   log.debug("value for URL: " + url);

   // now we need to get the task properties and populate the json body
   if (taskProperties != null && !taskProperties.isEmpty())
   {
	   message.put("text", taskProperties.get("slackMessage"));
	   log.debug("Slack message contents: " + Arrays.asList(message));
   }
   else
   {
	    externalTaskResult = ExternalTaskResult.createResponseForFailure("Missing Slack Message", "Please include a Slack Message to send");
	    return externalTaskResult;
   }
	   
   
   // configuring the http client to be used
   Client httpClient = ClientBuilder.newClient();

   // adding the HTTP header support
   Invocation.Builder request = httpClient.target(url).request();
   try 
   {
    addHeaders(request);
   } 
   catch (Exception e) 
   {
    externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to execute",
     "Could not parse headers " + e.getLocalizedMessage());
    return externalTaskResult;
   }

   // building the URL line that will be passed. This line include a hard-code value for simplicity
   //String completedURL = url + "services/T2P23425A/B2566PNRND9S/4Md3UijpfnwgObgfBFHuLO4M";
   
   url = url.concat("services/<enter the rest of the webhook here>");
   
   // publish this value to the log file
   //log.debug("value of the newURL: " + completedURL);
   log.debug("new updated value for url: " + url);

   // defining the response and executing the post request, which includes the message in a JSON body
   Response response = null;
   response = httpClient
		   .target(url)
		   .request(MediaType.APPLICATION_JSON)
		   .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, userName)
		   .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password)
		   .method(method, Entity.json(message));;

   if (response != null) 
   {
   int responseCode = response.getStatus();
    
   // there are issues with the different versions of the javax implementation
   String responseBody = (response).readEntity(String.class);
   
   // sending the response status to the debug log.
   log.debug("here is the response status of this msg: " + response.getStatus());
   log.debug("contents of the response status: " + responseBody);
   log.debug("response headers: " + response.getHeaders());
   
    return ExternalTaskResult.createResponseForFinished("Slack chat message sent successfully", responseBody);
   } 
   else 
   {
    return ExternalTaskResult.createResponseForFailure("Failed to send Slack chat message",
     "No response");
   }
   
  } 
  catch (Exception e) 
  {
   externalTaskResult = ExternalTaskResult.createResponseForFailure("Failure sending Slack chat message",
    e.getLocalizedMessage());
  }

  return externalTaskResult;
 }

 /**
  * This method will stop the task as requested by the user.
  * @param taskInputs
  * @return
  */
 private ExternalTaskResult stopTask(ExternalTaskInputs taskInputs) 
 {
  ExternalTaskResult externalTaskResult;

  externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to stop task", "Cannot stop");
  return externalTaskResult;
 }

 /** 
  * Adding the 'application/json' header
  * @param request
  */
 private void addHeaders(Invocation.Builder request) 
 {
  request.header("Content-Type", "application/json");
  return;
 }


}