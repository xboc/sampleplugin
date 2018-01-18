/**
 * Slack connect interface 
 * 
 * Description:
 * This interface is used by the testConnectivity class and it has a direct
 * relation to the "/slack/test-connectivity/connect" URL path.
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

public interface SlackConnect {
	
	/**
	 * These are the required parameters for this interface.
	 * @param connectivityInput
	 * @return
	 */
	ConnectivityResult connect(ConnectivityInput connectivityInput);

}
