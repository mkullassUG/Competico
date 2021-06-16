const AccountValidation = () => {

    if (AccountValidation.singleton)
        return AccountValidation.singleton;

    var self = {};
    if (!AccountValidation.singleton)
        AccountValidation.singleton = self;
        
    self.validateData = (data) => {
        
        let username = password = password2 =  email = valid = true;
      
        if (!self.emailValid(data.email)) {
            email = false;//"Niepoprawny email";
            valid = false;
        }
      
        if (!self.usernameValid(data.username)) {
            username = false;//"Niepoprawny username, min 8 znaków, max 32, bez znaków specjalnych";
            valid = false;
        }
      
        if (!self.passwordValid(data.password)) {
            password = false;//"Niepoprawne hasło, wzór: minimum 8 znaków, conajmniej jedna duża litera i conajmniej jedna cyfra";
            valid = false;
        }
      
        if (!self.password2Valid(data.password,data.password2)) {
            password2 = false;//"Hasła nie są identyczne"});
            valid = false;
        }
      
        return {valid: valid, username: username, password: password, password2: password2, email: email};
        
    }

    self.emailValid = (data) => {
        if (!data.match(/(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/)) 
            return false;
        else
            return true;
    }

    self.usernameValid = (data) => {
        if (data.match(/^(?=.{4,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$/)) 
            return true
        else 
            return false
    }

    self.nicknameValid = (data) => {
        if (data.match(/^(?:[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ!#$%^&\(\)_+}{":?|~`]|(?:\u00a9|\u00ae|[\uD83C-\uDBFF\uDC00-\uDFFF])){3,32}$/)) 
        // if (data.match(/^(?=.{3,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$/)) 
            return true;
        else 
            return false;
    }

    self.passwordValid = (data) => {
        if (data.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$/))
            return true;
        else
            return false;
    }

    self.password2Valid = (pass1, pass2) => {
        if (pass1 === pass2)
            return true;
        else
            return false;
    }

    return self;
}

AccountValidation.getInstance = () => {

    if (!AccountValidation.singleton)
        AccountValidation.singleton = AccountValidation();

    return AccountValidation.singleton;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {AccountValidation};