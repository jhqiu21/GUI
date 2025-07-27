package morpher.ui.visualization;

public record Connection(int srcIdx, int dstIdx) {
    public String toString() {
        return srcIdx + " -> " + dstIdx;
    }
}
