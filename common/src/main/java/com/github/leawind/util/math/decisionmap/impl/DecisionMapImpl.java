package com.github.leawind.util.math.decisionmap.impl;


import com.github.leawind.util.math.decisionmap.api.DecisionFactor;
import com.github.leawind.util.math.decisionmap.api.DecisionMap;
import com.github.leawind.util.math.decisionmap.api.anno.ADecisionFactor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DecisionMapImpl<T> implements DecisionMap<T> {
	private final Class<?> initializer;

	/**
	 * 规则构建器们
	 */
	private final          List<Runnable>           ruleBuilders = new LinkedList<>();
	/**
	 * 因素列表
	 */
	private final @NotNull List<DecisionFactor>     factors      = new ArrayList<>();
	/**
	 * 列出所有可能的情况的列表
	 * <p>
	 * flagBits -> Supplier
	 */
	private final @NotNull List<Supplier<T>>        strategyMap  = new ArrayList<>();
	private final          Map<Supplier<?>, String> nameMap      = new HashMap<>();

	private int     flagBits = 0;
	/**
	 * 此对象是否已经构建
	 */
	private boolean isBuilt  = false;

	/**
	 * 根据类中的定义构建决策表
	 * <p>
	 * 类中应当包含带有 ADecisionFactor 注解的公共静态字段
	 * <p>
	 * 还要包含 public static void build(DecisionMap) 方法
	 * <pre>
	 * {@literal @ADecisionFactor public static final DecisionFactor is_swimming = DecisionFactor.of(()->false)}
	 * {@literal public static void build(DecisionMap\<Double> map){}}
	 * </pre>
	 */
	public DecisionMapImpl (@NotNull Class<?> clazz) {
		initializer = clazz;

		// Register Factors
		List<Field> adfListIndexed   = new LinkedList<>();
		List<Field> adfListAutoIndex = new LinkedList<>();

		for (var field: clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ADecisionFactor.class)) {
				if (field.getType() != DecisionFactor.class) {
					throw new IllegalArgumentException(String.format("Type %s required, got %s", DecisionFactor.class, field));
				} else if (!Modifier.isStatic(field.getModifiers())) {
					throw new IllegalArgumentException(String.format("Static required: %s", field));
				} else if (!field.canAccess(null)) {
					throw new IllegalArgumentException(String.format("Cannot access field: %s", field));
				}
				var adf = field.getAnnotation(ADecisionFactor.class);
				if (adf.value() == -1 && adf.mask() == -1) {
					adfListAutoIndex.add(field);
				} else {
					adfListIndexed.add(field);
				}
			} else if (field.getType() == Supplier.class) {
				try {
					nameMap.put((Supplier<?>)field.get(null), field.getName());
				} catch (IllegalAccessException ignored) {
				}
			}
		}

		int factorCount = adfListIndexed.size() + adfListAutoIndex.size();
		if (factorCount > MAX_FACTOR_COUNT) {
			throw new IllegalArgumentException(String.format("Too many (%d) DecisionFactors in class %s", factorCount, clazz));
		}
		while (factors.size() < factorCount) {
			factors.add(null);
		}

		try {
			for (var field: adfListIndexed) {
				var adf   = field.getAnnotation(ADecisionFactor.class);
				int index = adf.value() != -1 ? adf.value(): Integer.numberOfTrailingZeros(adf.mask());
				if (factors.get(index) != null) {
					throw new IllegalArgumentException(String.format("Field %s: Index %d has been used already.", field, index));
				}
				var df = (DecisionFactor)field.get(null);
				df.setName(field.getName());
				factors.set(index, df);
			}
			for (var field: adfListAutoIndex) {
				for (int index = 0; index < factors.size(); index++) {
					if (factors.get(index) == null) {
						var df = (DecisionFactor)field.get(null);
						df.setName(field.getName());
						factors.set(index, df);
						df.setIndex(index);
						break;
					}
				}
			}
		} catch (IllegalAccessException e) {
			// This should never happen!
			throw new IllegalStateException(e);
		}

		// Build
		try {
			var building = getBuildMethod(clazz);
			building.invoke(null, this);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			// This should never happen!
			throw new IllegalStateException(e);
		}
		build();
	}

	private void assertBuilt (boolean expected) {
		if (expected ^ isBuilt) {
			throw new UnsupportedOperationException(isBuilt ? "DecisionMap has been built already.": "DecisionMap not built yet.");
		}
	}

	@Override
	public void reset () {
		ruleBuilders.clear();
		factors.clear();
		strategyMap.clear();

		isBuilt  = false;
		flagBits = 0;
	}

	@Override
	public DecisionMap<T> updateFactors () {
		flagBits = 0;
		final int factorCount = getFactorCount();
		for (int i = 0; i < factorCount; i++) {
			flagBits |= (factors.get(i).update().get() ? 1: 0) << i;
		}
		return this;
	}

	@Override
	public T make () {
		assertBuilt(true);
		return getStrategy(flagBits).map(Supplier::get).orElse(null);
	}

	@Override
	public @NotNull Optional<Supplier<T>> getStrategy (int flagBits) {
		return Optional.ofNullable(strategyMap.get(flagBits));
	}

	@Override
	public T remake () {
		assertBuilt(true);
		return updateFactors().make();
	}

	@Override
	public int getFactorCount () {
		return factors.size();
	}

	@Override
	public int getMapSize () {
		return (int)Math.pow(2, getFactorCount());
	}

	@Override
	public @NotNull DecisionMap<T> build () {
		strategyMap.clear();
		while (strategyMap.size() < getMapSize()) {
			strategyMap.add(null);
		}
		for (var ruleBuilder: ruleBuilders) {
			ruleBuilder.run();
		}
		isBuilt = true;
		return this;
	}

	@Override
	public boolean isBuilt () {
		return isBuilt;
	}

	@Override
	public @NotNull DecisionMap<T> addRule (@NotNull Function<boolean[], Supplier<T>> func) {
		assertBuilt(false);
		ruleBuilders.clear();
		ruleBuilders.add(() -> {
			int       factorCount = getFactorCount();
			int       mapSize     = getMapSize();
			boolean[] params      = new boolean[factorCount];
			for (int flagBits = 0; flagBits < mapSize; flagBits++) {
				for (int factorIndex = 0; factorIndex < factorCount; factorIndex++) {
					boolean factorValue = (flagBits & (1 << factorIndex)) > 0;
					params[factorIndex] = factorValue;
				}
				strategyMap.set(flagBits, func.apply(params));
			}
		});
		return this;
	}

	@Override
	public @NotNull DecisionMap<T> addRule (@NotNull BiFunction<Integer, boolean[], Supplier<T>> func) {
		assertBuilt(false);
		ruleBuilders.clear();
		ruleBuilders.add(() -> {
			int       factorCount = getFactorCount();
			int       mapSize     = getMapSize();
			boolean[] flagList    = new boolean[factorCount];
			for (int flagBits = 0; flagBits < mapSize; flagBits++) {
				for (int factorIndex = 0; factorIndex < factorCount; factorIndex++) {
					boolean factorValue = (flagBits & (1 << factorIndex)) > 0;
					flagList[factorIndex] = factorValue;
				}
				strategyMap.set(flagBits, func.apply(flagBits, flagList));
			}
		});
		return this;
	}

	@Override
	public @NotNull DecisionMap<T> addRule (int flagBits, @NotNull Supplier<T> strategy) {
		assertBuilt(false);
		ruleBuilders.add(() -> strategyMap.set(flagBits, strategy));
		return this;
	}

	@Override
	public @NotNull DecisionMap<T> addRule (int flagBits, int mask, @NotNull Supplier<T> strategy) {
		assertBuilt(false);
		ruleBuilders.add(() -> {
			int mapSize = strategyMap.size();
			int im      = flagBits & mask;
			for (int i = 0; i < mapSize; i++) {
				if ((i & mask) == im) {
					strategyMap.set(i, strategy);
				}
			}
		});
		return this;
	}

	@Override
	public String toString () {
		var sb = new StringBuilder();
		sb.append(String.format("%s from %s (factorCount=%d, mapSize=%d)\n", DecisionMap.class.getSimpleName(), initializer.getSimpleName(), getFactorCount(), getMapSize()));
		sb.append("Factors:\n");
		for (int i = 0; i < factors.size(); i++) {
			sb.append(String.format("\t[%d] %s\n", i, factors.get(i).getName()));
		}
		sb.append("Strategy Map:\n");
		for (int flagBits = 0; flagBits < getMapSize(); flagBits++) {
			var func = getStrategy(flagBits).orElse(null);
			sb.append(String.format("\t%s %s\n", padStart(Integer.toBinaryString(flagBits), factors.size(), '0'), nameMap.getOrDefault(func, "unnamed")));
		}
		return sb.toString();
	}

	private @NotNull String padStart (String s, int length, char filler) {
		return String.format("%" + length + "s", s).replace(' ', filler);
	}

	private static @NotNull Method getBuildMethod (@NotNull Class<?> clazz) throws NoSuchMethodException {
		var method = clazz.getMethod("build", DecisionMap.class);
		if (!Modifier.isStatic(method.getModifiers())) {
			throw new IllegalArgumentException(String.format("Expected static method %s", method));
		} else if (!method.canAccess(null)) {
			throw new IllegalArgumentException(String.format("Cannot access method %s", method));
		} else if (method.getReturnType() != void.class) {
			throw new IllegalArgumentException(String.format("Required return type void for method %s", method));
		}
		return method;
	}
}
