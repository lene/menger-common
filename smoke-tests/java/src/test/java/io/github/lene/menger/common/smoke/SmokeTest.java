package io.github.lene.menger.common.smoke;

import menger.common.Color;
import menger.common.ImageSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SmokeTest {

    @Test
    void colorConstructsAndReturnsComponents() {
        Color color = new Color(0.5f, 0.3f, 0.7f, 1.0f);
        assertEquals(0.5f, color.r(), 1e-6f);
        assertEquals(0.3f, color.g(), 1e-6f);
        assertEquals(0.7f, color.b(), 1e-6f);
        assertEquals(1.0f, color.a(), 1e-6f);
    }

    @Test
    void imageSizeConstructsAndReturnsComponents() {
        ImageSize size = new ImageSize(1920, 1080);
        assertEquals(1920, size.width());
        assertEquals(1080, size.height());
    }

    @Test
    void colorRejectsOutOfRangeValues() {
        assertDoesNotThrow(() -> new Color(0.0f, 0.0f, 0.0f, 0.0f));
        assertDoesNotThrow(() -> new Color(1.0f, 1.0f, 1.0f, 1.0f));
    }
}
