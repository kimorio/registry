/*
 * This file is part of registry, licensed under the MIT License.
 *
 * Copyright (c) 2021-2022 Kimorio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kimorio.registry;

import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class References {
  private References() {
  }

  record Immediate<K, V>(@NotNull K key, @NotNull V value) implements Reference<V> {
    @Override
    public boolean bound() {
      return true; // An immediate reference always has a value associated with it.
    }

    @Override
    public @NotNull V get() {
      return this.value;
    }

    @Override
    public @NotNull Type type() {
      return Type.IMMEDIATE;
    }
  }

  static final class Lazy<K, V> implements Reference<V> {
    private final K key;
    private @Nullable V value;

    Lazy(final @NotNull K key) {
      this.key = key;
    }

    @Nullable V bind(final @NotNull V value) {
      if (this.value == null) {
        this.value = value;
        return null;
      } else {
        return this.value;
      }
    }

    @Override
    public boolean bound() {
      return this.value != null;
    }

    @Override
    public @Nullable V get() {
      return this.value;
    }

    @Override
    public @NotNull Type type() {
      return Type.LAZY;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
        .add("key=" + this.key)
        .add("value=" + this.value)
        .toString();
    }
  }
}
