package logging;

public class StatisticLogger {

	private String ipadress;
	private String url;
	private String timestamp;
	private Integer sent_bytes, received_bytes;
	private String speed;

	public StatisticLogger(LoggerBuild lbuild) {
		this.ipadress = lbuild.ipadress;
		this.url = lbuild.url;
		this.timestamp = lbuild.timestamp;
		this.sent_bytes = lbuild.sent_bytes;
		this.received_bytes = lbuild.received_bytes;
		this.speed = lbuild.speed;
	}

	public static class LoggerBuild {
		private String ipadress;
		private String url;
		private String timestamp;
		private Integer sent_bytes, received_bytes;
		private String speed;
		
		public LoggerBuild(String ipadress) {
			this.ipadress=ipadress;
		}

		public LoggerBuild url(String url) {
			this.url = url;
			return this;
		}

		public LoggerBuild timestamp(String timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public LoggerBuild sent_bytes(Integer sent_bytes) {
			this.sent_bytes = sent_bytes;
			return this;
		}

		public LoggerBuild received_bytes(Integer received_bytes) {
			this.received_bytes = received_bytes;
			return this;
		}

		public LoggerBuild speed(String speed) {
			this.speed = speed;
			return this;
		}

		public StatisticLogger build() {
			StatisticLogger logger = new StatisticLogger(this);
			return logger;

		}

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StatisticLogger)) {
			return false;
		}
		StatisticLogger other = (StatisticLogger) obj;
		if (ipadress == null) {
			if (other.ipadress != null) {
				return false;
			}
		} else if (!ipadress.equals(other.ipadress)) {
			return false;
		}
		if (received_bytes == null) {
			if (other.received_bytes != null) {
				return false;
			}
		} else if (!received_bytes.equals(other.received_bytes)) {
			return false;
		}
		if (sent_bytes == null) {
			if (other.sent_bytes != null) {
				return false;
			}
		} else if (!sent_bytes.equals(other.sent_bytes)) {
			return false;
		}
		if (speed == null) {
			if (other.speed != null) {
				return false;
			}
		} else if (!speed.equals(other.speed)) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}
	public String getIpadress() {
		return ipadress;
	}
	public String getUrl() {
		return url;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public Integer getSent_bytes() {
		return sent_bytes;
	}
	public Integer getReceived_bytes() {
		return received_bytes;
	}
	public String getSpeed() {
		return speed;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ipadress == null) ? 0 : ipadress.hashCode());
		result = prime * result
				+ ((received_bytes == null) ? 0 : received_bytes.hashCode());
		result = prime * result
				+ ((sent_bytes == null) ? 0 : sent_bytes.hashCode());
		result = prime * result + ((speed == null) ? 0 : speed.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "StatisticLogger [ipadress=" + ipadress + ", url=" + url + ", timestamp="
				+ timestamp + ", sent_bytes=" + sent_bytes
				+ ", received_bytes=" + received_bytes + ", speed=" + speed
				+ "]";
	}

}
