
$(document).ready(function(){
    $('form').on('submit',function(e){

        e.preventDefault();

        let isPlayer = true;
        if ( $('form')[0][5].checked )
            isPlayer = false;

        let send = {
            email: $('form')[0][0].value,
            username: $('form')[0][1].value,
            password: $('form')[0][2].value,
            password2: $('form')[0][3].value,
            isPlayer: isPlayer, 
        };
        $("#usernamefeedback").hide();
        $("#password2feedback").hide();
        $("#passwordfeedback").hide();
        $("#emailfeedback").hide(); 
        
        //taką samąwalicaje musze mieć w profilu, przydał by się osobny obiekt od tego
        let validation = validateData(send);
        $("#accountExistValidation").hide();
        $("#passwordValidation").hide();
        $("#emailValidation").hide();
        $("#usernameValidation").hide();
        if (validation.valid)
            $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/register/",
            data     : JSON.stringify(send),
            contentType: "application/json",
            success: function(_data, _textStatus, _jqXHR) {
                registerAction(true);
            },

            error: function(jqXHR, _textStatus, _err) {
                $("#accountExistValidation").show();
                registerAction(jqXHR.responseText);
            }
          });
      else {
          if (!validation.username)
              $("#usernamefeedback").show();
          if (!validation.password2)
              $("#password2feedback").show();
          if (!validation.password)
              $("#passwordfeedback").show();
          if (!validation.email)
              $("#emailfeedback").show();
        }
        
    });
  
    $('[data-toggle="tooltip"]').tooltip();
    
    NavbarLogic.getInstance();
});

//taką samąwalicaje musze mieć w profilu, przydał by się osobny obiekt od tego
var validateData = (data) => {

  //https://getbootstrap.com/docs/4.0/components/popovers/
  
  let emailValid = (data) => {
    if (!data.match(/(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/)) 
      return false;
    else
      return true;
  }
  let username = password = password2 =  email = valid = true;

  let usernameValid = (data) => {
    if (data.match(/^(?=.{4,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$/)) 
      return true
    else 
      return false

  }

  let passwordValid = (data) => {
    if (data.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$/))
      return true;
    else
      return false;
  }

  let password2Valid = (pass1, pass2) => {
    if (pass1 === pass2)
      return true;
    else
      return false;
  }

  
  if (!emailValid(data.email)) {
    email = false;//"Niepoprawny email";
    valid = false;
  }

  if (!usernameValid(data.username)) {
    username = false;//"Niepoprawny username, min 8 znaków, max 32, bez znaków specjalnych";
    valid = false;
  }

  if (!passwordValid(data.password)) {
    password = false;//"Niepoprawne hasło, wzór: minimum 8 znaków, conajmniej jedna duża litera i conajmniej jedna cyfra";
    valid = false;
  }

  if (!password2Valid(data.password,data.password2)) {
    password2 = false;//"Hasła nie są identyczne"});
    valid = false;
  }

  return {valid: valid, username: username, password: password, password2: password2, email: email};
  
}

var registerAction = (data) => {

  if (data === true) {
      location.replace("dashboard");
  } else if (data === "BAD_USERNAME") {
      $("#usernameValidation").show();
  } else if (data === "BAD_EMAIL") {
      $("#emailValidation").show();
  } else if (data === "BAD_PASSWORD") {
      $("#passwordValidation").show();
  } else if (data === "DATA_ALREADY_USED") {
      $("#accountExistValidation").show();
  } else {
      console.warn("Coś poszło nie tak. Takiego kodu nie obsłużę.")
  }
}
