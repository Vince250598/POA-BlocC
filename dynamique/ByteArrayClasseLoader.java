package dynamique;

public class ByteArrayClasseLoader extends ClassLoader{

    private Iterable<ByteArrayClass> classes;

    public ByteArrayClasseLoader(Iterable<ByteArrayClass> classes) {
        this.classes = classes;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {

        for (ByteArrayClass classe: classes){
            if (classe.getName().equals("/" + name.replace('.', '/') + ".java")){
                byte[] bytes = classe.getCode();
                // La magie est ICI
                return defineClass(name, bytes, 0, bytes.length);
            }
        }
        throw new ClassNotFoundException(name);
    }
}
