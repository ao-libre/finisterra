package ar.com.tamborindeguy.objects.types;

public class PosterObj extends Obj {

    private String text;
    private int big;

    public PosterObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.POSTER;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBig() {
        return big;
    }

    public void setBig(int big) {
        this.big = big;
    }
}
