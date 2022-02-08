package im.juniq.telegram_handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

class TelegramHandlerTest {
    public static final String TEST_TELEGRAM_WITH_KOREAN = "12345testname  한글 321";

    @Test
    void 전문핸들러_생성() {
        assertThatCode(() -> new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN)).doesNotThrowAnyException();
    }

    @Test
    void 전문길이_비교_UTF8() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        assertThat(telegramHandler.isLengthOf(25)).isEqualTo(0);
        assertThat(telegramHandler.isLengthOf(26)).isEqualTo(1);
        assertThat(telegramHandler.isLengthOf(24)).isEqualTo(-1);
    }

    @Test
    void 전문길이_비교_EUCKR() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN, Charset.forName("EUC-KR"));

        assertThat(telegramHandler.isLengthOf(23)).isEqualTo(0);
        assertThat(telegramHandler.isLengthOf(24)).isEqualTo(1);
        assertThat(telegramHandler.isLengthOf(22)).isEqualTo(-1);
    }

    @Test
    void 특정필드_가져오기() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        assertThat(telegramHandler.field(1, 5)).isEqualTo("12345");
        assertThat(telegramHandler.field(6, 13)).isEqualTo("testname");
        assertThat(telegramHandler.field(1, 1)).isEqualTo("1");
        assertThat(telegramHandler.field(6, 6)).isEqualTo("t");
        assertThat(telegramHandler.field(16, 21)).isEqualTo("한글");
        assertThat(telegramHandler.field(23, 25)).isEqualTo("321");
    }

    @Test
    void 특정필드_가져오기_EUCKR() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN, Charset.forName("EUC-KR"));

        assertThat(telegramHandler.field(1, 5)).isEqualTo("12345");
        assertThat(telegramHandler.field(6, 13)).isEqualTo("testname");
        assertThat(telegramHandler.field(1, 1)).isEqualTo("1");
        assertThat(telegramHandler.field(6, 6)).isEqualTo("t");
        assertThat(telegramHandler.field(16, 19)).isEqualTo("한글");
        assertThat(telegramHandler.field(21, 23)).isEqualTo("321");
    }

    @Test
    void 특정필드_가져올때_전문길이_예외() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        assertThatThrownBy(() -> telegramHandler.field(6, 26)).isInstanceOf(RuntimeException.class)
            .hasMessage("endIndex가 전문길이보다 큽니다. telegram length: 25, bedinIndex: 6, endIndex: 26");
        assertThatThrownBy(() -> telegramHandler.field(0, 25)).isInstanceOf(RuntimeException.class)
            .hasMessage("beginIndex는 0보다 커야합니다. telegram length: 25, bedinIndex: 0, endIndex: 25");
        assertThatThrownBy(() -> telegramHandler.field(2, 1)).isInstanceOf(RuntimeException.class)
            .hasMessage("endIndex는 beginIndex보다 커야합니다. telegram length: 25, bedinIndex: 2, endIndex: 1");
    }

    @Test
    void 특정필드_값_바꾸기() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        telegramHandler.changeField(1, 5, "54321");

        assertThat(telegramHandler.field(1, 5)).isEqualTo("54321");
        assertThat(telegramHandler.field(6, 13)).isEqualTo("testname");
    }

    @Test
    void 특정필드_값_바꾸기2() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        telegramHandler.changeField(6, 9, "eeee");

        assertThat(telegramHandler.field(1, 5)).isEqualTo("12345");
        assertThat(telegramHandler.field(6, 13)).isEqualTo("eeeename");
    }

    @Test
    void 특정필드_값_바꾸기_필드길이_예외() {
        TelegramHandler telegramHandler = new TelegramHandler(TEST_TELEGRAM_WITH_KOREAN);

        assertThatThrownBy(() -> telegramHandler.changeField(1, 5, "5432")).isInstanceOf(RuntimeException.class)
            .hasMessage("새로운 필드의 길이는 이전 필드의 길이와 같아야 합니다.");
    }
}
