package shared.model.readers;

import shared.interfaces.Constants;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Loader<T> implements Constants {

    public abstract T load(DataInputStream fileName) throws IOException;

}
