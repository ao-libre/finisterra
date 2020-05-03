package shared.util;

public class UserSystemUtilities {

    private String msgErrorValid;


    public boolean userNameIsValid(String userName){
        Boolean answer = false;
        if (userNameIsNumeric(userName)) {
            msgErrorValid = "El nombre del personaje no puede ser nuemerico";
            //si existe algun caracter prohibido
        } else if (!userNameIsNormalChar(userName)) {
            msgErrorValid = "El nombre del personaje contiene caracteres invalidos, recuerde no usar {}/*:.,;[]";
        } else if (userNameIsOfensive(userName)){
            msgErrorValid = "El nombre del perosonaje elegijo se encuentra prohibido";
        } else if (userNameIsStartNumeric(userName)) {
            msgErrorValid = "El nombre del personaje no puede iniciar con nuemeros";
        } else {
            return true;
        }

        return false;
    }
    public boolean userNameIsNumeric(String userName){
        return userName.matches("-?\\d+(\\.\\d+)?");
    }

    public boolean userNameIsNormalChar(String userName){
        return  userName.matches("(\\w)+");
    }

    public  boolean userNameIsStartNumeric(String userName){
       return Character.isDigit(userName.charAt(0));
    }

    public boolean userNameIsOfensive(String userName){
        //TODO leer lista de nombres ofensivos o reservados
        return false;
    }

    public String getMsgErrorValid(){
        return this.msgErrorValid;
    }
}
