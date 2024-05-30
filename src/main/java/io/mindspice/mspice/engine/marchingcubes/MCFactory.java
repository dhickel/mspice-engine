package io.mindspice.mspice.engine.marchingcubes;



import io.mindspice.mspice.engine.core.graphics.primatives.Mesh;

import java.util.ArrayList;
import java.util.List;

public class MCFactory {

    public static Mesh generateMesh(int width, int height, int depth, FastNoiseLite noise) {
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> tangents = new ArrayList<>();
        List<Float> bitangents = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[][][] grid = new float[width][height][depth];

        // Generate noise values
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    grid[x][y][z] = noise.GetNoise(x, y, z);
                }
            }
        }

        // Marching Cubes algorithm
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                for (int z = 0; z < depth - 1; z++) {
                    // Calculate cube index
                    int cubeIndex = 0;
                    if (grid[x][y][z] < 0) cubeIndex |= 1;
                    if (grid[x + 1][y][z] < 0) cubeIndex |= 2;
                    if (grid[x + 1][y + 1][z] < 0) cubeIndex |= 4;
                    if (grid[x][y + 1][z] < 0) cubeIndex |= 8;
                    if (grid[x][y][z + 1] < 0) cubeIndex |= 16;
                    if (grid[x + 1][y][z + 1] < 0) cubeIndex |= 32;
                    if (grid[x + 1][y + 1][z + 1] < 0) cubeIndex |= 64;
                    if (grid[x][y + 1][z + 1] < 0) cubeIndex |= 128;

                    // Lookup edges and triangles from tables
                    int edgeFlags = MCConstants.EDGE_TABLE[cubeIndex];
                    if (edgeFlags == 0) continue;

                    // Interpolate vertices
                    float[][] edgeVertices = new float[12][3];
                    for (int i = 0; i < 12; i++) {
                        if ((edgeFlags & (1 << i)) != 0) {
                            int v0 = MCConstants.EDGE_FIRST_VERTEX[i];
                            int v1 = MCConstants.EDGE_SECOND_VERTEX[i];
                            edgeVertices[i] = interpolate(
                                    getPosition(x, y, z, v0),
                                    getPosition(x, y, z, v1),
                                    grid[getX(x, v0)][getY(y, v0)][getZ(z, v0)],
                                    grid[getX(x, v1)][getY(y, v1)][getZ(z, v1)]
                            );
                        }
                    }

                    // Create triangles
                    for (int i = 0; i < MCConstants.TRIANGLE_TABLE[cubeIndex].length; i += 3) {
                        int a0 = MCConstants.TRIANGLE_TABLE[cubeIndex][i];
                        int b0 = MCConstants.TRIANGLE_TABLE[cubeIndex][i + 1];
                        int c0 = MCConstants.TRIANGLE_TABLE[cubeIndex][i + 2];

                        addVertex(positions, edgeVertices[a0]);
                        addVertex(positions, edgeVertices[b0]);
                        addVertex(positions, edgeVertices[c0]);

                        int baseIndex = positions.size() / 3;
                        indices.add(baseIndex - 3);
                        indices.add(baseIndex - 2);
                        indices.add(baseIndex - 1);
                    }
                }
            }
        }

        // Convert lists to arrays
        float[] posArray = listToArray(positions);
        float[] normArray = listToArray(normals);
        float[] tangArray = listToArray(tangents);
        float[] bitanArray = listToArray(bitangents);
        float[] texArray = listToArray(texCoords);        int[] indArray = listToIntArray(indices);

        return new Mesh(posArray, normArray, tangArray, bitanArray, texArray, indArray);
    }

    private static float[] interpolate(float[] p0, float[] p1, float v0, float v1) {
        float t = -v0 / (v1 - v0);
        return new float[]{
                p0[0] + t * (p1[0] - p0[0]),
                p0[1] + t * (p1[1] - p0[1]),
                p0[2] + t * (p1[2] - p0[2])
        };
    }

    private static float[] getPosition(int x, int y, int z, int vertex) {
        // Map the vertex index to actual coordinates in the grid
        switch (vertex) {
            case 0: return new float[]{x, y, z};
            case 1: return new float[]{x + 1, y, z};
            case 2: return new float[]{x + 1, y + 1, z};
            case 3: return new float[]{x, y + 1, z};
            case 4: return new float[]{x, y, z + 1};
            case 5: return new float[]{x + 1, y, z + 1};
            case 6: return new float[]{x + 1, y + 1, z + 1};
            case 7: return new float[]{x, y + 1, z + 1};
            default: throw new IllegalArgumentException("Invalid vertex index");
        }
    }

    private static int getX(int x, int vertex) {
        return x + ((vertex & 1) != 0 ? 1 : 0);
    }

    private static int getY(int y, int vertex) {
        return y + ((vertex & 2) != 0 ? 1 : 0);
    }

    private static int getZ(int z, int vertex) {
        return z + ((vertex & 4) != 0 ? 1 : 0);
    }

    private static void addVertex(List<Float> list, float[] vertex) {
        for (float v : vertex) {
            list.add(v);
        }
    }

    private static float[] listToArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] listToIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
