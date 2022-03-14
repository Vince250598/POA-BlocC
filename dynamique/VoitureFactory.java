package dynamique;

import voiture.Voiture;
import voiture.VoitureSport;

import java.lang.reflect.InvocationTargetException;

public class VoitureFactory {
    public enum ModeConstruction {INSTANCIATION, REFLEXION, META};

    public static Voiture buildVoiture(ModeConstruction mode, boolean sport, int vitesse) throws {
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
    };
}
