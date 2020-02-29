package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.minlog.Log;
import design.editors.fields.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static launcher.DesignCenter.SKIN;

public class GenericEditor extends Dialog {

    Object obj;

    public GenericEditor(Object obj) {
        super("Object Editor", SKIN);
        this.obj = obj;
        addTable();
        button("Cancel", false);
        button("OK", obj);
    }

    public static Table getTable(Object obj, FieldEditor.FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();

        List<Field> allFields = getAllFields(new LinkedList<>(), obj.getClass());
        allFields.forEach(field -> {
            Class<?> type = field.getType();
            field.setAccessible(true);
            if (type.equals(int.class) || type.equals(Integer.class)) {
                table.add(IntegerEditor.create(field.getName(), getFieldProvider(field.getName()), (v) -> {
                    try {
                        field.set(obj, v);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to set field value", e);
                    }
                }, () -> {
                    try {
                        return (Integer) field.get(obj);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to get field value", e);
                    }
                    return 0;
                }, listener)).row();
            } else if (type.equals(String.class)) {
                table.add(StringEditor.simple(field.getName(), (v) -> {
                    try {
                        field.set(obj, v);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to set field value", e);
                    }
                }, () -> {
                    try {
                        return (String) field.get(obj);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to get field value", e);
                    }
                    return "";
                }, listener)).row();
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                table.add(BooleanEditor.simple(field.getName(), (v) -> {
                    try {
                        field.set(obj, v);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to set field value", e);
                    }
                }, () -> {
                    try {
                        return (Boolean) field.get(obj);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to get field value", e);
                    }
                    return false;
                }, listener)).row();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                table.add(FloatEditor.simple(field.getName(), (v) -> {
                    try {
                        field.set(obj, v);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to set field value", e);
                    }
                }, () -> {
                    try {
                        return (Float) field.get(obj);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Log.error("Failed to get field value", e);
                    }
                    return 0f;
                }, listener)).row();
            }
        });

        return table;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    @NotNull
    public static FieldProvider getFieldProvider(String field) {
        switch (field) {
            case "grhIndex":
                return FieldProvider.FX;
            case "iconGrh":
                return FieldProvider.IMAGE;
            case "bodyNumber":
            case "animationId":
            case "dwarfAnimationId":
            case "fxGrh":
                return FieldProvider.ANIMATION;
        }
        return FieldProvider.NONE;
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(obj, () -> {
        }))).prefHeight(300).prefWidth(300);
    }
}
