package dhbw.timetable.rapla.parser;

import dhbw.timetable.rapla.date.DateUtilities;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.data.event.BackportAppointment;
import dhbw.timetable.rapla.data.time.TimelessDate;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import dhbw.timetable.rapla.network.BaseURL;
import dhbw.timetable.rapla.network.NetworkUtilities;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hendrik Ulbrich (C) 2017
 *
 * This is a parser for the rapla website. It is implemented as a singleton with
 * backport compatibility for the java.util.Date API. Use the public import methods
 * to import events into appointment structure.
 */
public final class DataImporter {

    @Deprecated
    public static final class Backport {

        private Backport() {}

        public static Map<TimelessDate, ArrayList<BackportAppointment>> ImportWeekRange(TimelessDate startDate, TimelessDate endDate, String url) throws MalformedURLException, NoConnectionException, IllegalAccessException {
            final String deSuffix = ".de/rapla?", cityPrefix = "dhbw-";
            int urlSplit = url.indexOf(deSuffix);
            final String regularPrefix = url.substring(0, url.indexOf(cityPrefix));
            return Backport.ImportWeekRange(startDate, endDate, BaseURL.valueOf(url.substring(regularPrefix.length() + cityPrefix.length(), urlSplit).toUpperCase()), url.substring(urlSplit + deSuffix.length()));
        }

        public static Map<TimelessDate, ArrayList<BackportAppointment>> ImportWeekRange(TimelessDate startDate, TimelessDate endDate, BaseURL baseURL, String args) throws MalformedURLException, NoConnectionException, IllegalAccessException {
            DataImporter.checkConnection(baseURL.complete() + args);

            Map<TimelessDate, ArrayList<BackportAppointment>> appointments = new LinkedHashMap<>();

            // To monday
            DateUtilities.Backport.Normalize(startDate);
            DateUtilities.Backport.Normalize(endDate);

            HashMap<String, String> params = NetworkUtilities.getParams(args);

            String connectionURL = NetworkUtilities.generateConnection(params, baseURL);

            // Request every week and put them into the map
            do {
                try {
                    appointments.put((TimelessDate) startDate.clone(), Backport.ImportWeek(startDate, connectionURL
                            + "&day=" + startDate.get(Calendar.DAY_OF_MONTH)
                            + "&month=" + (startDate.get(Calendar.MONTH) + 1)
                            + "&year=" + startDate.get(Calendar.YEAR)));
                } catch (IOException | ParserConfigurationException e) {
                    System.out.println("FAIL!" + System.lineSeparator() + "Error date: " + DateUtilities.GERMAN_STD_SDATEFORMAT.format(startDate));
                    e.printStackTrace();
                }
                // Next week
                DateUtilities.Backport.NextWeek(startDate);
            } while (!DateUtilities.Backport.IsDateOver(startDate, endDate));

            return appointments;
        }

        public static ArrayList<BackportAppointment> ImportWeek(TimelessDate localDate, String connectionURL) throws IOException, ParserConfigurationException, IllegalAccessException {
            String line, pageContent;
            ArrayList<BackportAppointment> weekAppointments = new ArrayList<>();
            StringBuilder pageContentBuilder = new StringBuilder();
            URLConnection webConnection = new URL(connectionURL).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8));

            DateUtilities.Backport.Normalize(localDate);

            // Read the whole page
            while ((line = br.readLine()) != null) {
                pageContentBuilder.append(line).append("\n");
            }
            br.close();
            pageContent = pageContentBuilder.toString();

            // Trim and filter to correct tbody inner HTML
            pageContent = ("<?xml version=\"1.0\"?>\n" + pageContent.substring(pageContent.indexOf("<tbody>"), pageContent.lastIndexOf("</tbody>") + 8))
                    .replaceAll("&nbsp;", "&#160;")
                    .replaceAll("<br>", "<br/>");

