package com.bawnorton.runtimetrims.client.compat;

import com.bawnorton.runtimetrims.client.compat.iris.IrisCompat;
import com.bawnorton.runtimetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.runtimetrims.platform.Platform;
import java.util.Optional;

public final class Compat {
    private static IrisCompat irisCompat;
    private static ShowMeYourSkinCompat showMeYourSkinCompat;

    public static Optional<IrisCompat> getIrisCompat() {
        if (!Platform.isModLoaded("iris")) return Optional.empty();
        if (irisCompat == null) irisCompat = new IrisCompat();

        return Optional.of(irisCompat);
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        if (!Platform.isModLoaded("showmeyourskin")) return Optional.empty();
        if (showMeYourSkinCompat == null) showMeYourSkinCompat = new ShowMeYourSkinCompat();

        return Optional.of(showMeYourSkinCompat);
    }
}
