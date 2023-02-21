const NavbarLogic = (data, debug = false, deps = {}, successfulCreationCallback) => {
    
    /*  singleton   */
    if (NavbarLogic.singleton)
        return NavbarLogic.singleton;
    var self = {};
    if (!NavbarLogic.singleton && data)
        NavbarLogic.singleton = self;
    else if (!NavbarLogic.singleton && !data)
        return NavbarLogic.getInstance(false, debug, deps, successfulCreationCallback);

    /*  environment preparation  */
    if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
        throw Error("jQuery not defined");
    else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
        deps.$ = $;

    if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
        throw Error("window not defined");
    else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
        deps.window = window;
    
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
    
        if ( self.roles.includes("SWAGGER_ADMIN"))
            deps.$("#swaggerHyperlink").show();
        if ( self.roles.includes("TASK_DATA_ADMIN"))
            deps.$("#taskDataHyperlink").show();
        if ( self.roles.includes("ACTUATOR_ADMIN"))
            deps.$("#actuatorHyperlink").show();

        if ( self.authenticated ) {
            deps.$("#registerHyperlink").hide();
            deps.$("#loginHyperlink").hide();
            deps.$("#profileHyperlink").show();
            deps.$("#dashboardHyperlink").show();
            deps.$("#groupsHyperlinkMainNav").show();
            deps.$("#logOutButton").show();
            deps.$("#gameHyperlink").show();
        } 

        if (  self.roles.includes("LECTURER") ) {
            deps.$("#main_nav").find("a").each((e, b)=>{
                if (b.href.includes("/tasks/import/global/"))
                    b.href = b.href.replace("tasks/import/global","lecturer/taskmanager");
            });
            deps.$("#taskDataHyperlink").show();
        }

        otherFrontendLogic();

        if (successfulCreationCallback)
            successfulCreationCallback(true);
    }
    
    var otherFrontendLogic = () => {
    
        deps.$(deps.window.document).on('click',(e) => {
            deps.$(".focusOn").removeClass("focusOn");
        });

        deps.$("#main_nav").on('click', '.dropdown-menu', function (e) {
            e.stopPropagation();
        });
      
        var takeCareOfFocusOnElementInDropdownMenu = () => {
            if ( deps.$(".focusOn").next('.submenu').length) {
                deps.$(".focusOn").next('.submenu').css({"display":""});
            }
            deps.$(".focusOn").removeClass("focusOn");
        }

        deps.$('.dropdown-menu a').click(function(e){
            
            if ($(".focusOn").is($(this))) {
                takeCareOfFocusOnElementInDropdownMenu();
                deps.$('.submenu').attr("style","");
            } else {

                takeCareOfFocusOnElementInDropdownMenu();
                deps.$(this).addClass("focusOn");
                if ( deps.$(this).next('.submenu').length ) {
                    deps.$('.submenu').css({"display":"none"});
                    deps.$(this).next('.submenu').attr("style","");
                }
            }

            if ($(this).hasClass("focusOn") &&  deps.$(this).next('.submenu').length){
                deps.$(this).next('.submenu').show();
                deps.$(this).next('.submenu').css({"display":"block"});
            } else if ( deps.$(this).next('.submenu').length ) {
                deps.$(this).next('.submenu').hide();
                deps.$(this).next('.submenu').css({"display":""});
            } 

            deps.$('.dropdown').one('hide.bs.dropdown', function (e) {
                deps.$('.submenu').attr("style","");
            })
        });
    }
    
    /*  initalization  */
    NavbarLogicInit(data);
    
    return self;
}
  
NavbarLogic.getInstance = (dataLazy, debug = false, deps = {}, successfulCreationCallback) => {
    
    if (NavbarLogic.singleton)
      return NavbarLogic.singleton;
      
    if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
        throw Error("jQuery not defined");
    else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
        deps.$ = $;

    if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
        throw Error("window not defined");
    else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
        deps.window = window;
    
    var ajaxReceiveAccountInfo = ( ) => {
        
        deps.$.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/account/info",
            contentType: "application/json",
            success: function(accountInfo, textStatus, jqXHR) {

                if (debug) {
                    console.log("ajaxReceiveAccountInfo success");
                    if (typeof accountInfo == 'string')
                        console.log("nie zalogowany");
                    else
                        console.log(accountInfo);
                }
                if (typeof accountInfo == 'string') 
                    NavbarLogic.singleton = NavbarLogic({}, debug, deps, successfulCreationCallback);
                else 
                    NavbarLogic.singleton = NavbarLogic(accountInfo, debug, deps, successfulCreationCallback);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveAccountInfo error");
                }
            }
        });
    }
    
    if ( dataLazy )
        NavbarLogic.singleton = NavbarLogic(dataLazy, debug, deps, successfulCreationCallback);
    else
        ajaxReceiveAccountInfo();
    
    return NavbarLogic.singleton;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {NavbarLogic};