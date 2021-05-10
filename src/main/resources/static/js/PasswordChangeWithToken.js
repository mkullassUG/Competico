const PasswordChangeWithToken = (debug = false) => {
  
    /*       logic variables          */
    var self = {};
    if ( PasswordChangeWithToken.singleton )
        return PasswordChangeWithToken.singleton;
    else
        PasswordChangeWithToken.singleton = self;

    var plc = PageLanguageChanger();
    var av = AccountValidation();
    

    var PasswordChangeWithTokenInit = () => {

        $(document).ready(function(){

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
    }

    var badUsernameOrEmail = () => {
        if (debug)
            console.log("badUsernameOrEmail");

        $("#invalidEmailOrUsernamefeedback").show();
        setTimeout(function(){$("#invalidEmailOrUsernamefeedback").fadeOut()},4000);
    }

    var tokenSendSuccess = (data) => {
        if (debug)
            console.log("tokenSendSuccess");

        //zmiana tekstu na wysłano token na adres axxx@
        if (debug)
            console.log("email: " + data);

        $("#mainTextResetPasswordRequest").text(plc.getTextFor("#mainTextResetPasswordRequest.sendDone", null, isOther = false));
        $("#sendEmailOrUsernameBtn").text(plc.getTextFor("#sendEmailOrUsernameBtn.sendDone", null, isOther = false));
        $("#mainTextResetPasswordRequest").addClass("sendDone");
        $("#sendEmailOrUsernameBtn").addClass("sendDone");

        $("#inputEmailOrUsername").css("color",'lime');
        setTimeout(function(){$("#inputEmailOrUsername").css("color",'initial')},5000);
    }

    var codeHandler = (data) => {
        if (debug)
            console.log("codeHandler");

        switch (data) {
            case "ACCOUNT_NOT_PRESENT":
                console.warn("code ACCOUNT_NOT_PRESENT");

                $("#invalidAccountNotExists").show();
                setTimeout(function(){$("#invalidAccountNotExists").fadeOut()},5000);
                break;
            case "BAD_NEW_PASSWORD":
                console.warn("code BAD_NEW_PASSWORD");
                break;
            case "INVALID_TOKEN":
                console.warn("code INVALID_TOKEN");
                break;
            case "EXPIRED_TOKEN":
                console.warn("code EXPIRED_TOKEN");
                break;
            default:
                console.warn("code not recognized");
        }
    }

    var badPassword = (index = "") => {
        if (debug)
            console.log("badPassword");

        $("#invalidPassword" + index + "feedback").show();
        setTimeout(function(){$("#invalidPassword" + index + "feedback").fadeOut()},4000);
    }

    /* listeners */
    if ($('#formSendUsernameOrEmailForToken').length)
        $('#formSendUsernameOrEmailForToken').on('submit',function(e){
            e.preventDefault();

            if (debug)
                console.log('form submit');

            let usernameOrEmail =  $('#inputEmailOrUsername').val();

            var emailValid = av.emailValid(usernameOrEmail);
            var usernameValid = av.usernameValid(usernameOrEmail);

            if ( !emailValid && !usernameValid ) {
                badUsernameOrEmail();
                return;
            }

            if (debug)
                console.log(usernameOrEmail);

            self.ajaxSendUsernameOrEmailForToken(codeHandler, usernameOrEmail);
        });
    
    if ($('#formSendChangePassword').length)
        $('#formSendChangePassword').on('submit',function(e){
            e.preventDefault();

            if (debug)
                console.log('form submit');

            let password =  $('#inputNewPassword').val();
            let password2 =  $('#inputNewPassword2').val();

            var passwordValid = av.passwordValid(password);
            var passwordValid2 = av.password2Valid(password,password2);

            if ( !passwordValid ) {
                badPassword();
                return;
            }

            if ( !passwordValid2 ) {
                badPassword(2);
                return;
            }

            if (debug)
                console.log(password);

            var pathSplit = window.location.href.split("/");
            var token = pathSplit[pathSplit.length-1];

            self.ajaxSendChangePasswordWithToken(codeHandler, password, token);
        });

    self.ajaxSendUsernameOrEmailForToken = (cb, emailOrUsername) => {
        
        var send = emailOrUsername;

        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/forgotpassword",
            data     : send,
            contentType: "application/json",
            success: function(data, textStatus_, jqXHR_) {
                if (debug)
                    console.log("success forgotpassword");

                tokenSendSuccess(data);
            },
            error: function(jqXHR, status_, err_) {
                if (debug)
                    console.log("error forgotpassword");

                cb(jqXHR.responseText);
            }
        });
    }


    self.ajaxSendChangePasswordWithToken = (cb, password, token) => {
        
        var send = password;

        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/resetpassword/" + token,
            data     : send,
            contentType: "application/json",
            success: function(data, textStatus_, jqXHR_) {
                if (debug)
                    console.log("success resetpassword");
                
                location.replace("/login");
            },
            error: function(jqXHR, status_, err_) {
                if (debug)
                    console.log("error resetpassword");
                cb(jqXHR.responseText)
            }
        });
    }

    /*  initalization  */
    PasswordChangeWithTokenInit();
    
    return self;
}
