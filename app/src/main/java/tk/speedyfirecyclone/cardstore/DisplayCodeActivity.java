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
    public static BarcodeFormat format = null;

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
            Bitmap bitmapimage = encodeAsBitmap(barcodeString, formatTranslator(formatString), 600, 400);
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

        if (formatString.equals("AZTEC")) {
            format = BarcodeFormat.AZTEC;
        } else if (formatString.equals("CODABAR")) {
            format = BarcodeFormat.CODABAR;
        } else if (formatString.equals("CODE_39")) {
            format = BarcodeFormat.CODE_39;
        } else if (formatString.equals("CODE_93")) {
            format = BarcodeFormat.CODE_93;
        } else if (formatString.equals("CODE_128")) {
            format = BarcodeFormat.CODE_128;
        } else if (formatString.equals("DATA_MATRIX")) {
            format = BarcodeFormat.DATA_MATRIX;
        } else if (formatString.equals("EAN_8")) {
            format = BarcodeFormat.EAN_8;
        } else if (formatString.equals("EAN_13")) {
            format = BarcodeFormat.EAN_13;
        } else if (formatString.equals("ITF")) {
            format = BarcodeFormat.ITF;
        } else if (formatString.equals("PDF_417")) {
            format = BarcodeFormat.PDF_417;
        } else if (formatString.equals("QR_CODE")) {
            format = BarcodeFormat.QR_CODE;
        } else if (formatString.equals("RSS_14")) {
            format = BarcodeFormat.RSS_14;
        } else if (formatString.equals("RSS_EXPANDED")) {
            format = BarcodeFormat.RSS_EXPANDED;
        } else if (formatString.equals("UPC_A")) {
            format = BarcodeFormat.UPC_A;
        } else if (formatString.equals("UPC_E")) {
            format = BarcodeFormat.UPC_E;
        } else if (formatString.equals("UPC_EAN_EXTENSION")) {
            format = BarcodeFormat.UPC_EAN_EXTENSION;
        } else {
            //Safety measure to avoid null pointer exception.
            format = BarcodeFormat.QR_CODE;
            FirebaseCrash.report(new Exception("Unknown Barcodeformat for translation"));
        }

        return format;
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
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            FirebaseCrash.report(iae);
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}