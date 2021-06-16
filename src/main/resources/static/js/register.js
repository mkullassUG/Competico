//problem taki ze to nei singleton już
const RegisterModule = (function($) {

    const RegisterLogic = (debug = false, depMocks = {}, successfulCreationCallback) => {
    
        /*  singleton   */
        if (RegisterLogic.singleton)
            return RegisterLogic.singleton;
        var self = {};
        if (!RegisterLogic.singleton)
            RegisterLogic.singleton = self;
    
        /*  environment preparation  */
        // $ = typeof $ != 'undefined'? $ : depMocks.$mock;
        window =  typeof window != 'undefined'? window : depMocks.windowMock;
        if ( depMocks.NavbarLogic && typeof NavbarLogic == "undefined")
            NavbarLogic = depMocks.NavbarLogic;
        if ( depMocks.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
            PageLanguageChanger = depMocks.PageLanguageChanger;
            
        /*       logic variables          */
        self.name = "RegisterLogic";
        /*       logic functions          */
        var RegisterLogicInit = () => {
            
            if ( debug )
                console.log("RegisterLogicInit")

            if ( typeof NavbarLogic != "undefined" )
                NavbarLogic.getInstance(false, debug);
    
            if ( typeof PageLanguageChanger != "undefined")
                PageLanguageChanger();
            
            resizeWindow();
            listenersSetup();
            tooltipsUpdate();

            if (successfulCreationCallback)
                successfulCreationCallback(true);
        }
    
        var listenersSetup = () => {
    
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
    
            window.onresize = resizeWindow;
        }
    
        var tooltipsUpdate = () => {
            if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
                $('[data-toggle="tooltip"]').tooltip({
                    trigger : 'hover'
                });  
        }
    
        var resizeWindow = () => {
        
            $("html").height("100%");
            function isInt(n) {
                return n % 1 === 0;
            }
            
            var h1 = $(window.document).height();
            if ( isInt (h1))
                h1 -= 1;
            $("html").height(h1);
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
    
        RegisterLogicInit();
    
        return self;
    }
    
    RegisterLogic.getInstance = (debug = false, depMocks = {}, successfulCreationCallback) => {
    
        if (RegisterLogic.singleton)
            return RegisterLogic.singleton;
    
        RegisterLogic.singleton = RegisterLogic(debug, depMocks, successfulCreationCallback);
        
        return RegisterLogic.singleton;
    }

    return {
        getInstance: RegisterLogic.getInstance,
        RegisterLogic: RegisterLogic
    }
})




if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {RegisterModule};