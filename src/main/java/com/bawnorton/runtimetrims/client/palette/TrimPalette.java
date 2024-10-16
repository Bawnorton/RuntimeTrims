package com.bawnorton.runtimetrims.client.palette;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.colour.ColourInterpolation;
import com.bawnorton.runtimetrims.client.colour.OkLabHelper;
import com.google.common.collect.Lists;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TrimPalette {
    public static final TrimPalette DEFAULT = new TrimPalette(ColorHelper.Argb.getArgb(255, 255, 255, 255));
    public static final int PALETTE_SIZE = 8;
    private final List<Integer> staticColours;
    private final List<Integer> animatedColours;
    private long lastCycle;
    private int[] colourArr;

    public TrimPalette(List<Integer> colours) {
        if (colours.size() != PALETTE_SIZE) {
            throw new IllegalArgumentException("Trim palette requires exactly %s colours, but %s were found.".formatted(PALETTE_SIZE, colours.size()));
        }
        this.staticColours = colours;
        this.animatedColours = new ArrayList<>();

        computeInterpolation();
    }

    public TrimPalette(int singleColour) {
        this(Util.make(new ArrayList<>(), colours -> {
            for (int i = 0; i < PALETTE_SIZE; i++) {
                colours.add(singleColour);
            }
        }));
    }

    public void computeInterpolation() {
        List<Integer> interpolated = ColourInterpolation.interpolateColours(staticColours);
        animatedColours.clear();
        animatedColours.addAll(interpolated);
        computeColourArr();
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(PALETTE_SIZE, 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < PALETTE_SIZE; i++) {
            image.setRGB(i, 0, (255 << 24) | staticColours.get(i));
        }
        return image;
    }

    public InputStream toInputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = toBufferedImage();
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public int[] getColourArr() {
        return colourArr;
    }

    public void computeColourArr() {
        List<Integer> reversed = Lists.reverse(getColours());
        colourArr = new int[PALETTE_SIZE];
        for (int i = 0; i < PALETTE_SIZE; i++) {
            int colour = reversed.get(i);
            colourArr[i] = colour;
        }
    }

    public List<Integer> getColours() {
        return RuntimeTrimsClient.animate ? animatedColours : staticColours;
    }

    public int getAverageColour() {
        double[][] okLabSpace = new double[PALETTE_SIZE][3];
        for (int i = 0; i < colourArr.length; i++) {
            int colour = colourArr[i];
            double[] okLab = OkLabHelper.rgbToOKLab(colour);
            okLabSpace[i] = okLab;
        }
        double[] averaged = OkLabHelper.average(okLabSpace);
        return OkLabHelper.oklabToRGB(averaged);
    }

    public void cycleAnimatedColours() {
        long now = System.currentTimeMillis();
        long delta = now - lastCycle;
        float millisPerCylce = RuntimeTrimsClient.msBetweenCycles;
        if (delta < millisPerCylce) return;

        lastCycle = now;
        int last = animatedColours.getLast();
        for (int i = animatedColours.size() - 1; i > 0; i--) {
            animatedColours.set(i, animatedColours.get(i - 1));
        }
        animatedColours.set(0, last);
        computeColourArr();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TrimPalette other) {
            return Objects.equals(staticColours, other.staticColours);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(staticColours);
    }
}
