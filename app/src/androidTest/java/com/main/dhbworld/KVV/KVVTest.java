package com.main.dhbworld.KVV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.main.dhbworld.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.myjson.JSONException;
import org.myjson.JSONObject;
import org.myjson.XML;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

@RunWith(AndroidJUnit4.class)
public class KVVTest {

   boolean finished = false;
   String data = null;

   @Test
   public synchronized void loadFromServerTest() {

      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

      new Thread(() -> {
         try {
            data = KVVDataLoader.loadfromKVVServers(LocalDateTime.now(), appContext);
         } catch (IOException ignored) { }
         finished = true;
      }).start();

      long startTime = System.currentTimeMillis();

      while (!finished) {
         if (System.currentTimeMillis() > startTime + (1000 * 30)) {
            break;
         }
      }

      assertNotNull(data);
   }

   @Test
   public void createPostConnectionTest() throws IOException {
      Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
      URL url = new URL(context.getResources().getString(R.string.url_kvv));

      HttpsURLConnection connection = KVVDataLoader.createPostConnection(url);
      assertNotNull(connection);
      assertEquals(connection.getRequestMethod(), "POST");

      List<String> contentTypes = connection.getRequestProperties().get("Content-Type");

      assertNotNull(contentTypes);
      assertEquals(contentTypes.get(0), "application/xml");
   }
   
   @Test
   public void parseSituationsTest() throws JSONException {
      String kvvXMLData = "<PtSituation>\n<CreationTime\nxmlns='http://www.siri.org.uk/siri'>2022-04-20T09:52:00Z\n</CreationTime>\n<ParticipantRef\nxmlns='http://www.siri.org.uk/siri'>KVV\n</ParticipantRef>\n<SituationNumber\nxmlns='http://www.siri.org.uk/siri'>110006178_KVV_ICSKVV\n</SituationNumber>\n<Version\nxmlns='http://www.siri.org.uk/siri'>3\n</Version>\n<Source\nxmlns='http://www.siri.org.uk/siri'>\n<SourceType>other</SourceType>\n</Source>\n<Progress\nxmlns='http://www.siri.org.uk/siri'>open\n</Progress>\n<ValidityPeriod\nxmlns='http://www.siri.org.uk/siri'>\n<StartTime>2022-04-25T09:37:00Z</StartTime>\n<EndTime>2022-06-07T01:30:59Z</EndTime>\n</ValidityPeriod>\n<UnknownReason\nxmlns='http://www.siri.org.uk/siri'>unknown\n</UnknownReason>\n<Priority\nxmlns='http://www.siri.org.uk/siri'>3\n</Priority>\n<Audience\nxmlns='http://www.siri.org.uk/siri'>public\n</Audience>\n<ScopeType\nxmlns='http://www.siri.org.uk/siri'>line\n</ScopeType>\n<Planned\nxmlns='http://www.siri.org.uk/siri'>false\n</Planned>\n<Language\nxmlns='http://www.siri.org.uk/siri'>\n</Language>\n<Summary\nxmlns='http://www.siri.org.uk/siri' overridden='true'>Karlsruhe: Weichen- und Gleisbauarbeiten in der Karlstraße (Phase 1)\n</Summary>\n<Description\nxmlns='http://www.siri.org.uk/siri' overridden='true'>Linien 3, S12\n</Description>\n<Detail\nxmlns='http://www.siri.org.uk/siri' overridden='true'>&lt;p&gt;(kue)&lt;/p&gt;&#xA;&lt;p&gt;&lt;strong&gt;&lt;span style='color: #9b2321;'&gt;Von Montag, 25. April 2022, 03:30 Uhr&lt;/span&gt;&lt;/strong&gt;&lt;br /&gt;&lt;strong&gt;&lt;span style='color: #9b2321;'&gt;bis Dienstag, 07. Juni 2022, 03:30 Uhr&lt;/span&gt;&lt;/strong&gt;&lt;/p&gt;&#xA;&lt;p&gt;werden folgende Streckenabschnitte in jeweils beiden Fahrtrichtungen f&amp;uuml;r den Schienenverkehr gesperrt:&lt;/p&gt;&#xA;&lt;ul&gt;&#xA;&lt;li&gt;ab der Kreuzung Karlstra&amp;szlig;e/Ebertstra&amp;szlig;e &amp;uuml;ber Kolpingplatz bis Gleisviereck Mathystra&amp;szlig;e&lt;/li&gt;&#xA;&lt;li&gt;ab dem Gleisviereck s&amp;uuml;dlich der Haltestelle R&amp;uuml;ppurrer Tor &amp;uuml;ber Baumeisterstra&amp;szlig;e und Konzerthaus bis Gleisviereck Mathystra&amp;szlig;e&lt;/li&gt;&#xA;&lt;/ul&gt;&#xA;&lt;p&gt;Diese Sperrungen sind als Phase 1 der diesj&amp;auml;hrigen Bauma&amp;szlig;nahmen gekennzeichnet.&lt;br /&gt;&lt;br /&gt;Die Linien 3 und S12 werden zwischen den Haltestellen Ebertstra&amp;szlig;e und Karlstor wie folgt umgeleitet:&lt;br /&gt;Ebertstra&amp;szlig;e &amp;ndash; ZKM &amp;ndash; Otto-Sachs-Stra&amp;szlig;e &amp;ndash; Mathystra&amp;szlig;e (in der Mathystra&amp;szlig;e) &amp;ndash; Karlstor (Karlstra&amp;szlig;e) bzw. umgekehrt.&lt;/p&gt;&#xA;&lt;p&gt;Die morgendlichen bzw. nachmitt&amp;auml;glichen Verst&amp;auml;rkerfahrten der Linie 3 zwischen Tivoli und Europaplatz entfallen. Stattdessen werden die Linie 2 und 3 im Abschnitt Poststra&amp;szlig;e bis Yorckstra&amp;szlig;e miteinander vertaktet.&lt;/p&gt;&#xA;&lt;p&gt;Ein &lt;strong&gt;Schienenersatzverkehr&lt;/strong&gt; zwischen Ebertstra&amp;szlig;e und Mathystra&amp;szlig;e &amp;uuml;ber die Haltestelle Kolpingplatz &lt;strong&gt;kann nicht eingerichtet werden&lt;/strong&gt;, da wegen der Bauarbeiten in der Karlstra&amp;szlig;e kein Fahrweg zur Verf&amp;uuml;gung steht. Die Haltestelle Kolpingplatz kann t&amp;auml;glich bis ca. 20 Uhr mit der &lt;strong&gt;Buslinie 55&lt;/strong&gt; erreicht werden. Diese hat an den Haltestellen Ebertstra&amp;szlig;e und ZKM Anschluss zum Schienennetz.&lt;/p&gt;&#xA;&lt;p&gt;&lt;strong&gt;Aufgehobene bzw. verlegte Haltestellen&lt;/strong&gt;&lt;/p&gt;&#xA;&lt;ul&gt;&#xA;&lt;li&gt;Die Haltestelle Kolpingplatz ist au&amp;szlig;erhalb der Betriebszeiten der Linie 55 ersatzlos aufgehoben&lt;/li&gt;&#xA;&lt;li&gt;Die Haltestelle Mathystra&amp;szlig;e wird nur an den Bahnsteigen in der Mathystra&amp;szlig;e bedient&lt;/li&gt;&#xA;&lt;/ul&gt;&#xA;&lt;p&gt;&amp;nbsp;&lt;/p&gt;\n</Detail>\n</PtSituation>";

      JSONObject situations = XML.toJSONObject(kvvXMLData);

      Disruption disruption = KVVDataLoader.parseDisruption(situations);
      assertEquals(disruption.getLines(), "Linien 3, S12");
      assertEquals(disruption.getSummary(), "Karlsruhe: Weichen- und Gleisbauarbeiten in der Karlstraße (Phase 1)");
   }

