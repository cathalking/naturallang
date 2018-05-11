package ck.apps.leabharcleachtadh.games.domain;

public enum UserInput {
    SKIP("N"),
    SHOW_SUMMARY("P"),
    QUIT("Q"),
    UNKNOWN("N/A");

    private String command;

    UserInput(String command) {
        this.command = command;
    }

    public static UserInput from(String instruction) {
        for (UserInput ui : UserInput.values()) {
            if (ui.command.equals(instruction.toUpperCase())) {
                return ui;
            }
        }
        return UNKNOWN;
    }
}
