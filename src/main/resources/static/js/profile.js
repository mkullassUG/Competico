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

        $("#usernameDiv").html(self.username);
        $("#nicknameDiv").html(self.nickname);
        $("#emailDiv").html(self.email);
        $("#newEmailInput").val(self.email);
        //$("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")



        if ( self.roles.includes("LECTURER")) {
            $("#roleDiv").html(" Lektor");
            self.setupPageForLecturer();
        }
        else
            $("#roleDiv").html("Gracz");

        if ( self.roles.includes("SWAGGER_ADMIN"))
          $("#roleDiv").html("SAdmin");
        if ( self.roles.includes("TASK_DATA_ADMIN"))
            $("#roleDiv").html("TDAdmin");
        if ( self.roles.includes("ACTUATOR_ADMIN"))
            $("#roleDiv").html("AAdmin");
        
            
        $('[data-toggle="tooltip"]').tooltip();

        setupEventListenersForEdit();
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
        $("#scoreDiv").html("score: " + score);
    }

    self.setupMyScoreEmpty = () => {
        $("#scoreDiv").html("lektor nie może grać")
    }

    self.setupPageForLecturer = () => {
        $(".lectorEnableClass").show()
        $(".lectorDisableClass").hide()
    }

    self.afterNicknameChangeAction = (data, nickname) => {
        /*TODO:
            informowanie użytkownika o stanie, czy zmieniono nickame
            data:   true - udało się
                    string- kod błędu ("BAD_NICKNAME")
        */

        if ( $("#nicknameInput").length > 0) {
            if ( data === true ) { 
                //zmiana input na div 
                self.nickname = nickname;

                var div = $(`<div id="nicknameDiv" class="col-sm-7 text-secondary">`+ self.nickname + `</div>`);

                $("#nicknameInput").replaceWith(div);
                self.stopHoverAnimation_nickname = false;
                $('#nicknameEditButt').fadeOut();
                
                $("#oldPasswordInputPassword").val("");
            } else  if (data === "BAD_NICKNAME"){

                //wyświetlenie info o błędzie
                $("#emailInput").css({"color":"red"})
                setTimeout(function() {
                    $("#emailInput").css({"color":"initial"})
                },2000);
            } else {
                console.warn("Nickame change warning!");
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
            //zamknij modala albo wyświetl sukces
            $('#saveChangesEmailModal').modal('hide');
            //podmień na nowego emaila
            $("#emailDiv").html(email);
            $("#newEmailInput").val(email);

            self.email = email;
            $("#oldPasswordInputEmail").val("");
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
            //zamknij modala albo wyświetl sukces
            $('#saveChangesPasswordModal').modal('hide');
            $("#oldPasswordInputPassword").val("");
            $("#newPasswordInput").val("");
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
    // self.afterEmailChangeAction = (wasSuccessful) => {
    //     if ( $("#emailInput").length > 0) {
    //         if ( wasSuccessful ) {
    //             //zmiana input na div
    //             self.email = $("#emailInput").val();

    //             var div = $(`<div id="emailDiv" class="col-sm-7 text-secondary">`+ self.email + `</div>`);

    //             $("#emailInput").replaceWith(div);
    //             self.stopHoverAnimation_email = false;
    //             $('#emailEditButt').fadeOut();
    //         } else {
    //             //wyświetlenie info o błędzie
    //             $("#emailInput").css({"color":"red"})
    //             setTimeout(function() {
    //                 $("#emailInput").css({"color":"initial"})
    //             },2000);
    //         }
    //     }
    // }

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
                    if (!self.stopHoverAnimation_email) {
                        $('#emailEditButt').stop(true, true).fadeOut();
                        $('#emailEditButt').fadeIn();
                    }
                    
                },'mouseleave':function() {
                    if (!self.stopHoverAnimation_email) {
                        $('#emailEditButt').fadeIn();
                        $('#emailEditButt').stop(true, true).fadeOut();
                    }
                }
            });
        }

        if ( $('#nicknameEditButt').length > 0 ) {
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

            $('#nicknameEditButt').on("click",
                function () {
                    /*
                        1. zatrzymaj znikanie przycisku edycji
                        2. zrób z nicknameDiv inputa
                        3. focus na inputa
                        4. event listener na inputa
                    */
                    createNicknameEditInput();
                }
            )
        }

        var emailValid = (data) => {
            if (!data.match(/(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/)) 
                return false;
            else
                return true;
          }

        var passwordValid = (data) => {
            if (data.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$/))
                return true;
            else
                return false;
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

                var isEmailValid = emailValid(email);
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

                var isPasswordValid = passwordValid(newPassword);
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

        
            // var createEmailEditInput = () => {
            //     if ( $("#emailDiv").length > 0) {
    
            //         //change div to input
            //         self.stopHoverAnimation_email = true;
            //         var currentValue = $("#emailDiv").text();
    
            //         var input = $(`<input id="emailInput"  class="col-sm-7" value="`+currentValue+`"></input>`);
            //         $("#emailDiv").replaceWith(input);
    
            //         var value = $("#emailInput").val();
            //         $("#emailInput").focus().val("").val(value);

    
            //         input.on("input propertychange",
            //             function () {
            //                 showSaveButton();
            //             }
            //         );
    
            //         input.blur(
            //             function () {
            //                 if (  $("#emailInput").length > 0 && self.email == $("#emailInput").val()) {
            //                     //change input to div
            //                     var div = $(`<div id="emailDiv" class="col-sm-7 text-secondary">` + currentValue + `</input>`);
            //                     $("#emailInput").replaceWith(div);
            //                     self.stopHoverAnimation_email = false;
            //                     $('#emailEditButt').fadeOut();
            //                 }
            //             }
            //         )
            //     }
            // }
            // $('#emailEditButt').on("click",
            //     function () {
            //         /*
            //             1. zatrzymaj znikanie przycisku edycji
            //             2. zrób z emailDiv inputa
            //             3. focus na inputa
            //             4. event listener na inputa
            //         */
            //         createEmailEditInput();
            //     }
            // )
        //}

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
    }
    
    if ( $("#btnSendChanges").length > 0 ) {
        $("#btnSendChanges").click(function() {
            if ( $("#nicknameInput").length > 0)
                self.ajaxSendProfileNicknameChanges(self.afterNicknameChangeAction, $("#nicknameInput").val());
            // if ( $("#emailInput").length > 0)
            //     self.ajaxSendProfileEmailChanges(self.afterEmailChangeAction, $("#emailInput").val());

            $("#saveChangesDiv").hide();
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
                    console.log("ajaxSendProfileEmailChanges success");
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
                    console.warn("ajaxSendProfileEmailChanges error");
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