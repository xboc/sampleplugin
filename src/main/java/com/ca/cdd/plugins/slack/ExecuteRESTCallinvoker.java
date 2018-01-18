/**
 * Execute Restful Call Invoker Interface
 * 
 * Description:
 * This is the execute restful call invoker interface that is being used by the Slack test connectivity calss
 * to invoke the generic restful call to verify the correct connecvity to the slack site.
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

import javax.ws.rs.core.Response;


public interface ExecuteRESTCallinvoker {
	
	/**
	 * This interface requires the following parameters and it returns a response for further processing 
	 *  
	 * @param url
	 * @param method
	 * @param body
	 * @param userName
	 * @param password
	 * @return
	 */
	public Response executeRESTCall (String url, String method, String body, String userName, String password);

}
