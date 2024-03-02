package TwilightFall.world.blocks.darkEng;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import mindustry.gen.Building;

import java.util.Arrays;

public class DarkBuildFunc {
    //沿用了heat的计算方式，传输还是这个好用些，本来是写在传输线里面的，调用好些，这个反而是让连接器难写了（
    public static float calculateEng(Building building, float[] side, IntSet cameFrom){
        Arrays.fill(side, 0);
        if (cameFrom != null) {
            cameFrom.clear();
        }

        float eng = 0;
        Point2[] edges = building.block.getEdges();

        for (Point2 edge : edges) {
            Building build = building.nearby(edge.x, edge.y);
            if (build != null && build.team == building.team) {
                if(build instanceof DarkJunction.DarkJunctionBuild junction){
                    if(building.front() != junction && cameFrom != null && !cameFrom.contains(junction.id)) {
                        int i = Mathf.mod(building.relativeTo(junction), 4);
                        float add = junction.outputEng(i%2);
                        side[i] += add;
                        eng += add;
                    }

                    if (cameFrom != null) {
                        cameFrom.add(junction.id);
                    }
                } else if (build instanceof DarkGraph darkGraph) {
                    if (darkGraph instanceof DarkLine.DarkLineBuild line) {
                        line.updateDark();
                    }

                    if (building.front() != darkGraph && (!build.block.rotate || (building.relativeTo(build) + 2) % 4 == build.rotation)) {
                        DarkLine.DarkLineBuild dl;
                        f2:{
                            if (build instanceof DarkLine.DarkLineBuild) {
                                dl = (DarkLine.DarkLineBuild) build;
                                if (dl.cameFrom.contains(building.id())) {
                                    break f2;
                                }
                            }

                            float add = darkGraph.canOutput(building) ? darkGraph.outputEng() / ((DarkGraph) build).edge() : 0;
                            //maybe size > 1
                            int i = Mathf.mod(building.relativeTo(build), 4);
                            side[i] += add;
                            eng += add;
                        }

                        if (cameFrom != null) {
                            cameFrom.add(build.id);
                            if (build instanceof DarkLine.DarkLineBuild dll) {
                                cameFrom.addAll(dll.cameFrom);
                            }
                        }
                    }
                }
            }
        }

        return eng;
    }

    public static int baseEdge(Building bd) {
        int e = 0;
        for(int i = 0; i < bd.proximity.size; i++){
            Building b = bd.proximity.get(i);
            if(b instanceof DarkConsumer dc
                    && ((DarkConsumer)b).hasEng() > 0
                    && (b.block != null && (!b.block.rotate || b.front() != bd))
                    && dc.checkInput(bd)
            )
                e++;
        }
        return e == 0 ? 1 : e;
    }
}
