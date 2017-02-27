package com.teschglobal.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Java app to interact with DOM HealthLX WebServices
 * 
 * @author alejandro
 *
 */
public class Main {
	
	public static void main (String... args){
		Main test = new Main();
		String url = "http://54.221.173.39:8181/cxf/WSHealthLX/dimhl7";
		String payloadData = "MSH|^~\\&|hlxClient|TG|HLX|TG|20160712124739-0500||ADT^A01|1468345659-1|P|2.5"
				+ "EVN|A01|20160712124739-0500||||20160712124739-0500"
				+ "PID|1|TG1000615|2ijutrf5-4652-0145-7697-193670009385||Hart^Alan||20050310|M||White|711 O'Hara Station|||||||5933615078"
				+ "PR1|||93451|Right heart cath|20160830101530-0500"
				+ "PV1|1|E|ER^^|Emergency|||TG1007728^Garrett^Sara|||OBS|||R|8|||TG1007728^Garrett^Sara|||||||||||||||||||||||||||20160712124739-0500||"
				+ "DG1|1|I9|V72.42|Pregnancy Examination or Test, Positive Result"; 
		test.connectionPost(url, payloadData, "test", Format.TEXT);
	}

	/**
	 * Method to do connection according: url, payloadData and method name <br>
	 * <ul>
	 * <li>Build HttpPost object to call URL</li>
	 * <li>Build header of post with "application/xml" parameter</li>
	 * <li>If response status is 200 or 204 then process the response</li>
	 * <li>other wise, throws RuntimeException with codeStatus of response</li>
	 * </ul>
	 */
	private StringBuilder connectionPost(String url, String payloadData, String methodName, Format format) {

		// Creating post call according URL
		HttpPost post = new HttpPost(url);

		// Variables to handle Http connection
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse responseHttp = null;

		// Response of Post call
		StringBuilder responseStringBuilder = new StringBuilder("No Resposonse");

		try {
			// Building body to send it as request on post method
			StringEntity params = new StringEntity(payloadData, "UTF-8");
			params.setContentType(format.getName());
			post.addHeader("content-type", format.getName());
			post.addHeader("Accept", "*/*");
			post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
			post.addHeader("Accept-Language", "en-US,en;q=0.8");
			post.setEntity(params);

			// Calling by post method to URL defined
			responseHttp = httpClient.execute(post);
			int responseCode = responseHttp.getStatusLine().getStatusCode();
			System.out.println("------ STOP> " + methodName + " with responseCode: " + responseCode);
			org.apache.http.HttpEntity entity = responseHttp.getEntity();

			responseStringBuilder = convertInputStreamToStringBuilder(entity.getContent());

			// If there's an error on response of get calling
			if (!(responseCode == 200 || responseCode == 204)) {
				throw new RuntimeException("Failed : HTTP error code : " + responseCode
						+ ", " + responseHttp.getStatusLine().getReasonPhrase());
			}

		} catch (Exception ex) {
			System.err.println("url:" + url);
			System.err.println("exception code from server: " + ex);
			System.err.println("exception message from response:" + responseStringBuilder);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		// Method's response
		return responseStringBuilder;
	}


	/**
	 * Convert and format response of server to stringBuilder
	 * 
	 * @param instream
	 * @return stringBuilder
	 */
	private StringBuilder convertInputStreamToStringBuilder(InputStream instream) {
		// Building StringBuilder
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		StringBuilder sb = new StringBuilder();

		// Setting data into StringBuilder
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb;
	}


}

enum Format {
	JSON("application/json", "JsonBody"), XML("application/xml", "XMLBody"), TEXT("application/x-www-form-urlencoded", "Text");

	// fields of this enum
	private final String contentType;
	private final String _class;

	Format(String name, String _class) {
		this.contentType = name;
		this._class = _class;
	}

	// methods of this enum
	public String getName() {
		return contentType;
	}

	public String getClassName() {
		return _class;
	}
}
