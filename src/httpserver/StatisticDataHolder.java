package httpserver;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import logging.StatisticLogger;


public class StatisticDataHolder {
	
	public final AtomicLong totalCount = new AtomicLong(0);
	public final AtomicLong numOfconnections = new AtomicLong(0);
	public final ConcurrentSkipListSet<String> uniqRequests = new ConcurrentSkipListSet<String>();
	public final ConcurrentHashMap<String, AtomicInteger> generalStatisticMap = new ConcurrentHashMap<String, AtomicInteger>();
	public final ConcurrentHashMap<String, AtomicInteger> urlRedirectMap = new ConcurrentHashMap<String, AtomicInteger>();
	public final ConcurrentHashMap<String, String> timeStampMap = new ConcurrentHashMap<String, String>();
	public final BlockingDeque<StatisticLogger>  logsDeque = new LinkedBlockingDeque<StatisticLogger>();
	
	
	
	
	

}
