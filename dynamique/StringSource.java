package dynamique;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class StringSource extends SimpleJavaFileObject {

    private String code;

    public StringSource(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + ".java"), Kind.SOURCE);
        this.code = code;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors){
        return code;
    }
}
