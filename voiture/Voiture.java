package voiture;

public class Voiture {
    protected int vitesse;
    protected int position;
    protected int id;
    protected static int _id = 0;

    public Voiture(int vitesse) {
        this.vitesse = vitesse;
        this.id = _id;
        _id++;
        this.position = 0;
    }

    public void deplacement() {
        position += vitesse;
    }

    @Override
    public String toString() {
        return "Voiture{" +
                "vitesse=" + vitesse +
                ", position=" + position +
                ", id=" + id +
                '}';
    }

    public int getVitesse() {
        return vitesse;
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }
}

