package org.hx.rainbow.common.context;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class CustomDateSerializer extends ObjectMapper {

	private static final String format = "yyy-MM-dd HH:mm:ss";

	@SuppressWarnings("deprecation")
	public SerializationConfig getSerializationConfig() {
		SimpleDateFormat df = new SimpleDateFormat(format);
		this._serializationConfig.setDateFormat(df);
		return this._serializationConfig;
	}
}