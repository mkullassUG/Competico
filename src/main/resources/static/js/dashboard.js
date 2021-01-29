$(function () {
  'use strict'

  $('[data-toggle="offcanvas"]').on('click', function () {
    $('.offcanvas-collapse').toggleClass('open')
  })
})

const DashboardLogic = (playerInfo_) => {

  /*       logic variables          */
  var self = playerInfo_;
  self.nickname;
  self.username;

  /*       logic functions          */
  self.dashboardInit = (playerInfo) => {
    console.log("dashboardInit");
 
    self.nickname = playerInfo.nickname;
    self.username = playerInfo.username;

    $("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")
  }

  /*       event listeners          */

  /*     ajax http actions       */
  
  /*   ajax http requests       */

  /*  initalization  */
  self.dashboardInit(playerInfo_);
  
  return self;
}

DashboardLogic.getInstance = (debug = false) => {

  if (DashboardLogic.singleton)
    return DashboardLogic.singleton;

  var ajaxReceiveWhoAmI = ( ) => {
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/playerinfo",
      contentType: "application/json",
      success: function(playerInfo, textStatus, jqXHR) {
        if (debug){
          console.log("ajaxReceiveWhoAmI success");
          console.log(playerInfo);
          console.log(textStatus);
          console.log(jqXHR);
        }
        DashboardLogic.singleton = DashboardLogic(playerInfo, debug);
        console.log("DashboardLogic");
      },
      error: function(jqXHR, status, err) {
        if (debug){
          console.warn("ajaxReceiveWhoAmI error");
        }
      }
    });
  }
  
  ajaxReceiveWhoAmI();
}