package pro.sky.telegrambot.enums;

public enum CommandsEnum {

    START("/start"),
    INFO("/info"),
    HELP("/help"),
    SHOW_NOTIFICATIONS("/show_notifications"),
    NEXT("/next"),
    PREV("/prev"),
    EXIT("/exit"),
    EDIT("/edit"),
    DELETE("/delete");

    private final String command;

    CommandsEnum(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
