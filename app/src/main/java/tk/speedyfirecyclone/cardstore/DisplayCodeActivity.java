package tk.speedyfirecyclone.cardstore;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class DisplayCodeActivity extends Activity {

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * <p/>
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFEEEEEE;
    private static final int BLACK = 0xFF000000;
    public static BarcodeFormat format;

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_code);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.getWindow().setAttributes(lp);

        // barcode data
        String barcodeString = getIntent().getStringExtra("barcodeString");
        String formatString = getIntent().getStringExtra("formatString");
        String titleString = getIntent().getStringExtra("titleString");

        // barcode image
        try {
            ImageView code = (ImageView) findViewById(R.id.codeDisplayCode);
            Bitmap bitmapimage = encodeAsBitmap(barcodeString, formatTranslator(formatString), 3000, 1000);
            code.setImageBitmap(bitmapimage);
        } catch (WriterException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }

        //barcode text
        TextView text = (TextView) findViewById(R.id.textDisplayCode);
        TextView title = (TextView) findViewById(R.id.titleDisplayCode);
        text.setText(barcodeString);
        title.setText(titleString);
    }

    //Translate and return the format from string to BarcodeFormat
    public BarcodeFormat formatTranslator(String formatString) {

        switch (formatString) {
            case "AZTEC":
                return BarcodeFormat.AZTEC;
            case "CODABAR":
                return BarcodeFormat.CODABAR;
            case "CODE_39":
                return BarcodeFormat.CODE_39;
            case "CODE_93":
                return BarcodeFormat.CODE_93;
            case "CODE_128":
                return BarcodeFormat.CODE_128;
            case "DATA_MATRIX":
                return BarcodeFormat.DATA_MATRIX;
            case "EAN_8":
                return BarcodeFormat.EAN_8;
            case "EAN_13":
                return BarcodeFormat.EAN_13;
            case "ITF":
                return BarcodeFormat.ITF;
            case "PDF_417":
                return BarcodeFormat.PDF_417;
            case "QR_CODE":
                return BarcodeFormat.QR_CODE;
            case "RSS_14": //FIXME: Not generating.
                return BarcodeFormat.RSS_14;
            case "RSS_EXPANDED": //FIXME: Not generating.
                return BarcodeFormat.RSS_EXPANDED;
            case "UPC_A":
                return BarcodeFormat.UPC_A;
            case "UPC_E":
                return BarcodeFormat.UPC_E;
            case "UPC_EAN_EXTENSION": //TODO: Extensions are not detected in barcode scanner, possible fix might be an advanced edit menu.
                return BarcodeFormat.UPC_EAN_EXTENSION;
            case "PLESSEY":
                return BarcodeFormat.PLESSEY;
            default:
                //Default to QR to avoid NPE.
                FirebaseCrash.report(new Exception("Unknown Barcodeformat for translation: " + formatString));
                return BarcodeFormat.QR_CODE;
        }
    }

    Bitmap encodeAsBitmap(String contentsToEncode, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        if (format == BarcodeFormat.DATA_MATRIX) {
            img_width = 400;
            img_height = img_width;
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } else {
            try {
                result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
            } catch (IllegalArgumentException iae) {
                // Unsupported format
                FirebaseCrash.report(iae);
                return null;
            }
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[img_width * img_height];
        if (format == BarcodeFormat.DATA_MATRIX) {
            int pixelsize = img_width / width;
            if (pixelsize > img_height / height) {
                pixelsize = img_height / height;
            }

            // All are 0, or black, by default
            for (int y = 0; y < width; y++) {
                int offset = y * img_width * pixelsize;

                // scaling pixel height
                for (int pixelsizeHeight = 0; pixelsizeHeight < pixelsize; pixelsizeHeight++, offset += img_width) {
                    for (int x = 0; x < height; x++) {
                        int color = result.get(x, y) ? BLACK : WHITE;

                        // scaling pixel width
                        for (int pixelsizeWidth = 0; pixelsizeWidth < pixelsize; pixelsizeWidth++) {
                            pixels[offset + x * pixelsize + pixelsizeWidth] = color;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(img_width, img_height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, img_height, 0, 0, img_width, img_height);
            return bitmap;
        } else {
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }
}