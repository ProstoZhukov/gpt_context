package ru.tensor.sbis.design.cloud_view.content.quote;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 * Реализация ClickableSpan для обработки нажатий на цитаты
 *
 * @author ma.kolpakov
 */
public class QuoteClickSpan extends ClickableSpan {
    private final UUID quotedMessageUuid;
    private final QuoteClickListener listener;

    public QuoteClickSpan(UUID quotedMessageUuid, QuoteClickListener listener) {
        this.quotedMessageUuid = quotedMessageUuid;
        this.listener = listener;
    }

    @Override
    public void onClick(@NotNull View widget) {
        listener.onQuoteClicked(quotedMessageUuid);
    }

    @Override
    public void updateDrawState(@NotNull TextPaint ds) {
        // оставить пустым, чтоб спан не переопределял стили
    }
}
