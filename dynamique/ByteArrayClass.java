package dynamique;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ByteArrayClass extends SimpleJavaFileObject {

    private ByteArrayOutputStream out;

    public ByteArrayClass(String name) {
        super(URI.create("string:///" + name.replace('.', '/') + ".java"), Kind.SOURCE);
    }

    public byte[] getCode(){
        return out.toByteArray();
    }

    public OutputStream openOutputStream() throws IOException {

        out = new ByteArrayOutputStream();
        return out;
    }
}
