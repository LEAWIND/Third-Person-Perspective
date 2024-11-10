package com.github.leawind.util.surroundings;

import com.github.leawind.util.math.LMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

@SuppressWarnings("unused")
public class Surroundings {
  private static final Pattern BLANK_PATTERN = Pattern.compile("[\\s\\n|]");
  private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[\\s\\n|]+");

  /** Identifier -> offset[] */
  private final Map<String, List<Vector3i>> identifierMap = new HashMap<>();

  /** Identifier -> Matches[] */
  private final Map<String, Matches> matchesMap = new HashMap<>();

  /**
   * 解析提供的多行字符串表达式，该表达式定义一个3x3x3区域的方块布局。
   *
   * <p>表达式中的每个字符代表一个标识符，用于区分不同的方块类型或位置。
   *
   * <p>表达式可以由多个部分组成，每个部分之间通过空格、换行符或竖线（|）分隔，每个部分内部也使用相同的分隔符来组织字符。
   *
   * <p>示例：
   *
   * <pre>
   * new Surroundings("""
   *     B B B  M M M  T T T
   *     B B B  M M M  T T T
   *     B B B  M M M  T T T
   * """);
   * </pre>
   *
   * 在此示例中，'B', 'M', 'T' 都是不同的标识符，它们分别代表了不同类型的方块或位置。
   *
   * <p>其中 B 表示下层 3x1x3 的方块，M 表示中间 3x1x3 的一层方块，T 表示上层 3x1x3 的方块
   */
  public Surroundings(String expr) {
    var tokens = SEPARATOR_PATTERN.split(BLANK_PATTERN.matcher(expr).replaceAll(" ").trim());
    for (int i = 0; i < tokens.length; i++) {
      var word = tokens[i];
      List<Vector3i> offsets;
      if (identifierMap.containsKey(word)) {
        offsets = identifierMap.get(word);
      } else {
        offsets = new ArrayList<>(27);
        identifierMap.put(word, offsets);
      }

      int ix = i % 3;
      int iy = (i / 3) % 3;
      int iz = i / 9;
      offsets.add(new Vector3i(ix - 1, iy - 1, iz - 1));
    }
  }

  public void clear() {
    this.matchesMap.clear();
  }

  public void rematch(BlockPos center, BlockGetter level, Predicate<BlockState> predicate) {
    clear();
    match(center, level, predicate);
  }

  public void match(BlockPos center, BlockGetter level, Predicate<BlockState> predicate) {
    for (var identifier : getIdentifiers()) {
      var seq = new Matches(getOffsets(identifier), center, level);
      matchesMap.put(identifier, seq);
    }
    for (var seq : matchesMap.values()) {
      seq.apply(predicate);
    }
  }

  public Matches getMatches(String identifier) {
    if (!matchesMap.containsKey(identifier)) {
      throw new IllegalArgumentException("Unknown identifier: " + identifier);
    }
    return matchesMap.get(identifier);
  }

  public Set<String> getIdentifiers() {
    return identifierMap.keySet();
  }

  public List<Vector3i> getOffsets(String identifier) {
    if (!identifierMap.containsKey(identifier)) {
      throw new IllegalStateException("Unknown identifier: " + identifier);
    }
    return identifierMap.get(identifier);
  }

  public static class Matches {
    private final BlockState[] blockStates;
    private final Vector3i[] offsets;
    private final boolean[] states;
    private boolean isApplied = false;

    private Matches(List<Vector3i> offsets, BlockPos center, BlockGetter level) {
      blockStates = new BlockState[offsets.size()];
      states = new boolean[offsets.size()];
      this.offsets = new Vector3i[offsets.size()];
      offsets.toArray(this.offsets);
      for (int i = 0; i < blockStates.length; i++) {
        var offset = offsets.get(i);
        var pos = center.offset(LMath.toVec3i(offset));
        blockStates[i] = level.getBlockState(pos);
      }
    }

    public void apply(Predicate<BlockState> predicate) {
      for (int i = 0; i < blockStates.length; i++) {
        states[i] = predicate.test(blockStates[i]);
      }
      isApplied = true;
    }

    public boolean all() {
      if (!isApplied) {
        throw new IllegalStateException("No predicate applied yet");
      }
      for (var s : states) {
        if (!s) {
          return false;
        }
      }
      return true;
    }

    public boolean all(Predicate<BlockState> predicate) {
      for (var blockState : blockStates) {
        if (!predicate.test(blockState)) {
          return false;
        }
      }
      return true;
    }

    public boolean any() {
      if (!isApplied) {
        throw new IllegalStateException("No predicate applied yet");
      }
      for (var s : states) {
        if (!s) {
          return true;
        }
      }
      return false;
    }

    public boolean any(Predicate<BlockState> predicate) {
      for (var blockState : blockStates) {
        if (predicate.test(blockState)) {
          return true;
        }
      }
      return false;
    }

    public int count() {
      int sum = 0;
      for (var s : states) {
        if (s) {
          sum++;
        }
      }
      return sum;
    }

    public int count(Predicate<BlockState> predicate) {
      int sum = 0;
      for (var state : blockStates) {
        if (predicate.test(state)) {
          sum++;
        }
      }
      return sum;
    }

    public boolean has(Vector3i v) {
      for (var offset : offsets) {
        if (offset.equals(v)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      var s = new StringBuilder("SurroundingPattern:\n");
      for (int z = 0; z < 3; z++) {
        s.append("  | ");
        for (int y = 0; y < 3; y++) {
          for (int x = 0; x < 3; x++) {
            Vector3i v = new Vector3i(x, y, z);
            if (has(v)) {
              s.append('#').append(' ');
            } else {
              s.append("  ");
            }
          }
          s.append("| ");
        }
        s.append("\n");
      }
      return s.toString();
    }
  }
}
