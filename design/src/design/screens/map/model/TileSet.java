package design.screens.map.model;

import model.ID;

import java.util.Arrays;

public class TileSet implements ID {

    private int id;
    private int rows;
    private int cols;
    private int[][] images;

    public TileSet() {
    }

    public TileSet(TileSet other) {
        this.id = other.id;
        this.rows = other.rows;
        this.cols = other.cols;
        this.images = new int[rows][cols];
        for (int i = 0; i < other.rows; i++) {
            this.images[i] = Arrays.copyOf(other.images[i], other.images[i].length);
        }
    }

    public TileSet(int id, int cols, int rows) {
        this.id = id;
        this.rows = cols;
        this.cols = rows;
        this.images = new int[cols][rows];
    }

    public void setImage(int x, int y, int id) {
        if (x < images.length) {
            if (y < images[x].length) {
                images[x][y] = id;
            }
        }
    }

    public int getImage(int x, int y) {
        if (x < images.length) {
            if (y < images[x].length) {
                return images[x][y];
            }
        }
        return 0;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        int diff = rows - this.rows;
        this.rows = rows;
        images = Arrays.copyOf(images, rows);
        if (diff > 0) {
            for (int j = 0; j < diff; j++) {
                images[rows - j - 1] = new int[cols];
            }
        }
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        int diff = cols - this.cols;
        this.cols = cols;
        for (int i = 0; i < rows; i++) {
            images[i] = Arrays.copyOf(images[i], cols);
        }
        // hace falta?
        if (diff > 0) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < diff; j++) {
                    images[i][cols - j - 1] = 0;
                }

            }
        }
    }

    public int[][] getImages() {
        return images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tile Set: " + getId();
    }
}
