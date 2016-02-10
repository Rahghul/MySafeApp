package fr.mysafeauto.mysafe.Services;

import android.app.AlertDialog;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rahghul on 06/02/2016.
 */
public class WebServiceUtil {

    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int DATARETRIEVAL_TIMEOUT = 10000;
    private static final String LOGGER_TAG = "----------->Service";

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

    /*public static String requestWebService2(String url, String postParameters, String method) {
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle POST parameters
            if (postParameters != null && method.equals("POST")) {

                if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
                    Log.i(LOGGER_TAG, "POST parameters: " + postParameters);
                }

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                //send the POST out
                PrintWriter out = null;
                try {
                    out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(postParameters);
                }catch (Exception e){
                    Log.d(LOGGER_TAG,e.getMessage());
                }finally {
                    if(out != null)
                        out.close();
                }
            }
            if(method.equals("DELETE")){
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setRequestMethod("DELETE");
            }
            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
                Log.d("Http status ERROR", ""+statusCode);
            }
            Log.d("Http status", ""+statusCode);

            // read output (only for GET)
            if (postParameters != null && (method == "POST" || method == "DELETE")) {
                return null;
            } else
            if(method == "GET"){
                // create JSON object from content
                InputStream in = urlConnection.getInputStream();


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);

                }
                return result.toString();

            }


        } catch (Exception e) {
            // handle invalid URL
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }
*/
    public static int requestWebServicePOST(String url, String postParameters) {
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle POST parameters

            if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
                Log.i(LOGGER_TAG, "POST parameters: " + postParameters);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            //send the POST out
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();


            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
                Log.d("Http status ERROR", ""+statusCode);
            }
            return statusCode;


        } catch (Exception e) {
            // handle invalid URL
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return 0;
    }

    public static int requestWebServiceDELETE(String url) {
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("DELETE");

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
                Log.d("Http status ERROR", ""+statusCode);
            }
            Log.d("Http status", "" + statusCode);

            // read output (only for GET)
            return statusCode;

        } catch (Exception e) {
            // handle invalid URL
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return 0;
    }

    public static String requestWebServiceGET(String url) {
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);



            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
                Log.d("Http status ERROR", ""+statusCode);
                return null;

            }

            Log.d("Http status", ""+statusCode);

            // read output (only for GET)
            // create JSON object from content
            InputStream in = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();

        } catch (Exception e) {
            // handle invalid URL
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }




}
