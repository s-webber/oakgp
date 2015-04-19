package org.oakgp.util;

import static java.util.Objects.requireNonNull;

public class DummyRandom implements Random {
   public static final Random EMPTY = new DummyRandom();

   private final DummyValuesQueue<Boolean> booleans;
   private final DummyValuesQueue<Double> doubles;
   private final DummyValuesMap<Integer, Integer> integers;

   private DummyRandom() {
      this(null, null, null);
   }

   private DummyRandom(Integer bound, Integer... values) {
      this(new DummyValuesMap<>(bound, values), null, null);
   }

   public DummyRandom(DummyValuesMap<Integer, Integer> integers) {
      this(integers, null, null);
   }

   public DummyRandom(Double... doubles) {
      this(null, new DummyValuesQueue<>(doubles), null);
   }

   public DummyRandom(Boolean... booleans) {
      this(null, null, new DummyValuesQueue<>(booleans));
   }

   public DummyRandom(DummyValuesMap<Integer, Integer> integers, DummyValuesQueue<Double> doubles, DummyValuesQueue<Boolean> booleans) {
      this.integers = integers;
      this.doubles = doubles;
      this.booleans = booleans;
   }

   @Override
   public int nextInt(int bound) {
      requireNonNull(integers);
      return integers.next(bound);
   }

   @Override
   public double nextDouble() {
      requireNonNull(doubles);
      return doubles.next();
   }

   @Override
   public boolean nextBoolean() {
      requireNonNull(booleans);
      return booleans.next();
   }

   public void assertEmpty() {
      if (integers != null) {
         integers.assertEmpty();
      }
      if (doubles != null && !doubles.isEmpty()) {
         throw new IllegalArgumentException("Not all doubles have been selected");
      }
      if (booleans != null && !booleans.isEmpty()) {
         throw new IllegalArgumentException("Not all booleans have been selected");
      }
   }

   public static GetIntExpectation getInt(int bound) {
      return new GetIntExpectation(bound);
   }

   public static class Builder {
      private DummyValuesMap.Builder<Integer, Integer> integersBuilder = new DummyValuesMap.Builder<Integer, Integer>();
      private DummyValuesQueue<Double> doubles;
      private DummyValuesQueue<Boolean> booleans;

      private Builder setInts(Integer key, Integer... values) {
         integersBuilder.put(key, values);
         return this;
      }

      public GetIntBuilderExpectation getInt(int bound) {
         return new GetIntBuilderExpectation(this, bound);
      }

      public Builder setDoubles(Double... doubles) {
         if (doubles != null) {
            throw new IllegalStateException();
         }
         this.doubles = new DummyValuesQueue<Double>(doubles);
         return this;
      }

      public Builder setBooleans(Boolean... booleans) {
         if (booleans != null) {
            throw new IllegalStateException();
         }
         this.booleans = new DummyValuesQueue<Boolean>(booleans);
         return this;
      }

      public DummyRandom build() {
         return new DummyRandom(integersBuilder.build(), doubles, booleans);
      }
   }

   public static class GetIntBuilderExpectation {
      private final Builder builder;
      private final Integer bound;

      public GetIntBuilderExpectation(Builder builder, Integer bound) {
         this.builder = builder;
         this.bound = bound;
      }

      public Builder returns(Integer... values) {
         builder.setInts(bound, values);
         return builder;
      }
   }

   public static class GetIntExpectation {
      private final Integer bound;

      public GetIntExpectation(Integer bound) {
         this.bound = bound;
      }

      public DummyRandom returns(Integer... values) {
         return new DummyRandom(bound, values);
      }
   }
}
