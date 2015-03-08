package httpserver;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPHandlerConfig {
	public String ipaddress;
	public String uri;
	public String uripattern;
	public Pattern uriPatternObj = null;
	public Matcher isRedirection;
	public SimpleDateFormat timestampFormat;

	public HTTPHandlerConfig(String uri, String uripattern,
			SimpleDateFormat timestampFormat) {
		this.uri = uri;
		this.uripattern = uripattern;
		this.timestampFormat = timestampFormat;
		uriPatternObj = Pattern.compile(uripattern);
	}
}