package club.curahq.core.util.core;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import me.apache.commons.lang3.time.FastDateFormat;

public final class DateTimeFormats {
	public static final TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
	public static final ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
	public static final FastDateFormat DAY_MTH_HR_MIN = FastDateFormat.getInstance("dd/MM HH:mm", SERVER_TIME_ZONE,
			Locale.ENGLISH);
	public static final FastDateFormat DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss",
			SERVER_TIME_ZONE, Locale.ENGLISH);
	public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM/yy hh:mma",
			SERVER_TIME_ZONE, Locale.ENGLISH);
	public static final FastDateFormat DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma",
			SERVER_TIME_ZONE, Locale.ENGLISH);
	public static final FastDateFormat HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", SERVER_TIME_ZONE,
			Locale.ENGLISH);
	public static final FastDateFormat HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", SERVER_TIME_ZONE,
			Locale.ENGLISH);
	public static final FastDateFormat HR_MIN = FastDateFormat.getInstance("hh:mm", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static final FastDateFormat MIN_SECS = FastDateFormat.getInstance("mm:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static final FastDateFormat KOTH_FORMAT = FastDateFormat.getInstance("m:ss", SERVER_TIME_ZONE,
			Locale.ENGLISH);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal() {
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.#");
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = new ThreadLocal() {
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.0");
		}
	};
}