   @Test
   public void parseDepartureTest() {
      String kvvXMLData = "<StopEvent>\n<ThisCall>\n<CallAtStop>\n<StopPointRef>de:08212:12:1:1</StopPointRef>\n<StopPointName>\n<Text>Karlsruhe Duale Hochschule</Text>\n<Language>de</Language>\n</StopPointName>\n<PlannedBay>\n<Text>Gleis 1</Text>\n<Language>de</Language>\n</PlannedBay>\n<ServiceDeparture>\n<TimetabledTime>2022-04-28T15:33:18Z</TimetabledTime>\n</ServiceDeparture>\n<StopSeqNumber>18</StopSeqNumber>\n</CallAtStop>\n</ThisCall>\n<Service>\n<OperatingDayRef>2022-04-28</OperatingDayRef>\n<JourneyRef>kvv:21001:E:H:j22:464</JourneyRef>\n<LineRef>kvv:21001:E:H</LineRef>\n<DirectionRef>outward</DirectionRef>\n<Mode>\n<PtMode>tram</PtMode>\n<TramSubmode>cityTram</TramSubmode>\n<Name>\n<Text>Straßenbahn</Text>\n<Language>de</Language>\n</Name>\n</Mode>\n<PublishedLineName>\n<Text>Straßenbahn 1</Text>\n<Language>de</Language>\n</PublishedLineName>\n<OperatorRef>kvv:02</OperatorRef>\n<RouteDescription>\n<Text>Durlach - Neureut-Heide</Text>\n<Language>de</Language>\n</RouteDescription>\n<Attribute>\n<Text>\n<Text>Niederflurwagen</Text>\n<Language>de</Language>\n</Text>\n<Code>372872</Code>\n<Mandatory>false</Mandatory>\n</Attribute>\n<OriginStopPointRef>de:08212:18:1:1</OriginStopPointRef>\n<OriginText>\n<Text>Durlach Turmberg</Text>\n<Language>de</Language>\n</OriginText>\n<DestinationText>\n<Text>Heide</Text>\n<Language>de</Language>\n</DestinationText>\n</Service>\n</StopEvent>";

      JSONObject stopResult = XML.toJSONObject(kvvXMLData);

      Departure departure = KVVDataLoader.parseDeparture(stopResult);

      assertEquals(departure.getLine(), "Straßenbahn 1");
      assertEquals(departure.getPlatform(), "Gleis 1");
      assertEquals(departure.getAttribute(), "Niederflurwagen");
      assertEquals(departure.getDestination(), "Heide");
      assertEquals(departure.getDepartureTime(), LocalDateTime.ofInstant(Instant.parse("2022-04-28T15:33:18Z"), ZoneOffset.UTC));
   }
}
