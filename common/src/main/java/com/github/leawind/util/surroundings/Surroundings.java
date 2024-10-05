package com.github.leawind.util.surroundings;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Surroundings {
	private final BlockGetter           level;
	private final BlockPos              center;
	private final Map<String, Sequence> sequenceMap = new HashMap<>();

	public Surroundings (BlockGetter level, BlockPos center) {
		this.level  = level;
		this.center = center;
	}

	public Sequence get (String name) {
		if (!sequenceMap.containsKey(name)) {
			throw new IllegalArgumentException("Unknown name: " + name);
		}
		return sequenceMap.get(name);
	}

	public void apply (SurroundingsPattern pattern, Predicate<BlockState> predicate) {
		for (var name: pattern.getNames()) {
			var seq = new Sequence(name, pattern.getOffsets(name));
			sequenceMap.put(name, seq);
		}
		for (var seq: sequenceMap.values()) {
			seq.apply(predicate);
		}
	}

	public class Sequence {
		private final String       name;
		private final BlockState[] blockStates;
		private final Vec3i[]      offsets;
		private final boolean[]    states;
		private       boolean      isApplied = false;

		private Sequence (String name, List<Vec3i> offsets) {
			this.name    = name;
			blockStates  = new BlockState[offsets.size()];
			states       = new boolean[offsets.size()];
			this.offsets = new Vec3i[offsets.size()];
			offsets.toArray(this.offsets);
			for (int i = 0; i < blockStates.length; i++) {
				var offset = offsets.get(i);
				var pos    = center.offset(offset);
				blockStates[i] = level.getBlockState(pos);
			}
		}

		public void apply (Predicate<BlockState> predicate) {
			for (int i = 0; i < blockStates.length; i++) {
				states[i] = predicate.test(blockStates[i]);
			}
			isApplied = true;
		}

		/**
		 * @see Sequence#apply
		 * @see Surroundings#apply
		 */
		public boolean all () {
			if (!isApplied) {
				throw new IllegalStateException("No predicate applied yet");
			}
			for (var s: states) {
				if (!s) {
					return false;
				}
			}
			return true;
		}

		public boolean all (Predicate<BlockState> predicate) {
			for (var blockState: blockStates) {
				if (!predicate.test(blockState)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * @see Sequence#apply
		 * @see Surroundings#apply
		 */
		public boolean any () {
			if (!isApplied) {
				throw new IllegalStateException("No predicate applied yet");
			}
			for (var s: states) {
				if (!s) {
					return true;
				}
			}
			return false;
		}

		public boolean any (Predicate<BlockState> predicate) {
			for (var blockState: blockStates) {
				if (predicate.test(blockState)) {
					return true;
				}
			}
			return false;
		}

		public int count () {
			int sum = 0;
			for (var s: states) {
				if (s) {
					sum++;
				}
			}
			return sum;
		}

		public int count (Predicate<BlockState> predicate) {
			int sum = 0;
			for (var state: blockStates) {
				if (predicate.test(state)) {
					sum++;
				}
			}
			return sum;
		}

		public boolean has (Vec3i v) {
			for (var offset: offsets) {
				if (offset.equals(v)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString () {
			var s = new StringBuilder("SurroundingPattern:\n");
			for (int z = 0; z < 3; z++) {
				s.append("  | ");
				for (int y = 0; y < 3; y++) {
					for (int x = 0; x < 3; x++) {
						Vec3i v = new Vec3i(x, y, z);
						if (has(v)) {
							s.append(name).append(" ");
						} else {
							s.append(" ".repeat(name.length())).append(" ");
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
