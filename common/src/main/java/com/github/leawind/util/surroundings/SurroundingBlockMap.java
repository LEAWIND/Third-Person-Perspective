package com.github.leawind.util.surroundings;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * TODO remove
 */
public final class SurroundingBlockMap {
	private static final Pattern IGNORED_CHARS_PATTERN = Pattern.compile("[\\s\\n|]");

	/**
	 * Example
	 * <pre>
	 *  A A A
	 *  A A A        (bottom)
	 *  A A A
	 *
	 *  T A T
	 *  A A A        (middle)
	 *  T A T
	 *
	 *  A A A
	 *  A T A        (top)
	 *  A A A
	 * </pre>
	 */
	public static SurroundingBlockMap ofVertical (String str) {
		str = IGNORED_CHARS_PATTERN.matcher(str).replaceAll("");
		if (str.length() != 27) {
			throw new IllegalArgumentException("Invalid input string: " + str);
		}
		return new SurroundingBlockMap(parseLayer(str.substring(0, 9)), parseLayer(str.substring(9, 18)), parseLayer(str.substring(18, 27)));
	}

	/**
	 * Example:
	 * <pre>
	 * A A A  T A T  A A A
	 * A A A  A A A  A T A
	 * A A A  T A T  A A A
	 * </pre>
	 */
	public static SurroundingBlockMap of (String str) {
		str = IGNORED_CHARS_PATTERN.matcher(str).replaceAll("");
		if (str.length() != 27) {
			throw new IllegalArgumentException("Invalid string length");
		}
		String bottomStr = str.substring(0, 3) + str.substring(9, 12) + str.substring(18, 21);
		String middleStr = str.substring(3, 6) + str.substring(12, 15) + str.substring(21, 24);
		String topStr    = str.substring(6, 9) + str.substring(15, 18) + str.substring(24, 27);
		return new SurroundingBlockMap(parseLayer(bottomStr), parseLayer(middleStr), parseLayer(topStr));
	}

	public static SurroundingBlockMap of (String bottom, String middle, String top) {
		return new SurroundingBlockMap(parseLayer(bottom), parseLayer(middle), parseLayer(top));
	}

	public static SurroundingBlockMap of (BlockGetter blockGetter, BlockPos center, Function<BlockState, State> stateGetter) {
		int[] layers = new int[]{0, 0, 0};
		for (int y = 0; y < 3; y++) {
			layers[y] = putRawLayer(layers[y], 0, 0, stateGetter.apply(blockGetter.getBlockState(center.offset(-1, y - 1, -1))).code);
			layers[y] = putRawLayer(layers[y], 1, 0, stateGetter.apply(blockGetter.getBlockState(center.offset(0, y - 1, -1))).code);
			layers[y] = putRawLayer(layers[y], 2, 0, stateGetter.apply(blockGetter.getBlockState(center.offset(1, y - 1, -1))).code);
			layers[y] = putRawLayer(layers[y], 0, 1, stateGetter.apply(blockGetter.getBlockState(center.offset(-1, y - 1, 0))).code);
			layers[y] = putRawLayer(layers[y], 1, 1, stateGetter.apply(blockGetter.getBlockState(center.offset(0, y - 1, 0))).code);
			layers[y] = putRawLayer(layers[y], 2, 1, stateGetter.apply(blockGetter.getBlockState(center.offset(1, y - 1, 0))).code);
			layers[y] = putRawLayer(layers[y], 0, 2, stateGetter.apply(blockGetter.getBlockState(center.offset(-1, y - 1, 1))).code);
			layers[y] = putRawLayer(layers[y], 1, 2, stateGetter.apply(blockGetter.getBlockState(center.offset(0, y - 1, 1))).code);
			layers[y] = putRawLayer(layers[y], 2, 2, stateGetter.apply(blockGetter.getBlockState(center.offset(1, y - 1, 1))).code);
		}
		return new SurroundingBlockMap(layers[0], layers[1], layers[2]);
	}

