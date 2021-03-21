const NavbarLogic = (accountInfo_) => {
  
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
        self.authenticated = accountInfo.authenticated;
    
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
    }
  
    /*       event listeners          */
  
    /*     ajax http actions       */
    
    /*   ajax http requests       */
  
    /*  initalization  */
    self.navbarLogic(accountInfo_);
    
    return self;
}
  
NavbarLogic.getInstance = (debug = false) => {
  
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
          NavbarLogic.singleton = NavbarLogic({}, debug);
          else //zalogowany
          NavbarLogic.singleton = NavbarLogic(accountInfo, debug);
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