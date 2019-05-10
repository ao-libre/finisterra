package launcher;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import design.Scale2x;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

public class DesignCenter extends Game {

    public static final String OUTPUT_FOLDER = "/output/";

    @Override
    public void create() {
//        SeparateObjByType.run(OUTPUT_FOLDER);
//        SpellsToJson.run(OUTPUT_FOLDER);
//        GraphicsToJson.run(OUTPUT_FOLDER);

        File graficosInput = new File("data/graficos/");
        File graficosOutput = new File("data/graficos2x/");
        if (!graficosOutput.exists()) {
            graficosOutput.mkdir();
        }

        Stream.of(Objects.requireNonNull(graficosInput.list())).forEach(fileName -> {
            if (fileName.endsWith(".png")) {
                String inputFile = graficosInput.getPath() + "/" + fileName;
                String outputFile = graficosOutput.getPath() + "/" + fileName;
                if (!new File(graficosOutput + "/" + fileName).exists()) {
                    Gdx.app.log(DesignCenter.class.getSimpleName(), "Scaling graphic " + fileName);
                    Scale2x.run(inputFile, outputFile);
                }
            }
        });
    }
}
