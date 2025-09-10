package functionality;

public class BotCommands {
    public BotCommands() {};

    public String helpCommand() {
        return "/quizua <number of questions> - for making quiz from ukrainian to polish (default value 12) \n" +
                "/quizpl <number of questions> - for making quiz from polish to ukrainian (default value 12)";
    }
}
