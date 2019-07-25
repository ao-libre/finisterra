package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import model.descriptors.Descriptor;

public abstract class DescriptorActor extends Actor {

    public abstract void move();

    public abstract void rotate();

    public abstract void setDescriptor(Descriptor descriptor);
}
