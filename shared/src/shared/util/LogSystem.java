package shared.util;

import com.esotericsoftware.minlog.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogSystem extends Log.Logger {
    @Override
    public void log (int level, String category, String message, Throwable ex) {

        StringBuilder builder = new StringBuilder(256);
        builder.append('[');
        builder.append(getLevelName(level));
        builder.append(']');
        builder.append('[');
        builder.append(category);
        builder.append("] ");
        builder.append(message);

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            builder.append('\n');
            builder.append(writer.toString().trim());
        }

        // We only print ERROR logs into Errores.log
        if (level != Log.LEVEL_INFO) {
            try (FileOutputStream file = new FileOutputStream("Errores.log", true)) {
                byte[] output = builder.toString().getBytes();
                file.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // We keep printing the logs in the console.
        System.out.println(builder);

    }
    
    private String getLevelName(int level){
        switch(level){
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
        }
        return "NONE";
    }
}
