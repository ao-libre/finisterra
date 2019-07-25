package design.editors.utils;

import model.textures.AOImage;

import java.util.ArrayList;
import java.util.List;

public class SliceResult {

    private List<AOImage> images;

    public List<AOImage> getImages() {
        return images;
    }

    public int count() {
        return images != null ? images.size() : 0;
    }

    public static class Builder {

        private SliceResult result;

        public static Builder create() {
            Builder builder = new Builder();
            builder.result = new SliceResult();
            builder.result.images = new ArrayList<>();
            return builder;
        }

        public Builder withImage(AOImage image) {
            result.images.add(image);
            return this;
        }

        public SliceResult build() {
            return result;
        }

    }


}