	/**
	 * Example:
	 * <pre>
	 * T A T
	 * A A A
	 * T A T
	 * </pre>
	 */
	static int parseLayer (String str) {
		int layer = 0;
		layer = putRawLayer(layer, 0, 0, State.of(str.charAt(0)).code);
		layer = putRawLayer(layer, 1, 0, State.of(str.charAt(1)).code);
		layer = putRawLayer(layer, 2, 0, State.of(str.charAt(2)).code);
		layer = putRawLayer(layer, 0, 1, State.of(str.charAt(3)).code);
		layer = putRawLayer(layer, 1, 1, State.of(str.charAt(4)).code);
		layer = putRawLayer(layer, 2, 1, State.of(str.charAt(5)).code);
		layer = putRawLayer(layer, 0, 2, State.of(str.charAt(6)).code);
		layer = putRawLayer(layer, 1, 2, State.of(str.charAt(7)).code);
		layer = putRawLayer(layer, 2, 2, State.of(str.charAt(8)).code);
		return layer;
	}

	/**
	 * Match layer
	 */
	static boolean match (int pattern, int layer) {
		if ((pattern & ~0b111111111111111111) > 0) {
			System.out.println("Bad pattern: " + pattern);
		}
		if ((layer & ~0b111111111111111111) > 0) {
			System.out.println("Bad layer: " + layer);
		}
		pattern &= 0b111111111111111111;
		layer &= 0b111111111111111111;
		int s = ~((pattern & 0b010101010101010101) ^ (layer & 0b010101010101010101));
		s |= pattern >>> 1;
		s = (~s) & 0b010101010101010101;
		return s == 0;
	}

	static int putRawLayer (int layer, int x, int z, int value) {
		int offset = 6 * x + 2 * z;
		layer &= ~(0b11 << offset);
		return layer | ((value & 0b11) << (6 * x + 2 * z));
	}

	static int putLayer (int layer, int x, int z, int value) {
		if (x < -1 || x > 1 || z < -1 || z > 1) {
			throw new IllegalArgumentException(String.format("Invalid coordinates: (%d, %d)", x, z));
		}
		return putRawLayer(layer, x - 1, z - 1, value);
	}

	/**
	 * 每两位表示一个 {@link State}
	 * <p>
	 * 低位表示值
	 * <p>
	 * 高位为1表示其作为匹配器时可以匹配任意值
	 */
	private final int bottom;
	private final int middle;
	private final int top;

	private SurroundingBlockMap (int bottom, int middle, int top) {
		this.bottom = bottom;
		this.middle = middle;
		this.top    = top;
	}

	/**
	 * Match the map using this pattern
	 *
	 * @param map map
	 */
	public boolean match (SurroundingBlockMap map) {
		return match(bottom, map.bottom) && match(middle, map.middle) && match(top, map.top);
	}

	private int layer (int y) {
		return switch (y) {
			case 0 -> bottom;
			case 1 -> middle;
			case 2 -> top;
			default -> throw new IllegalArgumentException(String.format("Invalid layer id: %d", y));
		};
	}

	private State getRaw (int x, int y, int z) {
		return State.values()[layer(y) >> (6 * x + 2 * z) & 0b11];
	}

	public State get (int x, int y, int z) {
		if (x < -1 || x > 1 || y < 0 || y > 2 || z < -1 || z > 1) {
			throw new IllegalArgumentException(String.format("Invalid coordinates: (%d, %d, %d)", x, y, z));
		}
		return getRaw(x + 1, y, z + 1);
	}

	public String toString () {
		var s = new StringBuilder("SurroundBlockStates:\n");
		for (int z = 0; z < 3; z++) {
			s.append("  | ");
			for (int y = 0; y < 3; y++) {
				s.append(getRaw(0, y, z).ch).append(" ");
				s.append(getRaw(1, y, z).ch).append(" ");
				s.append(getRaw(2, y, z).ch).append(" | ");
			}
			s.append("\n");
		}
		return s.toString();
	}

	public enum State {
		FALSE(0b00, '0'),
		TRUE(0b01, '1'),
		ANY(0b10, '·');

		public static State of (char ch) {
			return switch (ch) {
				case '0', '-', 'O' -> FALSE;
				case '1', '#', '+', 'T' -> TRUE;
				case '*', '?', 'A', '·', '.' -> ANY;
				default -> throw new IllegalArgumentException("Invalid state char: " + ch);
			};
		}

		public final int  code;
		public final char ch;

		State (int code, char ch) {
			this.code = code;
			this.ch   = ch;
		}
	}
}
