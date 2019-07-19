package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import design.editors.NPCEditor;
import shared.model.loaders.NPCLoader;
import shared.model.npcs.NPC;
import shared.util.AOJson;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

import static design.designers.NPCDesigner.NPCParameters;

public class NPCDesigner implements IDesigner<NPC, NPCParameters> {

    private final String NPCS_FOLDER_PATH = "npcs/";
    private final String NPCS_FILE_NAME = "npcs";
    private final String JSON_EXT = ".json";
    private final String DAT_EXT = ".dat";

    private final String NPCS_JSON = NPCS_FILE_NAME + JSON_EXT;

    private final String OUTPUT_FOLDER = "output/";

    private Map<Integer, NPC> npcs;
    private AOJson json = new AOJson();

    private int getFreeId() {
        return 0;
    }

    public NPCDesigner(NPCParameters parameters) {
        load(parameters);
    }

    @Override
    public void load(NPCParameters params) {
        switch (params.model) {
            case DAT:
                FileHandle file = Gdx.files.internal(NPCS_FOLDER_PATH + NPCS_FILE_NAME + DAT_EXT);
                try {
                    npcs = new NPCLoader().load(new DataInputStream(file.read()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JSON:
                // TODO
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        FileHandle outputFile = Gdx.files.local(OUTPUT_FOLDER + NPCS_JSON);
        json.toJson(npcs, HashMap.class, NPC.class, outputFile);
    }

    @Override
    public List<NPC> get() {
        return new ArrayList<>(npcs.values());
    }

    @Override
    public Optional<NPC> get(int id) {
        return Optional.ofNullable(npcs.get(id));
    }

    @Override
    public NPC create() {
        return new NPC(getFreeId());
    }

    @Override
    public void modify(NPC npc, Stage stage) {
        NPCEditor npcEditor = new NPCEditor(new NPC(npc)) {
            @Override
            protected void result(Object object) {
                if (object instanceof NPC) {
                    if (!object.equals(npc)) {
                        npcs.put(((NPC) object).getId(), (NPC) object);
                    }
                }
            }
        };
        npcEditor.show(stage);
    }

    @Override
    public void delete(NPC npc) {
        npcs.remove(npc.getId());
    }

    @Override
    public void add(NPC npc) {
        npcs.put(npc.getId(), npc);
    }


    public static class NPCParameters implements Parameters<NPC> {
        enum NPCModel {
            JSON,
            DAT
        }

        private NPCModel model;

        NPCParameters(NPCModel model) {
            this.model = model;
        }

        public NPCParameters json() {
            return new NPCParameters(NPCModel.JSON);
        }

        public static NPCParameters dat() {
            return new NPCParameters(NPCModel.DAT);
        }
    }
}
