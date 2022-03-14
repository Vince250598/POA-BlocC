package dynamique;

import voiture.Voiture;

public class VoitureFactory {
    //regarder TestFactory pour exemple

    public enum ModeConstruction {INSTANCIATION, REFLEXION, META};

    public static Voiture buildVoiture(ModeConstruction mode, boolean sport, int vitesse) {
        return new Voiture(0);
    }
}
