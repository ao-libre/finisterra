package shared.network.interaction;


import position.WorldPos;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;
import shared.objects.types.WorkKind;

public class WorkRequest implements IRequest {

    private WorkKind workKind;
    private WorldPos worldPos;
    private String recipeName;
    private boolean craft;

    public WorkRequest(){
    }


    public WorkRequest(WorkKind workKind, WorldPos worldPos) {

        this.workKind = workKind;
        this.worldPos = worldPos;
        craft = false;
    }

    public WorkRequest(WorkKind workKind, WorldPos worldPos, String recipeName) {
        this.workKind = workKind;
        this.worldPos = worldPos;
        this.recipeName = recipeName;
        craft = true;
    }

    public WorkKind getWorkKind() {
        return workKind;
    }

    public WorldPos getWorldPos() {
        return worldPos;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public boolean isCraft() {
        return craft;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
    
