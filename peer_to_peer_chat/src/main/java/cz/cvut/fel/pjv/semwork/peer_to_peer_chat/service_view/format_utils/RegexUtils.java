package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils;

public class RegexUtils {
    /**
     * Checks whether the given string contains
     * Letters: a–z, A–Z
     * Digits: 0–9
     * Underscore: _
     * Dot: . (not at the start or end)
     * Hyphen: - (not at the start or end)

     * @param input the string to be checked
     * @return {@code true} if the string matches the above criteria, {@code false} otherwise
     */
    public static boolean usernameCheck(String input) {
        return input.matches("^(?=.{3,20}$)(?![.-])[a-zA-Z0-9._-]+(?<![.-])$");
    }

    /**
     * Returns string with all enters removed.
     *
     * @param text the string to be converted
     */
    public static String removeNewlines(String text) {
        return text.replaceAll("\\n+$", "");
    }
}
