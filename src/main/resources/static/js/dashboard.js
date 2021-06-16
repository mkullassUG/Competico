$(function () {
  'use strict'

  $('[data-toggle="offcanvas"]').on('click', function () {
    $('.offcanvas-collapse').toggleClass('open')
  })
})

const DashboardLogic = (data, debug = false, depMocks = {}) => {

    /*  singleton   */
    if (DashboardLogic.singleton)
        return DashboardLogic.singleton;
    var self = data;
    if (!DashboardLogic.singleton && !data)
        DashboardLogic.getInstance(data, depMocks, debug);

    /*  environment preparation  */
    $ = typeof depMocks.$mock != 'undefined'? depMocks.$mock : $;
    window =  typeof depMocks.windowMock != 'undefined'? depMocks.windowMock : window;

    if ( depMocks.NavbarLogic && typeof NavbarLogic == "undefined")
        NavbarLogic = depMocks.NavbarLogic;
    if ( depMocks.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
        PageLanguageChanger = depMocks.PageLanguageChanger;

    /*       logic variables          */
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;
    self.topPlayers = [];
    self.relativePlayers = [];
    self.myScore;
    self.initDone = false;
    /*       logic functions          */
    self.dashboardInit = (accountInfo) => {
        
        var initActions = () => {
            if (self.initDone === true) 
                return;
            else
                self.initDone = true;
            self.roles = accountInfo.roles?accountInfo.roles:[];

            $("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")

            //navbar preparation
            if ( typeof NavbarLogic != "undefined" )
                NavbarLogic(accountInfo, debug);

            self.ajaxGetTopLeaderboard(self.setupLeaderboard);

            //new bug fix
            var resizeWindow = () => {
            
                var sy = window.scrollY;
                $("html").height("100%");

                function isInt(n) {
                    return n % 1 === 0;
                }
                
                var h1 = $(window.document).height();
                // if ( isInt (h1))
                //     h1 -= 1;
                $("html").height(h1);

                window.scrollTo(0,sy);
            } 

            
            if ( self.roles.includes("LECTURER")) {
                $("#gameHistoryHyperlink").hide();
                $("#currentUserRating").hide();
            }
            window.onresize = resizeWindow;
            resizeWindow();
        }

        if ( typeof PageLanguageChanger != "undefined")
            PageLanguageChanger(false, debug, false, ()=>{initActions();self.InitWithPageLanguageChanger();});
        else
            initActions();
        
    }

    self.InitWithPageLanguageChanger = (data, lang_) => {

        if ( data === true){
            if ( debug )
                console.log("PageLanguageChanger done");
            return;
        }

        if ($("#ranksTable").length)
            $("#ranksTable").text(PageLanguageChanger().getTextFor("ranksTable"));
        if ($("#playersDiv").length)
            $("#playersDiv").text(PageLanguageChanger().getTextFor("players"));
        if ($("#scoreDiv").length)
            $("#scoreDiv").text(PageLanguageChanger().getTextFor("score"));
        if ($("#profilesDiv").length)
            $("#profilesDiv").text(PageLanguageChanger().getTextFor("profiles"));
        if ($(".playerProfile").length)
            $(".playerProfile").text(PageLanguageChanger().getTextFor("profile"));
    }

    self.setupLeaderboard = (data) => {

        if (data.length <= 0)
            return;

        self.topPlayers = data;
        
        var topLeaderboard = $("#topLeaderboard").html(`
            <h5 class="pb-2 mb-1" id="ranksTable">` +
            ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("ranksTable"):"Tablica rankingów:") 
            + `</h5>
            
            <div class="row border-bottom border-gray">
                <div class="col-1 text-center">
                    <h6>#</h6>  
                </div>
                <div class="col-4">
                    <h6>
                        <span id="playersDiv">` +
                            ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("players"):"Gracze:") + 
                        `</span> 
                    </h6>
                </div>
                <div class="col-sm-4 col-3">
                    <h6>
                        <span id="scoreDiv">` +
                            ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("score"):"Wyniki:") + 
                        `</span>
                    </h6>
                </div>
                <div class="col-3">
                    <h6>
                        <span id="profilesDiv">` +
                            ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("profile"):"Profile:") + 
                        `</span>
                    </h6>
                </div>
            </div>`);

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

            var rankCol = $(`<div class="col-1 align-self-center">`);
            rankCol.append(`
            <svg class="bd-placeholder-img mr-2 rounded tra" `+((color==="#007bff")?"":`style="box-shadow: 0px 0px 10px ` + color +`"`)+` width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
                <title>Placeholder</title>
                <rect width="100%" height="100%" fill="`+color+`"/>
                <text x="50%" y="50%" fill="white" dy=".3em">`+player.position+`</text>
            </svg>`);

            var nameCol = $(`<div class="col-4 align-self-center">`);
            nameCol.append(`
                <strong class="d-block">` + player.nickname + 
                ((player.username===self.username&&player.nickname===self.nickname)?' (JA)  ':'') +`</strong>
                <span class="text-gray-dark">`+ player.username + `</span>
                <small class="d-block">ostatnio grano: 0 dni temu</small>`);

            var scoreCol = $(`<div class="col-sm-4 col-3 align-self-center">`);
            scoreCol.append(`<strong>`+player.rating+`</strong>`)
            var profileCol = $(`<div class="col-3 align-self-center">`);
            profileCol.append(`
            <a href="/profile/`+ player.username + `" class="playerProfile">` +
                ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("profiles"):"Profil") + 
            `</a>`);

            var scoreAndProfileHolder = $(`<div class="row media-body pb-3 mb-0 small lh-125">`);

            scoreAndProfileHolder.append(rankCol).append(nameCol).append(scoreCol).append(profileCol);
            // var playerHolder = $(`<div class="media text-muted pt-3`+(isMe?' bg-light':'')+`">
            //     <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
            //         <title>Placeholder</title>
            //         <rect width="100%" height="100%" fill="`+color+`"/>
            //         <text x="50%" y="50%" fill="white" dy=".3em">`+player.position+`</text>
            //     </svg>
            //     <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
            //         <div class="d-flex justify-content-between align-items-center w-100">
            //             <strong class="d-block">` + player.nickname + 
            //             ((player.username===self.username&&player.nickname===self.nickname)?' (JA)  ':'') +`</strong>
            //             <strong>`+player.rating+`</strong>
            //             <a href="/profile/`+ player.username + `" class="playerProfile">` +
            //             ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("profiles"):"Profil:") 
            //             + `</a>
            //         </div>
            //         <span class="text-gray-dark">`+ player.username + `</span>
            //       <small class="d-block">ostatnio grano: x dni temu</small>
            //     </div>
            // </div>`);

            var playerHolder = $(`<div class="border-bottom border-gray media text-muted pt-3`+(isMe?' bg-light':'')+`">`);
            playerHolder.append(scoreAndProfileHolder);

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


        for (let i = 0; i < data.length; i++) {

            var player = data[i];

            var isMe = (player.username===self.username && player.nickname===self.nickname);
            if (isMe)
                self.myScore = player.rating;

            if ( player.position <= self.topPlayers.length)
                continue;

            /* porówna czy w tabliy poprzedniej byli tacy:
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
            var rankCol = $(`<div class="col-1 align-self-center">`);
            rankCol.append(`
            <svg class="bd-placeholder-img mr-2 rounded tra"  width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
                <title>Placeholder</title>
                <rect width="100%" height="100%" fill="#007bff"/>
                <text x="50%" y="50%" fill="white" dy=".3em">`+player.position+`</text>
            </svg>`);

            var nameCol = $(`<div class="col-4 align-self-center">`);
            nameCol.append(`
                <strong class="d-block">` + player.nickname + 
                ((player.username===self.username&&player.nickname===self.nickname)?' (Me)  ':'') +`</strong>
                <span class="text-gray-dark">`+ player.username + `</span>
                <small class="d-block">ostatnio grano: 0 dni temu</small>`);

            var scoreCol = $(`<div class="col-sm-4 col-3 align-self-center">`);
            scoreCol.append(`<strong>`+player.rating+`</strong>`)
            var profileCol = $(`<div class="col-3 align-self-center">`);
            profileCol.append(`
            <a href="/profile/`+ player.username + `" class="playerProfile">` +
                ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("profiles"):"Profil") + 
            `</a>`);

            var scoreAndProfileHolder = $(`<div class="row media-body pb-3 mb-0 small lh-125">`);

            scoreAndProfileHolder.append(rankCol).append(nameCol).append(scoreCol).append(profileCol);

            // var playerHolder = $(`<div class="media text-muted pt-3 `+(isMe?'bg-light':'')+`">
            //     <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32">
            //         <title>Placeholder</title>
            //         <rect width="100%" height="100%" fill="#007bff"/>
            //         <text x="50%" y="50%" dy=".3em" fill="white">`+player.position+`</text>
            //     </svg>
            //     <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
            //         <div class="d-flex justify-content-between align-items-center w-100">
            //             <strong class="d-block">` + player.nickname + 
            //             ((player.username===self.username&&player.nickname===self.nickname)?' (JA)  ':'') +`</strong>
            //             <strong>`+player.rating+`</strong>
            //             <a href="/profile/`+ player.username + `-` + player.nickname +`">Profil</a>
            //         </div>
            //         <span class="text-gray-dark">`+ player.username + `</span>
            //         <small class="d-block">ostatnio grano: x dni temu</small>
            //     </div>
            // </div>`);
            
            var playerHolder = $(`<div class="border-bottom border-gray media text-muted pt-3`+(isMe?' bg-light':'')+`">`);
            playerHolder.append(scoreAndProfileHolder);


            topLeaderboard.append(playerHolder);
        }
        topLeaderboard.append("...");


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
    self.dashboardInit(data);
    
    return self;
}

DashboardLogic.getInstance = (dataLazy, debug = false, depMocks = {}) => {

    if (DashboardLogic.singleton)
        return DashboardLogic.singleton;

    /*  environment preparation  */
    $ = typeof depMocks.$mock != 'undefined'? depMocks.$mock : $;
    window =  typeof depMocks.windowMock != 'undefined'? depMocks.windowMock : window;

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
                    DashboardLogic.singleton = DashboardLogic({}, debug, depMocks);
                else //zalogowany
                    DashboardLogic.singleton = DashboardLogic(accountInfo, debug, depMocks);
                
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveAccountInfo error");
                }
            }
        });
    }
    
    if (dataLazy)
        return DashboardLogic(dataLazy, debug, depMocks);
    else
        ajaxReceiveAccountInfo()

  return DashboardLogic.singleton;
}