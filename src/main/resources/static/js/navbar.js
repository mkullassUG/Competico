const NavbarLogic = (accountInfo_, debug = false, $jq, myWindow) => {
    
    /* environment preparation */
    if ( $jq )
        $ = $jq;
    if ( myWindow )
        window = myWindow;

    /*       logic variables          */
    var self = accountInfo_;
    self.nickname;
    self.username;
  
    /*       logic functions          */
    self.navbarLogic = (accountInfo) => {
    
        self.nickname = accountInfo.nickname;
        self.username = accountInfo.username;
        self.email = accountInfo.email;
        self.roles = accountInfo.roles?accountInfo.roles:[];
        self.authenticated = accountInfo.authenticated?true:(self.nickname?true:false);
    
        //$("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")

        //navbar preparation
        if ( self.roles.includes("SWAGGER_ADMIN"))
            $("#swaggerHyperlink").show();
        if ( self.roles.includes("TASK_DATA_ADMIN"))
            $("#taskDataHyperlink").show();
        if ( self.roles.includes("ACTUATOR_ADMIN"))
            $("#actuatorHyperlink").show();

        if ( self.authenticated ) {
            $("#registerHyperlink").hide();
            $("#loginHyperlink").hide();
            $("#profileHyperlink").show();
            $("#dashboardHyperlink").show();
            $("#logOutButton").show();
            $("#gameHyperlink").show();
            
        } 

        otherFrontendLogic();
    }
    
    var otherFrontendLogic = () => {
        if ( debug )
            console.log("otherFrontendLogic");
      /*  dropdown menu   */
        /*TODO 
        przerobić tak żeby działało jak ja chce przy dynamicznie zmieniającym się ekranie (chyba moge usunąć TODO, bo zrobiłem wysuwanie na połowe ekranu i konetant teżsię ładnie kurczy)*/
        // Prevent closing from click inside dropdown

        $(window.document).on('click',(e) => {
            $(".focusOn").removeClass("focusOn");
        });

        $("#main_nav").on('click', '.dropdown-menu', function (e) {
            e.stopPropagation();
        });
      
        // make it as accordion for smaller screens
        //BUG 2021-04-24 zapętla się jak submenu odpalam i chowam, tylko niektóre "a" chowają całe menu
        var takeCareOfFocusOnElementInDropdownMenu = () => {
            if ( $(".focusOn").next('.submenu').length) {
                $(".focusOn").next('.submenu').css({"display":""});
            }
            $(".focusOn").removeClass("focusOn");
        }

        $('.dropdown-menu a').click(function(e){
            
            if ($(".focusOn").is($(this))) {
                takeCareOfFocusOnElementInDropdownMenu();
                $('.submenu').attr("style","");
            } else {

                takeCareOfFocusOnElementInDropdownMenu();
                $(this).addClass("focusOn");
                if ( $(this).next('.submenu').length ) {
                    $('.submenu').css({"display":"none"});
                    $(this).next('.submenu').attr("style","");
                }
            }

            if ($(this).hasClass("focusOn") &&  $(this).next('.submenu').length){
                $(this).next('.submenu').show();
                $(this).next('.submenu').css({"display":"block"});
            } else if ( $(this).next('.submenu').length ) {
                $(this).next('.submenu').hide();
                $(this).next('.submenu').css({"display":""});
            } 
            // else {
            //     $('.submenu').attr("style","");
            // }

            $('.dropdown').one('hide.bs.dropdown', function (e) {
                $('.submenu').attr("style","");
            })
        });
    }

    /* listeners */
    
    /*  initalization  */
    self.navbarLogic(accountInfo_);
    
    return self;
}
  
NavbarLogic.getInstance = (debug = false, $jq, myWindow) => {
    
    if ( $jq )
        $ = $jq;
    if ( myWindow )
        window = myWindow;

    if (NavbarLogic.singleton)
      return NavbarLogic.singleton;
  
    var ajaxReceiveAccountInfo = ( ) => {
        
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/account/info",
            // url      : "/api/v1/playerinfo",
            contentType: "application/json",
            success: function(accountInfo, textStatus, jqXHR) {

                if (debug){
                    console.log("ajaxReceiveAccountInfo success");
                    console.log(typeof accountInfo);
                    console.log(accountInfo);
                    console.log(textStatus);
                    console.log(jqXHR);
                }
                if (typeof accountInfo == 'string') //nie zalogowany
                    NavbarLogic.singleton = NavbarLogic({}, debug, $, window);
                else //zalogowany
                    NavbarLogic.singleton = NavbarLogic(accountInfo, debug, $, window);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveAccountInfo error");
                }
            }
        });
    }
    
    ajaxReceiveAccountInfo();
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {NavbarLogic};