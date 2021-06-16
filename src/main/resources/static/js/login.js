const LoginLogic = (debug = false, depMocks = {}, successfulCreationCallback) => {

    /*  singleton   */
    if (LoginLogic.singleton)
        return LoginLogic.singleton;
    var self = {};
    if (!LoginLogic.singleton)
        LoginLogic.singleton = self;

    /*  environment preparation  */
    $ = typeof $ != 'undefined'? $ : depMocks.$mock;
    window =  typeof window != 'undefined'? window : depMocks.windowMock;
    if ( depMocks.NavbarLogic && typeof NavbarLogic == "undefined")
        NavbarLogic = depMocks.NavbarLogic;
    if ( depMocks.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
        PageLanguageChanger = depMocks.PageLanguageChanger;

    var LoginLogicInit = () => {

        if ( typeof PageLanguageChanger != "undefined")
        PageLanguageChanger();

        tooltipsUpdate();
    
        NavbarLogic.getInstance();
        listenersSetup();
        resizeWindow();
    }

    /*  listeners   */
    var listenersSetup = () => {

        $('form').on('submit',function(e){
            e.preventDefault();
            //console.log(this)
    
    
            let send = {
                email: $('form')[0][0].value,
                    password: $('form')[0][1].value,
            };
            //console.log(JSON.stringify(send))
            $.ajax({
                type     : "POST",
                cache    : false,
                url      : "api/v1/login/",
                data     : JSON.stringify(send),
                contentType: "application/json",
    
                success: function(data, textStatus, jqXHR) {
                    location.replace("dashboard");
                },
                error: function(jqXHR, status, err) {
                    $(".invalid-feedback").show();
                }
            });
        });
    
        $(".input-group-append .btn").on('click', function(event) {
            event.preventDefault();
            if($('#inputPassword').attr("type") == "text"){
                $('#inputPassword').attr('type', 'password');
                $('.btn i').addClass( "fa-eye-slash" );
                $('.btn i').removeClass( "fa-eye" );
            }else if($('#inputPassword').attr("type") == "password"){
                $('#inputPassword').attr('type', 'text');
                $('.btn i').removeClass( "fa-eye-slash" );
                $('.btn i').addClass( "fa-eye" );
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

    LoginLogicInit()

    return self;
}

LoginLogic.getInstance = (debug = false, depMocks = {}, successfulCreationCallback) => {

    if (LoginLogic.singleton)
        return LoginLogic.singleton;

    LoginLogic.singleton = LoginLogic(debug, depMocks, successfulCreationCallback);
    
    return LoginLogic.singleton;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {LoginLogic};

