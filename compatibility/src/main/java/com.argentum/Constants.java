package com.argentum;

import com.badlogic.gdx.graphics.Color;

public interface Constants {

	public static final float PI2 = 3.1415926535897932384626433832795f * 2.0f;

    public static final int GAME_FILE_HEADER_SIZE = 263;
    
    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;
    
    public static final float OFFSET_HEAD = 12.0f;
    
    public static final Color COLOR_DAYLIGHT = new Color(0.5f, 0.5f, 0.5f, 0.2f);
    public static final Color COLOR_DAWN = new Color(0.35f, 0.3f, 0.3f, 0.2f);
    public static final Color COLOR_NIGHT = new Color(0.2f, 0.2f, 0.2f, 1.0f);
    
    public static final float ALPHA_TREES = 1.0f;
    public static final float ALPHA_LIGHTS = 0.4f;
    public static final float ALPHA_FXS = 0.6f;
    
    public static final int DEFAULT_NUM_RAYS = 128;

    public enum Heading {
    	NORTH(0), EAST(1), SOUTH(2), WEST(3);

    	final int mHeading;

    	Heading(int pHeading) {
    		this.mHeading = pHeading;
    	}

    	public int toInt() {
    		return this.mHeading;
    	}
    }
    
    
}