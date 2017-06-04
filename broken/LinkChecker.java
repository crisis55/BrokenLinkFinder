package broken;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

// Author: Lucas Galleguillos
// Filename: LinkChecker.java

public class LinkChecker {
	private ArrayList<String> pageContent;
	private Scanner urlScanner;
	private Status stat;
	private String toScan;
	private TreeMap<Integer, String> brokenLinks;
	private URL url;
	private HttpURLConnection scanConnection;

	public LinkChecker(String toScan) {
		this.toScan = toScan;
	}

	public void scanPage() {
		if (makeConnection()) { // Check if connection is possible.
			if (inspectPage()) { // Check if page can be inspected.
				stat = Status.SUCCESS;
			} else {
				stat = Status.SCAN_ERROR;
			}
			scanConnection.disconnect();
		} else {
			stat = Status.BAD_URL_OR_CONNECTION;
		}
	}

	private boolean makeConnection() {
		try {
			url = new URL(toScan);
			scanConnection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean inspectPage() {
		try {
			int i = 1;
			String line;
			brokenLinks = new TreeMap<>();
			pageContent = new ArrayList<>();
			urlScanner = new Scanner(scanConnection.getInputStream());
			while (urlScanner.hasNext()) {
				line = urlScanner.nextLine().trim();
				if (closeInspect(line)) {
					brokenLinks.put(i, line);
				}
				pageContent.add(line);
				i ++;
			}
			urlScanner.close();
		} catch (Exception e) {
			return true;
		}
		return true;
	}

	private boolean closeInspect(String s) {
		String find = "href=";
		if (s.contains(find)) {
			int hrefIndex = s.indexOf(find);
			int linkStartIndex = hrefIndex + 5;
			String linkAndTag;
			int linkEndIndex;
			String linkString = "";
			if (s.charAt(linkStartIndex) == '"'){
				linkAndTag = s.substring(linkStartIndex + 1, s.length());
				linkEndIndex = linkAndTag.indexOf('"');
				linkString += linkAndTag.substring(0, linkEndIndex);
			} else if (s.charAt(linkStartIndex) == '\'') {
				linkAndTag = s.substring(linkStartIndex + 1, s.length());
				linkEndIndex = linkAndTag.indexOf('\'');
				linkString += linkAndTag.substring(0, linkEndIndex);
			}
			try {
				URL link = new URL(linkString);
				HttpURLConnection http = (HttpURLConnection)link.openConnection();
				int statusCode = http.getResponseCode();
				if (statusCode != 200) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public Status getStatus() {
		return stat;
	}

	public TreeMap<Integer, String> getBrokenLinks() {
		return brokenLinks;
	}
}
