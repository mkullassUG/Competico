$(function () {
  'use strict'

  $('[data-toggle="offcanvas"]').on('click', function () {
    $('.offcanvas-collapse').toggleClass('open')
  })
})

const DashboardLogic = (accountInfo_, debug) => {

    /*       logic variables          */
    var self = accountInfo_;
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;
    self.topPlayers = [];
    self.relativePlayers = [];
    self.myScore;
    /*       logic functions          */
    self.dashboardInit = (accountInfo) => {
        console.log("dashboardInit");
        
        self.roles = accountInfo.roles?accountInfo.roles:[];

        $("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")

        //navbar preparation
        NavbarLogic.singleton = NavbarLogic(accountInfo, debug);


        self.ajaxGetTopLeaderboard(self.setupLeaderboard);


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

    self.setupLeaderboard = (data) => {

        if (data.length <= 0)
            return;

        self.topPlayers = data;
        
        var topLeaderboard = $("#topLeaderboard").html(`
            <h5 class="border-bottom border-gray pb-2 mb-0">Tablica rankingów:</h5>
            <h6 class="border-bottom border-gray pb-2 mb-0 d-flex justify-content-between align-items-center w-100" style="
            display: inline-block;">
                <span>Gracze:</span> 
                <span>Wyniki:</span>
                <span>Profile:</span>
            </h6>`);

        for (let i = 0; i < data.length; i++) {
            var player = data[i];

            var isMe = (player.username===self.username && player.nickname===self.nickname);
            if (isMe)
                self.myScore = player.rating;


            var color = "#007bff"; 
            if ( player.position == 1)
                color = "#FFD700";
            if ( player.position == 2)
                color = "#C0C0C0";
            if (player.position == 3)
                color = "#b08d57";

            var playerHolder = $(`<div class="media text-muted pt-3 `+(isMe?'bg-light':'')+`">
                <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
                    <title>Placeholder</title>
                    <rect width="100%" height="100%" fill="`+color+`"/>
                    <text x="50%" y="50%" fill="white" dy=".3em">`+player.position+`</text>
                </svg>
                <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                    <div class="d-flex justify-content-between align-items-center w-100">
                        <strong class="d-block">` + player.nickname + 
                        ((player.username===self.username&&player.nickname===self.nickname)?' (JA)  ':'') +`</strong>
                        <strong>`+player.rating+`</strong>
                        <a href="/profile/`+ player.username + `-` + player.nickname +`">Profil</a>
                    </div>
                    <span class="text-gray-dark">`+ player.username + `</span>
                  <small class="d-block">ostatnio grano: x dni temu</small>
                </div>
            </div>`);
            topLeaderboard.append(playerHolder);
        }
        self.ajaxGetRelativeLeaderboard(self.setupRelativeLeaderboard);
  }

     self.setupRelativeLeaderboard = (data) => {

        if (data.length <= 0)
            return;

        self.relativePlayers = data;

        var topLeaderboard = $("#topLeaderboard");

        //
        if (data[0].position > self.topPlayers.length+1 )
            topLeaderboard.append("...");

      console.log("self.topPlayers.length " + self.topPlayers.length);

        for (let i = 0; i < data.length; i++) {

            var player = data[i];
            console.log("player.position: " + player.position);

            var isMe = (player.username===self.username && player.nickname===self.nickname);
            if (isMe)
                self.myScore = player.rating;

            if ( player.position <= self.topPlayers.length)
                continue;

            /*TODO: porówna czy w tabliy poprzedniej byli tacy:
                był: pomijam
                nie był:
                    czy jest pierwszy w tablicy:
                        nie: wstawiam
                        tak: sprawdzam czy pozycja jest odległa od ostatniego w tablicy o więcej niż 1:
                            tak: wstawiam z wielokropkiem
                            nie: wstawiam
                        czy jest ostatni w tablicy
                            tak: zakończ wielokropkiem
            */
            var playerHolder = $(`<div class="media text-muted pt-3 `+(isMe?'bg-light':'')+`">
                <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
                    <title>Placeholder</title>
                    <rect width="100%" height="100%" fill="#007bff"/>
                    <text x="50%" y="50%" dy=".3em" fill="white">`+player.position+`</text>
                </svg>
                <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                    <div class="d-flex justify-content-between align-items-center w-100">
                        <strong class="d-block">` + player.nickname + 
                        ((player.username===self.username&&player.nickname===self.nickname)?' (JA)  ':'') +`</strong>
                        <strong>`+player.rating+`</strong>
                        <a href="/profile/`+ player.username + `-` + player.nickname +`">Profil</a>
                    </div>
                    <span class="text-gray-dark">`+ player.username + `</span>
                    <small class="d-block">ostatnio grano: x dni temu</small>
                </div>
            </div>`);
            topLeaderboard.append(playerHolder);
        }
        topLeaderboard.append("...");


        console.log(self.topPlayers);
        console.log(self.relativePlayers);
        if (self.myScore)
            self.setupMyScore();
    }

    self.setupMyScore = () => {
        $("#currentUserRating").html(self.myScore);
    }
    /*       event listeners          */

    /*     ajax http actions       */
    
    /*   ajax http requests       */

    self.ajaxGetTopLeaderboard = (callback) => {
        console.log("ajaxGetTopLeaderboard");
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/game/leaderboard/top",
            contentType: "application/json",
            success: function(data) {
                if (debug){
                    console.log("ajaxGetTopLeaderboard success");
                    console.log(data);
                }

                callback(data);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxGetTopLeaderboard error");
                }
            }
        });
    }

    self.ajaxGetRelativeLeaderboard = (callback) => {
        console.log("ajaxGetRelativeLeaderboard");
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/game/leaderboard/relative",
            contentType: "application/json",
            success: function(data) {
                if (debug){
                    console.log("ajaxGetRelativeLeaderboard success");
                    console.log(data);
                }

                callback(data);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxGetRelativeLeaderboard error");
                }
            }
        });
    }


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