package me.syldium.thimble.service;

import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.service.ScoreboardService;
import me.syldium.thimble.common.service.ScoreboardServiceImpl;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ConstantConditions")
public class ScoreboardServiceTest {

    @Test
    public void render() {
        List<String> lines = List.of("Points: <points>", "<points>");
        ScoreboardService service = new ScoreboardServiceImpl("Thimble", lines);

        InGamePlayer player = new InGamePlayer(UUID.randomUUID(), "playerName", null, null);
        assertComponentsEquals(List.of("Points: " + player.points(), String.valueOf(player.points())), service.render(player));

        player.stats(2, 4);
        assertComponentsEquals(List.of("Points: " + player.points(), String.valueOf(player.points())), service.render(player));
    }

    @Test
    public void noPlaceholder() {
        List<String> lines = List.of("Stuff to read");
        ScoreboardService service = new ScoreboardServiceImpl("Thimble", lines);
        assertComponentsEquals(List.of("Stuff to read"), service.render(null));
    }

    private static void assertComponentsEquals(@NotNull List<String> expected, @NotNull List<Component> actual) {
        assertEquals(expected, actual.stream().map(component -> plainText().serialize(component)).collect(Collectors.toList()));
    }
}
