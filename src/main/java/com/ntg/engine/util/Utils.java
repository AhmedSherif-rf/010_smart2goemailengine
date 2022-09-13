package com.ntg.engine.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class Utils {

	public static boolean isNotEmpty(List obj) {
		return obj != null && obj.size() != 0;
	}

	public static boolean isNotEmpty(String obj) {
		return obj != null && obj.length() != 0;
	}

	public static boolean isNotEmpty(Object obj) {
		return obj != null;
	}

	public static Boolean isEmptyString(String str) {
		if (str == null || str.equals(""))
			return true;
		return false;
	}

	public static boolean isEmpty(Long obj) {
		return obj == null || obj.equals(0L);
	}

	public static boolean isNotEmpty(Long obj) {
		return obj != null && obj.toString().length() != 0;
	}

	public static boolean isNotEmpty(Object[] obj) {
		return obj != null && obj.length != 0;
	}

	public static boolean isEmpty(Object obj) {
		return obj == null;
	}

	public static boolean isEmpty(List obj) {
		return obj == null || obj.size() == 0;
	}

	public static Boolean isValidEmail(String email) {
		if (email == null || email.isEmpty())
			return true;
		Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		return b;
	}

	public static boolean compareDates(Date date1, Date date2) {
		try {
			String dt1 = null;
			String dt2 = null;

			if ((Utils.isEmpty(date1) & Utils.isNotEmpty(date2)) || (Utils.isNotEmpty(date1) & Utils.isEmpty(date2))) {
				return false;
			} else if (Utils.isEmpty(date1) & Utils.isEmpty(date2)) {
				return true;
			}

			dt1 = date1.toString();
			dt2 = date2.toString();

			SimpleDateFormat sdf = null;

			if (dt1.contains("/"))
				sdf = new SimpleDateFormat("dd/MM/yyyy");
			else
				sdf = new SimpleDateFormat("dd-MM-yyyy");

			if (dt2.contains("/"))
				sdf = new SimpleDateFormat("dd/MM/yyyy");
			else
				sdf = new SimpleDateFormat("dd-MM-yyyy");

			Date d1 = sdf.parse(dt1);
			Date d2 = sdf.parse(dt2);

			if (d1.compareTo(d2) > 0) {
				return false;
			} else if (d1.compareTo(d2) < 0) {
				return false;
			} else if (d1.compareTo(d2) == 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static MediaType getMediaType(int mediaTypeId) {
		switch (mediaTypeId) {
			case 1:
				return MediaType.APPLICATION_JSON;

			default:
				return MediaType.APPLICATION_JSON;
		}

	}

	public static HttpMethod getHttpMethod(int httpMethodId) {
		switch (httpMethodId) {
			case 1:
				return HttpMethod.GET;

			case 2:
				return HttpMethod.POST;

			default:
				return HttpMethod.GET;
		}
	}
}