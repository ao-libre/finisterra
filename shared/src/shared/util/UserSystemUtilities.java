package shared.util;


public class UserSystemUtilities {


    public boolean userNameIsNumeric(String userName) {
        return userName.matches("-?\\d+(\\.\\d+)?");
    }

    public boolean userNameIsNormalChar(String userName) {
        return userName.matches("(\\w)+");
    }

    public boolean userNameIsStartNumeric(String userName) {
        return Character.isDigit(userName.charAt(0));
    }

    public boolean userNameIsOfensive(String userName) {
        //TODO leer lista de nombres ofensivos o reservados
        return false;
    }


}
