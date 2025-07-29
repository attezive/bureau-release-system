package bureau.release.system.config;

import feign.RequestTemplate;
import feign.codec.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class OutputStreamEncoder implements Encoder {
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        if (object instanceof ByteArrayOutputStream) {
            ByteArrayOutputStream os = (ByteArrayOutputStream) object;
            template.body(os.toByteArray(), StandardCharsets.UTF_8);
        } else if (object instanceof OutputStream) {
            throw new UnsupportedOperationException("Only ByteArrayOutputStream supported");
        }
    }
}