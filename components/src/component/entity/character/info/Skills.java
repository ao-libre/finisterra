package component.entity.character.info;

import com.artemis.Component;

import java.io.Serializable;
import java.lang.reflect.Field;

public class Skills extends Component implements Serializable {

    private int magia;
    private int robar;
    private int tacticas;
    private int armas;
    private int meditar;
    private int apunalar;
    private int ocultarse;
    private int supervivencia;
    private int talar;
    private int comerciar;
    private int defensa;
    private int pesca;
    private int mineria;
    private int carpinteria;
    private int herreria;
    private int liderazgo;
    private int domar;
    private int proyectiles;
    private int wrestling;
    private int navegacion;
    private int equitacion;

    public Skills() {}

    public int getMagia() {
        return magia;
    }

    public void setMagia(int magia) {
        this.magia = magia;
    }

    public int getRobar() {
        return robar;
    }

    public void setRobar(int robar) {
        this.robar = robar;
    }

    public int getTacticas() {
        return tacticas;
    }

    public void setTacticas(int tacticas) {
        this.tacticas = tacticas;
    }

    public int getArmas() {
        return armas;
    }

    public void setArmas(int armas) {
        this.armas = armas;
    }

    public int getMeditar() {
        return meditar;
    }

    public void setMeditar(int meditar) {
        this.meditar = meditar;
    }

    public int getApunalar() {
        return apunalar;
    }

    public void setApunalar(int apunalar) {
        this.apunalar = apunalar;
    }

    public int getOcultarse() {
        return ocultarse;
    }

    public void setOcultarse(int ocultarse) {
        this.ocultarse = ocultarse;
    }

    public int getSupervivencia() {
        return supervivencia;
    }

    public void setSupervivencia(int supervivencia) {
        this.supervivencia = supervivencia;
    }

    public int getTalar() {
        return talar;
    }

    public void setTalar(int talar) {
        this.talar = talar;
    }

    public int getComerciar() {
        return comerciar;
    }

    public void setComerciar(int comerciar) {
        this.comerciar = comerciar;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getPesca() {
        return pesca;
    }

    public void setPesca(int pesca) {
        this.pesca = pesca;
    }

    public int getMineria() {
        return mineria;
    }

    public void setMineria(int mineria) {
        this.mineria = mineria;
    }

    public int getCarpinteria() {
        return carpinteria;
    }

    public void setCarpinteria(int carpinteria) {
        this.carpinteria = carpinteria;
    }

    public int getHerreria() {
        return herreria;
    }

    public void setHerreria(int herreria) {
        this.herreria = herreria;
    }

    public int getLiderazgo() {
        return liderazgo;
    }

    public void setLiderazgo(int liderazgo) {
        this.liderazgo = liderazgo;
    }

    public int getDomar() {
        return domar;
    }

    public void setDomar(int domar) {
        this.domar = domar;
    }

    public int getProyectiles() {
        return proyectiles;
    }

    public void setProyectiles(int proyectiles) {
        this.proyectiles = proyectiles;
    }

    public int getWrestling() {
        return wrestling;
    }

    public void setWrestling(int wrestling) {
        this.wrestling = wrestling;
    }

    public int getNavegacion() {
        return navegacion;
    }

    public void setNavegacion(int navegacion) {
        this.navegacion = navegacion;
    }

    public int getEquitacion() {
        return equitacion;
    }

    public void setEquitacion(int equitacion) {
        this.equitacion = equitacion;
    }

    public void initial(int initialValue) {
        try {
            for (Field field : this.getClass().getFields()) {
                field.setInt(this, initialValue);
            }
        } catch (Exception ignored) {

        }
    }
}
