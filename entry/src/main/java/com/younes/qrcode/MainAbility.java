package com.younes.qrcode;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main);
        initUI();
    }

    private void initUI() {
        Button btn = (Button) findComponentById(ResourceTable.Id_btn);
        btn.setClickedListener(v -> {
            showQrCode();
        });
    }

    private void showQrCode() {
        QrCodeAbility.startAbility(this, "qr code content");
    }
}
