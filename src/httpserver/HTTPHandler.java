package httpserver;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import logging.StatisticLogger;
import ajax.AjaxHandler;

public class HTTPHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private static final StatisticDataHolder metricsObj = new StatisticDataHolder();

	private StatisticLogger log;
	private Integer receivedBytes = 0;
	private HTTPHandlerConfig configuration = new HTTPHandlerConfig("/",
			"/redirect\\?url=(.*)", new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz"));
	private final long startTimeRequest = System.nanoTime();

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest msg)
			throws Exception {
		
		
		
		metricsObj.numOfconnections.incrementAndGet();
		

		ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				metricsObj.numOfconnections.decrementAndGet();
				flushLogQueue();
			}
		});

		HttpRequest request = (HttpRequest) msg;
		configuration.uri = request.getUri();
		configuration.isRedirection = configuration.uriPatternObj
				.matcher(configuration.uri);

		HttpHeaders headers = request.headers();

		if (!headers.isEmpty()) {
			for (Map.Entry<String, String> h : headers) {
				receivedBytes = receivedBytes + h.getKey().length()
						+ h.getValue().length();
			}
		}

		receivedBytes += request.getMethod().name().length()
				+ request.getProtocolVersion().text().length()
				+ configuration.uri.length();

		InetSocketAddress ipadress = (InetSocketAddress) ctx.channel()
				.remoteAddress();

		configuration.ipaddress = ipadress.getAddress().getHostAddress();
		metricsObj.uniqRequests.add(configuration.ipaddress);

		if (configuration.uri.equals("/status")) {
			writeGeneralStatistic();
			StringBuilder buffer = new StringBuilder();
			loadHtml(new File("src/statistic.html"),buffer);
			
			StatisticHandler statisticHandler = new StatisticHandler();
			statisticHandler.showCount(buffer, metricsObj.totalCount.get(),
					false);
			statisticHandler.showUniqCount(buffer,
					metricsObj.uniqRequests.size());
			statisticHandler.showCountByIpStatistic(buffer,
					metricsObj.generalStatisticMap, metricsObj.timeStampMap);
			statisticHandler
					.showUrlRedirectionStatistic(buffer, metricsObj.urlRedirectMap);
			statisticHandler.showCount(buffer,
					metricsObj.numOfconnections.get() / 2, true);
			statisticHandler.showLog(buffer, metricsObj.logsDeque);

			buffer.append("</body></html>");

			writeResponse(buffer, ctx);

		}

		if (configuration.uri.equals("/hello")) {
			writeGeneralStatistic();
			StringBuilder buffer = new StringBuilder();
			loadHtml(new File("src/hellopage.html"),buffer);
			
			new HashedWheelTimer().newTimeout(new TimerTask() {

				@Override
				public void run(Timeout timeout) throws Exception {
					writeResponse(buffer, ctx);
				}
			}, 10, TimeUnit.SECONDS);
		}

		
		if (configuration.isRedirection.matches()) {
			String redirectUrl = configuration.isRedirection.group(1);
			addUrlCounterToMap(redirectUrl);
			writeGeneralStatistic();
			writeRedirectResponse(redirectUrl, ctx);
		}

		// AJAX requests
		if (configuration.uri.equals("/getRequestNumbers")) {
			AjaxHandler a = new AjaxHandler();
			a.showCount(ctx, metricsObj.totalCount.get());
		}

		if (configuration.uri.equals("/getUniqCount")) {
			AjaxHandler a = new AjaxHandler();
			a.showUniqCount(ctx, metricsObj.uniqRequests.size());
		}

		if (configuration.uri.equals("/getIPStatistic")) {
			AjaxHandler a = new AjaxHandler();
			a.showCountStatistic(ctx, metricsObj.generalStatisticMap,
					metricsObj.timeStampMap);
		}
		if (configuration.uri.equals("/getUrlStatistic")) {
			AjaxHandler a = new AjaxHandler();
			a.showUrlStatistic(ctx, metricsObj.urlRedirectMap);
		}
		if (configuration.uri.equals("/getConnectionsCount")) {
			AjaxHandler a = new AjaxHandler();
			a.showCount(ctx, metricsObj.numOfconnections.get() / 2);
		}
		if (configuration.uri.equals("/showLog")) {
			AjaxHandler a = new AjaxHandler();
			a.showLog(ctx, metricsObj.logsDeque);
		}

	}

	private void addStatisticCounterToMap(String key) {
		if (metricsObj.generalStatisticMap.containsKey(key)) {
			metricsObj.generalStatisticMap.get(key).incrementAndGet();

		} else {
			metricsObj.generalStatisticMap.put(key, new AtomicInteger(1));
		}
	}

	private void addUrlCounterToMap(String key) {
		if (metricsObj.urlRedirectMap.containsKey(key)) {
			metricsObj.urlRedirectMap.get(key).incrementAndGet();
		} else {
			metricsObj.urlRedirectMap.put(key, new AtomicInteger(1));
		}
	}

	private void addTimestamp(String ip, String date) {
		metricsObj.timeStampMap.put(ip, date);
	}

	public void writeResponse(StringBuilder buffer, ChannelHandlerContext ctx) {

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.copiedBuffer(buffer.toString(), CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
		response.headers().set(CONTENT_LENGTH,
				response.content().readableBytes());

		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				final double latency = System.nanoTime() - startTimeRequest;
				log(response, latency);
				ctx.close();
			}
		});

	}

	private void log(FullHttpResponse response, final double latency) {
		int sent_bytes = response.content().readableBytes();
		final double totalBytes = sent_bytes + receivedBytes;
		BigDecimal test = new BigDecimal(totalBytes * 1000000000).divide(
				new BigDecimal(latency), 2, RoundingMode.HALF_UP);

		log = new StatisticLogger.LoggerBuild(configuration.ipaddress)
				.url(configuration.uri)
				.timestamp(configuration.timestampFormat.format(new Date()))
				.url(configuration.uri).sent_bytes(sent_bytes)
				.received_bytes(receivedBytes)
				.speed(String.valueOf(test).concat("b/s")).build();

		metricsObj.logsDeque.offer(log);
	}

	public void writeRedirectResponse(String redirectUrl,

			ChannelHandlerContext ctx) {
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);

		response.headers().set(LOCATION, redirectUrl);
		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				final double latency = System.nanoTime() - startTimeRequest;
				log(response, latency);
				ctx.close();
			}
		});
	}
	
	public void writeGeneralStatistic() {
		addStatisticCounterToMap(configuration.ipaddress);
		metricsObj.totalCount.incrementAndGet();
		addTimestamp(configuration.ipaddress,
				configuration.timestampFormat.format(new Date()));
		
	}
	
	public void loadHtml(File file, StringBuilder buffer) {
		
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String str;
		try {
			while ((str = br.readLine()) != null) {
				buffer.append(str);
				buffer.append("\n");
			}
			br.close();
			buffer.append("</body>");
			buffer.append("</html>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	public  void flushLogQueue() {
		if (metricsObj.logsDeque.size()<16) {
			return;
		}
		int todelete=metricsObj.logsDeque.size()-16;
		
		for (int i=0;i<todelete;i++) { 
			metricsObj.logsDeque.poll();
		}
	}
}


