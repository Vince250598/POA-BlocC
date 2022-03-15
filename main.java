import dynamique.ByteArrayClass;
import dynamique.ByteArrayClasseLoader;
import dynamique.VoitureFactory;
import voiture.Surveillable;
import voiture.Voiture;

import javax.tools.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        List<Voiture> mesVoitures = new ArrayList<Voiture>();
        ajoutVoituresInstanciation(mesVoitures);
        ajoutVoituresReflexion(mesVoitures);
        ajoutVoituresMeta(mesVoitures);
        try {
            int x = 0;
            while (x++ < 20)
                for (Voiture v : mesVoitures){
                    System.out.println(v);
                    v.deplacement();
                    System.out.println(v);
                    surveillerVoiture(v);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void ajoutVoituresInstanciation(List<Voiture>
                                                           mesVoitures) {

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                INSTANCIATION, true, 0));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                INSTANCIATION,false, 50));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                INSTANCIATION,false, 10));
    }
    private static void ajoutVoituresMeta(List<Voiture> mesVoitures) {

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                META, true, 0));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                META,false, 50));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                META,false, 10));
    }

    private static void ajoutVoituresReflexion(List<Voiture> mesVoitures) {

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                REFLEXION,true, 0));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                REFLEXION, false, 50));

        mesVoitures.add(VoitureFactory.buildVoiture(VoitureFactory.ModeConstruction.
                REFLEXION, false, 10));
    }
    private static void surveillerVoiture(Voiture v) throws Exception {
        if (v.getPosition() > 1000){
            if (v.getClass().getName().contains("Meta")){
                int depassement = ((Surveillable)v).surveiller(60);
                if (depassement > 10)
                    throw new Exception("ID : " + v.getId() + " --> depassement de " + depassement + " (" + v.getClass() + ")");
            }
        }
    }
}