package dynamique;

import voiture.Surveillable;
import voiture.Voiture;
import voiture.VoitureSport;
import javax.tools.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class VoitureFactory {
    public enum ModeConstruction {INSTANCIATION, REFLEXION, META};

    private static Voiture buildMetaVoiture(boolean sport, int vitesse){

        // ******** ETAPE #1 : Préparation pour la compilation
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<ByteArrayClass> classes = new ArrayList<>();           // pour mettre les .class   (IMPORTANT)
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        JavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // La classe qui se charge de fournir les "conteneurs" au compilateur à la volée, sans accès au disque
        fileManager = new ForwardingJavaFileManager<JavaFileManager>(fileManager){
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                if (kind == JavaFileObject.Kind.CLASS){
                    ByteArrayClass outFile = new ByteArrayClass(className);
                    classes.add(outFile);           // ICI IMPORTANT
                    return outFile;
                }
                else
                    return super.getJavaFileForOutput(location, className, kind, sibling);
            }
        };

        // ******** ETAPE #2 : Génération du code source
        List<JavaFileObject> sources = List.of(
            VoitureFactory.buildSource("MetaVoitureSport", true, vitesse),
            VoitureFactory.buildSource("MetaVoiture", false, vitesse)
        );

        // ******** ETAPE #3 : Compilation
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, null,
                null, sources);
        Boolean result = task.call();

        for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics())
            System.out.println(d);

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!result) {
            System.out.println("ECHEC DE LA COMPILATION");
            System.exit(1);
        }


        // ******** ETAPE #4 : Instanciation
        ByteArrayClasseLoader loader = new ByteArrayClasseLoader(classes);
        Voiture maVoiture = null;
        try {
            // Recherche la classe dans le contexte "local" sinon il passe par le "loader"
            if(sport){
                maVoiture = (Voiture)(Class.forName("voiture.MetaVoitureSport", true, loader)
                        .getDeclaredConstructor()
                        .newInstance()
                );
            }
            else{
                maVoiture = (Voiture)(Class.forName("voiture.MetaVoiture", true, loader)
                        .getDeclaredConstructor()
                        .newInstance()
                );
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // ******** ETAPE #5 : Renvoi
        return maVoiture;
    }


    private static JavaFileObject buildSource(String nomClasse, boolean sport, int vitesse) {

        StringBuilder sb = new StringBuilder();
        sb.append("package voiture;\n");
        if(sport){
            sb.append("public class " + nomClasse + " extends VoitureSport implements Surveillable{\n");
            sb.append("public " + nomClasse + "(){ super();}\n");
        }
        else{
            sb.append("public class " + nomClasse + " extends Voiture implements Surveillable{\n");
            sb.append("public " + nomClasse + "(){ super("+vitesse+");}\n");
        }

        genererMethodes(sb);

        sb.append("}\n");

        System.out.println("LA CLASSE");
        System.out.println(sb.toString());

        return new StringSource(nomClasse, sb.toString());
    }

    private static void genererConstructeurs(String nomClasse, int vitesse, StringBuilder sb) {
        sb.append("public " + nomClasse + "(){ super("+vitesse+");}\n");
    }

    private static void genererMethodes(StringBuilder sb) {
        sb.append("public int surveiller(int limite){" +
                "   return vitesse - limite;\n" +
                "}\n");
    }

    public static Voiture buildVoiture(ModeConstruction mode, boolean sport, int vitesse) {
        if (mode == ModeConstruction.INSTANCIATION) {
            if (sport) {
                return new VoitureSport();
            } else {
                return new Voiture(vitesse);
            }
        } else if(mode == ModeConstruction.REFLEXION) {
            try {
                if(sport) {
                    return (VoitureSport) Class.forName("voiture.VoitureSport").getDeclaredConstructor().newInstance();
                } else {
                    return (Voiture) Class.forName("voiture.Voiture").getDeclaredConstructor(int.class).newInstance(vitesse);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } else if(mode == ModeConstruction.META) {
            return buildMetaVoiture(sport, vitesse);
        }
        return null;
    }
}
