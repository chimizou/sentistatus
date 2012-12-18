package edu.mt.sapna.sentistatus;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

@SuppressWarnings("serial")
public class SentiStatusServlet extends HttpServlet {
	
	private FacebookClient facebookClient;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {		
		String signedRequest = (String) request.getParameter("signed_request");
		FacebookSignedRequest facebookSR = null;
		try {
			facebookSR = FacebookSignedRequest.getFacebookSignedRequest(signedRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String oauthToken = facebookSR.getOauth_token();
		PrintWriter writer = response.getWriter();
		if(oauthToken == null) {			
			response.setContentType("text/html");
			String authURL = "https://www.facebook.com/dialog/oauth?client_id="
								+ Constants.API_KEY + "&redirect_uri=https://apps.facebook.com/sentistatus/&scope=";
			writer.print("<script> top.location.href='"	+ authURL + "'</script>");
			writer.close();
		}
		else {			
			facebookClient = new DefaultFacebookClient(oauthToken);
			Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
			writer.print("<table><tr><th>Photo</th><th>Name</th><th>Id</th></tr>");
			for (List<User> myFriendsList : myFriends) {	
				for(User user: myFriendsList)
					writer.print("<tr><td><img src=\"https://graph.facebook.com/" + user.getId() + "/picture\"/></td><td>" + user.getName() +"</td><td>" + user.getId() + "</td></tr>");	
			}
			writer.print("</table>");
			writer.close();			
		}
	}
	
	
}
