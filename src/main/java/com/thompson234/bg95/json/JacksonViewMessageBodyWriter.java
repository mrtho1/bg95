package com.thompson234.bg95.json;

import com.yammer.dropwizard.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.eclipse.jetty.io.EofException;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("text/x-json")
public class JacksonViewMessageBodyWriter implements MessageBodyWriter<Object> {
    private static final Log LOG = Log.forClass(JacksonViewMessageBodyWriter.class);

    private final ObjectMapper _mapper;
    private final Class _viewClass;

    public JacksonViewMessageBodyWriter(ObjectMapper mapper, Class viewClass) {

        _mapper = mapper;
        _viewClass = viewClass;
    }

    @Override
    public boolean isWriteable(Class<?> type,
                               Type genericType,
                               Annotation[] annotations,
                               MediaType mediaType) {

        return _mapper.canSerialize(type);
    }

    @Override
    public long getSize(Object t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            final ObjectWriter writer = _mapper.writerWithView(_viewClass);
            writer.writeValue(entityStream, t);
        } catch (EofException ignored) {
            // we don't care about these
        } catch (IOException e) {
            LOG.error(e, "Error writing response");
        }
    }
}
