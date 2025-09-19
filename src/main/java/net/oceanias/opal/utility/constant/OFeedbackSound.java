package net.oceanias.opal.utility.constant;

import org.bukkit.Sound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@Getter
@RequiredArgsConstructor
public enum OFeedbackSound {
    SUCCESS(Sound.BLOCK_NOTE_BLOCK_BELL),
    ERROR(Sound.BLOCK_NOTE_BLOCK_BASS),
    CLICK(Sound.UI_BUTTON_CLICK);

    private final Sound delegate;
}