            // Parse the document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            try {
                Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(pageContent.getBytes("utf-8"))));
                doc.getDocumentElement().normalize();

                // Scan the table row by row
                NodeList nList = doc.getDocumentElement().getChildNodes();
                Node tableRow;
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    tableRow = nList.item(temp);
                    if (tableRow.getNodeType() == Node.ELEMENT_NODE) {
                        Backport.importTableRow(weekAppointments, tableRow, ((TimelessDate) localDate.clone()));
                    }
                }

            } catch (SAXException e) {
                System.out.println("FAIL!");
                System.out.println("Error while parsing:" + System.lineSeparator() + pageContent);
                e.printStackTrace();
            }

            return weekAppointments;
        }

        private static void importTableRow(ArrayList<BackportAppointment> appointments, Node tableRow, TimelessDate currDate) {
            // For each <td> in row
            NodeList cells = tableRow.getChildNodes();
            for (int i = 0; i < cells.getLength(); i++) {
                Node cell = cells.item(i);
                // Filter <th> and other crap, extract class=week_block
                if (cell.getNodeType() == Node.ELEMENT_NODE && cell.getNodeName().equals("td")) {
                    Element element = (Element) cell;
                    String type = element.getAttribute("class");
                    if (type.startsWith("week_block")) {
                        appointments.add(Backport.importAppointment(cell, (TimelessDate) currDate.clone()));
                    } else if (type.startsWith("week_separatorcell")) {
                        DateUtilities.Backport.AddDays(currDate, 1);
                    }
                }
            }
        }

        private static BackportAppointment importAppointment(Node block, TimelessDate date) {
            // All children from the event
            NodeList aChildren = block.getFirstChild().getChildNodes();

            String time = getTime(aChildren);

            // Rows from table body
            NodeList rows = aChildren.item(aChildren.getLength() - 1).getLastChild().getChildNodes();

            String[] description = getDescription(rows);

            return new BackportAppointment(time, date, description[0], description[1], description[2]);
        }

    }

    private DataImporter() {}

    /**
     * Imports all appointments from the given url which are in the parameters week range
     * @param startDate A day of the week to start (include)
     * @param endDate A day of the week to end (include)
     * @param url The complete url with the protocol and all arguments
     * @return Map of (LocalDate, ArrayList of (Appointment)) events ordered through weeks
     * @throws MalformedURLException If the passed url does not match pattern
     * @throws NoConnectionException If the server could not be reached
     * @throws IllegalAccessException If the passed arguments don't match
     */
    public static Map<LocalDate, ArrayList<Appointment>> ImportWeekRange(LocalDate startDate, LocalDate endDate, String url) throws MalformedURLException, NoConnectionException, IllegalAccessException {
		final String deSuffix = ".de/rapla?", cityPrefix = "dhbw-";
        int urlSplit = url.indexOf(deSuffix);
        final String regularPrefix = url.substring(0, url.indexOf(cityPrefix));
		return ImportWeekRange(startDate, endDate, BaseURL.valueOf(url.substring(regularPrefix.length() + cityPrefix.length(), urlSplit).toUpperCase()), url.substring(urlSplit + deSuffix.length()));
    }

    /**
     * Imports all appointments from the given url data which are in the parameters week range
     * @param startDate A day of the week to start (include)
     * @param endDate A day of the week to end (include)
     * @param baseURL The host enum BaseURL
     * @param args Arguments such as key or (user page file)
     * @return Map of (LocalDate, ArrayList(Appointment)) events ordered through weeks
     * @throws MalformedURLException If the passed url does not match pattern
     * @throws NoConnectionException If the server could not be reached
     * @throws IllegalAccessException If the passed arguments don't match
     */
    public static Map<LocalDate, ArrayList<Appointment>> ImportWeekRange(LocalDate startDate, LocalDate endDate, BaseURL baseURL, String args) throws MalformedURLException, NoConnectionException, IllegalAccessException {
        // Ensure connection. Throw errors if invalid connection
        checkConnection(baseURL.complete() + args);

	    Map<LocalDate, ArrayList<Appointment>> appointments = new LinkedHashMap<>();

	    // To monday
	    startDate = DateUtilities.Normalize(startDate);
		endDate = DateUtilities.Normalize(endDate);

        HashMap<String, String> params = NetworkUtilities.getParams(args);

        String connectionURL = NetworkUtilities.generateConnection(params, baseURL);

        // Request every week and put them into the map
		do {
            try {
                appointments.put(startDate, ImportWeek(startDate, connectionURL
                        + "&day=" + startDate.getDayOfMonth()
                        + "&month=" + startDate.getMonthValue()
                        + "&year=" + startDate.getYear()));
            } catch (IOException | ParserConfigurationException e) {
            	System.out.println("FAIL!" + System.lineSeparator() + "Error date: " + startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                e.printStackTrace();
            }
            // Next week
            startDate = startDate.plusDays(7);
		} while (!startDate.isAfter(endDate));

		return appointments;
	}

    /**
     * Imports all events of the week
     * @param localDate A day of the week to import
     * @param connectionURL The host enum BaseURL
     * @return Unordered list of appointments scheduled for this week (not-null)
     * @throws IOException If input could not be loaded
     * @throws ParserConfigurationException If the parsing failed
     * @throws IllegalAccessException If the passed arguments don't match
     */
	public static ArrayList<Appointment> ImportWeek(LocalDate localDate, String connectionURL) throws IOException, ParserConfigurationException, IllegalAccessException {
        String line, pageContent;
	    ArrayList<Appointment> weekAppointments = new ArrayList<>();
		StringBuilder pageContentBuilder = new StringBuilder();
        URLConnection webConnection = new URL(connectionURL).openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8));
        localDate = DateUtilities.Normalize(localDate);

		// Read the whole page
		while ((line = br.readLine()) != null) {
			pageContentBuilder.append(line).append("\n");
		}
		br.close();
		pageContent = pageContentBuilder.toString();

		// Trim and filter to correct tbody inner HTML
		pageContent = ("<?xml version=\"1.0\"?>\n" + pageContent.substring(pageContent.indexOf("<tbody>"), pageContent.lastIndexOf("</tbody>") + 8))
				.replaceAll("&nbsp;", "&#160;")
				.replaceAll("<br>", "<br/>");

        Pattern pattern = Pattern.compile("(<a.*</a>)");
        Matcher matcher = pattern.matcher(pageContent);
        while(matcher.find()) {
            String anchor = matcher.group(1);
            if (anchor == null) {
                continue;
            }
            String[] anchorSplit = anchor.split("\"");
            if (anchorSplit[1].contains("<") || anchorSplit[1].contains(">")) {
                pageContent = pageContent.replaceAll(anchor, "");
            }
        }

		// Parse the document
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		try {
            Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(pageContent.getBytes("utf-8"))));
            doc.getDocumentElement().normalize();

            // Scan the table row by row
            NodeList nList = doc.getDocumentElement().getChildNodes();
            Node tableRow;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                tableRow = nList.item(temp);
                if (tableRow.getNodeType() == Node.ELEMENT_NODE) {
                    importTableRow(weekAppointments, tableRow, DateUtilities.Clone(localDate));
                }
            }

        } catch (SAXException e) {
		    System.out.println("FAIL!");
		    System.out.println("Error while parsing:" + System.lineSeparator() + pageContent);
            e.printStackTrace();
        }

		return weekAppointments;
	}

    /**
     * Checks if the URL pattern matches a regular expression and pings the server
     * @param fullURL The full URL to test against
     * @throws MalformedURLException If the passed url does not match pattern
     * @throws NoConnectionException If the server could not be reached
     */
    private static void checkConnection(String fullURL) throws MalformedURLException, NoConnectionException {
        if(!NetworkUtilities.URLIsValid(fullURL)) {
            throw new MalformedURLException();
        } else if (!NetworkUtilities.TestConnection(fullURL)) {
            throw new NoConnectionException(fullURL);
        }
    }

    /**
     * Inserts any appointments from the horizontal row to the container list.
     * @param appointments Container to insert appointment into
     * @param tableRow The horizontal row node
     * @param currDate Date for the start of the row
     */
    private static void importTableRow(ArrayList<Appointment> appointments, Node tableRow, LocalDate currDate) {
		// For each <td> in row
		NodeList cells = tableRow.getChildNodes();
		for (int i = 0; i < cells.getLength(); i++) {
			Node cell = cells.item(i);
			// Filter <th> and other crap, extract class=week_block
			if (cell.getNodeType() == Node.ELEMENT_NODE && cell.getNodeName().equals("td")) {
				Element element = (Element) cell;
				String type = element.getAttribute("class");
				if (type.startsWith("week_block")) {
					appointments.add(importAppointment(cell, currDate));
				} else if (type.startsWith("week_separatorcell")) {
					currDate = currDate.plusDays(1);
				}
			}
		}
	}

    /**
     * Parses a node into an appointment for the given date. The information is based on anchor and tooltip data.
     * @param block The appointments week_block node
     * @param date The given date
     * @return Imported appointment
     */
	private static Appointment importAppointment(Node block, LocalDate date) {
        // All children from the event
        NodeList aChildren = block.getFirstChild().getChildNodes();

        String time = getTime(aChildren);

        //TODO HIER MUSS GEFIXT WERDEN

        // Rows from table body
        NodeList rows = aChildren.item(aChildren.getLength() - 1).getLastChild().getChildNodes();

        String[] description = getDescription(rows);

        LocalDateTime[] times = DateUtilities.ConvertToTime(date, time);
        return new Appointment(times[0], times[1], description[0], description[1], description[2]);
    }

    private static String getTime(NodeList aChildren) {
        // If no event is provided, appointment is whole working day
        String timeData, time = "99:99-99:99";
        Node firstElNode = aChildren.item(0);
        // If no event is provided, event is whole working day
        if (firstElNode.getNodeType() == Node.ELEMENT_NODE) {
            Element el = (Element) firstElNode;
            String tagName  = el.getTagName();
            if (tagName.equals("span") && el.getAttribute("class" ).equals("link")) {
                time = getTime(firstElNode.getChildNodes());
            } else {
                time = "08:00-18:00";
            }
        } else if (firstElNode.getNodeType() == Node.TEXT_NODE){
            timeData = ((CharacterData) firstElNode).getData();
            // Filter &#160; alias &nbsp;
            time = timeData.substring(0, 5).concat(timeData.substring(6));
        }
        return time;
    }

    private static String[] getDescription(NodeList rows) {
        NodeList dataItems;
        Element labelElement, dataElement;
        String className;
        StringBuilder titleBuilder = new StringBuilder(),
                personsBuilder = new StringBuilder(),
                resourcesBuilder = new StringBuilder(),
                tempBuilder = null;
        // Handle each row of the table
        for (int i = 0; i < rows.getLength(); i++) {
            Node row = rows.item(i);
            if(row.getNodeType() == Node.ELEMENT_NODE) {
                dataItems = row.getChildNodes();
                labelElement = (Element) dataItems.item(1);

                // Get category: info or title
                switch (labelElement.getTextContent()) {
                    case "Titel:":
                    case "Veranstaltungsname:":
                        tempBuilder = titleBuilder;
                        break;
                    case "Personen:":
                        tempBuilder = personsBuilder;
                        break;
                    case "Bemerkung:":
                    case "Ressourcen:":
                        tempBuilder = resourcesBuilder;
                        break;
                    default:
                        tempBuilder = null;
                        // System.out.println("WARN: Unknown labelElement text content: " + labelElement.getTextContent());
                        break;
                }

                if (tempBuilder != null) {
                    // Skip label and text
                    for (int j = 3; j < dataItems.getLength(); j++) {
                        Node dataNode = dataItems.item(j);
                        if (dataNode.getNodeType() == Node.ELEMENT_NODE) {
                            dataElement = (Element) dataItems.item(j);
                            className = dataElement.getAttribute("class");
                            if (className.equals("value")) {
                                tempBuilder.append(dataElement.getTextContent()).append(" ");
                            } else {
                                System.out.println("WARN: Unknown className in data table: " + className);
                            }
                        }
                    }
                }
            }
        }
        return new String[] { titleBuilder.toString().trim(), personsBuilder.toString().trim(), resourcesBuilder.toString().trim() };
    }

}
