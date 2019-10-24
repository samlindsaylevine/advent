package advent.year2015.day7;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Circuit {

    private Map<String, Gate> gatesByOutput = new HashMap<>();

    private LoadingCache<String, Integer> memoizedValues = CacheBuilder.newBuilder()
            .build(CacheLoader.from(w -> this.gatesByOutput.get(w).get(this)));

    public int get(String wire) {
        try {
            return this.memoizedValues.get(wire);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void addGate(String representation) {
        Gate gate = Gate.of(representation);
        this.gatesByOutput.put(gate.getTargetWire(), gate);
        this.memoizedValues.invalidateAll();
    }

    public static void main(String[] args) throws IOException {
        Circuit circuit = new Circuit();
        Files.lines(Paths.get("src/main/java/advent/year2015/day7/input.txt")) //
                .forEach(circuit::addGate);

        System.out.println(circuit.get("a"));
    }

}
