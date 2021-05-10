const ProfileLogic = (accountInfo_, debug) => {
  
    /*       logic variables          */
    var self = accountInfo_;
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;
    self.myScore;

    /*       logic functions          */
    self.profileInit = (accountInfo) => {
        console.log("profileInit");
        
        self.roles = accountInfo.roles?accountInfo.roles:[];
        //navbar preparation
        NavbarLogic.singleton = NavbarLogic(accountInfo, debug);

        
        self.ajaxGetRelativeLeaderboard(self.setupMyScoreFromRelativeLeaderboard);

        $("#usernameDiv").text(self.username);
        $("#nicknameDiv").text(self.nickname);
        $("#emailDiv").text(self.email);
        $("#newEmailInput").val(self.email);
        //$("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")



        if ( self.roles.includes("LECTURER")) {
            $("#roleDiv").text(" Lektor");
            self.setupPageForLecturer();
        }
        else
            $("#roleDiv").text("Gracz");

        if ( self.roles.includes("SWAGGER_ADMIN"))
          $("#roleDiv").text("SAdmin");
        if ( self.roles.includes("TASK_DATA_ADMIN"))
            $("#roleDiv").text("TDAdmin");
        if ( self.roles.includes("ACTUATOR_ADMIN"))
            $("#roleDiv").text("AAdmin");
        
            
        

        setupEventListenersForEdit();
        //NEW!!!!!!
        if ( typeof PageLanguageChanger != "undefined")
            PageLanguageChanger();

        tooltipsUpdate();
    }
    
    var tooltipsUpdate = () => {
        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }

    self.setupMyScoreFromRelativeLeaderboard = (data) => {
  
        if (data.length <= 0)
            return;

  
        for (let i = 0; i < data.length; i++) {

            var player = data[i];
            console.log("player.position: " + player.position);

            var isMe = (player.username===self.username && player.nickname===self.nickname);
            if (isMe) {
                self.myScore = player.rating;
                break;
            }
        }

        if (self.myScore) {
            self.setupMyScore(self.myScore);
        } else {
            self.setupMyScoreEmpty()
        }
    }
  
    self.setupMyScore = (score) => {
        $("#scoreDiv").text("score: " + score);
    }

    self.setupMyScoreEmpty = () => {
        $("#scoreDiv").text("lektor nie może grać")
    }

    self.setupPageForLecturer = () => {
        $(".lectorEnableClass").show()
        $(".lectorDisableClass").hide()
    }

    var createNicknameEditInput = () => {
        if ( $("#nicknameDiv").length > 0) {

            //change div to input
            self.stopHoverAnimation_nickname = true;
            var currentValue = $("#nicknameDiv").text();

            var input = $(`<input id="nicknameInput" class="col-sm-7" value="`+currentValue+`"></input>`);
            $("#nicknameDiv").replaceWith(input);

            input.focus();
            var value = input.val();
            input.focus().val("").val(value);

            input.on("input propertychange",
                function () {
                    showSaveButton();
                }
            );

            input.blur(
                function () {
                    if (  $("#nicknameInput").length > 0 && self.nickname == $("#nicknameInput").val()) {
                        //change input to div
                        var div = $(`<div id="nicknameDiv" class="col-sm-7 text-secondary">` + currentValue + `</input>`);
                        $("#nicknameInput").replaceWith(div);
                        self.stopHoverAnimation_nickname = false;
                        $('#nicknameEditButt').fadeOut();
                    }
                }
            )
        }
    }

    var createNicknameEditDiv = () => {
        var div = $(`<div id="nicknameDiv" class="col-sm-7 text-secondary">`+ self.nickname + `</div>`);

        $("#nicknameInput").replaceWith(div);
        self.stopHoverAnimation_nickname = false;
        $('#nicknameEditButt').fadeOut();
    }

    self.afterNicknameChangeAction = (data, nickname) => {
        /*TODO:
            informowanie użytkownika o stanie, czy zmieniono nickame
            data:   true - udało się
                    string- kod błędu ("BAD_NICKNAME")
        */

        if ( $("#nicknameInput").length > 0) {
            if ( data === true ) { 
                // location.reload();

                //zmiana input na div 
                self.nickname = nickname;
                createNicknameEditDiv();
                // $("#oldPasswordInputPassword").val("");
            } else if (data === "BAD_NICKNAME"){

                //wyświetlenie info o błędzie
                $("#nicknameInput").css({"color":"red"})
                setTimeout(function() {
                    $("#nicknameInput").css({"color":"initial"})
                },2000);
            } else {
                console.warn("Nickame change warning!");
                console.warn(data);

                $("#nicknameInput").css({"color":"red"})
                setTimeout(function() {
                    $("#nicknameInput").css({"color":"initial"})
                },2000);
            }
        }
    }

    self.afterEmailChangeAction = (data, email) => {

        /*TODO:
            informowanie użytkownika o stanie, czy hasło złe, czy email zmieniony?
            data:   true - udało się
                    string- kod błędu (BAD_PASSWORD", "BAD_EMAIL", "USED_EMAIL", "SAME_EMAIL)
        */
        if ( data === true ) {
            location.reload();
            //zamknij modala albo wyświetl sukces
            // $('#saveChangesEmailModal').modal('hide');
            // //podmień na nowego emaila
            // $("#emailDiv").html(email);
            // $("#newEmailInput").val(email);

            // self.email = email;
            // $("#oldPasswordInputEmail").val("");
        } else if (data === "BAD_PASSWORD") {
            $("#invalidPasswordInfoEmail").show();
        } else if (data === "BAD_EMAIL") {
            $("#invalidEmailInfoBad").show();
        } else if (data === "USED_EMAIL") {
            $("#invalidEmailInfoUsed").show();
        } else if (data === "SAME_EMAIL") {
            $("#invalidEmailInfoIdentical").show();
        } else {
            console.warn("Email change warning!");
        }
    }

    self.afterPasswordChangeAction = (data) => {
        /*
            TODO:
            informowanie użytkownika o stanie, czy hasło złe obecne lub stare
            data:   true - udało się
                    string- kod błędu ("BAD_NEW_PASSWORD, BAD_OLD_PASSWORD")
        */
        console.log(data);
        
        if ( data === true ) {
            location.reload();

            //zamknij modala albo wyświetl sukces
            // $('#saveChangesPasswordModal').modal('hide');
            // $("#oldPasswordInputPassword").val("");
            // $("#newPasswordInput").val("");
        } else if (data === "BAD_OLD_PASSWORD") {

            $("#invalidPasswordInfoPassword").show();
        } else if (data === "BAD_NEW_PASSWORD") {

            $("#invalidNewPasswordInfoPassword").show();
        } else if (data === "SAME_PASSWORD") {

            $("#invalidNewPasswordInfoPasswordIdentical").show();
        } else {
            console.warn("Password change warning!");
        }
    }

    var showSaveButton = () => {
        if ( !areEmailAndNicknameSameAsCurrent() ) {
            //pokaż przycisk
            $("#saveChangesDiv").show();
        } else {
            //schowaj przycisk
            $("#saveChangesDiv").hide();
        }
    }

    var areEmailAndNicknameSameAsCurrent = () => {

        var emailInput = $("#emailInput");
        var nicknameInput = $("#nicknameInput");

        if ( emailInput.length > 0 && self.email != emailInput.val()) {
            return false
        }

        if (  nicknameInput.length > 0 && self.nickname != nicknameInput.val()) {
            return false;
        }
        return true;
    }
    /*       event listeners          */
    self.stopHoverAnimation_nickname = false;
    self.stopHoverAnimation_email = false;
    var setupEventListenersForEdit = () => {
        self.stopHoverAnimation_nickname = false;
        self.stopHoverAnimation_email = false;
        //przycisk edycji znikanie
        if ($("#nicknameRow").length > 0) {
            $("#nicknameRow").hover(
                function () {
                    if (!self.stopHoverAnimation_nickname) {
                        $('#nicknameEditButt').stop(true, true).fadeOut();
                        $('#nicknameEditButt').fadeIn();
                    }
                },
                function () {
                    if (!self.stopHoverAnimation_nickname) {
                        $('#nicknameEditButt').fadeIn();
                        $('#nicknameEditButt').stop(true, true).fadeOut();
                    }
                }
            );
        }
        if ($("#emailRow").length > 0) {
            $("#emailRow").on({
                'mouseenter':function() {
                    if (!self.stopHoverAnimation_email && $('#emailEditButt').length > 0) {
                        $('#emailEditButt').stop(true, true).fadeOut();
                        $('#emailEditButt').fadeIn();
                    }
                    
                },'mouseleave':function() {
                    if (!self.stopHoverAnimation_email && $('#emailEditButt').length > 0) {
                        $('#emailEditButt').fadeIn();
                        $('#emailEditButt').stop(true, true).fadeOut();
                    }
                }
            });
        }

        if ( $('#nicknameEditButt').length > 0 ) {
            

            $('#nicknameEditButt').on("click",
                function () {
                    /*
                        1. zatrzymaj znikanie przycisku edycji
                        2. zrób z nicknameDiv inputa
                        3. focus na inputa
                        4. event listener na inputa
                    */
                    if ( $("#nicknameInput").length > 0 )
                        createNicknameEditDiv();
                    else
                        createNicknameEditInput();
                }
            )
        }

        if ( $('#btnSendChangesEmail').length > 0 ) {
            
            $('#btnSendChangesEmail').on("click", (e) => {
                e.preventDefault();
                console.log("click btnSendChangesEmail");

                

                $("#invalidEmailInfoBad").hide();
                $("#invalidEmailInfoIdentical").hide();
                $("#invalidEmailInfoUsed").hide();
                $("#invalidPasswordInfoEmail").hide();
                //walidacja emaila
                var email = $("#newEmailInput").val();
                var password = $("#oldPasswordInputEmail").val();

                if ( email === "" && password === "") {
                    $('#saveChangesEmailModal').modal('hide');
                    return;
                }

                var isEmailValid = AccountValidation().emailValid(email);
                if ( isEmailValid )
                    self.ajaxSendProfileEmailChanges(
                        self.afterEmailChangeAction,
                        email,
                        password
                    );
                else 
                    $("#invalidEmailInfoBad").show();
            })

            
        }

        if ( $('#btnSendChangesPassword').length > 0 ) {
            
            $('#btnSendChangesPassword').on("click", (e) => {
                e.preventDefault();
                console.log("click btnSendChangesEmail");

                $("#invalidPasswordInfoPassword").hide();
                $("#invalidNewPasswordInfoPassword").hide();
                $("#invalidNewPasswordInfoPasswordIdentical").hide();
                //walidacja hasła
                var newPassword = $("#newPasswordInput").val();
                var password = $("#oldPasswordInputPassword").val();

                if ( newPassword === "" && password === "") {
                    $('#saveChangesPasswordModal').modal('hide');
                    return;
                }

                var isPasswordValid = AccountValidation().passwordValid(newPassword);
                console.log(newPassword);
                console.log(isPasswordValid);
                if ( isPasswordValid )
                    self.ajaxSendProfilePasswordChanges(
                        self.afterPasswordChangeAction,
                        newPassword,
                        password
                    );
                else
                    $("#invalidNewPasswordInfoPassword").show();
            });
        }
    }
    
    if ( $("#btnSendChanges").length > 0 ) {
        $("#btnSendChanges").click(function() {
            if ( $("#nicknameInput").length > 0) {
                var nickname = $("#nicknameInput").val();
                var isNicknameValid = AccountValidation().nicknameValid(nickname);
                if ( isNicknameValid )
                    self.ajaxSendProfileNicknameChanges(self.afterNicknameChangeAction, nickname);
                else {
                    $("#nicknameInput").css({"color":"red"})
                    setTimeout(function() {
                        $("#nicknameInput").css({"color":"initial"})
                    },2000);
                    // $("#invalidNewPasswordInfoPassword").show();
                    console.warn("ERROR !!!");
                }

            }
            
            $("#saveChangesDiv").hide();
        })
    }

    if ( $('#emailEditButt').length > 0) {
        $('#emailEditButt').on('click',() => {

            if ( $("#newEmailInput").length > 0)
                $("#newEmailInput").val(self.email);
        })
    }
    /*     ajax http actions       */
    self.ajaxSendProfileNicknameChanges = (callback, nickname) => {
        send = nickname;
        $.ajax({
            type     : "PUT",
            cache    : false,
            url      : "/api/v1/account/nickname",
            contentType: "application/json",
            data     : send,
            success: function(data) {
                if (debug){
                    console.log("ajaxSendProfileNicknameChanges success");
                    console.log(data);
                }
                /*
                    changeNickname():
                    - OK
                    - BAD_REQUEST ("BAD_NICKNAME")
                */
                callback(true, nickname);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxSendProfileNicknameChanges error");
                    console.warn(jqXHR);
                }
                callback(jqXHR.responseText, nickname);
            }
        });
    }

    self.ajaxSendProfileEmailChanges = (callback, email, password) => {

        send = JSON.stringify({
            email: email,
            password: password
        });

        $.ajax({
            type     : "PUT",
            cache    : false,
            url      : "/api/v1/account/email",
            data     : send,
            contentType: "application/json",
            success: function(data) {
                if (debug){
                    console.log("ajaxSendProfileEmailChanges success");
                    console.log(data);
                }
                /*
                    changeEmail():
                    - OK
                    - BAD_REQUEST ("BAD_PASSWORD", "BAD_EMAIL", "USED_EMAIL", "SAME_EMAIL")
                */
                callback(true, email);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxSendProfileEmailChanges error");
                    console.warn(jqXHR);
                }
                callback(jqXHR.responseText, email);
            }
        });
    }

    self.ajaxSendProfilePasswordChanges = (callback, newPassword, password) => {

        send = JSON.stringify({
            newPassword: newPassword,
            oldPassword: password
        });

        $.ajax({
            type     : "PUT",
            cache    : false,
            url      : "/api/v1/account/password",
            data     : send,
            contentType: "application/json",
            success: function(data) {
                if (debug){
                    console.log("ajaxSendProfilePasswordChanges success");
                    console.log(data);
                }
                /*
                    changePassword():
                    - OK
                    - BAD_REQUEST ("BAD_OLD_PASSWORD, BAD_OLD_PASSWORD")
                */
                callback(true);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxSendProfilePasswordChanges error");
                    console.warn(jqXHR);
                }
                callback(jqXHR.responseText);
            }
        });
    }

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
    self.profileInit(accountInfo_);
    
    return self;
}
  
ProfileLogic.getInstance = (debug = false) => {

    if (ProfileLogic.singleton)
        return ProfileLogic.singleton;

    var ajaxReceiveAccountInfo = ( ) => {
        $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/account/info",
        contentType: "application/json",
        success: function(accountInfo, textStatus, jqXHR) {
            if (debug){
            console.log("ajaxReceiveAccountInfo success");
            console.log(accountInfo);
            console.log(textStatus);
            console.log(jqXHR);
            }
            if (typeof accountInfo == 'string') //nie zalogowany
                ProfileLogic.singleton = ProfileLogic({}, debug);
            else //zalogowany
                ProfileLogic.singleton = ProfileLogic(accountInfo, debug);

            console.log("ProfileLogic");
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