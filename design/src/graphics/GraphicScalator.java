package graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import design.Scale2x;
import launcher.DesignCenter;

public class GraphicScalator {

    public static void scaleGraphics(String graphicsOrigin) {
        int count = 0;
        FileHandle graphicsInput = Gdx.files.internal(graphicsOrigin);
        FileHandle output = Gdx.files.internal(graphicsOrigin + "@2x");
        if (!output.exists()) {
            output.mkdirs();
        }

        int missingFiles = graphicsInput.list().length - output.list().length;

        for (FileHandle inputChild : graphicsInput.list()) {
            if (inputChild.extension().equals("png")) {
                String fileName = inputChild.name();
                if (!output.child(fileName).exists()) {
                    String inputFile = graphicsInput.file().getPath() + "/" + fileName;
                    String outputFile = output.file().getPath() + "/" + fileName;
                    Gdx.app.log(DesignCenter.class.getSimpleName(), "Scaling graphic " + fileName);
                    Scale2x.run(inputFile, outputFile);
                    Gdx.app.log(DesignCenter.class.getSimpleName(), "Faltan :" + --missingFiles);
                }
            }
        }

//        Stream.of(Objects.requireNonNull(graphicsInput.list())).forEach(fileName -> {
//            if (fileName.extension().equals(".png")) {
//
//                if (!output.child(fileName.name()).exists()) {
//                    count[0] = count[0] + 1;
//                }
//                if (!new File(output + "/" + fileName).exists()) {
//
//                }
//            }
//        });
        Gdx.app.log(DesignCenter.class.getSimpleName(), "Missing scaled graphics: " + count);
    }
}
