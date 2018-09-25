package ar.com.tamborindeguy.model.loaders;

import ar.com.tamborindeguy.interfaces.Constants;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Loader<T> implements Constants {

	public abstract T load(DataInputStream fileName) throws IOException;

}
