$(function () {
  'use strict'

  $('[data-toggle="offcanvas"]').on('click', function () {
    $('.offcanvas-collapse').toggleClass('open')
  })
})

const DashboardLogic = (accountInfo_) => {

  /*       logic variables          */
  var self = accountInfo_;
  self.nickname;
  self.username;
  self.email;
  self.roles;
  self.authenticated;
  /*       logic functions          */
  self.dashboardInit = (accountInfo) => {
    console.log("dashboardInit");
    
    self.roles = accountInfo.roles?accountInfo.roles:[];

    $("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")

    //navbar preparation
    NavbarLogic.singleton = NavbarLogic(accountInfo, debug);

    // if ( self.roles.includes("SWAGGER_ADMIN"))
    //   $("#swaggerHyperlink").show();
    // if ( self.roles.includes("TASK_DATA_ADMIN"))
    //     $("#taskDataHyperlink").show();
    // if ( self.roles.includes("ACTUATOR_ADMIN"))
    //     $("#actuatorHyperlink").show();

    // if ( self.authenticated ) {
    //     $("#registerHyperlink").hide();
    //     $("#loginHyperlink").hide();
    //     $("#profileHyperlink").show();
    //     $("#dashboardHyperlink").show();
    //     $("#logOutButton").show();
    //     $("#gameHyperlink").show();
    // }
  }

  /*       event listeners          */

  /*     ajax http actions       */
  
  /*   ajax http requests       */

  /*  initalization  */
  self.dashboardInit(accountInfo_);
  
  return self;
}

DashboardLogic.getInstance = (debug = false) => {

  if (DashboardLogic.singleton)
    return DashboardLogic.singleton;

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
          console.log(accountInfo);
          console.log(textStatus);
          console.log(jqXHR);
        }
        if (typeof accountInfo == 'string') //nie zalogowany
          DashboardLogic.singleton = DashboardLogic({}, debug);
        else //zalogowany
          DashboardLogic.singleton = DashboardLogic(accountInfo, debug);
        console.log("DashboardLogic");
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