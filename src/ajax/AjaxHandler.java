package ajax;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import logging.StatisticLogger;

public class AjaxHandler  {

	public void showCount(ChannelHandlerContext currentContext,
			long numOfRequests) {
		StringBuilder buf = new StringBuilder();
		buf.append(numOfRequests);
		writeAjaxResponse(buf, currentContext);
	}

	public void showUniqCount(ChannelHandlerContext currentContext,
			long uniqRequests) {
		StringBuilder buf = new StringBuilder();
		buf.append(uniqRequests);
		writeAjaxResponse(buf, currentContext);
	}

	public void showCountStatistic(ChannelHandlerContext currentContext,ConcurrentHashMap<String, AtomicInteger> ipToCountMap,
			ConcurrentHashMap<String, String> ipToTimeMap) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("<table border=\"1\">");		
		
		for (String ipadress:ipToCountMap.keySet()) {
			buf.append("<tr><td>").append(ipadress).append("</td><td>").append(ipToCountMap.get(ipadress)).append("</td><td>").append(ipToTimeMap.get(ipadress)).append("</td></tr>");
		}
		buf.append("</table>");
		writeAjaxResponse(buf,currentContext);

	}
	
	public void showLog(ChannelHandlerContext currentContext,BlockingDeque<StatisticLogger> logList) {
		
		LinkedBlockingDeque<StatisticLogger> internalCopy = new LinkedBlockingDeque<StatisticLogger>(logList);
		StringBuilder buf = new StringBuilder();
		buf.append("<table border=\"1\">");
		int numOfElements=16;
		StatisticLogger tmpLogger;
		Iterator<StatisticLogger> iterator = internalCopy.descendingIterator();
		
		while (iterator.hasNext() && numOfElements>0) {
			tmpLogger = iterator.next();
			buf.append("<tr><td>").append(tmpLogger.getIpadress()).append("</td><td>").append(tmpLogger.getUrl())
			.append("</td><td>").append(tmpLogger.getTimestamp()).append("</td><td>").append(tmpLogger.getSent_bytes())
			.append("</td><td>").append(tmpLogger.getReceived_bytes()).append("</td><td>").append(tmpLogger.getSpeed());
			numOfElements--;
		}

		buf.append("</table>");
		writeAjaxResponse(buf,currentContext);

		
	}

	public void showUrlStatistic(ChannelHandlerContext currentContext,ConcurrentHashMap<String, AtomicInteger> urlToCountMap) {
		StringBuilder buf = new StringBuilder();
		buf.append("<table border=\"1\">");		
		
		for (String url:urlToCountMap.keySet()) {
			buf.append("<tr><td>").append(url).append("</td><td>").append(urlToCountMap.get(url)).append("</td></tr>");
		
		}
		buf.append("</table>");
		writeAjaxResponse(buf,currentContext);
	}
	
	private void writeAjaxResponse(StringBuilder buf,ChannelHandlerContext currentContext) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.copiedBuffer(buf.toString(),
						CharsetUtil.UTF_8), true);
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());
		response.headers().set(CONNECTION, KEEP_ALIVE);
		currentContext.writeAndFlush(response).addListener(
				ChannelFutureListener.CLOSE);
	}
	
}
