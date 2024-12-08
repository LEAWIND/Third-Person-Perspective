package com.github.leawind.thirdperson.util.math.decisionmap.test;

import com.github.leawind.thirdperson.util.math.decisionmap.DecisionMap;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class DecisionMapTest {
  boolean isSunny = false;
  boolean isWindy = false;
  double temperature = 25;

  @Test
  public void testBuilder() {
    final var DEFAULT = "Default operation: I wanna go to the park";
    final var SUNNY = "It is sunny, I don't go out";
    final var TOO_HOT = "Too hot, stay at home";
    final var PERFECT = "Perfect, Let's go!";

    var builder = DecisionMap.<String>builder();

    builder.factor(0, "sunny", () -> isSunny);
    builder.factor(1, "windy", () -> isWindy);
    builder.factor(2, "hot", () -> temperature > 33);
    builder.factor(3, "cold", () -> temperature < 16);

    // 默认情况
    builder.whenDefault(() -> DEFAULT);
    // 当 sunny == true 时
    builder.when(0, true, () -> SUNNY);
    // 太热时
    builder.when(2, true, () -> TOO_HOT);
    // 一种特定情况
    builder.whenAll(0b0000, () -> PERFECT);

    var map = builder.build();

    System.out.println(map.toDescriptionWithCases(true));

    {
      isSunny = false;
      isWindy = true;
      temperature = 23;
      assert map.updateAll().make().equals(DEFAULT);
    }
    {
      isSunny = true;
      isWindy = false;
      temperature = 23;
      assert map.updateAll().make().equals(SUNNY);
    }
    {
      isSunny = true;
      isWindy = true;
      temperature = 45;
      assert map.updateAll().make().equals(TOO_HOT);
    }
    {
      isSunny = false;
      isWindy = false;
      temperature = 23;
      assert map.updateAll().make().equals(PERFECT);
    }
  }

  @Test
  public void performanceTest() {
    final var factorCount = 24;
    final var filterMask = (1 << factorCount) - 1;
    final var factors = new boolean[factorCount];

    var builder = DecisionMap.<String>builder();
    for (int i = 0; i < factorCount; i++) {
      final int finalI = i;
      builder.factor(String.format("Factor[%d]", i), () -> factors[finalI]);
    }
    builder.whenDefault(() -> "Default");

    var random = new Random(12138);
    for (int i = 0; i < 16; i++) {
      final String message = String.format("Message[%d]", i);

      var mask = filterMask & random.nextInt();
      var values = mask & random.nextInt();

      builder.when(mask, values, () -> message);
    }

    var map = builder.build();
    System.out.println(map.toDescription());
    map.updateAll();
    map.make();
  }
}
