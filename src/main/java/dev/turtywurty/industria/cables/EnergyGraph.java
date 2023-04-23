package dev.turtywurty.industria.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class EnergyGraph implements INBTSerializable<CompoundTag> {
    private final Map<BlockPos, Set<BlockPos>> graph = new HashMap<>();

    public EnergyGraph(BlockPos startPos) {
        this.graph.put(startPos, new HashSet<>());
    }

    public EnergyGraph() {
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();

        var graph = new ListTag();
        for (BlockPos pos : this.graph.keySet()) {
            var list = new ListTag();
            for (BlockPos neighbor : this.graph.get(pos)) {
                list.add(NbtUtils.writeBlockPos(neighbor));
            }

            var entry = new CompoundTag();
            entry.put("Position", NbtUtils.writeBlockPos(pos));
            entry.put("Neighbors", list);
            graph.add(entry);
        }

        nbt.put("Graph", graph);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var graph = nbt.getList("Graph", Tag.TAG_COMPOUND);
        for (int index = 0; index < graph.size(); index++) {
            var entry = graph.getCompound(index);
            var pos = NbtUtils.readBlockPos(entry.getCompound("Position"));
            var neighbors = entry.getList("Neighbors", Tag.TAG_COMPOUND);
            for (int neighborIndex = 0; neighborIndex < neighbors.size(); neighborIndex++) {
                var neighbor = NbtUtils.readBlockPos(neighbors.getCompound(neighborIndex));
                addEdge(pos, neighbor);
            }
        }
    }

    public void addEdge(BlockPos from, BlockPos to) {
        if (!graph.containsKey(from)) {
            graph.put(from, new HashSet<>());
        }

        graph.get(from).add(to);

        if (!graph.containsKey(to)) {
            graph.put(to, new HashSet<>());
        }

        graph.get(to).add(from);
    }

    public void removeEdge(BlockPos from, BlockPos to) {
        graph.get(from).remove(to);
        graph.get(to).remove(from);
    }

    public Set<BlockPos> getNeighbors(BlockPos pos) {
        return graph.getOrDefault(pos, Collections.emptySet());
    }

    public static EnergyGraph mergeNetworks(EnergyGraph network1, EnergyGraph network2) {
        var mergedNetwork = new EnergyGraph();

        // Add all edges from the first network
        for (BlockPos pos : network1.graph.keySet()) {
            mergedNetwork.graph.put(pos, network1.graph.get(pos));
        }

        // Add all edges from the second network
        for (BlockPos pos : network2.graph.keySet()) {
            if (mergedNetwork.graph.containsKey(pos)) {
                mergedNetwork.graph.get(pos).addAll(network2.graph.get(pos));
            } else {
                mergedNetwork.graph.put(pos, network2.graph.get(pos));
            }
        }

        return mergedNetwork;
    }

    public List<EnergyGraph> splitNetwork(LevelReader level, BlockPos splitPoint) {
        List<EnergyGraph> networks = new ArrayList<>();

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(splitPoint);
        while(!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if(!visited.contains(pos)) {
                visited.add(pos);

                if(level.getBlockEntity(pos) instanceof CableBlockEntity cable) {
                    EnergyGraph network = cable.getNetwork();
                    if(!pos.equals(splitPoint))
                        network.removeNode(pos);

                    cable.update();

                    System.out.println(pos + " has removed " + splitPoint);

                    queue.addAll(network.getNeighbors(pos));
                    networks.add(network);
                }
            }
        }

        return networks;
    }

    public Set<BlockPos> getEndPoints() {
        Set<BlockPos> endpoints = new HashSet<>();
        for (BlockPos pos : this.graph.keySet()) {
            Set<BlockPos> neighbors = this.graph.get(pos);
            if (neighbors.size() == 1) {
                endpoints.add(pos);
            }
        }
        return endpoints;
    }

    public Map<BlockPos, Set<BlockPos>> getGraph() {
        return Map.copyOf(this.graph);
    }

    public void clearEdges(BlockPos pos) {
        this.graph.get(pos).clear();
    }

    public void insertEdges(EnergyGraph network) {
        for (BlockPos pos : network.graph.keySet()) {
            if (!this.graph.containsKey(pos)) {
                this.graph.put(pos, new HashSet<>());
            }

            this.graph.get(pos).addAll(network.graph.get(pos));
        }
    }

    public void removeNode(BlockPos pos) {
        this.graph.remove(pos);
        this.graph.values().forEach(neighbors -> neighbors.remove(pos));
    }
}