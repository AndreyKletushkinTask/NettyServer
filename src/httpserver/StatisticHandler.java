package httpserver;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import logging.StatisticLogger;

public class StatisticHandler extends HTTPHandler {

	public void showCount(StringBuilder buffer,
			long numOfRequests, boolean isConnectionsRequest) {
		if (isConnectionsRequest) {
			buffer.append("Number of alive connections:");
		} else {
			buffer.append("Whole numbers of requests:");
		}
		buffer.append(numOfRequests);
		
	}

	public void showUniqCount(StringBuilder buffer,
			long uniqRequests) {
		buffer.append("<br>Whole numbers of uniq requests:");
		buffer.append(uniqRequests);
	
	}

	public void showCountByIpStatistic(StringBuilder buffer ,ConcurrentHashMap<String, AtomicInteger> ipToCountMap,
			ConcurrentHashMap<String, String> ipToTimeMap) {
		buffer.append("<br>Request by one ip:");
		buffer.append("<br><table border=\"1\">");		
		buffer.append("<tr><td>").append("IP").append("</td><td>").append("Count").append("</td><td>").append("Time").append("</td></tr>");
		
		for (String ipadress:ipToCountMap.keySet()) {
			buffer.append("<tr><td>").append(ipadress).append("</td><td>").append(ipToCountMap.get(ipadress)).append("</td><td>").append(ipToTimeMap.get(ipadress)).append("</td></tr>");
		}
		buffer.append("</table>");
	}
	
	public void showUrlRedirectionStatistic(StringBuilder buffer,ConcurrentHashMap<String, AtomicInteger> urlToCountMap) {
		
		buffer.append("<table border=\"1\">");	
		buffer.append("<br>Redirect counter by URL:");
		buffer.append("<tr><td>").append("URL").append("</td><td>").append("Count").append("</td></tr>");
		
		for (String url:urlToCountMap.keySet()) {
			buffer.append("<tr><td>").append(url).append("</td><td>").append(urlToCountMap.get(url)).append("</td></tr>");
		
		}
		buffer.append("</table>");
	
	}
	
	public void showLog(StringBuilder buffer,BlockingDeque<StatisticLogger> logList) {
		
		LinkedBlockingDeque<StatisticLogger> internalCopy = new LinkedBlockingDeque<StatisticLogger>(logList);
		buffer.append("<br>LogTable:");
		buffer.append("<table border=\"1\">");
		int numOfElements=16;
		StatisticLogger tmpLogger;
		Iterator<StatisticLogger> iterator = internalCopy.descendingIterator();
		buffer.append("<tr><td>").append("IP").append("</td><td>").append("URL").append("</td><td>")
		.append(" timestamp").append("</td><td>").append("sent_bytes").append("</td><td>")
		.append("received_bytes").append("</td><td>").append("speed").append("</td><tr>");
		
		while (iterator.hasNext() && numOfElements>0) {
			tmpLogger = iterator.next();
			buffer.append("<tr><td>").append(tmpLogger.getIpadress()).append("</td><td>").append(tmpLogger.getUrl())
			.append("</td><td>").append(tmpLogger.getTimestamp()).append("</td><td>").append(tmpLogger.getSent_bytes())
			.append("</td><td>").append(tmpLogger.getReceived_bytes()).append("</td><td>").append(tmpLogger.getSpeed());
			numOfElements--;
		}

		buffer.append("</table>");

	}
}
