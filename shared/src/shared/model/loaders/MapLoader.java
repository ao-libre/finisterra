package shared.model.loaders;

public class MapLoader {

//    public Map load(DataInputStream file, DataInputStream inf) throws IOException {
//        file.skipBytes(Constants.GAME_FILE_HEADER_SIZE + (2 * 5)); // Skip complete map header
//
//        inf.readFloat();
//        inf.readFloat();
//        inf.readShort();
//
//        Map map = new Map();
//
//        // Read map info (rows first, then columns)
//        for (int y = Map.MIN_MAP_SIZE_WIDTH; y <= Map.MAX_MAP_SIZE_WIDTH; y++) {
//            for (int x = Map.MIN_MAP_SIZE_HEIGHT; x <= Map.MAX_MAP_SIZE_HEIGHT; x++) {
//                int charIndex = 0, objCount = 0, objIndex = 0, npcIndex = 0, trigger = 0, graphic[] = new int[4];
//                WorldPosition tileExit = null;
//                boolean blocked;
//                byte byFlags;
//
//                byFlags = file.readByte();
//                blocked = (1 == (byFlags & 1));
//
//                graphic[0] = WorldPosConversion.leShort(file.readShort());
//
//                if ((byFlags & 2) == 2) {
//                    graphic[1] = WorldPosConversion.leShort(file.readShort());
//                } else {
//                    graphic[1] = 0;
//                }
//
//                if ((byFlags & 4) == 4) {
//                    graphic[2] = WorldPosConversion.leShort(file.readShort());
//                } else {
//                    graphic[2] = 0;
//                }
//
//                if ((byFlags & 8) == 8) {
//                    graphic[3] = WorldPosConversion.leShort(file.readShort());
//                } else {
//                    graphic[3] = 0;
//                }
//
//                if ((byFlags & 16) == 16) {
//                    trigger = WorldPosConversion.leShort(file.readShort());
//                }
//
//                byFlags = inf.readByte();
//                if ((1 == (byFlags & 1))) {
//                    tileExit = new WorldPosition(WorldPosConversion.leShort(inf.readShort()), WorldPosConversion.leShort(inf.readShort()), WorldPosConversion.leShort(inf.readShort()));
//                }
//                if ((byFlags & 2) == 2) {
//                    npcIndex = WorldPosConversion.leShort(inf.readShort());
//                }
//                if ((byFlags & 4) == 4) {
//                    objIndex = WorldPosConversion.leShort(inf.readShort());
//                    objCount = WorldPosConversion.leShort(inf.readShort());
//                }
//
//                Tile tile = new Tile(graphic, charIndex, objCount, objIndex, npcIndex, tileExit, blocked, trigger);
//                map.setTile(x, y, tile);
//            }
//        }
//
//        return map;
//    }

}
