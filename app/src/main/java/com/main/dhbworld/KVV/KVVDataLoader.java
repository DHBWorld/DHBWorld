package com.main.dhbworld.KVV;

import android.app.Activity;

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
    private String loadfromKVVServers(LocalDateTime time) throws IOException {
        URL apiUrl = new URL(context.getResources().getString(R.string.url_kvv));

        String apiKey = context.getResources().getString(R.string.api_key_kvv);
        String timeStr = time.toInstant(ZoneOffset.UTC).toString();
        String currentTimeString = Instant.now().toString();
        String data = context.getResources().getString(R.string.request_data_kvv, apiKey, currentTimeString, timeStr);

        HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/xml");

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

            String kvvXMLData;
            try {
                kvvXMLData = loadfromKVVServers(time);
                if (kvvXMLData == null) {
                    throw new IOException();
                }
            } catch (IOException e) {
                context.runOnUiThread(() -> {
                    if (dataLoaderListener != null) {
                        dataLoaderListener.onDataLoaded(departures);
                    }
                });
                return;
            }

            JSONObject jsonObject = XML.toJSONObject(kvvXMLData);

            try {
                JSONArray stopResultArray = jsonObject.getJSONObject("Trias")
                        .getJSONObject("ServiceDelivery")
                        .getJSONObject("DeliveryPayload")
                        .getJSONObject("StopEventResponse")
                        .getJSONArray("StopEventResult");

                for (int i=0; i<stopResultArray.length(); i++) {
                    JSONObject stopResult = stopResultArray.getJSONObject(i);

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

                    Departure departure = new Departure(line, platform, attribute, destination, departureTime);
                    departures.add(departure);
                }
            } catch (JSONException ignored) { }

            context.runOnUiThread(() -> {
                if (dataLoaderListener != null) {
                    dataLoaderListener.onDataLoaded(departures);
                }
            });

        }).start();
    }
}
