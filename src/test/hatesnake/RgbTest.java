package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RgbTest {

    @Test
    void recordExposesComponents() {
        Rgb c = new Rgb(10, 20, 30);
        assertThat(c.red()).isEqualTo(10);
        assertThat(c.green()).isEqualTo(20);
        assertThat(c.blue()).isEqualTo(30);
    }

    @ParameterizedTest
    @CsvSource({"-1,0,0", "0,-1,0", "0,0,-1", "256,0,0", "0,256,0", "0,0,256"})
    void rejectsOutOfRangeComponents(int r, int g, int b) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Rgb(r, g, b));
    }
}
