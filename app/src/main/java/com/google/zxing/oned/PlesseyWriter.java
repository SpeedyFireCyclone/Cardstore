package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Map;

public class PlesseyWriter extends OneDimensionalCodeWriter {

    //Bar width values
    static final int A1 = 8;
    static final int A2 = 25 - A1;
    static final int B1 = 18;
    static final int B2 = 25 - B1;
    static final int X = 35;

    //Encodings
    static final String ALPHABET_STRING = "0123456789ABCDEF";

    static final int[][] CHARACTER_ENCODINGS = {
            {A1, A2, A1, A2, A1, A2, A1, A2}, // 0
            {B1, B2, A1, A2, A1, A2, A1, A2}, // 1
            {A1, A2, B1, B2, A1, A2, A1, A2}, // 2
            {B1, B2, B1, B2, A1, A2, A1, A2}, // 3
            {A1, A2, A1, A2, B1, B2, A1, A2}, // 4
            {B1, B2, A1, A2, B1, B2, A1, A2}, // 5
            {A1, A2, B1, B2, B1, B2, A1, A2}, // 6
            {B1, B2, B1, B2, B1, B2, A1, A2}, // 7
            {A1, A2, A1, A2, A1, A2, B1, B2}, // 8
            {B1, B2, A1, A2, A1, A2, B1, B2}, // 9
            {A1, A2, B1, B2, A1, A2, B1, B2}, // A
            {B1, B2, B1, B2, A1, A2, B1, B2}, // B
            {A1, A2, A1, A2, B1, B2, B1, B2}, // C
            {B1, B2, A1, A2, B1, B2, B1, B2}, // D
            {A1, A2, B1, B2, B1, B2, B1, B2}, // E
            {B1, B2, B1, B2, B1, B2, B1, B2}  // F
    };

    static final int[] START = {B1, B2, B1, B2, A1, A2, B1, B2};
    static final int[] TERM = {X};
    static final int[] END = {A2, A1, A2, A1, B2, B1, B2, B1};

    //CRC
    private static final byte[] crcGrid = new byte[]{1, 1, 1, 1, 0, 1, 0, 0, 1};
    private static final int[] crc0Widths = {A1, A2};
    private static final int[] crc1Widths = {B1, B2};

    public BitMatrix encode(String contents,
                            BarcodeFormat format,
                            int width,
                            int height,
                            Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.PLESSEY) {
            throw new IllegalArgumentException("Can only encode PLESSEY, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override
    public boolean[] encode(String contents) {
        int length = contents.length();
        //start + contents + CRC + terminator + end
        int codeWidth = 100 + length * 100 + 2 * 100 + 35 + 100;
        for (int i = 0; i < length; i++) {
            int indexInString = ALPHABET_STRING.indexOf(contents.charAt(i)) + 1;
            if (indexInString < 0) {
                throw new IllegalArgumentException("Bad contents: " + contents);
            }
        }
        boolean[] result = new boolean[codeWidth];
        int pos = 0;
        byte[] crcBuffer = new byte[4 * length + 8];
        int crcBufferPos = 0;
        //append start pattern
        pos += appendPattern(result, pos, START, true);
        //append next character to byte matrix
        for (int i = 0; i < length; i++) {
            int indexInString = ALPHABET_STRING.indexOf(contents.charAt(i));
            int[] widths = CHARACTER_ENCODINGS[indexInString];
            pos += appendPattern(result, pos, widths, true);
            crcBuffer[crcBufferPos++] = (byte) (indexInString & 1);
            crcBuffer[crcBufferPos++] = (byte) ((indexInString >> 1) & 1);
            crcBuffer[crcBufferPos++] = (byte) ((indexInString >> 2) & 1);
            crcBuffer[crcBufferPos++] = (byte) ((indexInString >> 3) & 1);
        }
        //calculate CRC
        for (int i = 0; i < (4 * length); i++) {
            if (crcBuffer[i] != 0) {
                for (int j = 0; j < 9; j++) {
                    crcBuffer[i + j] ^= crcGrid[j];
                }
            }
        }
        //append CRC pattern
        for (int i = 0; i < 8; i++) {
            switch (crcBuffer[length * 4 + i]) {
                case 0:
                    pos += appendPattern(result, pos, crc0Widths, true);
                    break;
                case 1:
                    pos += appendPattern(result, pos, crc1Widths, true);
                    break;
            }
        }
        //append terminator bar and end pattern
        pos += appendPattern(result, pos, TERM, true);
        appendPattern(result, pos, END, false);
        return result;
    }
}
