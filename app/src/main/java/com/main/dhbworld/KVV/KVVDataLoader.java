package com.main.dhbworld.KVV;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.main.dhbworld.R;

import org.myjson.JSONArray;
import org.myjson.JSONException;
import org.myjson.JSONObject;
import org.myjson.XML;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class KVVDataLoader {

    DataLoaderListener dataLoaderListener;
    Context context;

    public KVVDataLoader(Context context) {
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.kvv_api);
            while (parser.nextToken() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("api_key")) {
                        System.out.println(parser.nextText());
                    } else if (parser.getName().equals("url")) {
                        System.out.println(parser.nextText());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void setDataLoaderListener(DataLoaderListener dataLoaderListener) {
        this.dataLoaderListener = dataLoaderListener;
    }

    private String loadfromKVVServers() {



        return "";
    }

    public void loadData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Departure> departures = new ArrayList<>();

                boolean success = true;

                String kvvXMLData = loadfromKVVServers();

                String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Trias\n" +
                        "\txmlns=\"http://www.vdv.de/trias\" version=\"1.1\">\n" +
                        "\t<ServiceDelivery>\n" +
                        "\t\t<ResponseTimestamp\n" +
                        "\t\t\txmlns=\"http://www.siri.org.uk/siri\">2022-04-12T08:07:45Z\n" +
                        "\t\t</ResponseTimestamp>\n" +
                        "\t\t<ProducerRef\n" +
                        "\t\t\txmlns=\"http://www.siri.org.uk/siri\">EFAController10.3.18.46-EFA03\n" +
                        "\t\t</ProducerRef>\n" +
                        "\t\t<Status\n" +
                        "\t\t\txmlns=\"http://www.siri.org.uk/siri\">true\n" +
                        "\t\t</Status>\n" +
                        "\t\t<MoreData>false</MoreData>\n" +
                        "\t\t<Language>de</Language>\n" +
                        "\t\t<DeliveryPayload>\n" +
                        "\t\t\t<StopEventResponse>\n" +
                        "\t\t\t\t<StopEventResponseContext>\n" +
                        "\t\t\t\t\t<Situations></Situations>\n" +
                        "\t\t\t\t</StopEventResponseContext>\n" +
                        "\t\t\t\t<StopEventResult>\n" +
                        "\t\t\t\t\t<ResultId>ID-6EABD3C6-662E-46ED-B0F3-82FCE2967119</ResultId>\n" +
                        "\t\t\t\t\t<StopEvent>\n" +
                        "\t\t\t\t\t\t<ThisCall>\n" +
                        "\t\t\t\t\t\t\t<CallAtStop>\n" +
                        "\t\t\t\t\t\t\t\t<StopPointRef>de:08212:12:1:1</StopPointRef>\n" +
                        "\t\t\t\t\t\t\t\t<StopPointName>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Karlsruhe Duale Hochschule</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</StopPointName>\n" +
                        "\t\t\t\t\t\t\t\t<PlannedBay>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Gleis 1</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</PlannedBay>\n" +
                        "\t\t\t\t\t\t\t\t<ServiceDeparture>\n" +
                        "\t\t\t\t\t\t\t\t\t<TimetabledTime>2022-04-12T08:13:18Z</TimetabledTime>\n" +
                        "\t\t\t\t\t\t\t\t</ServiceDeparture>\n" +
                        "\t\t\t\t\t\t\t\t<StopSeqNumber>18</StopSeqNumber>\n" +
                        "\t\t\t\t\t\t\t</CallAtStop>\n" +
                        "\t\t\t\t\t\t</ThisCall>\n" +
                        "\t\t\t\t\t\t<Service>\n" +
                        "\t\t\t\t\t\t\t<OperatingDayRef>2022-04-12</OperatingDayRef>\n" +
                        "\t\t\t\t\t\t\t<JourneyRef>kvv:21001:E:H:j22:245</JourneyRef>\n" +
                        "\t\t\t\t\t\t\t<LineRef>kvv:21001:E:H</LineRef>\n" +
                        "\t\t\t\t\t\t\t<DirectionRef>outward</DirectionRef>\n" +
                        "\t\t\t\t\t\t\t<Mode>\n" +
                        "\t\t\t\t\t\t\t\t<PtMode>tram</PtMode>\n" +
                        "\t\t\t\t\t\t\t\t<TramSubmode>cityTram</TramSubmode>\n" +
                        "\t\t\t\t\t\t\t\t<Name>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Straßenbahn</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</Name>\n" +
                        "\t\t\t\t\t\t\t</Mode>\n" +
                        "\t\t\t\t\t\t\t<PublishedLineName>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Straßenbahn 1</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</PublishedLineName>\n" +
                        "\t\t\t\t\t\t\t<OperatorRef>kvv:02</OperatorRef>\n" +
                        "\t\t\t\t\t\t\t<RouteDescription>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Durlach - Neureut-Heide</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</RouteDescription>\n" +
                        "\t\t\t\t\t\t\t<Attribute>\n" +
                        "\t\t\t\t\t\t\t\t<Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Niederflurwagen</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Code>152429</Code>\n" +
                        "\t\t\t\t\t\t\t\t<Mandatory>false</Mandatory>\n" +
                        "\t\t\t\t\t\t\t</Attribute>\n" +
                        "\t\t\t\t\t\t\t<OriginStopPointRef>de:08212:18:1:1</OriginStopPointRef>\n" +
                        "\t\t\t\t\t\t\t<OriginText>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Durlach Turmberg</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</OriginText>\n" +
                        "\t\t\t\t\t\t\t<DestinationText>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Heide</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</DestinationText>\n" +
                        "\t\t\t\t\t\t</Service>\n" +
                        "\t\t\t\t\t</StopEvent>\n" +
                        "\t\t\t\t</StopEventResult>\n" +
                        "\t\t\t\t<StopEventResult>\n" +
                        "\t\t\t\t\t<ResultId>ID-750EDBC2-DAEE-44E7-904D-A47CA014FAD1</ResultId>\n" +
                        "\t\t\t\t\t<StopEvent>\n" +
                        "\t\t\t\t\t\t<ThisCall>\n" +
                        "\t\t\t\t\t\t\t<CallAtStop>\n" +
                        "\t\t\t\t\t\t\t\t<StopPointRef>de:08212:12:2:2</StopPointRef>\n" +
                        "\t\t\t\t\t\t\t\t<StopPointName>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Karlsruhe Duale Hochschule</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</StopPointName>\n" +
                        "\t\t\t\t\t\t\t\t<PlannedBay>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Gleis 2</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</PlannedBay>\n" +
                        "\t\t\t\t\t\t\t\t<ServiceDeparture>\n" +
                        "\t\t\t\t\t\t\t\t\t<TimetabledTime>2022-04-12T08:14:36Z</TimetabledTime>\n" +
                        "\t\t\t\t\t\t\t\t</ServiceDeparture>\n" +
                        "\t\t\t\t\t\t\t\t<StopSeqNumber>3</StopSeqNumber>\n" +
                        "\t\t\t\t\t\t\t</CallAtStop>\n" +
                        "\t\t\t\t\t\t</ThisCall>\n" +
                        "\t\t\t\t\t\t<Service>\n" +
                        "\t\t\t\t\t\t\t<OperatingDayRef>2022-04-12</OperatingDayRef>\n" +
                        "\t\t\t\t\t\t\t<JourneyRef>kvv:21001:E:R:j22:46</JourneyRef>\n" +
                        "\t\t\t\t\t\t\t<LineRef>kvv:21001:E:R</LineRef>\n" +
                        "\t\t\t\t\t\t\t<DirectionRef>return</DirectionRef>\n" +
                        "\t\t\t\t\t\t\t<Mode>\n" +
                        "\t\t\t\t\t\t\t\t<PtMode>tram</PtMode>\n" +
                        "\t\t\t\t\t\t\t\t<TramSubmode>cityTram</TramSubmode>\n" +
                        "\t\t\t\t\t\t\t\t<Name>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Straßenbahn</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</Name>\n" +
                        "\t\t\t\t\t\t\t</Mode>\n" +
                        "\t\t\t\t\t\t\t<PublishedLineName>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Straßenbahn 1</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</PublishedLineName>\n" +
                        "\t\t\t\t\t\t\t<OperatorRef>kvv:02</OperatorRef>\n" +
                        "\t\t\t\t\t\t\t<RouteDescription>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Neureut-Heide - Durlach</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</RouteDescription>\n" +
                        "\t\t\t\t\t\t\t<Attribute>\n" +
                        "\t\t\t\t\t\t\t\t<Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Text>Niederflurwagen</Text>\n" +
                        "\t\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t\t</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Code>152430</Code>\n" +
                        "\t\t\t\t\t\t\t\t<Mandatory>false</Mandatory>\n" +
                        "\t\t\t\t\t\t\t</Attribute>\n" +
                        "\t\t\t\t\t\t\t<OriginStopPointRef>de:08212:13:2:2</OriginStopPointRef>\n" +
                        "\t\t\t\t\t\t\t<OriginText>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Neureut-Heide</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</OriginText>\n" +
                        "\t\t\t\t\t\t\t<DestinationText>\n" +
                        "\t\t\t\t\t\t\t\t<Text>Durlach</Text>\n" +
                        "\t\t\t\t\t\t\t\t<Language>de</Language>\n" +
                        "\t\t\t\t\t\t\t</DestinationText>\n" +
                        "\t\t\t\t\t\t</Service>\n" +
                        "\t\t\t\t\t</StopEvent>\n" +
                        "\t\t\t\t</StopEventResult>\n" +
                        "\t\t\t</StopEventResponse>\n" +
                        "\t\t</DeliveryPayload>\n" +
                        "\t</ServiceDelivery>\n" +
                        "</Trias>";
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

                        String line = service.getJSONObject("PublishedLineName")
                                .getString("Text");

                        String attribute = service.getJSONObject("Attribute")
                                .getJSONObject("Text")
                                .getString("Text");

                        String destination = service.getJSONObject("DestinationText")
                                .getString("Text");

                        LocalDateTime departureTime = Instant.parse(departureTimeString)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        Departure departure = new Departure(line, platform, attribute, destination, departureTime);
                        departures.add(departure);
                    }
                } catch (JSONException e) {
                    success = false;
                }

                if (dataLoaderListener != null) {
                    dataLoaderListener.onDataLoaded(success, departures);
                } else {
                    for (Departure departure : departures) {
                        System.out.println(departure);
                    }
                }
            }
        }).start();
    }
}
