package shared.util;

import com.esotericsoftware.minlog.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogSystem extends Log.Logger {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private final String SALTO_DE_LINEA = OS.contains("win") ? "\r\n" : "\n";

    @Override
    public void log(int level, String category, String message, Throwable ex) {

        StringBuilder builder = new StringBuilder(256);

        // We print Date & Time ONLY in ERROR or WARN logs.
        if (level == Log.LEVEL_ERROR || level == Log.LEVEL_WARN) {
            builder.append(new Date());
            builder.append(" ");
        }

        builder.append("[");
        builder.append(getLevelName(level));
        builder.append("] ");
        builder.append("[");
        builder.append(category);
        builder.append("] ");
        builder.append(message);
        
        /*
            Para mejor visibilidad.
            Separamos el mensaje de error de el stacktrace.
        */

        if (level == Log.LEVEL_ERROR || level == Log.LEVEL_WARN) {
            builder.append(SALTO_DE_LINEA);
        }

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            builder.append(SALTO_DE_LINEA);
            builder.append(writer.toString().trim());
            builder.append(SALTO_DE_LINEA);
            builder.append(SALTO_DE_LINEA);
        }

        // We only print ERROR logs into Errores.log
        if (level == Log.LEVEL_ERROR || level == Log.LEVEL_WARN) {
            try (FileOutputStream file = new FileOutputStream("Errores.log", true)) {
                byte[] output = builder.toString().getBytes();
                file.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // We keep printing the logs in the component.console.
        System.out.println(builder);

    }

    private String getLevelName(int level) {
        switch (level) {
            case Log.LEVEL_INFO:
                return "INFO";

            case Log.LEVEL_ERROR:
                return "ERROR";

            case Log.LEVEL_DEBUG:
                return "DEBUG";

            case Log.LEVEL_TRACE:
                return "TRACE";

            case Log.LEVEL_WARN:
                return "WARNING";

            default:
                return "OTHER";
        }
    }
}
