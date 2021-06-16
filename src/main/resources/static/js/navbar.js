const NavbarLogic = (data, debug = false, depMocks = {}, successfulCreationCallback) => {
    
    /*  singleton   */
    if (NavbarLogic.singleton)
        return NavbarLogic.singleton;
    var self = {};
    if (!NavbarLogic.singleton && data)
        NavbarLogic.singleton = self;
    else if (!NavbarLogic.singleton && !data)
        return NavbarLogic.getInstance(false, debug, depMocks, successfulCreationCallback);

    /*  environment preparation  */
    $ = typeof $ != 'undefined'? $ : depMocks.$mock;
    window =  typeof window != 'undefined'? window : depMocks.windowMock;
    
    /*       logic variables          */
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;

    /*       logic functions          */
    var NavbarLogicInit = (accountInfoData) => {
    
        self.nickname = accountInfoData.nickname;
        self.username = accountInfoData.username;
        self.email = accountInfoData.email;
        self.roles = accountInfoData.roles?accountInfoData.roles:[];
        self.authenticated = accountInfoData.authenticated?true:(self.nickname?true:false);
    
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

        //Task MAnager v2
        if (  self.roles.includes("LECTURER") ) {
            $("#main_nav").find("a").each((e, b)=>{
                if (b.href.includes("/tasks/import/global/"))
                    b.href = b.href.replace("tasks/import/global","lecturer/taskmanager");
            });
            $("#taskDataHyperlink").show();
        }

        otherFrontendLogic();

        if (successfulCreationCallback)
            successfulCreationCallback(true);
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
    
    /*  initalization  */
    NavbarLogicInit(data);
    
    return self;
}
  
NavbarLogic.getInstance = (dataLazy, debug = false, depMocks = {}, successfulCreationCallback) => {
    
    if (NavbarLogic.singleton)
      return NavbarLogic.singleton;
      
    $ = typeof $ != 'undefined'? $ : depMocks.$mock;
    window = typeof window != 'undefined'? window : depMocks.windowMock;
    
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
                    if (typeof accountInfo == 'string')
                        console.log("nie zalogowany");
                    else
                        console.log(accountInfo);
                }
                if (typeof accountInfo == 'string') //nie zalogowany
                    NavbarLogic.singleton = NavbarLogic({}, debug, depMocks, successfulCreationCallback);
                else //zalogowany
                    NavbarLogic.singleton = NavbarLogic(accountInfo, debug, depMocks, successfulCreationCallback);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveAccountInfo error");
                }
            }
        });
    }
    
    if ( dataLazy )
        NavbarLogic.singleton = NavbarLogic(dataLazy, debug, depMocks);
    else
        ajaxReceiveAccountInfo();
    
    return NavbarLogic.singleton;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {NavbarLogic};