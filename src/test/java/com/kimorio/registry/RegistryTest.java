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

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistryTest {
  private static final String EMPTY = "empty";

  private final Registry<String, Item> registry = Registry.create();

  @Test
  void testGetBeforeGetOrCreate() {
    assertNull(this.registry.get(EMPTY));
    final Reference<Item> reference = this.registry.getOrCreate(EMPTY);
    assertSame(reference, this.registry.get(EMPTY)); // get should now return the (unbound) reference we created
  }

  @Test
  void testImmediate() {
    final Item item = new Item();

    final Reference<Item> reference = this.registry.register(EMPTY, item);

    assertThat(this.registry.keys()).containsExactly(EMPTY);

    assertThat(reference).isInstanceOf(References.Immediate.class);
    assertSame(Reference.Type.IMMEDIATE, reference.type());

    assertTrue(reference.bound());
    assertSame(item, reference.get());
    assertSame(item, assertDoesNotThrow(reference::need));

    final Reference<Item> holderAfterRegistration = this.registry.get(EMPTY);
    assertSame(reference, holderAfterRegistration);

    this.testRegistrationOfAlreadyRegistered(reference, item);
  }

  @Test
  void testLazy() {
    final Reference<Item> holderBeforeRegistration = this.registry.getOrCreate(EMPTY);

    // The registry should contain the key even when a value is not bound.
    assertThat(this.registry.keys()).containsExactly(EMPTY);

    // The returned reference should be lazy, since we have not yet registered a value.
    assertThat(holderBeforeRegistration).isInstanceOf(References.Lazy.class);
    assertSame(Reference.Type.LAZY, holderBeforeRegistration.type());

    // Lazy reference have no value by default.
    assertFalse(holderBeforeRegistration.bound());
    assertNull(holderBeforeRegistration.get());
    assertThrows(NoSuchElementException.class, holderBeforeRegistration::need);

    final Item value = new Item();

    final Reference<Item> holderAfterRegistration = this.registry.register(EMPTY, value);

    // The returned reference should still be of the lazy type, as the reference itself has not been replaced.
    assertThat(holderAfterRegistration).isInstanceOf(References.Lazy.class);
    assertSame(Reference.Type.LAZY, holderAfterRegistration.type());

    // The holder returned from registering should be the holder we already have, due to us
    // requesting a holder prior to registering the value.
    assertSame(holderBeforeRegistration, holderAfterRegistration);

    assertTrue(holderBeforeRegistration.bound());

    // The value has been set - both of these are now possible.
    assertSame(value, holderBeforeRegistration.get());
    assertSame(value, assertDoesNotThrow(holderBeforeRegistration::need));

    this.testRegistrationOfAlreadyRegistered(holderBeforeRegistration, value);
  }

  private void testRegistrationOfAlreadyRegistered(final Reference<Item> reference, final Item item) {
    // Attempting to re-register with the same value is perfectly fine...
    assertSame(reference, this.registry.register(EMPTY, item));
    // ...but with a different value is not allowed.
    assertThrows(IllegalStateException.class, () -> this.registry.register(EMPTY, new Item()));
  }

  @Test
  void testAlreadyBoundSameValue() {
    final Object a = new Object();
    assertNull(RegistryImpl.alreadyBound("aa", a, a));
  }

  @Test
  void testAlreadyBoundDifferentValue() {
    final Object a = new Object();
    final Object b = new Object();
    assertNotNull(RegistryImpl.alreadyBound("ab", a, b));
    assertNotNull(RegistryImpl.alreadyBound("ba", b, a));
  }

  static final class Item {
    @Override
    public boolean equals(final Object that) {
      return this == that;
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(this);
    }
  }
}
