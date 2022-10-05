package Server.Communication;

import Server.User.User;

public class Question {

    protected final User user; 
    protected final Callback callback;

    private String question;

    public Question(String question, User user,Callback callback) {
        this.user = user;
        this.callback = callback;

        this.question = question;
        this.ask();
    }

    public Question(String question, User user, Callback callback, boolean ask) {
        this.user = user;
        this.callback = callback;

        this.question = question;
        if(ask) this.ask();
    }


    public void ask() {
        user.send(this.question);
        this.answer();
    }

    private void answer() {
        this.callback.answer(this.user.lisen());
    }

    public interface Callback {
        public void answer(String message);
    }

    public Question(User user, Callback callback) {
        this.user = user;
        this.callback = callback;
    }

}