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

import org.jetbrains.annotations.NotNull;

/**
 * A registry.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @since 1.0.0
 */
public interface Registry<K, V> extends RegistryView<K, V> {
  /**
   * Creates a new registry.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @return a registry
   * @since 1.0.0
   */
  static <K, V> @NotNull Registry<K, V> create() {
    return new RegistryImpl<>();
  }

  /**
   * Registers {@code value} to {@code key}, returning a {@link Reference}.
   *
   * @param key the key
   * @param value the value
   * @return a reference
   * @since 1.0.0
   */
  @NotNull Reference<V> register(final @NotNull K key, final @NotNull V value);
}
