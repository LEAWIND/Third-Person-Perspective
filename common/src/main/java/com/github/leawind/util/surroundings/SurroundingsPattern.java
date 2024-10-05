package com.github.leawind.util.surroundings;


import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SurroundingsPattern {
	private static final Pattern BLANK_PATTERN    = Pattern.compile("[\\s\\n|]");
	private static final String  SEPARATOR_REGEXP = "[\\s\\n|]+";

	public static SurroundingsPattern of (String str) {
		var words   = BLANK_PATTERN.matcher(str).replaceAll(" ").trim().split(SEPARATOR_REGEXP);
		var pattern = new SurroundingsPattern();
		for (int i = 0; i < words.length; i++) {
			var         word = words[i];
			List<Vec3i> offsets;
			if (pattern.names.containsKey(word)) {
				offsets = pattern.names.get(word);
			} else {
				offsets = new ArrayList<>(27);
				pattern.names.put(word, offsets);
			}
			int x = i % 3;
			int y = (i / 3) % 3;
			int z = i / 9;
			offsets.add(new Vec3i(x - 1, y - 1, z - 1));
			pattern.tensor[x][y][z] = word;
		}
		return pattern;
	}

	private final String[][][]             tensor = new String[][][]{new String[][]{new String[3], new String[3], new String[3],},//
																	 new String[][]{new String[3], new String[3], new String[3],},//
																	 new String[][]{new String[3], new String[3], new String[3],},};
	private final Map<String, List<Vec3i>> names  = new HashMap<>();

	private SurroundingsPattern () {
	}

	public Set<String> getNames () {
		return names.keySet();
	}

	public List<Vec3i> getOffsets (String name) {
		if (!names.containsKey(name)) {
			throw new IllegalStateException("Unknown name: " + name);
		}
		return names.get(name);
	}

	@Override
	public String toString () {
		int maxNameLength = 0;
		for (String name: names.keySet()) {
			maxNameLength = Math.max(maxNameLength, name.length());
		}
		var s = new StringBuilder("SurroundingPattern:\n");
		for (int z = 0; z < 3; z++) {
			s.append("  | ");
			for (int y = 0; y < 3; y++) {
				s.append(String.format("%" + maxNameLength + "s", tensor[0][y][z])).append(" ");
				s.append(String.format("%" + maxNameLength + "s", tensor[1][y][z])).append(" ");
				s.append(String.format("%" + maxNameLength + "s", tensor[2][y][z])).append(" | ");
			}
			s.append("\n");
		}
		return s.toString();
	}
}
