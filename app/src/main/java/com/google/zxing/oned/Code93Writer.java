/*
 * Copyright 2015 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Map;

/**
 * This object renders a CODE93 code as a BitMatrix
 */
public class Code93Writer extends OneDimensionalCodeWriter {
  static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
  static final int[] CHARACTER_ENCODINGS = {
          0x114, 0x148, 0x144, 0x142, 0x128, 0x124, 0x122, 0x150, 0x112, 0x10A, // 0-9
          0x1A8, 0x1A4, 0x1A2, 0x194, 0x192, 0x18A, 0x168, 0x164, 0x162, 0x134, // A-J
          0x11A, 0x158, 0x14C, 0x146, 0x12C, 0x116, 0x1B4, 0x1B2, 0x1AC, 0x1A6, // K-T
          0x196, 0x19A, 0x16C, 0x166, 0x136, 0x13A, // U-Z
          0x12E, 0x1D4, 0x1D2, 0x1CA, 0x16E, 0x176, 0x1AE, // - - %
          0x126, 0x1DA, 0x1D6, 0x132, 0x15E, // Control chars? $-*
  };

  private static void toIntArray(int a, int[] toReturn) {
    for (int i = 0; i < 9; i++) {
      int temp = a & (1 << (8 - i));
      toReturn[i] = temp == 0 ? 0 : 1;
    }
  }

  protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
    for (int bit : pattern) {
      target[pos++] = bit != 0;
    }
    return 9;
  }

  private static int computeChecksumIndex(String contents, int maxWeight) {
    int weight = 1;
    int total = 0;

    for (int i = contents.length() - 1; i >= 0; i--) {
      int indexInString = ALPHABET_STRING.indexOf(contents.charAt(i));
      total += indexInString * weight;
      if (++weight > maxWeight) {
        weight = 1;
      }
    }
    return total % 47;
  }

  @Override
  public BitMatrix encode(String contents,
                          BarcodeFormat format,
                          int width,
                          int height,
                          Map<EncodeHintType, ?> hints) throws WriterException {
    if (format != BarcodeFormat.CODE_93) {
      throw new IllegalArgumentException("Can only encode CODE_93, but got " + format);
    }
    return super.encode(contents, format, width, height, hints);
  }

  @Override
  public boolean[] encode(String contents) {
    int length = contents.length();
    if (length > 80) {
      throw new IllegalArgumentException(
              "Requested contents should be less than 80 digits long, but got " + length);
    }
    //each character is encoded by 9 of 0/1's
    int[] widths = new int[9];

    //lenght of code + 2 start/stop characters + 2 checksums, each of 9 bits, plus a termination bar
    int codeWidth = (contents.length() + 2 + 2) * 9 + 1;

    boolean[] result = new boolean[codeWidth];

    //start character (*)
    toIntArray(CHARACTER_ENCODINGS[47], widths);
    int pos = appendPattern(result, 0, widths, true);

    for (int i = 0; i < length; i++) {
      int indexInString = ALPHABET_STRING.indexOf(contents.charAt(i));
      toIntArray(CHARACTER_ENCODINGS[indexInString], widths);
      pos += appendPattern(result, pos, widths, true);
    }

    //add two checksums
    int check1 = computeChecksumIndex(contents, 20);
    toIntArray(CHARACTER_ENCODINGS[check1], widths);
    pos += appendPattern(result, pos, widths, true);

    //append the contents to reflect the first checksum added
    contents += ALPHABET_STRING.charAt(check1);

    int check2 = computeChecksumIndex(contents, 15);
    toIntArray(CHARACTER_ENCODINGS[check2], widths);
    pos += appendPattern(result, pos, widths, true);

    //end character (*)
    toIntArray(CHARACTER_ENCODINGS[47], widths);
    pos += appendPattern(result, pos, widths, true);

    //termination bar (single black bar)
    result[pos] = true;

    return result;
  }
}
