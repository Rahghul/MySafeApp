package fr.mysafeauto.mysafe.Services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rahghul on 06/02/2016.
 */
public class WebServiceUtil {

    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int DATARETRIEVAL_TIMEOUT = 10000;

    public static String requestWebService(String serviceUrl) {

        HttpURLConnection urlConnection = null;
        // create connection
        try {
            URL urlToRequest = null;
            urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle issues
            int statusCode = 0;
            statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
            }

            // create JSON object from content
            InputStream in = null;
            in = urlConnection.getInputStream();


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);

            }
            return result.toString();

        } catch (Exception e) {
            //Exception
            Log.d("Exception", "on requestWebService() in WebServiceUtil");
        }
        return null;

    }

}
