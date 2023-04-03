package com.younes.qrcode;


import com.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.media.image.PixelMap;

import java.util.concurrent.atomic.AtomicInteger;

public class QrCodeAbility extends Ability {

    public static final String TAG = QrCodeAbility.class.getSimpleName();

    public static final String EXTRA_QR_CODE = "qr_code";

    private boolean isLarge = false;
    public String qrCodeString = null;

    private Image qrCodeImage = null;
    private Text qrCodeCaption = null;

    //bitmap
    private PixelMap qrCodePixelMap = null;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        qrCodeString = intent.getStringParam(EXTRA_QR_CODE);
        initUI();
        generateQRCode();
    }

    public void initUI() {
        super.setUIContent(ResourceTable.Layout_ability__qr_code);

        qrCodeImage = (Image) findComponentById(ResourceTable.Id_qr_code_image);
        qrCodeImage.setClickedListener(c -> {
            toggleSize();
        });

        qrCodeCaption = (Text) findComponentById(ResourceTable.Id_qr_code_caption);
    }


    /**
     * change QR code size to fill entire screen
     * If the QR is full screen, then reset size to default
     */
    private void toggleSize() {
        this.isLarge = !isLarge;

        qrCodeImage.setComponentSize(getQrCodeSizePx(isLarge), getQrCodeSizePx(isLarge));
        //hide caption (text) if qr code is going full screen
        if (isLarge)
            qrCodeCaption.setVisibility(Component.HIDE);
        else
            qrCodeCaption.setVisibility(Component.VISIBLE);

    }

    /**
     * display the QR code
     * called after converting {@link #qrCodeString} to a pixelMap (bitmap)
     */
    private void showQrCode() {
        getUITaskDispatcher().syncDispatch(() -> {
            qrCodeImage.setPixelMap(qrCodePixelMap);
        });
    }

    /**
     * use QRCodeEncoder library to convert {@link #qrCodeString} to a QR code in form of a pixelMap (bitmap)
     */
    private void generateQRCode() {
        AtomicInteger size = new AtomicInteger(getQrCodeSizePx(false));

        new Thread(() -> {
            qrCodePixelMap = QRCodeEncoder.syncEncodeQRCode(qrCodeString, size.get());

            showQrCode();
        }).start();
    }

    /**
     * @return int representing QR size in pixels
     */
    private int getQrCodeSizePx(Boolean isLarge) {

        //density pixel size
        int sizeVp = isLarge ? 175 : 150;
        return AttrHelper.vp2px(sizeVp, this);

    }


    /**
     * start this ability from anywhere by passing an ability and the string to be encoded as QR code
     */
    public static void startAbility(Ability startingAbility, String qrCodeString) {
        Intent intent = new Intent();
        intent.setParam(QrCodeAbility.EXTRA_QR_CODE, qrCodeString);

        Operation build = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(startingAbility.getBundleName())
                .withAbilityName(QrCodeAbility.class)
                .build();

        intent.setOperation(build);

        startingAbility.startAbility(intent);
    }
}


