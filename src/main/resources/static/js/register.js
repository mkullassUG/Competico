
$(document).ready(function(){

    //NEW!!!!!!
    if ( typeof PageLanguageChanger != "undefined")
        PageLanguageChanger();
        
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
        
        //taką samąwalicaje musze mieć w profilu, przydał by się osobny obiekt od tego [AccountValidation w pliku accountValidation]
        let validation = AccountValidation().validateData(send);
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

    var tooltipsUpdate = () => {
        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }
    
    tooltipsUpdate();
    
    

    NavbarLogic.getInstance();

    //new bug fix
    var resizeWindow = () => {
    
        $("html").height("100%");
        $("html").height($(document).height());
    } 
	window.onresize = resizeWindow;
	resizeWindow();
});

//taką samąwalicaje musze mieć w profilu, przydał by się osobny obiekt od tego


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
