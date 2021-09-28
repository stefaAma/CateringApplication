package businesslogic.user;

public class UserManager {
    private User currentUser;

    public void trueLogin(String username, String password) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = User.loadUser(username, password);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

}
