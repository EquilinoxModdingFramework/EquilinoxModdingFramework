package kd.equilinox.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains various methods for manipulating Dates.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public final class DateUtils {
	private DateUtils() {
	}

	public static String getNow() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
		Date date = new Date();
		String dateFormatted = dateFormat.format(date);
		return dateFormatted;
	}
}
