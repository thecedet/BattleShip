package Server.Communication;

import Server.User.User;

public class Choice extends Question{
    
    private final String[] choices;

    public Choice(String[] choices, User user, Callback callback) {
        super(user, callback);
        this.choices = choices;

        user.send(this.show());
        this.answer();
    }

    private void answer() {
        int answer = Integer.parseInt(this.user.lisen());
        if(answer > 0 && answer <= this.choices.length) {
            this.callback.answer(String.valueOf(answer));
        }else {
            user.send(this.show());
            this.answer();
        }
    }

    public String show() {
        String result = "";
        for(int i = 0; i < this.choices.length; i++) {
            result += "["+(i+1)+"] " + this.choices[i] + "\\n";
        }

        return result + "Votre choix: ";
    }

}
