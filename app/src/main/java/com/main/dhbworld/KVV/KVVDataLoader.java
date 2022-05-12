package com.main.dhbworld.KVV;

import android.app.Activity;
import android.content.Context;

import com.main.dhbworld.R;

import org.myjson.JSONArray;
import org.myjson.JSONException;
import org.myjson.JSONObject;
import org.myjson.XML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class KVVDataLoader {

    DataLoaderListener dataLoaderListener;
    Activity context;

    /**
     * KVVDataLoader constructor for loading the tram departures
     * @param context of the calling class
     */
    public KVVDataLoader(Activity context) {
        this.context = context;
    }

    /**
     * Sets the dataLoaderListener to retrieve the information
     * @param dataLoaderListener where information is send to
     */
    public void setDataLoaderListener(DataLoaderListener dataLoaderListener) {
        this.dataLoaderListener = dataLoaderListener;
    }

    /**
     * Loads the data from the KVV-Server
     * @return Returns the XML-response
     * @throws IOException If something went wrong calling the server
     */
    protected static String loadfromKVVServers(LocalDateTime time, Context context) throws IOException {
        URL apiUrl = new URL(context.getResources().getString(R.string.url_kvv));

        String apiKey = context.getResources().getString(R.string.api_key_kvv);
        String timeStr = time.toInstant(ZoneOffset.UTC).toString();
        String currentTimeString = Instant.now().toString();
        String data = context.getResources().getString(R.string.request_data_kvv, apiKey, currentTimeString, timeStr);

        HttpsURLConnection connection = createPostConnection(apiUrl);

        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(data);
        writer.flush();
        writer.close();
        outputStream.close();

        connection.connect();

        if (connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return reader.lines().collect(Collectors.joining("\n"));
        } else {
            return null;
        }
    }

    /**
     * Loads and parses the data from the KVV-Server
     * @see #setDataLoaderListener(DataLoaderListener)
     */
    public void loadData(LocalDateTime time) {
        new Thread(() -> {
            ArrayList<Departure> departures = new ArrayList<>();
            Disruption disruption = null;

            String kvvXMLData;
            try {
                kvvXMLData = loadfromKVVServers(time, context);
                if (kvvXMLData == null) {
                    throw new IOException();
                }
            } catch (IOException e) {
                context.runOnUiThread(() -> {
                    if (dataLoaderListener != null) {
                        dataLoaderListener.onDataLoaded(departures, null);
                    }
                });
                return;
            }

            JSONObject jsonObject = XML.toJSONObject(kvvXMLData);

            try {
                JSONObject stopEventResponse = jsonObject.getJSONObject("Trias")
                        .getJSONObject("ServiceDelivery")
                        .getJSONObject("DeliveryPayload")
                        .getJSONObject("StopEventResponse");

                JSONArray stopResultArray = stopEventResponse
                        .getJSONArray("StopEventResult");

                if (stopEventResponse.getJSONObject("StopEventResponseContext").get("Situations") instanceof JSONObject) {
                    JSONObject situations = stopEventResponse.getJSONObject("StopEventResponseContext")
                            .getJSONObject("Situations");

                    if (situations.has("PtSituation")) {

                        disruption = parseDisruption(situations);
                    }
                }

                for (int i=0; i<stopResultArray.length(); i++) {

                    JSONObject stopResult = stopResultArray.getJSONObject(i);

                    Departure departure = parseDeparture(stopResult);

                    departures.add(departure);
                }
            } catch (JSONException ignored) { }

            Disruption finalDisruption = disruption;
            context.runOnUiThread(() -> {
                if (dataLoaderListener != null) {
                    dataLoaderListener.onDataLoaded(departures, finalDisruption);
                }
            });

        }).start();
    }

    protected static Departure parseDeparture(JSONObject stopResult) {
        JSONObject service = stopResult.getJSONObject("StopEvent")
                .getJSONObject("Service");

        JSONObject callAtStop = stopResult.getJSONObject("StopEvent")
                .getJSONObject("ThisCall")
                .getJSONObject("CallAtStop");

        String platform = callAtStop.getJSONObject("PlannedBay")
                .getString("Text");


        String departureTimeString = callAtStop.getJSONObject("ServiceDeparture")
                .getString("TimetabledTime");

        if (callAtStop.getJSONObject("ServiceDeparture").has("EstimatedTime")) {
            departureTimeString = callAtStop.getJSONObject("ServiceDeparture")
                    .getString("EstimatedTime");
        }

        String line = service.getJSONObject("PublishedLineName")
                .getString("Text");

        String attribute = null;
        if (service.has("Attribute")) {
            attribute = service.getJSONObject("Attribute")
                    .getJSONObject("Text")
                    .getString("Text");
        }

        String destination = service.getJSONObject("DestinationText")
                .getString("Text");

        LocalDateTime departureTime = LocalDateTime.ofInstant(Instant.parse(departureTimeString), ZoneOffset.UTC);

        return new Departure(line, platform, attribute, destination, departureTime);
    }

    protected static Disruption parseDisruption(JSONObject situations) {
        String summary = situations.getJSONObject("PtSituation")
                .getJSONObject("Summary")
                .getString("content");

        String details = situations.getJSONObject("PtSituation")
                .getJSONObject("Detail")
                .getString("content");

        String lines = situations.getJSONObject("PtSituation")
                .getJSONObject("Description")
                .getString("content");

        return new Disruption(summary, details, lines);
    }

    protected static HttpsURLConnection createPostConnection(URL url) throws IOException{
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/xml");
        return connection;
    }
}
