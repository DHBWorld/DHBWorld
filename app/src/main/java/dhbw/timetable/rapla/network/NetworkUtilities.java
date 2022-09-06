package dhbw.timetable.rapla.network;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by Hendrik Ulbrich (C) 2017
 */
public final class NetworkUtilities {

	private final static String PROTOCOL_REGEX = "(HTTPS|https)://";

	private NetworkUtilities() {}

    /**
     * Try to establish an URL internet connection.
     *
     * @param url The url to check
     * @return true if the connection is open, false if an error occured.
     */
	public static boolean TestConnection(String url) {
		try {
			new URL(url).openConnection();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

    /**
     * Checks if a given url matches a specific pattern of a valid rapla url
     *
     * @param url The url to validate
     * @return true if the pattern matched, false if not
     */
	public static boolean URLIsValid(String url) {
		return url.matches(PROTOCOL_REGEX + "rapla\\.dhbw-" + BaseURL.getRegex() + "\\.de/rapla?(.+=.+)(&.+=.+)*");
	}

	public static HashMap<String, String> getParams(String args) {
		HashMap<String, String> params = new HashMap<>();
		String[] paramsStrings = args.split("&");
		for (String paramsString : paramsStrings) {
			if (paramsString.contains("=")) {
                String[] kvStrings = paramsString.split("=");
                params.put(kvStrings[0], kvStrings[1]);
            }
		}
		return params;
	}

	public static String generateConnection(HashMap<String, String> params, BaseURL baseURL) throws IllegalAccessException {
		// Extract the parameters
		final StringBuilder connectionURLBuilder = new StringBuilder(baseURL.complete()).append("?");
		// Appending only necessary parameters
		// key=txB1FOi5xd1wUJBWuX8lJhGDUgtMSFmnKLgAG_NVMhA_bi91ugPaHvrpxD-lcejo&today=Heute
		if (params.containsKey("key")) {
			connectionURLBuilder.append("key=").append(params.get("key"));
			// page=calendar&user=vollmer&file=tinf15b3&today=Heute
		} else if (params.containsKey("page") && params.get("page").equalsIgnoreCase("calendar") && params.containsKey("user") && params.containsKey("file")) {
			connectionURLBuilder.append("page=calendar&user=").append(params.get("user")).append("&file=").append(params.get("file"));
		} else {
			throw new IllegalAccessException();
		}
		return connectionURLBuilder.toString();
	}

}
