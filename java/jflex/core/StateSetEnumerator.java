/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * JFlex 1.8.0-SNAPSHOT                                                    *
 * Copyright (C) 1998-2018  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package jflex.core;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

/**
 * Enumerates the states of a StateSet.
 *
 * @author Gerwin Klein
 * @version JFlex 1.8.0-SNAPSHOT
 */
public final class StateSetEnumerator implements PrimitiveIterator.OfInt {

  private static final boolean DEBUG = false;

  private int index;
  private int offset;
  private long mask;

  private long[] bits;

  /**
   * creates a new StateSetEnumerator that is not yet associated with a StateSet. hasMoreElements()
   * and nextElement() will throw NullPointerException when used before reset()
   */
  public StateSetEnumerator() {}

  /**
   * Constructor for StateSetEnumerator.
   *
   * @param states a {@link StateSet} object.
   */
  public StateSetEnumerator(StateSet states) {
    reset(states);
  }

  /**
   * reset.
   *
   * @param states a {@link StateSet} object.
   */
  public void reset(StateSet states) {
    bits = states.bits;
    index = 0;
    offset = 0;
    mask = 1;
    while (index < bits.length && bits[index] == 0) index++;

    if (index >= bits.length) return;

    while (offset <= StateSet.MASK && ((bits[index] & mask) == 0)) {
      mask <<= 1;
      offset++;
    }
  }

  private void advance() {

    if (DEBUG) {
      Out.dump(
          "Advancing, at start, index = "
              + index
              + ", offset = "
              + offset); // $NON-NLS-1$ //$NON-NLS-2$
    }
    // cache fields in local variable for faster access
    int _index = this.index;
    int _offset = this.offset;
    long _mask = this.mask;
    long[] _bits = this.bits;

    long bi = _bits[_index];

    do {
      _offset++;
      _mask <<= 1;
    } while (_offset <= StateSet.MASK && ((bi & _mask) == 0));

    if (_offset > StateSet.MASK) {
      int length = _bits.length;

      do _index++;
      while (_index < length && _bits[_index] == 0);

      if (_index >= length) {
        this.index = length; // indicates "no more elements"
        return;
      }

      _offset = 0;
      _mask = 1;
      bi = _bits[_index];

      // terminates, because bi != 0
      while ((bi & _mask) == 0) {
        _mask <<= 1;
        _offset++;
      }
    }

    // write back cached values
    this.index = _index;
    this.mask = _mask;
    this.offset = _offset;
  }

  /**
   * hasMoreElements.
   *
   * @return a boolean.
   */
  public boolean hasMoreElements() {
    if (DEBUG) {
      Out.dump(
          "hasMoreElements, index = "
              + index
              + ", offset = "
              + offset); // $NON-NLS-1$ //$NON-NLS-2$
    }
    return index < bits.length;
  }

  /**
   * nextElement.
   *
   * @return a int.
   */
  public int nextElement() {
    if (DEBUG) {
      Out.dump(
          "nextElement, index = " + index + ", offset = " + offset); // $NON-NLS-1$ //$NON-NLS-2$
    }
    if (index >= bits.length) throw new NoSuchElementException();
    int x = (index << StateSet.BITS) + offset;
    advance();
    return x;
  }

  @Override
  public boolean hasNext() {
    return hasMoreElements();
  }

  @Override
  public int nextInt() {
    return nextElement();
  }
}
