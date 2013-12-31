package org.springframework.http.converter.json;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

public class MappingJacksonHttpMessageConverter extends AbstractHttpMessageConverter<Object>
{
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private static final String format = "yyyy-MM-dd";
  private ObjectMapper objectMapper = new ObjectMapper();

  private boolean prefixJson = false;

  @SuppressWarnings("deprecation")
public MappingJacksonHttpMessageConverter()
  {
    super(new MediaType("application", "json", DEFAULT_CHARSET));
    SimpleDateFormat df = new SimpleDateFormat(format);
    this.objectMapper.getSerializationConfig().setDateFormat(df);
  }

  public void setObjectMapper(ObjectMapper objectMapper)
  {
    Assert.notNull(objectMapper, "ObjectMapper must not be null");
    this.objectMapper = objectMapper;
  }

  public ObjectMapper getObjectMapper()
  {
	  
    return this.objectMapper;
  }

  public void setPrefixJson(boolean prefixJson)
  {
    this.prefixJson = prefixJson;
  }

  public boolean canRead(Class<?> clazz, MediaType mediaType)
  {
    JavaType javaType = getJavaType(clazz);
    return (this.objectMapper.canDeserialize(javaType)) && (canRead(mediaType));
  }

  public boolean canWrite(Class<?> clazz, MediaType mediaType)
  {
    return (this.objectMapper.canSerialize(clazz)) && (canWrite(mediaType));
  }

  protected boolean supports(Class<?> clazz)
  {
    throw new UnsupportedOperationException();
  }

  protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException
  {
    JavaType javaType = getJavaType(clazz);
    try {
      return this.objectMapper.readValue(inputMessage.getBody(), javaType);
    }
    catch (IOException ex) {
      throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
    }
  }

  protected void writeInternal(Object object, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException
  {
    JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
    JsonGenerator jsonGenerator = 
      this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
    try {
      if (this.prefixJson) {
        jsonGenerator.writeRaw("{} && ");
      }
      this.objectMapper.writeValue(jsonGenerator, object);
    }
    catch (IOException ex) {
      throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("deprecation")
protected JavaType getJavaType(Class<?> clazz)
  {
    return TypeFactory.type(clazz);
  }

  protected JsonEncoding getJsonEncoding(MediaType contentType)
  {
    if ((contentType != null) && (contentType.getCharSet() != null)) {
      Charset charset = contentType.getCharSet();
      for (JsonEncoding encoding : JsonEncoding.values()) {
        if (charset.name().equals(encoding.getJavaName())) {
          return encoding;
        }
      }
    }
    return JsonEncoding.UTF8;
  }
}