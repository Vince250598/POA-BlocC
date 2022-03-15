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

    private void buildMetaVoiture(boolean sport, int vitesse){

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
        List<JavaFileObject> sources =  null;
        /*List.of(
                VoitureFactory.buildSource("MetaVoitureSport", true),
                VoitureFactory.buildSource("MetaVoiture", false)
        );*/

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
        List<Voiture> mesVoitures = new ArrayList<Voiture>();
        try {
            // Recherche la classe dans le contexte "local" sinon il passe par le "loader"
            mesVoitures.add((Voiture)(Class.forName("dynamique.Voiture", true, loader).getDeclaredConstructor().newInstance()));
            mesVoitures.add((Voiture)(Class.forName("dynamique.Voiture6", true, loader).getDeclaredConstructor().newInstance()));
            mesVoitures.add((Voiture)(Class.forName("dynamique.Voiture7", true, loader).getDeclaredConstructor().newInstance()));
            mesVoitures.add((Voiture)(Class.forName("dynamique.Voiture8", true, loader).getDeclaredConstructor().newInstance()));
            mesVoitures.add((Voiture)(Class.forName("dynamique.Voiture9", true, loader).getDeclaredConstructor().newInstance()));
            // Creation d'une classe inexistante, erreur d'exécution plus loin en fin de programme
            mesVoitures.add((Voiture)(Class.forName("dynamique.VoitureX", true, loader).getDeclaredConstructor().newInstance()));
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // ******** ETAPE #5 : Exécution
        for (Voiture t : mesVoitures){
            System.out.println("CLASSE : " + t.getClass());
            //System.out.println("X : " + t.getX());
        }
    }


    public static JavaFileObject buildSource(String nomClasse, boolean sport) {

        StringBuilder sb = new StringBuilder();
        sb.append("package dynamique;\n");
        if(sport){
            sb.append("public class " + nomClasse + " extends VoitureSport implements Surveillable{\n");

            genererConstructeurs(nomClasse, 200, sb);
        }

        /*
        genererAttributs(sb);
        genererMethodes(sb);

         */

        sb.append("}\n");

        System.out.println("LA CLASSE");
        System.out.println(sb.toString());

        return new StringSource(nomClasse, sb.toString());
    }

    private static void genererConstructeurs(String nomClasse, int vitesse, StringBuilder sb) {

        sb.append("public " + nomClasse + "(){ super("+vitesse+";}\n");
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
                    return (VoitureSport) Class.forName("VoitureSport").getDeclaredConstructor(int.class).newInstance(vitesse);
                } else {
                    return (Voiture) Class.forName("Voiture").getDeclaredConstructor(int.class).newInstance(vitesse);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } else if(mode == ModeConstruction.META) {
            //TODO
        }
        return null;
    }
}
