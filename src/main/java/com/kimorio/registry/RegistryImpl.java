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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import static java.util.Objects.requireNonNull;

final class RegistryImpl<K, V> implements Registry<K, V> {
  private final Map<K, Reference<V>> byKey = new HashMap<>();

  @Override
  public @NotNull Set<K> keys() {
    return Collections.unmodifiableSet(this.byKey.keySet());
  }

  @Override
  public @NotNull Reference<V> get(final @NotNull K key) {
    requireNonNull(key, "key");

    @Nullable Reference<V> reference = this.byKey.get(key);
    if (reference == null) {
      // No value has been registered for the given key yet - creating a lazy reference here
      // allows us to provide a way to access the value once it has been registered later on.
      reference = new References.Lazy<>(key);
      this.byKey.put(key, reference);
    }

    return reference;
  }

  @Override
  public @NotNull Reference<V> register(final @NotNull K key, final @NotNull V value) {
    requireNonNull(key, "key");
    requireNonNull(value, "value");

    @Nullable Reference<V> reference = this.byKey.get(key);

    if (reference == null) {
      // No reference was previously requested prior to registration.
      reference = new References.Immediate<>(key, value);
      this.byKey.put(key, reference);
    } else {
      @Nullable V oldValue = null;

      // We can't pass "K" to these casts, they are incompatible
      if (reference instanceof References.Immediate<?, V> immediate) {
        oldValue = immediate.value();
      } else if (reference instanceof References.Lazy<?, V> lazy) {
        // A reference was requested for this key prior to the actual
        // registration of a value - let's attempt to bind the value to the reference
        oldValue = lazy.bind(value);
      }

      if (oldValue != null) {
        final @Nullable IllegalStateException alreadyBound = alreadyBound(key, oldValue, value);
        if (alreadyBound != null) {
          // A reference already exists with a different value.
          throw alreadyBound;
        }
      }
    }

    return reference;
  }

  @VisibleForTesting
  static <K, V> @Nullable IllegalStateException alreadyBound(final K key, final V oldValue, final V newValue) {
    if (oldValue != newValue) {
      return new IllegalStateException(key + " is already bound to " + oldValue + ", cannot bind to " + newValue);
    }
    return null;
  }
}
